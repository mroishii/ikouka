package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class MainActivity : AppCompatActivity() {
    private var inputEmail : EditText? = null
    private var inputPassword: EditText? = null
    private var btnLogin: Button? = null
    private var textStatus: TextView? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        inputEmail = findViewById(R.id.input_email)
        inputPassword = findViewById(R.id.input_password)
        btnLogin = findViewById(R.id.btnLogin)
        textStatus = findViewById(R.id.textStatus)

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
                    textStatus!!.setText(user!!.email + "logged in")
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            textStatus!!.setText(currentUser.email)
        } else {
            textStatus!!.setText("Please log in")
        }
    }

}
