package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Adapter.MessageListAdapter
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.ChatMessage

import jp.ac.ecc.sk3a12.ikouka.R
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


class GroupChatFragment : Fragment() {
    companion object {
        private val TEXT_OTHER = 0
        private val TEXT_SELF = 1
        private val IMAGE_OTHER = 2
        private val IMAGE_SELF = 3

        private val UPLOADING = "uploading"

        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    private var TAG = "GroupChatActiv"

    private var imageUri = ""
    //Firebase
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore
    private var mStore = FirebaseStorage.getInstance()

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
                when (viewType) {
                    TEXT_OTHER -> {
                        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_single, parent, false), viewType)
                    }
                    TEXT_SELF -> {
                        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_self, parent, false), viewType)
                    }
                    IMAGE_OTHER -> {
                        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_image_single, parent, false), viewType)
                    }
                    IMAGE_SELF -> {
                        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_image_self, parent, false), viewType)
                    }
                    else -> {
                        return MessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.message_single, parent, false))
                    }
                }
            }

            override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: ChatMessage) {
                Log.d(TAG, "ViewHolderType => ${holder.itemViewType}")
                when (holder.viewType) {
                    TEXT_OTHER, TEXT_SELF -> {
                        (holder.content as TextView).text = model.message
                    }
                    IMAGE_OTHER, IMAGE_SELF -> {
                        holder.uploading!!.visibility = ProgressBar.VISIBLE
                        if (model.message != UPLOADING) {
                            holder.uploading!!.visibility = ProgressBar.GONE
                            Glide.with(context!!)
                                    .load(model.message)
                                    .into(holder.content as ImageView)
                        }
                    }
                }

                //Not self message
                if (holder.itemViewType == TEXT_OTHER || holder.itemViewType == IMAGE_OTHER) {
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
                                        Glide.with(context!!)
                                                .load(it.getString("image"))
                                                .into(holder.avatar as ImageView)
                                    }
                                }
                    }
                }
            }


            override fun getItemViewType(position: Int): Int {
                var chatMessage = getItem(position)
                //if current user's message
                if (chatMessage.sender == mAuth.currentUser!!.uid) {
                    if (chatMessage.type == "image") {
                        return IMAGE_SELF
                    } else {
                        return TEXT_SELF
                    }

                } else {
                    if (chatMessage.type == "image") {
                        return IMAGE_OTHER
                    } else {
                        return TEXT_OTHER
                    }
                }

            }
        }

        messageRecyclerView!!.adapter = adapter
        adapter.startListening()
    }

    class MessageViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var avatar: CircleImageView? = null
        var sender: TextView? = null
        var content: Any
        var uploading: ProgressBar? = null

        init {
            if (viewType == TEXT_OTHER || viewType == IMAGE_OTHER) {
                avatar = view.findViewById(R.id.chatMessageAvatar)
                sender = view.findViewById(R.id.chatMessageSender)
            }

            if (viewType == TEXT_OTHER || viewType == TEXT_SELF) {
                content= view.findViewById<TextView>(R.id.chatMessageContent)
            } else {
                content= view.findViewById<ImageView>(R.id.chatMessageContent)
            }

            if (viewType == IMAGE_OTHER || viewType == IMAGE_SELF) {
                uploading = view.findViewById(R.id.uploading)
            }
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

        plusButton!!.setOnClickListener {
            selectImageInAlbum()
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

        mDb.collection("Groups")
                .document(groupId)
                .collection("Chat")
                .add(messageMap as Map<String, Any>)
        messageRecyclerView!!.scrollToPosition(messageRecyclerView!!.adapter!!.itemCount - 1)

    }

    private fun sendImageMessage() {
        var currentUser = mAuth.currentUser!!.uid

        var messageMap: HashMap<String, Any> = HashMap()
        messageMap.put("sender", currentUser)
        messageMap.put("message", UPLOADING)
        messageMap.put("type", "image")
        messageMap.put("timestamp", Timestamp.now())

        mDb.collection("Groups")
                .document(groupId)
                .collection("Chat")
                .add(messageMap as Map<String, Any>)
                .addOnSuccessListener {
                    uploadImage(it.id)
                }
        messageRecyclerView!!.scrollToPosition(messageRecyclerView!!.adapter!!.itemCount - 1)

    }

    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.type = "image/*"
        if (intent.resolveActivity(context!!.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM && data != null) {
            imageUri = data!!.dataString
            showImageConfirmDialog()
        }
    }

    private fun showImageConfirmDialog() {
        AlertDialog.Builder(context).apply {
            setTitle("イメージ送信")
            setMessage("この画像を送ります。よろしいですか")
            val imageView = ImageView(context)
            imageView.setImageURI(Uri.parse(imageUri))
            setView(imageView)

            setNeutralButton("キャンセル") { dialog, which ->
                dialog.dismiss()
            }
            setNegativeButton("画像選択") {dialog, which ->
                selectImageInAlbum()
                dialog.dismiss()
            }
            setPositiveButton("送信") { dialog, which ->
                sendImageMessage()
            }
            create().show()
        }
    }

    private fun uploadImage(messageId: String) {
        var extension = Magic.getFileExtension(context!!, Uri.parse(imageUri))
        var original = MediaStore.Images.Media.getBitmap(context!!.contentResolver, Uri.parse(imageUri))
        var outStream = ByteArrayOutputStream()
        when (extension) {
            "jpg", "jpeg" -> {
                original.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
            }
            "png" -> {
                original.compress(Bitmap.CompressFormat.PNG, 80, outStream)
            }
        }

        var filename = messageId + "." + extension
        var fileRef = mStore.reference.child("GroupChatImage/$groupId/$filename")
        var uploadTask = fileRef.putBytes(outStream.toByteArray())
        var urlTask = uploadTask.continueWithTask (Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                mDb.collection("Groups/$groupId/Chat")
                        .document(messageId)
                        .update("message", task.result.toString())
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                            Log.e(TAG, "DATABASE ERROR => ${it.message}")
                            //Delete message if failed uploading
                            mDb.collection("Groups/$groupId/Chat")
                                    .document(messageId)
                                    .delete()
                        }
            } else {
                Log.e(TAG, "CANNOT UPLOAD IMAGE -> ${task.exception!!.message}")

                //Delete message if failed uploading
                mDb.collection("Groups/$groupId/Chat")
                        .document(messageId)
                        .delete()
            }
        }
    }
}
