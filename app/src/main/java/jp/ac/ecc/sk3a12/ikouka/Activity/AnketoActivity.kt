package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoActivity : AppCompatActivity() {
    private val TAG = "AnketoActiv"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo)

        val anketo : Anketo = intent.getParcelableExtra("anketo")
        Log.d(TAG, anketo.toString())
    }
}
