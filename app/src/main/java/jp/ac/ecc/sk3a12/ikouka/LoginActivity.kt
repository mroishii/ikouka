package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    private var labelLoginTitle: TextView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //アイテムのインスタンスを取得
        var inputEmail : EditText = findViewById(R.id.inputEmail)
        var inputPassword: EditText = findViewById(R.id.inputPassword)
        var btnLogin: Button = findViewById(R.id.btnLogin)
        var btnGoRegister: Button = findViewById(R.id.btnGoRegister)
        labelLoginTitle = findViewById(R.id.labelLoginTitle)
        auth = FirebaseAuth.getInstance() //FirebaseAuthenticator のインスタンス

        btnLogin.setOnClickListener {
            var email = inputEmail!!.text.toString()
            var password = inputPassword!!.text.toString()
            Log.w("info", "email:" + email)
            Log.w("info", "password:" + password)
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userEmail", user!!.email)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnGoRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                //ただの画面変更です
            }
            startActivity(intent)
        }
    }

    //ビューの起動時、ログイン状態をチェック
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }
}
