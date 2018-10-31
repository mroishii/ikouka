package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val btnRegister: Button = findViewById(R.id.btnRegister)
        val inputEmail: EditText = findViewById(R.id.inputEmail)
        val inputUsername: EditText = findViewById(R.id.inputUsername)
        val inputPassword: EditText = findViewById(R.id.inputPassword)
        val inputPasswordConfirm: EditText = findViewById(R.id.inputPasswordConfirm)

        btnRegister.setOnClickListener {
            var email = inputEmail.getText().toString().trim()
            var username = inputUsername.getText().toString().trim()
            var password = inputPassword.getText().toString().trim()
            var passwordC = inputPasswordConfirm.getText().toString().trim()

            Log.d("data", email + ", " + username + ", " + password + ", " + passwordC)

            if (!password.equals(passwordC)) {
                Toast.makeText(this, "パスワード確認は一等ではない", Toast.LENGTH_SHORT).show()
            } else {
                doRegister(email, password, username)
            }
        }


    }

    fun doRegister(email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("userEmail", auth.currentUser!!.email.toString())
                startActivity(intent)
            } else {
                Toast.makeText(this, "ユーザ登録が失敗しました", Toast.LENGTH_SHORT).show()
            }
        }
    }
}