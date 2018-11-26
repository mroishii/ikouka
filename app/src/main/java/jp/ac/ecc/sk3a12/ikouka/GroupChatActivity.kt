package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.util.*

class GroupChatActivity : AppCompatActivity() {
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbRoot: DatabaseReference

    //Toolbar
    private lateinit var mToolbar: Toolbar

    private var sendButton: ImageButton? = null
    private var plusButton: ImageButton? = null
    private var inputMessage: EditText? = null

    //current group
    private lateinit var currentGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //Toolbar
        mToolbar = findViewById(R.id.groupChatActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "Chat"
        supportActionBar!!.subtitle = "ima chatting with my friend"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Initialize variable
        sendButton = findViewById(R.id.groupChatSendButton)
        plusButton = findViewById(R.id.groupChatAddButton)
        inputMessage = findViewById(R.id.groupChatTypeTextBox)

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        dbRoot = FirebaseDatabase.getInstance().getReference()

        //Get Current Group Info
        if (savedInstanceState == null) {
            currentGroup = intent.getParcelableExtra("group")
        } else {
            currentGroup = savedInstanceState.getParcelable("currentGroup")
        }
    }

    override fun onStart() {
        super.onStart()

        sendButton!!.setOnClickListener{
            if (!TextUtils.isEmpty(inputMessage!!.text.toString())) {
                sendMessage()
                inputMessage!!.setText("")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("currentGroup", currentGroup)
    }

    private fun sendMessage() {
        var message = inputMessage!!.text.toString()
        var currentUser = mAuth.currentUser!!.uid

        var mChatMessage = ChatMessage(currentUser, message)

        //db ref for group chat
        var dbGroupChat =  dbRoot.child("GroupChat").child(currentGroup.groupId)

        //add message to database
        var addedMessageId = dbGroupChat.push().key.toString()
        dbGroupChat.child(addedMessageId).child("sender").setValue(mChatMessage.sender)
        dbGroupChat.child(addedMessageId).child("timestamp").setValue(mChatMessage.timestamp)
        dbGroupChat.child(addedMessageId).child("message").setValue(mChatMessage.message)


    }
}
