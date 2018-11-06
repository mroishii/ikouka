package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountActivity : AppCompatActivity() {
    //Firebase auth
    private lateinit var auth: FirebaseAuth
    //Firebase Database
    private lateinit var mDatabase: DatabaseReference
    //Toolbar
    private var mToolbar: Toolbar? = null
    //ProgressBar
    private var mProgressBar: ProgressBar? = null
    //Other things
    private var accountName: TextView? = null
    private var accountImage: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        //set toolbar
        mToolbar = findViewById(R.id.accountToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Firebase auth
        auth = FirebaseAuth.getInstance()
        //Firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users")
        //ProgressBar
        mProgressBar = findViewById(R.id.pbLoading)
        //Other things
        accountName = findViewById(R.id.accountName)
        accountImage = findViewById(R.id.accountImage)

        //On create activity, show loading progress, hide all
        mProgressBar!!.visibility = ProgressBar.VISIBLE
        accountName!!.visibility = TextView.INVISIBLE
        accountImage!!.visibility = ImageView.INVISIBLE
    }

    override fun onStart() {
        super.onStart()

        var uid = intent.getStringExtra("uid")
        var listener: ValueEventListener = ValueEventListener(){

        }
    }
}
