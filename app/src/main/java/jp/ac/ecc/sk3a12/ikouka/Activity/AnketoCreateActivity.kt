package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.R

class AnketoCreateActivity : AppCompatActivity() {
    private val TAG = "AktCreateActiv"

    private var currentGroupId: String = ""

    //Toolbar
    private var mToolbar: Toolbar? = null

    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anketo_create)

        //Current Group Id
        currentGroupId = intent.getStringExtra("currentGroup")
    }
}
