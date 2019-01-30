package jp.ac.ecc.sk3a12.ikouka.Fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView

import jp.ac.ecc.sk3a12.ikouka.R
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import jp.ac.ecc.sk3a12.ikouka.Magic


/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class UserProfileFragment : DialogFragment() {
    val TAG = "UserProfileFrag"

    val USERNAME_FIELD = "userName"
    val IMAGE_FIELD = "image"

    private lateinit var parent: Context
    private var userId: String = ""
    private var imageUri = ""

    //FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    //Firestore
    private lateinit var mDb: FirebaseFirestore
    //Firebase Store
    private val mStore = FirebaseStorage.getInstance()

    //LayoutElements
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var image: CircleImageView
    private lateinit var qrCode: ImageView
    private lateinit var changeUsername: Button
    private lateinit var changeImage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString("userId")

        }
        mAuth = FirebaseAuth.getInstance()
        mDb = Magic.getDbInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Layout Elements
        username = view.findViewById(R.id.userProfileUsername)
        email = view.findViewById(R.id.userProfileEmail)
        image = view.findViewById(R.id.userProfileImage)
        qrCode = view.findViewById(R.id.userProfileQR)
        changeUsername = view.findViewById(R.id.header_editname)
        changeImage = view.findViewById(R.id.header_editimage)

        //Set-up header
        view.findViewById<Button>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }

        if (mAuth.currentUser!!.uid == userId) {
            changeUsername.visibility = Button.VISIBLE
            changeImage.visibility = Button.VISIBLE
        }

        changeUsername.setOnClickListener {
            val textBox = EditText(context)
            AlertDialog.Builder(context).apply {
                setTitle("ユーザ名変更")
                setView(textBox)
                setPositiveButton("変更") { dialog, which ->
                    updateInfo(USERNAME_FIELD, textBox.text.toString())
                }
                setNeutralButton("キャンセル") {dialog, which ->
                    dialog.dismiss()
                }
                create().apply {
                    setOnShowListener {
                        (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                        textBox.addTextChangedListener(object: TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s!!.isNotEmpty()
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                            }
                        })
                    }

                    show()
                }
            }
        }

        getInfo()

        changeImage.setOnClickListener {
            selectImageInAlbum()
        }

    }

    fun getInfo() {
        mDb.collection("Users").document(userId).get()
                .addOnSuccessListener {
                    username.text = it.getString("userName")
                    email.text = it.getString("email")
                    if (it.getString("image") != "default") {
                        Glide.with(parent)
                                .load(it.getString("image"))
                                .into(image as ImageView)
                    }
                    imageUri = it.getString("image")!!

                    //generate qr code
                    val multiFormatWriter = MultiFormatWriter()
                    try {
                        val bitMatrix = multiFormatWriter.encode(userId, BarcodeFormat.QR_CODE, 200, 200)
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                        qrCode.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }

                }
    }

    fun updateInfo(field: String, data: String) {
        mDb.collection("Users").document(userId)
                .update(field, data)
                .addOnSuccessListener {
                    Toast.makeText(context, "更新されました", Toast.LENGTH_SHORT)
                    when (field) {
                        USERNAME_FIELD -> {
                            username.text = data
                        }
                        IMAGE_FIELD -> {

                        }
                    }
                }
    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "image/*"
        if (intent.resolveActivity(parent.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    fun afterSelectImage() {
        Log.d(TAG, "GOT IMAGE => $imageUri")
        //Show confirm dialog
        AlertDialog.Builder(context).apply{
            setTitle("代表画像の変更")
            setMessage("この画像に変更します。よろしいですか？")
//            val confirmView = layoutInflater.inflate(R.layout.change_image_confirm_dialog, null)
//            setView(confirmView)
//            confirmView.findViewById<ImageView>(R.id.imageChanged).setImageURI(Uri.parse(imageUri))
            val imageView = ImageView(context)
            imageView.setImageURI(Uri.parse(imageUri))
            setView(imageView)

            setNeutralButton("キャンセル") { dialog, which ->
                dialog.dismiss()
            }
            setPositiveButton("変更") { dialog, which ->
                uploadImage()

            }
            create().show()

        }
    }

    fun uploadImage() {
//        var filename = uid + "." + getFileExtension(Uri.parse(imageUri))
        var filename = mAuth.currentUser!!.uid + "." + Magic.getFileExtension(context!!, Uri.parse(imageUri))
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
                mDb.collection("Users")
                        .document(mAuth.currentUser!!.uid)
                        .update("image", task.result.toString())
                        .addOnSuccessListener {
                            Glide.with(this)
                                    .load(imageUri)
                                    .into(image)
                            Toast.makeText(context, "代表写真が変更されました。", Toast.LENGTH_SHORT)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "代表写真の変更に失敗しました。", Toast.LENGTH_SHORT)
                            Log.e(TAG, "DATABASE ERROR => ${it.message}")
                        }
            } else {
                Log.e(TAG, "CANNOT UPLOAD IMAGE")
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && data != null) {
            imageUri = data!!.dataString
            afterSelectImage()
        }
    }

    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        @JvmStatic
        fun newInstance(context: Context, userId: String) =
                UserProfileFragment().apply {
                    arguments = Bundle().apply {
                        putString("userId", userId)
                    }
                    parent = context
                }
    }
}
