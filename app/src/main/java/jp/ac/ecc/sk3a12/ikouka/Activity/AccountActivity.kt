package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.R

class AccountActivity : AppCompatActivity() {
    //Firebase auth
    private lateinit var mAuth: FirebaseAuth
    //Firebase Database
    private lateinit var mDb: FirebaseFirestore
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ProgressBar
    private var mProgressBar: ProgressBar? = null
    //Other things
    private var accountName: TextView? = null
    private var accountImage: CircleImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        //set toolbar
        mToolbar = findViewById(R.id.accountToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Firebase auth
        mAuth = FirebaseAuth.getInstance()
        //Firebase database
        mDb = FirebaseFirestore.getInstance()
        //ProgressBar
        mProgressBar = findViewById(R.id.pbLoading)
        //Other things
        accountName = findViewById(R.id.accountName)
        accountImage = findViewById(R.id.accountImage)

        //On create activity, show loading progress, hide all
        mProgressBar!!.visibility = ProgressBar.VISIBLE
        accountName!!.visibility = TextView.INVISIBLE
        //accountImage!!.visibility = CircleImageView.INVISIBLE
    }

    override fun onStart() {
        super.onStart()

        mDb.collection("Users")
                .document(mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    mProgressBar!!.visibility = ProgressBar.INVISIBLE
                    accountName!!.visibility = TextView.VISIBLE
                    accountName!!.text = it.getString("userName")
                }

    }
}
