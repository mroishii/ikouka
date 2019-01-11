package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.R
import java.lang.Exception

class RegisterActivity: AppCompatActivity() {
    private val TAG: String = "RegisterActi"
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore
    //Storage
    private lateinit var mStore: FirebaseStorage
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ProgressDialog
    private lateinit var mProgress : ProgressBar

    private lateinit var profilePicture: CircleImageView
    private lateinit var btnRegister: Button
    private lateinit var inputEmail: EditText
    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var inputPasswordConfirm: EditText
    private lateinit var errorMessage: TextView

    private var imageUri = "default"

    private var userMap: HashMap<String, Any> = HashMap()

    private lateinit var uid: String

    //View for picture dialog box
    private lateinit var dialogView: View

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        //Set toolbar
        mToolbar = findViewById(R.id.RegisterToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "登録"
        //enable back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Progressbar
        mProgress = findViewById(R.id.pbRegister)
        //Firebase Auth
        auth = FirebaseAuth.getInstance()
        //Firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings
        //Storage
        mStore = FirebaseStorage.getInstance()

        profilePicture = findViewById(R.id.profilePicture)
        inputEmail = findViewById(R.id.inputEmail)
        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm)
        errorMessage = findViewById(R.id.labelErrorMessage)
        btnRegister = findViewById(R.id.btnRegister)

        //Picture picker dialog box view
        dialogView = layoutInflater.inflate(R.layout.dialogbox_picture_picker, null)
        dialogView.findViewById<ImageButton>(R.id.btnPickPicture).setOnClickListener {
            selectImageInAlbum()
        }

        dialogView.findViewById<ImageButton>(R.id.btnTakePicture).setOnClickListener {
            takePhoto()
        }

        profilePicture.setOnClickListener {
            selectImageInAlbum()
        }


        btnRegister!!.setOnClickListener {
            //Hide error message
            errorMessage.visibility = TextView.INVISIBLE

            if (TextUtils.isEmpty(inputEmail.text) || TextUtils.isEmpty(inputUsername.text) || TextUtils.isEmpty(inputPassword.text)) {
                errorMessage.visibility = TextView.VISIBLE
                errorMessage.text = "全フィールドを入力してください"
            } else {
                var email = inputEmail.getText().toString().trim()
                var username = inputUsername.getText().toString().trim()
                var password = inputPassword.getText().toString().trim()
                var passwordC = inputPasswordConfirm.getText().toString().trim()

                Log.d("data", email + ", " + username + ", " + password + ", " + passwordC)

                if (password.length < 6){
                    errorMessage.visibility = TextView.VISIBLE
                    errorMessage.text = "６文字以上のパースワードを入力してください。"
                }else if (!password.equals(passwordC)) {
                    errorMessage.visibility = TextView.VISIBLE
                    errorMessage.text = "パスワード確認が一致しません。"
                } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    errorMessage.visibility = TextView.VISIBLE
                    errorMessage.text = "メールが正しくありません。"
                } else {
                    //Hide error
                    errorMessage.visibility = TextView.INVISIBLE
                    //disable text field and input
                    profilePicture.isClickable = false
                    inputEmail.isEnabled = false
                    inputUsername.isEnabled = false
                    inputPassword.isEnabled = false
                    inputPasswordConfirm.isEnabled = false
                    btnRegister.visibility = Button.INVISIBLE
                    //show progress bar
                    mProgress.visibility = ProgressBar.VISIBLE
                    //register
                    doRegister(email, password, username)
                }
            }
        }

    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }
    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }

    fun doRegister(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                uid = auth.currentUser!!.uid
                //email
                userMap.put("email", auth.currentUser!!.email.toString())
                //userName
                userMap.put("userName", username)
                //thumbImage
                userMap.put("thumbImage", "default")
                //groups
                val groups: ArrayList<String> = ArrayList()
                userMap.put("groups", groups)
                //joined
                val timestamp: Timestamp = Timestamp.now()
                userMap.put("joined", timestamp)

                //image
                if (imageUri.equals("default")) {
                    userMap.put("image", "default")
                    Log.d(TAG, "UserMap created -> $userMap")
                    updateUsersDb()
                } else {
                    uploadImage()
                }

            } else {
                if (task.exception is FirebaseAuthUserCollisionException) {
                    showError("このメールはご利用できません。")
                } else if (task.exception is Exception) {
                    showError("エラーが発生した：${task.exception.toString()}")
                }
            }
        }
//        try {
//
//        } catch (e: FirebaseAuthUserCollisionException) {
//            showError("このメールが登録済みでした。")
//        } catch (e: Exception) {
//            showError("エラーが発生した：${e.message}")
//        }
    }

    fun uploadImage() {
//        var filename = uid + "." + getFileExtension(Uri.parse(imageUri))
        var filename = uid + "." + Magic.getFileExtension(this, Uri.parse(imageUri))
        var fileRef = mStore.reference.child("UsersProfileImage/$filename")
        var uploadTask = fileRef.putFile(Uri.parse(imageUri))
        var urlTask = uploadTask.continueWithTask (Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "UserMap created -> $userMap")
                userMap.put("image", task.result.toString())

                updateUsersDb()
            } else {
                // Handle failures
                // ...
            }
        }

    }

    fun updateUsersDb() {
        //write to users db
        mDb.collection("Users")
                .document(auth.currentUser!!.uid)
                .set(userMap as Map<String, Any>)
                .addOnSuccessListener { task ->
                    Log.d(TAG, "WRITE USER SUCCESSFULLY")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Log.d(TAG, "WRITE FAILED AT -> ${it.message}")

                    Toast.makeText(this, "ユーザ登録が失敗しました", Toast.LENGTH_SHORT).show()
                    //hide progress bar
                    mProgress.visibility = ProgressBar.INVISIBLE
                    //enable button and textfield
                    inputEmail.isEnabled = true
                    inputUsername.isEnabled = true
                    inputPassword.isEnabled = true
                    inputPasswordConfirm.isEnabled = true
                    btnRegister.isEnabled = true
                }
        //-----------------------------------------------------------
    }

    fun showError(error: String) {
        errorMessage.visibility = TextView.VISIBLE
        errorMessage.text = error
        //hide progress bar
        mProgress.visibility = ProgressBar.INVISIBLE
        //re-enable button and textfield
        inputEmail.isEnabled = true
        inputUsername.isEnabled = true
        inputPassword.isEnabled = true
        inputPasswordConfirm.isEnabled = true
        btnRegister.visibility = Button.VISIBLE
        profilePicture.isClickable = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM) {
            imageUri = data!!.dataString
            Glide.with(this)
                    .load(imageUri)
                    .into(profilePicture)
        }
    }

    fun getFileExtension(uri: Uri): String {
        lateinit var filename : String
        if (uri.getScheme().equals("content")) {
            var cursor: Cursor = contentResolver.query(uri, null, null, null, null)
            try {
              if (cursor != null && cursor.moveToFirst()) {
                  filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
              }
            } finally {
              cursor.close()
            }
          }

          if (filename == null) {
            filename = uri.getPath()
            var cut = filename.lastIndexOf('/')
            if (cut != -1) {
              filename = filename.substring(cut + 1)
            }
          }

        return filename.substring(filename.lastIndexOf('.') + 1)
    }


}