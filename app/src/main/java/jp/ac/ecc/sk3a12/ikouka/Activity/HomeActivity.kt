package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import jp.ac.ecc.sk3a12.ikouka.R
import android.graphics.Typeface
import android.widget.TextView



class HomeActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tx = findViewById(R.id.homeTitle) as TextView

        val custom_font = Typeface.createFromAsset(assets, "fonts/homeTitle.TTC")

        tx.typeface = custom_font

        val btnToRegister: Button = findViewById(R.id.btnToRegister)
        val btnToLogin: Button = findViewById(R.id.btnToLogin)

        btnToRegister.typeface = custom_font
        btnToLogin.typeface = custom_font

        btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}