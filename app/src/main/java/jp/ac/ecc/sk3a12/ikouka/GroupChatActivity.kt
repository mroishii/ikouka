package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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

    //message list display
    private var messageRecyclerView: RecyclerView? = null
    //message list
    private var messageList: ArrayList<ChatMessage> = ArrayList<ChatMessage>()

    private lateinit var linearLayout: LinearLayoutManager

    //MessageListAdapter
    private lateinit var mMessageListAdapter: MessageListAdapter

    //current group
    private lateinit var currentGroup: Group

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        //Get Current Group Info
        if (savedInstanceState == null) {
            currentGroup = intent.getParcelableExtra("group")
        } else {
            currentGroup = savedInstanceState.getParcelable("currentGroup")
        }

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        dbRoot = FirebaseDatabase.getInstance().getReference()

        //Toolbar
        mToolbar = findViewById(R.id.groupChatActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "チャット"
        supportActionBar!!.subtitle = currentGroup.title
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Initialize variable
        sendButton = findViewById(R.id.groupChatSendButton)
        plusButton = findViewById(R.id.groupChatAddButton)
        inputMessage = findViewById(R.id.groupChatTypeTextBox)
        //Initialize recyclerview, set config, adapter
        messageRecyclerView = findViewById(R.id.groupChatMessageList)
        linearLayout = LinearLayoutManager(this)
        messageRecyclerView!!.setHasFixedSize(true)
        messageRecyclerView!!.layoutManager = linearLayout

        mMessageListAdapter = MessageListAdapter(messageList)
        messageRecyclerView!!.adapter = mMessageListAdapter
        //Load the messages
        loadMessage()

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
        var timestamp = Calendar.getInstance().timeInMillis

        var messageMap: HashMap<String, String> = HashMap()
        messageMap.put("sender", currentUser)
        messageMap.put("message", message)
        Log.d("MessageAdd", ServerValue.TIMESTAMP.toString())
        messageMap.put("timestamp", timestamp.toString())

        var mChatMessage = ChatMessage(currentUser, message)

        //db ref for group chat
        var dbGroupChat =  dbRoot.child("GroupChat").child(currentGroup.groupId)

        //add message to database
        var addedMessageId = dbGroupChat.push().key.toString()
        dbGroupChat.child(addedMessageId).child("sender").setValue(mChatMessage.sender)
        dbGroupChat.child(addedMessageId).child("timestamp").setValue(mChatMessage.timestamp)
        dbGroupChat.child(addedMessageId).child("message").setValue(mChatMessage.message)


    }


    private fun loadMessage() {
        var messageListener: ChildEventListener = object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MessageList", p0.details)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                Log.d("MessageList", p0.toString())
                var message = ChatMessage(  p0.child("sender").value.toString(),
                                            p0.child("message").value.toString(),
                                            p0.child("timestamp").value.toString())
                messageList.add(message)
                mMessageListAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        }

        var dbGroupChat = dbRoot.child("GroupChat").child(currentGroup.groupId)
        dbGroupChat.addChildEventListener(messageListener)
    }
}
