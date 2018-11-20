package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var mToolBar: Toolbar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        mToolBar = findViewById(R.id.createGroupActionBar)
        setSupportActionBar(mToolBar)
        supportActionBar!!.title = "グループ作成"

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()

    }

    override fun onStart() {
        super.onStart()

        val createGroupTitle: EditText = findViewById(R.id.groupCreateTitle)
        val createGroupDescription: EditText = findViewById(R.id.groupCreateDescription)
        val createGroupButton: Button =  findViewById(R.id.groupCreateCreateButton)

        createGroupButton.setOnClickListener {
            val groupsDatabase: DatabaseReference = mDatabase.child("Groups")
            val createdGroupId = groupsDatabase.push().key
            groupsDatabase.child(createdGroupId!!).child("title").setValue(createGroupTitle.text)
            groupsDatabase.child(createdGroupId!!).child("description").setValue(createGroupDescription.text)
            groupsDatabase.child(createdGroupId!!).child("image").setValue("default")
            groupsDatabase.child(createdGroupId!!).child("owner").setValue(mAuth.currentUser!!.uid)
            val createdEvent = groupsDatabase.child(createdGroupId!!).child("events").push().key
        }
    }
}
