package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Model.ChatMessage

import jp.ac.ecc.sk3a12.ikouka.R


class GroupChatFragment : Fragment() {
    private var TAG = "GroupChatActiv"
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore

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
        groupId = arguments!!.getString("groupId")
        
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Initialize Firebase
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()

        //Initialize variable
        sendButton = view.findViewById(R.id.groupChatSendButton)
        plusButton = view.findViewById(R.id.groupChatAddButton)
        inputMessage = view.findViewById(R.id.groupChatTypeTextBox)
        //Setup RecyclerView
        messageRecyclerView = view.findViewById(R.id.groupChatMessageList)
        val linearLayout = LinearLayoutManager(context)
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
        val adapter: FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder> = object : FirestoreRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
                if (viewType == 0) {
                    return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_single, parent, false))
                }

                return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_self, parent, false))

            }

            override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: ChatMessage) {
                Log.d(TAG, "ViewHolderType => ${holder.itemViewType}")
                holder.content!!.text = model.message

                //Not self message
                if (holder.itemViewType == 0) {
                    if (usersMap.containsKey(model.sender)) {
                        var userMap = usersMap.get(model.sender) as HashMap<String, String>

                        holder.sender!!.text = userMap.get("userName")
                        if (userMap.get("image") != "default") {
                            Glide.with(this@GroupChatFragment)
                                    .load(userMap.get("image"))
                                    .into(holder.avatar as ImageView)
                        }
                    } else {
                        mDb.collection("Users")
                                .document(model.sender)
                                .get()
                                .addOnSuccessListener {
                                    Log.d(TAG, "GotUser => $it")

                                    var userMap: HashMap<String, String?> = HashMap()
                                    userMap.put("userName", it.getString("userName"))
                                    userMap.put("image", it.getString("image"))
                                    usersMap.put(it.id, userMap)

                                    holder.sender!!.text = it.getString("userName")
                                    if (it.getString("image") != "default") {
                                        Glide.with(this@GroupChatFragment)
                                                .load(it.getString("image"))
                                                .into(holder.avatar as ImageView)
                                    }
                                }
                    }
                }
            }


            override fun getItemViewType(position: Int): Int {
                var chatMessage = getItem(position)
                if (chatMessage.sender == mAuth.currentUser!!.uid) {
                    Log.d(TAG, "$chatMessage => self")
                    return 1
                }

                Log.d(TAG, "$chatMessage => other")
                return 0
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
                sendMessage()
                inputMessage!!.setText("")
            }
        }
    }

    private fun sendMessage() {
        var message = inputMessage!!.text.toString()
        var currentUser = mAuth.currentUser!!.uid


        var messageMap: HashMap<String, Any> = HashMap()
        messageMap.put("sender", currentUser)
        messageMap.put("message", message)
        messageMap.put("type", "text")
        messageMap.put("timestamp", Timestamp.now())

        mDb.collection("Groups").document(groupId).collection("Chat").add(messageMap as Map<String, Any>)
        messageRecyclerView!!.scrollToPosition(messageRecyclerView!!.adapter!!.itemCount - 1)

    }
}
