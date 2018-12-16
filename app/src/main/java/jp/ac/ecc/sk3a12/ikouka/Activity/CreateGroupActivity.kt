package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.R
import jp.ac.ecc.sk3a12.ikouka.Model.User

class CreateGroupActivity : AppCompatActivity() {
    private val TAG = "CreateGroupActiv"
    //ToolBar
    private lateinit var mToolBar: Toolbar
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore
    //Current user object
    private lateinit var currentUserId: String

    //Elements
    var createGroupTitle: EditText? = null
    var createGroupDescription: EditText? = null
    var createGroupButton: Button? = null

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
        mDb = FirebaseFirestore.getInstance()

        //current user object
        currentUserId = mAuth.currentUser!!.uid

        //get elements
        createGroupTitle = findViewById(R.id.groupCreateTitle)
        createGroupDescription = findViewById(R.id.groupCreateDescription)
        createGroupButton =  findViewById(R.id.groupCreateCreateButton)

        currentUserId = mAuth.currentUser!!.uid

        //CREATE button click
        createGroupButton!!.setOnClickListener {
            var groupMap: HashMap<String, Any> = HashMap()
            groupMap.put("title", createGroupTitle!!.text.toString())
            groupMap.put("description", createGroupDescription!!.text.toString())
            groupMap.put("owner", currentUserId)
            groupMap.put("created", Timestamp.now())
            groupMap.put("anketos", object: ArrayList<String>(){})
            groupMap.put("events", object: ArrayList<Any>(){})
            groupMap.put("image", "default")

            var usersId = listOf(currentUserId)
            groupMap.put("usersId", usersId)

            var usersMap: HashMap<String, Any> = HashMap()
            var userMap: HashMap<String, Any> = HashMap()
            userMap.put("displayName", intent.getStringExtra("username"))
            userMap.put("image", "default")
            userMap.put("roles", arrayListOf("owner"))
            usersMap.put(currentUserId, userMap)
            groupMap.put("users", usersMap)

            Log.d(TAG, "groupMap Created -> $groupMap")

            //add group to database
            mDb.collection("Groups")
                    .add(groupMap as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d(TAG, "GROUP CREATED AND ADDED TO DATABASE")

                        //add created group id to current user groups array, then go to group activity
                        val createdGroupId = it.id
                        mDb.collection("Users")
                                .document(currentUserId)
                                .update("groups", FieldValue.arrayUnion(createdGroupId))
                                .addOnSuccessListener {
                                    var intent = Intent(this, GroupActivity::class.java)
                                    intent.putExtra("groupId", createdGroupId)
                                    intent.putExtra("groupTitle", createGroupTitle!!.text.toString())
                                    startActivity(intent)
                                    finish()
                                }
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "GROUP CREATED FAILED WITH -> ${it.message}")
                    }

        }

    }
}
