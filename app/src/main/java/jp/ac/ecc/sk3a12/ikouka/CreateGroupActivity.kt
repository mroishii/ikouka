package jp.ac.ecc.sk3a12.ikouka

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateGroupActivity : AppCompatActivity() {
    //ToolBar
    private lateinit var mToolBar: Toolbar
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    //Current user object
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        //set toolbar
        mToolBar = findViewById(R.id.createGroupActionBar)
        setSupportActionBar(mToolBar)
        supportActionBar!!.title = "グループ作成"

        //firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().getReference()

        //current user object
        user = intent.getParcelableExtra("currentUser")

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
            val groupsDatabase: DatabaseReference = mDatabase.child("Groups")

            //push new node to Groups database, get key as new groupId
            val createdGroupId = groupsDatabase.push().key

            //write to database
            groupsDatabase.child(createdGroupId!!).child("title").setValue(createGroupTitle.text.toString())
            groupsDatabase.child(createdGroupId!!).child("description").setValue(createGroupDescription.text.toString())
            groupsDatabase.child(createdGroupId!!).child("image").setValue("default")
            groupsDatabase.child(createdGroupId!!).child("owner").setValue(currentUserId.toString())
            groupsDatabase.child(createdGroupId!!).child("users").child(currentUserId).child("roles").setValue("owner")

            //add newly added group id to current user groups info...........
            var currentUserGroups = user.userGroups
            if (currentUserGroups.equals("null")) {
                currentUserGroups = createdGroupId
            } else {
                currentUserGroups += "//" + createdGroupId
            }
            //.......then update groups info to database................
            mDatabase.child("Users").child(currentUserId).child("groups").setValue(currentUserGroups)
            //........also update the current user object.............
            user.userGroups = currentUserGroups
            //.......then create new object for new group.............
            val group : Group = Group(
                    createdGroupId,
                    createGroupTitle.text.toString(),
                    createGroupDescription.text.toString(),
                    currentUserId,
                    "default"
            )
            //........finally, put all object to intent and start the group activity
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("group", group)
            intent.putExtra("currentUser", user)
            intent.putExtra("groupTitle", group.title)
            startActivity(intent)
            finish()

        }
    }
}
