package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import jp.ac.ecc.sk3a12.ikouka.Magic

import jp.ac.ecc.sk3a12.ikouka.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EditGroupFragment : DialogFragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDb: FirebaseFirestore
    private lateinit var mStore: FirebaseStorage

    private var imageUri = ""
    private var groupId = ""
    private var groupMap = HashMap<String, Any>()

    //Layout Element
    private lateinit var groupTitle: EditText
    private lateinit var groupDescription: EditText
    private lateinit var groupImage: ImageView
    private lateinit var groupEditButton: Button
    private lateinit var groupImageChangeButton: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()
        mDb = Magic.getDbInstance()
        mStore = FirebaseStorage.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Header Exit Button
        view.findViewById<ImageView>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }

        //Layout Elements
        groupTitle = view.findViewById(R.id.editGroupTitle)
        groupDescription = view.findViewById(R.id.editGroupDescription)
        groupImage = view.findViewById(R.id.editGroupImage)
        groupEditButton = view.findViewById(R.id.editGroupEdit)
        groupImageChangeButton = view.findViewById(R.id.editGroupImageChange)
        progressBar = view.findViewById(R.id.editGroupProgress)

        //Load group info
        mDb.collection("Groups")
                .document(groupId)
                .get()
                .addOnSuccessListener {
                    groupTitle.text.insert(0, it.getString("title"))
                    groupDescription.text.insert(0, it.getString("description"))
                    if (it.getString("image") != "default") {
                        Glide.with(context!!)
                                .load(it.getString("image"))
                                .into(groupImage)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(context, "グループデータを取得できません。", Toast.LENGTH_SHORT).show()
                    this.dismiss()
                }

        groupImageChangeButton.setOnClickListener {
            selectImageInAlbum()
        }

        groupEditButton.setOnClickListener {
            switchDisplay(true)

            var title = groupTitle.text.toString()
            var description = groupDescription.text.toString()

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                switchDisplay(false)
                Toast.makeText(context, "入力を確認してください。", Toast.LENGTH_LONG).show()
            } else {
                groupMap.apply {
                    put("title", title)
                    put("description", description)
                }

                if (imageUri != "") {
                    doUploadImage()
                } else {
                    doUpdateGroup()
                }
            }
        }
    }

    fun doUpdateGroup() {
        mDb.collection("Groups")
                .document(groupId)
                .update(groupMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "グループを更新されました", Toast.LENGTH_SHORT).show()
                    this.dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "グループを更新できません。エラー：${it.message}", Toast.LENGTH_SHORT).show()
                    switchDisplay(false)
                }


    }

    fun doUploadImage() {
        var filename = groupId + "." + Magic.getFileExtension(context!!, Uri.parse(imageUri))
        var fileRef = mStore.reference.child("GroupImage/$filename")
        var uploadTask = fileRef.putFile(Uri.parse(imageUri))
        var urlTask = uploadTask.continueWithTask (Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }

            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                groupMap.put("image", task.result.toString())
                doUpdateGroup()
            } else {
                // Handle failures
                Toast.makeText(context, "写真アップロードに失敗しました。", Toast.LENGTH_SHORT).show()
                doUpdateGroup()
            }
        }
    }

    fun switchDisplay(isLoading: Boolean) {
        if (isLoading) {
            groupTitle.isEnabled = false
            groupDescription.isEnabled = false
            progressBar.visibility = ProgressBar.VISIBLE
            groupImageChangeButton.visibility = ImageButton.INVISIBLE
            groupEditButton.visibility = Button.INVISIBLE
            this.dialog.setCanceledOnTouchOutside(false)
        } else {
            groupTitle.isEnabled = true
            groupDescription.isEnabled = true
            progressBar.visibility = ProgressBar.INVISIBLE
            groupImageChangeButton.visibility = ImageButton.VISIBLE
            groupEditButton.visibility = Button.VISIBLE
            this.dialog.setCanceledOnTouchOutside(true)
        }
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
            Glide.with(this)
                    .load(imageUri)
                    .into(groupImage)
        }
    }

    companion object {
        @JvmStatic
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
        fun newInstance(groupId: String) =
                EditGroupFragment().apply {
                    this.groupId = groupId
                }
    }


}
