package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Activity.AnketoCreateActivity
import jp.ac.ecc.sk3a12.ikouka.Activity.GroupActivity
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.Anketo
import jp.ac.ecc.sk3a12.ikouka.Model.Event

import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [GroupAnketo.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class GroupAnketoFragment : Fragment() {
    private val TAG = "GroupAnketoFragment"

    private var groupId: String? = null
    private var fm: FragmentManager? = null

    private val mDb = Magic.getDbInstance()
    private val mAuth = FirebaseAuth.getInstance()

    //layout
    private lateinit var anketoList: RecyclerView
    private lateinit var createButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            groupId = it.getString("groupId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        this.fm = activity!!.supportFragmentManager
        return inflater.inflate(R.layout.fragment_group_anketo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        anketoList = view.findViewById(R.id.anketo_list)
        anketoList.setHasFixedSize(true)
        anketoList.layoutManager = LinearLayoutManager(this@GroupAnketoFragment.context)

        createButton = view.findViewById(R.id.anketo_list_fab)
        createButton.setOnClickListener {
            val intent = Intent(context, AnketoCreateActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)

        }

        val query = mDb.collection("Groups/$groupId/Anketos")
                .orderBy("created", Query.Direction.DESCENDING)

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "DATABASE ERROR => ${exception.message}")
                return@addSnapshotListener
            }

            for (document in snapshot!!.documents) {
                Log.d(TAG, document.toString())
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

        anketoList.adapter = adapter
        adapter.startListening()

    }

    class AnketoViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var title = view.findViewById<TextView>(R.id.anketo_list_title)
        var description = view.findViewById<TextView>(R.id.anketo_list_description)
        var due = view.findViewById<TextView>(R.id.anketo_list_due)
        var owner: CircleImageView = view.findViewById(R.id.anketo_list_image)
        var done: ImageView = view.findViewById(R.id.anketo_list_answered)
    }
}
