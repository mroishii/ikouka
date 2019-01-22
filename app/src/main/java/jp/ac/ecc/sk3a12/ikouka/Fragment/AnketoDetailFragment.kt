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
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo

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
        view.findViewById<TextView>(R.id.header_title).text = anketoId
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

        mDb.collection("Users")
                .document(anketoOwner!!)
                .get()
                .addOnSuccessListener {
                    if (it.getString("image") != "default") {
                        Glide.with(this.context!!)
                                .load(it.getString("image"))
                                .into(owner as ImageView)
                    }
                }

        answers.setHasFixedSize(true)
        answers.layoutManager = LinearLayoutManager(context)

        val query = mDb.collection("Groups/$groupId/Anketos/$anketoId/Answers")

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "DATABASE ERROR => ${exception.message}")
                return@addSnapshotListener
            }
        }

        val options = FirestoreRecyclerOptions.Builder<Anketo>()
                .setQuery(query, object : SnapshotParser<Anketo> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): Anketo {
                        return Anketo(snapshot.id,
                                Date((snapshot.get("created") as Timestamp).seconds * 1000),
                                snapshot.getString("type"),
                                snapshot.getString("title"),
                                snapshot.getString("description"),
                                snapshot.getString("owner"),
                                Date((snapshot.get("due") as Timestamp).seconds * 1000))
                    }
                })
                .build()

        val adapter: FirestoreRecyclerAdapter<Anketo, AnketoViewHolder> = object : FirestoreRecyclerAdapter<Anketo, AnketoViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnketoViewHolder {
                return AnketoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.anketo_list_item, parent, false))
            }

            override fun onBindViewHolder(holder: AnketoViewHolder, position: Int, model: Anketo) {
                Log.d(TAG, model.toString())
                holder.title.text = model.title
                holder.description.text = model.description
                holder.due.text = "締切：" + model.due.toLocaleString()

                mDb.collection("Users")
                        .document(model.owner)
                        .get()
                        .addOnSuccessListener {
                            if (it.getString("image") != "default") {
                                Glide.with(this@GroupAnketoFragment)
                                        .load(it.getString("image"))
                                        .into(holder.owner as ImageView)
                            }
                        }

                holder.itemView.setOnClickListener {
                    val detailFragment = AnketoDetailFragment()
                    var arguments = Bundle()

                    arguments.putString("groupId", groupId)
                    arguments.putString("id", model.id)
                    arguments.putString("title", model.title)
                    arguments.putString("description", model.description)
                    arguments.putString("owner", model.owner)
                    arguments.putLong("due", model.due.time)
                    detailFragment.arguments = arguments

                    detailFragment.showNow(activity!!.supportFragmentManager, "ANKETO_DETAIL")
                }
            }
        }


    }


}
