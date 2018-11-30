package jp.ac.ecc.sk3a12.ikouka

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class GroupChatActivity : AppCompatActivity() {
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var dbRoot: DatabaseReference
    private lateinit var dbGroupChat: DatabaseReference

    //Toolbar
    private lateinit var mToolbar: Toolbar
    //Input message bar
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
        dbGroupChat = dbRoot.child("GroupChat").child(currentGroup.groupId)

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
        //Setup RecyclerView
        messageRecyclerView = findViewById(R.id.groupChatMessageList)
        linearLayout = LinearLayoutManager(this)
        messageRecyclerView!!.setHasFixedSize(true)
        messageRecyclerView!!.layoutManager = linearLayout
        //Setup Adapter
        mMessageListAdapter = MessageListAdapter(messageList, mAuth.currentUser!!.uid, currentGroup.users)
        messageRecyclerView!!.adapter = mMessageListAdapter
        //Listener for database
        var messageListener: ChildEventListener = object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Log.d("MessageList", p0.details)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                loadMessage(p0)
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        }
        dbGroupChat.addChildEventListener(messageListener)

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

        //var mChatMessage = ChatMessage(currentUser, message)

        var messageMap: HashMap<String, String> = HashMap()
        messageMap.put("sender", currentUser)
        messageMap.put("message", message)
        messageMap.put("type", "text")
        messageMap.put("timestamp", Calendar.getInstance().timeInMillis.toString())

        var pushedId = dbGroupChat.push().key.toString()
        dbGroupChat.child(pushedId).updateChildren(messageMap.toMap())
    }

    private fun loadMessage(p0: DataSnapshot) {
        var message: ChatMessage? = p0.getValue(ChatMessage::class.java)
        messageList.add(message!!)
        mMessageListAdapter.notifyDataSetChanged()
        Log.d("MessageLoad", p0.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        super.onOptionsItemSelected(item)

        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return true
    }
}
