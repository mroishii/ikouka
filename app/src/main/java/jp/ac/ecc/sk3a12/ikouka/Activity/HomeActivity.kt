package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.ac.ecc.sk3a12.ikouka.R
import android.graphics.Typeface
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.Magic


class HomeActivity: AppCompatActivity() {
    //Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    //Database
    private val mDb = Magic.getDbInstance()

    //Progress bar
    private lateinit var mProgress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //ProgressBar
        mProgress = findViewById(R.id.pbLogin)

        //アイテムのインスタンスを取得
        var inputEmail : EditText = findViewById(R.id.inputEmail)
        var inputPassword: EditText = findViewById(R.id.inputPassword)

        //Set logo text font
        val tx = findViewById(R.id.homeTitle) as TextView
        val custom_font = Typeface.createFromAsset(assets, "fonts/homeTitle.TTC")
        tx.typeface = custom_font

        //Button
        val btnToRegister: Button = findViewById(R.id.btnToRegister)
        val btnToLogin: Button = findViewById(R.id.btnToLogin)
        btnToRegister.typeface = custom_font
        btnToLogin.typeface = custom_font

        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnToLogin.setOnClickListener {
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
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
                btnToLogin.visibility = Button.INVISIBLE
                btnToRegister.visibility = Button.INVISIBLE
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
                        btnToLogin.visibility = Button.VISIBLE
                        btnToRegister.visibility = Button.VISIBLE
                    }
                }
            }
        }

    }
}