package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Model.ChatMessage
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.Adapter.MessageListAdapter
import jp.ac.ecc.sk3a12.ikouka.Fragment.GroupsListFragment
import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*
import kotlin.collections.HashMap

class GroupChatActivity : AppCompatActivity() {
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

    //Toolbar
    private lateinit var mToolbar: Toolbar
    //Input message bar
    private var sendButton: ImageButton? = null
    private var plusButton: ImageButton? = null
    private var inputMessage: EditText? = null
    //message list display
    private var messageRecyclerView: RecyclerView? = null
    //usersMap
    private var usersMap: HashMap<String, Any> = HashMap()

    //current group
    private var groupId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_chat)

        groupId = intent.getStringExtra("groupId")

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        //Toolbar
        mToolbar = findViewById(R.id.groupChatActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "チャット"
        supportActionBar!!.subtitle = intent.getStringExtra("groupTitle")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //Initialize variable
        sendButton = findViewById(R.id.groupChatSendButton)
        plusButton = findViewById(R.id.groupChatAddButton)
        inputMessage = findViewById(R.id.groupChatTypeTextBox)
        //Setup RecyclerView
        messageRecyclerView = findViewById(R.id.groupChatMessageList)
        val linearLayout = LinearLayoutManager(this)
        messageRecyclerView!!.setHasFixedSize(true)
        messageRecyclerView!!.layoutManager = linearLayout

        //Query
        val query: Query = mDb.collection("Groups").document(groupId).collection("Chat").orderBy("timestamp")

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //エラーメッセ―ジを表示する
                return@addSnapshotListener
            }

            Log.d("snapshot", snapshot!!.documents.toString())
        }

        val options = FirestoreRecyclerOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage::class.java)
                .build()

        //Setup Adapter
        val adapter: FirestoreRecyclerAdapter<ChatMessage, GroupChatActivity.MessageViewHolder> = object : FirestoreRecyclerAdapter<ChatMessage, GroupChatActivity.MessageViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupChatActivity.MessageViewHolder {
                if (viewType == 0) {
                    return GroupChatActivity.MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_single, parent, false))
                } else {
                    return GroupChatActivity.MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_self, parent, false))
                }

            }

            override fun onBindViewHolder(holder: GroupChatActivity.MessageViewHolder, position: Int, model: ChatMessage) {
                holder.content!!.text = model.message

                //Not self message
                if (holder.viewType == 0) {
                    if (usersMap.containsKey(model.sender)) {
                        var userMap = usersMap.get(model.sender) as HashMap<String, String>

                        holder.sender!!.text = userMap.get("userName")
                        if (userMap.get("image") != "default") {
                            Glide.with(applicationContext)
                                    .load(userMap.get("image"))
                                    .into(holder.avatar as ImageView)
                        }
                    } else {
                        mDb.collection("Users")
                                .document(model.sender)
                                .get()
                                .addOnSuccessListener {
                                    var userMap: HashMap<String, String?> = HashMap()
                                    userMap.put("userName", it.getString("userName"))
                                    userMap.put("image", it.getString("image"))
                                    usersMap.put(it.id, userMap)

                                    holder.sender!!.text = it.getString("userName")
                                    if (it.getString("image") != "default") {
                                        Glide.with(applicationContext)
                                                .load(it.getString("image"))
                                                .into(holder.avatar as ImageView)
                                    }
                                }
                    }
                }
            }


            override fun getItemViewType(position: Int): Int {
                var chatMessage = getItem(position)
                if (chatMessage.sender != mAuth.currentUser!!.uid) {
                    return 0
                } else {
                    return 1
                }
            }
        }

        messageRecyclerView!!.adapter = adapter
        adapter.startListening()

    }

    class MessageViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var avatar: CircleImageView? = null
        var sender: TextView? = null
        var content: TextView? = null

        init {
            if (viewType == 0) {
                avatar = view.findViewById(R.id.chatMessageAvatar)
                sender = view.findViewById(R.id.chatMessageSender)
            }
            content= view.findViewById(R.id.chatMessageContent)


        }
    }

    override fun onStart() {
        super.onStart()

        sendButton!!.setOnClickListener{
            if (!TextUtils.isEmpty(inputMessage!!.text.toString())) {
//                sendMessage()
                inputMessage!!.setText("")
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }

        return super.onOptionsItemSelected(item)
    }

//    private fun sendMessage() {
//        var message = inputMessage!!.text.toString()
//        var currentUser = mAuth.currentUser!!.uid
//
//        //var mChatMessage = ChatMessage(currentUser, message)
//
//        var messageMap: HashMap<String, String> = HashMap()
//        messageMap.put("sender", currentUser)
//        messageMap.put("message", message)
//        messageMap.put("type", "text")
//        messageMap.put("timestamp", Calendar.getInstance().timeInMillis.toString())
//
//        var pushedId = dbGroupChat.push().key.toString()
//        dbGroupChat.child(pushedId).updateChildren(messageMap.toMap())
//    }
}
