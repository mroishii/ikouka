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

class RegisterActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var mToolbar: Toolbar? = null

    //ProgressDialog
    private lateinit var mProgress : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //set toolbar
        mToolbar = findViewById(R.id.RegisterToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "登録"
        //enable back button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Progressbar
        mProgress = findViewById(R.id.pbRegister)

        auth = FirebaseAuth.getInstance()

        val btnRegister: Button = findViewById(R.id.btnRegister)
        val inputEmail: EditText = findViewById(R.id.inputEmail)
        val inputUsername: EditText = findViewById(R.id.inputUsername)
        val inputPassword: EditText = findViewById(R.id.inputPassword)
        val inputPasswordConfirm: EditText = findViewById(R.id.inputPasswordConfirm)

        btnRegister.setOnClickListener {
            if (TextUtils.isEmpty(inputEmail.text) || TextUtils.isEmpty(inputUsername.text) || TextUtils.isEmpty(inputPassword.text)) {
                Toast.makeText(this, "入力を確認してください", Toast.LENGTH_SHORT).show()
            } else {
                var email = inputEmail.getText().toString().trim()
                var username = inputUsername.getText().toString().trim()
                var password = inputPassword.getText().toString().trim()
                var passwordC = inputPasswordConfirm.getText().toString().trim()

                Log.d("data", email + ", " + username + ", " + password + ", " + passwordC)

                if (!password.equals(passwordC)) {
                    Toast.makeText(this, "パスワードは一等ではない", Toast.LENGTH_SHORT).show()
                } else {
                    //disable text field and input
                    inputEmail.isEnabled = false
                    inputUsername.isEnabled = false
                    inputPassword.isEnabled = false
                    inputPasswordConfirm.isEnabled = false
                    btnRegister.isEnabled = false
                    mProgress.visibility = ProgressBar.VISIBLE
                    doRegister(email, password, username)
                }
            }
        }
    }

    fun doRegister(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "ユーザ登録が失敗しました", Toast.LENGTH_SHORT).show()
                mProgress.visibility = ProgressBar.INVISIBLE
            }
        }
    }
}