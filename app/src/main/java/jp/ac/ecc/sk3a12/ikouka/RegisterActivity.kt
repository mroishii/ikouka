package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.ProgressBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity: AppCompatActivity() {
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Firebase Database
    private lateinit var mDatabase: DatabaseReference
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ProgressDialog
    private lateinit var mProgress : ProgressBar

    private var btnRegister: Button? = null
    private var inputEmail: EditText? = null
    private var inputUsername: EditText? = null
    private var inputPassword: EditText? = null
    private var inputPasswordConfirm: EditText? = null

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

        btnRegister = findViewById(R.id.btnRegister)
        inputEmail = findViewById(R.id.inputEmail)
        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        inputPasswordConfirm = findViewById(R.id.inputPasswordConfirm)

        btnRegister!!.setOnClickListener {
            if (TextUtils.isEmpty(inputEmail!!.text) || TextUtils.isEmpty(inputUsername!!.text) || TextUtils.isEmpty(inputPassword!!.text)) {
                Toast.makeText(this, "入力を確認してください", Toast.LENGTH_SHORT).show()
            } else {
                var email = inputEmail!!.getText().toString().trim()
                var username = inputUsername!!.getText().toString().trim()
                var password = inputPassword!!.getText().toString().trim()
                var passwordC = inputPasswordConfirm!!.getText().toString().trim()

                Log.d("data", email + ", " + username + ", " + password + ", " + passwordC)

                if (!password.equals(passwordC)) {
                    Toast.makeText(this, "パスワードは一等ではない", Toast.LENGTH_SHORT).show()
                } else {
                    //disable text field and input
                    inputEmail!!.isEnabled = false
                    inputUsername!!.isEnabled = false
                    inputPassword!!.isEnabled = false
                    inputPasswordConfirm!!.isEnabled = false
                    btnRegister!!.isEnabled = false
                    //show progress bar
                    mProgress.visibility = ProgressBar.VISIBLE
                    //register
                    doRegister(email, password, username)
                }
            }
        }
    }

    fun doRegister(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                //------------write user data to database--------------------
                var uid = auth.currentUser!!.uid
                    //create new node <root> -> Users -> uid
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                    //create user info map
                var userMap: HashMap<String, String> = HashMap()
                userMap.put("userName", username)
                userMap.put("thumbImage", "default")
                userMap.put("image", "link")
                userMap.put("groups", "null")
                    //write to node
                mDatabase.setValue(userMap).addOnCompleteListener(this) {task ->
                    //go to main activity after completing register
                    if (task.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    }
                }
                //-----------------------------------------------------------
            } else {
                Toast.makeText(this, "ユーザ登録が失敗しました", Toast.LENGTH_SHORT).show()
                //hide progress bar
                mProgress.visibility = ProgressBar.INVISIBLE
                //enable button and textfield
                //disable text field and input
                inputEmail!!.isEnabled = true
                inputUsername!!.isEnabled = true
                inputPassword!!.isEnabled = true
                inputPasswordConfirm!!.isEnabled = true
                btnRegister!!.isEnabled = true
            }
        }
    }
}