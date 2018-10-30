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


class LoginActivity : AppCompatActivity() {
    private var labelLoginTitle: TextView? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var mDrawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //アイテムのインスタンスを取得
        var inputEmail : EditText = findViewById(R.id.inputEmail)
        var inputPassword: EditText = findViewById(R.id.inputPassword)
        var btnLogin: Button = findViewById(R.id.btnLogin)
        var btnRegister: Button = findViewById(R.id.btnRegister)
        labelLoginTitle = findViewById(R.id.labelLoginTitle)
        auth = FirebaseAuth.getInstance() //FirebasAuthenticator のインスタンス

        btnLogin!!.setOnClickListener {
            var email = inputEmail!!.text.toString()
            var password = inputPassword!!.text.toString()
            Log.w("info", "email:" + email)
            Log.w("info", "password:" + password)
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) {task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Toast.makeText(baseContext, user!!.email.toString() + " logged in.",
                            Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRegister!!.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                //ただの画面変更です
            }
            startActivity(intent)
        }

        mDrawerLayout = findViewById(R.id.drawer_layout)

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer_layout when item is tapped
            mDrawerLayout.closeDrawers()

            // Add code here to update the UI based on the item selected
            // For example, swap UI fragments here

            true
        }


    }

    //ビューの起動時、ログイン状態をチェック
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
    }

}
