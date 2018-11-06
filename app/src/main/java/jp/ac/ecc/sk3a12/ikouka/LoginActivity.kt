package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Toolbar
    private var mToolbar: Toolbar? = null
    //Progress bar
    private lateinit var mProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //set toolbar
        mToolbar = findViewById(R.id.loginActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "ログイン"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //ProgressBar
        mProgress = findViewById(R.id.pbLogin)
        //アイテムのインスタンスを取得
        var inputEmail : EditText = findViewById(R.id.inputEmail)
        var inputPassword: EditText = findViewById(R.id.inputPassword)
        var btnLogin: Button = findViewById(R.id.btnLogin)
        //FirebaseAuthenticator のインスタンス
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(inputEmail.text) || TextUtils.isEmpty(inputPassword.text)) {
                Toast.makeText(this, "入力を確認してください。", Toast.LENGTH_SHORT).show()
            } else {
                //get value
                var email = inputEmail.text.toString()
                var password = inputPassword.text.toString()
                //Show progress bar
                mProgress.visibility = ProgressBar.VISIBLE
                //Disable input
                inputEmail.isEnabled = false
                inputPassword.isEnabled = false
                btnLogin.isEnabled = false
                //Do authorize
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
                    if (task.isSuccessful) {
                        // Sign in success
                        val intent = Intent(this, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        // Sign in fails
                        Toast.makeText(this, "ログイン失敗しました", Toast.LENGTH_SHORT).show()
                        mProgress.visibility = ProgressBar.INVISIBLE
                        //Enable input
                        inputEmail.isEnabled = true
                        inputPassword.isEnabled = true
                        btnLogin.isEnabled = true
                    }
                }
            }
        }
    }

    //ビューの起動時、ログイン状態をチェック
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }
}
