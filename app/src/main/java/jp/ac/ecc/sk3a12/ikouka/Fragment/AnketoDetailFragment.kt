package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Adapter.AnketoMultipleAnswerListAdapter
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer

import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class AnketoDetailFragment : DialogFragment() {
    private val TAG = "AnketoDetailFrag"

    private val today = Date()
    private var dued = false
    private var usersMap: HashMap<String, String> = HashMap()

    private var groupId: String? = null
    private var anketoId: String? = null
    private var anketoTitle: String? = null
    private var anketoDescription:  String? = null
    private var anketoOwner: String? = null
    private var anketoDue: Long? = null

    private val mDb = Magic.getDbInstance()
    private val mAuth = FirebaseAuth.getInstance()

    //layout
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var due: TextView
    private lateinit var owner: CircleImageView
    private lateinit var answers: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let{
            groupId = it!!.getString("groupId")
            anketoId = it!!.getString("id")
            anketoTitle = it!!.getString("title")
            anketoDescription = it!!.getString("description")
            anketoOwner = it!!.getString("owner")
            anketoDue = it!!.getLong("due")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_anketo_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //header
        view.findViewById<Button>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }

        //layout
        title = view.findViewById(R.id.anketo_title)
        description = view.findViewById(R.id.anketo_description)
        due = view.findViewById(R.id.anketo_due)
        owner = view.findViewById(R.id.anketo_owner_image)
        answers = view.findViewById(R.id.anketo_answers)


        //setup layout
        title.text = anketoTitle
        description.text = anketoDescription
        due.text = "締切：" + Date(anketoDue!!).toLocaleString()

        if (Date(anketoDue!!).before(today)) {
            due.setTextColor(context!!.resources.getColor(R.color.md_red_500))
            dued = true
        }

        mDb.collection("Users")
                .document(anketoOwner!!)
                .get()
                .addOnSuccessListener {
                    if (it.getString("image") != "default") {
                        usersMap.put(anketoOwner!!, it.getString("image")!!)
                        Glide.with(this.context!!)
                                .load(it.getString("image"))
                                .into(owner as ImageView)
                    }
                }

        answers.setHasFixedSize(true)
        answers.layoutManager = LinearLayoutManager(context)

        val path = "Groups/$groupId/Anketos/$anketoId/Answers"
        val query = mDb.collection(path)

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "DATABASE ERROR => ${exception.message}")
                return@addSnapshotListener
            }
        }

        val options = FirestoreRecyclerOptions.Builder<AnketoAnswer>()
                .setQuery(query, object: SnapshotParser<AnketoAnswer> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): AnketoAnswer {
                        Log.d(TAG, snapshot.toString())
                        return AnketoAnswer(snapshot.id, snapshot.getString("description"), snapshot.get("answered") as ArrayList<String>)
                    }
                })
                .build()

        val adapter: FirestoreRecyclerAdapter<AnketoAnswer, AnketoAnswerViewHolder> = object : FirestoreRecyclerAdapter<AnketoAnswer, AnketoAnswerViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnketoAnswerViewHolder {
                return AnketoAnswerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anketo_multiple_answer_item, parent, false))
            }

            override fun onBindViewHolder(holder: AnketoAnswerViewHolder, position: Int, model: AnketoAnswer) {
                holder.answer.text = model.description
                holder.answer.isChecked = model.answered.contains(mAuth.currentUser!!.uid)

                holder.answer.isClickable = !dued

                holder.answered.removeAllViews()
                for (uid in model.answered) {
                    if (!usersMap.containsKey(uid)) {
                        Log.d(TAG, "USER IMG NOT FOUND")
                        mDb.collection("Users").document(uid).get()
                                .addOnSuccessListener {
                                    usersMap.put(uid, it.getString("image")!!)
                                    showAnswered(holder.answered, uid)
                                }
                    } else {
                        Log.d(TAG, "USER IMG FOUND => ${usersMap.get(uid)}")
                        showAnswered(holder.answered, uid)
                    }
                }

                holder.answer.setOnCheckedChangeListener { buttonView, isChecked ->
                    holder.answer.isEnabled = false
                    val docRef =  mDb.collection(path).document(model.id)
                    if (isChecked) {
                        mDb.runTransaction { transaction ->
                            transaction.update(docRef, "answered", FieldValue.arrayUnion(mAuth.currentUser!!.uid))

                            //success
                            null
                        } .addOnSuccessListener {
                            Toast.makeText(context, "更新されました。", Toast.LENGTH_SHORT).show()
                            holder.answer.isEnabled = true
                        } .addOnFailureListener {
                            holder.answer.isChecked = !isChecked
                            holder.answer.isEnabled = true
                        }

//                        mDb.collection(path)
//                                .document(model.id)
//                                .update("answered", FieldValue.arrayUnion(mAuth.currentUser!!.uid))
//                                .addOnSuccessListener {
//                                    Toast.makeText(context, "更新されました。", Toast.LENGTH_SHORT).show()
//                                    holder.answer.isEnabled = true
//                                }
//                                .addOnFailureListener {
//                                    holder.answer.isChecked = !isChecked
//                                    holder.answer.isEnabled = true
//                                }
                    } else {
//                        mDb.collection(path)
//                                .document(model.id)
//                                .update("answered", FieldValue.arrayRemove(mAuth.currentUser!!.uid))
//                                .addOnSuccessListener {
//                                    Toast.makeText(context, "更新されました。", Toast.LENGTH_SHORT).show()
//                                    holder.answer.isEnabled = true
//                                }
//                                .addOnFailureListener {
//                                    holder.answer.isChecked = !isChecked
//                                    holder.answer.isEnabled = true
//                                }
                        mDb.runTransaction { transaction ->
                            transaction.update(docRef, "answered", FieldValue.arrayRemove(mAuth.currentUser!!.uid))

                            //success
                            null
                        } .addOnSuccessListener {
                            Toast.makeText(context, "更新されました。", Toast.LENGTH_SHORT).show()
                            holder.answer.isEnabled = true
                        } .addOnFailureListener {
                            holder.answer.isChecked = !isChecked
                            holder.answer.isEnabled = true
                        }
                    }
                }
            }
        }

        answers.adapter = adapter
        adapter.startListening()

    }
    private fun showAnswered(layout: LinearLayout, uid:String) {
        val inputParams = LinearLayout.LayoutParams(32, LinearLayout.LayoutParams.MATCH_PARENT)

        val userImage = CircleImageView(this.context)
        layout.addView(userImage, inputParams)
        if (usersMap.get(uid) != "default") {
            Log.d(TAG, "USER IMG: ${usersMap.get(uid)}")
            Glide.with(this.context!!)
                    .load(usersMap.get(uid))
                    .into(userImage)
        } else {
            Glide.with(this.context!!)
                    .load(resources.getDrawable(R.drawable.default_avatar))
                    .into(userImage)
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params
    }

    class AnketoAnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var answer = view.findViewById<CheckBox>(R.id.anketo_answer_checkbox)
        var answered = view.findViewById<LinearLayout>(R.id.anketo_answered)
    }


}
