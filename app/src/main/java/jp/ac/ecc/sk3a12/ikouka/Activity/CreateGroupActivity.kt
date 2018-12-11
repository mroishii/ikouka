package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import jp.ac.ecc.sk3a12.ikouka.R
import jp.ac.ecc.sk3a12.ikouka.Model.User

class CreateGroupActivity : AppCompatActivity() {
    //ToolBar
    private lateinit var mToolBar: Toolbar
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    //Current user object
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //set toolbar
        mToolBar = findViewById(R.id.createGroupActionBar)
        setSupportActionBar(mToolBar)
        supportActionBar!!.title = "グループ作成"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()

        //current user object
        currentUserId = mAuth.currentUser!!.uid

    }

    override fun onStart() {
        super.onStart()

        //get textview and edittext
        val createGroupTitle: EditText = findViewById(R.id.groupCreateTitle)
        val createGroupDescription: EditText = findViewById(R.id.groupCreateDescription)
        val createGroupButton: Button =  findViewById(R.id.groupCreateCreateButton)

        val currentUserId = mAuth.currentUser!!.uid

        //CREATE button click
        createGroupButton.setOnClickListener {


        }
    }
}
