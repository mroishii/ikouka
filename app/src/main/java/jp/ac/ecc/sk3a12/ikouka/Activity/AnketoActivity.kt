package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoActivity : AppCompatActivity() {
    private val TAG = "AnketoActiv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo)

        intent.setExtrasClassLoader(Anketo::class.java.classLoader)
        val anketo : Anketo = intent.getParcelableExtra("anketo")

        val tv: TextView = findViewById(R.id.textView)
        tv.text = anketo.toString()
    }
}
