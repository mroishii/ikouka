package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import jp.ac.ecc.sk3a12.ikouka.Activity.AllGroupsActivity
import jp.ac.ecc.sk3a12.ikouka.Activity.GroupActivity
import jp.ac.ecc.sk3a12.ikouka.Activity.TestActivity
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupListAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.Model.User
import jp.ac.ecc.sk3a12.ikouka.R
import jp.ac.ecc.sk3a12.ikouka.R.id.groups
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupsListFragment : Fragment() {
    private val TAG = "GroupsListFrag"

    //RecycleView
    private lateinit var  mRecyclerView: RecyclerView
    //Database Reference
    private lateinit var mDatabase: FirebaseFirestore
    //Firebase auth
    private lateinit var mAuth: FirebaseAuth
    //Shimmer
    private lateinit var shimmerContainer: ShimmerFrameLayout

    private var mContext = this
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //init firebase
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseFirestore.getInstance()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //shimmer
        shimmerContainer = view.findViewById(R.id.shimmer_view_container)
        //RecycleView
        mRecyclerView = view.findViewById(R.id.groupslist)
        mRecyclerView.setHasFixedSize(true)
//        mRecyclerView.layoutManager = GridLayoutManager(context, 2)
        mRecyclerView.layoutManager = LinearLayoutManager(context)

        var query: Query = mDatabase.collection("Groups")
                .whereArrayContains("usersId", mAuth.currentUser!!.uid)

        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                //エラーメッセ―ジを表示する
                return@addSnapshotListener
            }

            Log.d("snapshot", snapshot!!.documents.toString())
        }

        val options = FirestoreRecyclerOptions.Builder<Group>()
                .setQuery(query, object : SnapshotParser<Group> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): Group {
                        return Group(snapshot.id,
                                snapshot.getString("title"),
                                snapshot.getString("description"),
                                snapshot.getString("owner"),
                                snapshot.getString("image"))
                    }
                })
                .build()

        val adapter: FirestoreRecyclerAdapter<Group, GroupViewHolder> = object : FirestoreRecyclerAdapter<Group, GroupViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                return GroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grouplist_item, parent, false))
            }

            override fun onBindViewHolder(holder: GroupViewHolder, position: Int, model: Group) {
                holder.groupTitle.text = model.title
                holder.groupDescription.text = model.description
                if (!model.image.equals("default")) {
                    Glide.with(mContext)
                            .load(model.image)
                            .into(holder.groupImage)
                }
                holder.groupImage.setClipToOutline(true)

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, TestActivity::class.java)
                    intent.putExtra("groupId", model.getGroupId())
                    intent.putExtra("groupTitle", model.getTitle())
                    mContext.startActivity(intent)
                }

                if (shimmerContainer.visibility != View.GONE) {
                    shimmerContainer.stopShimmer()
                    shimmerContainer.visibility = View.GONE
                }
            }
        }

        mRecyclerView.adapter = adapter
        adapter.startListening()
    }

    override fun onResume() {
        super.onResume()
        shimmerContainer.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerContainer.stopShimmer()
    }

    class GroupViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var groupImage: ImageView = view.findViewById(R.id.grouplist_item_image)
        var groupTitle: TextView = view.findViewById(R.id.grouplist_item_title)
        var groupDescription: TextView = view.findViewById(R.id.grouplist_item_description)
    }

}
