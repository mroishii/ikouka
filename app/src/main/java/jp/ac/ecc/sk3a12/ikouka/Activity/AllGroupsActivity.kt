package jp.ac.ecc.sk3a12.ikouka.Activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.facebook.shimmer.ShimmerFrameLayout
import com.firebase.ui.firestore.SnapshotParser
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.google.firebase.auth.FirebaseAuth
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import com.google.firebase.firestore.Query


class AllGroupsActivity : AppCompatActivity() {
    //ToolBar
    private lateinit var mToolbar: Toolbar
    //RecycleView
    private lateinit var  mRecyclerView: RecyclerView
    //Database Reference
    private lateinit var mDatabase: FirebaseFirestore
    //Firebase auth
    private lateinit var mAuth: FirebaseAuth
    //Shimmer
    private lateinit var shimmerContainer: ShimmerFrameLayout

    private var mContext = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_groups)

        //set toolbar
        mToolbar = findViewById(R.id.allGroupsActionBar)
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "All Groups"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //RecycleView
        mRecyclerView = findViewById(R.id.allGroupsList)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)

        mAuth = FirebaseAuth.getInstance()
        //Database Reference
        mDatabase = FirebaseFirestore.getInstance()

        //shimmer
        shimmerContainer = findViewById(R.id.shimmer_view_container)

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
                .setQuery(query, object: SnapshotParser<Group> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): Group {
                        return Group(snapshot.id,
                                snapshot.getString("title"),
                                snapshot.getString("description"),
                                snapshot.getString("owner"),
                                snapshot.getString("image"))
                    }
                })
                .build()

        val adapter: FirestoreRecyclerAdapter<Group, GroupViewHolder> = object: FirestoreRecyclerAdapter<Group, GroupViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                return GroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grouplist_item, parent, false))
            }

            override fun onBindViewHolder(holder: GroupViewHolder, position: Int, model: Group) {
                holder.groupTitle.text = model.title
                holder.groupDescription.text = model.description
                if (model.image != "default") {
                    Glide.with(mContext)
                            .load(model.image)
                            .into(holder.groupImage)
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(mContext, GroupActivity::class.java)
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



//        var query: Query = mDatabase.collection("BuyNotification")
//                .whereEqualTo("owner", mAuth.currentUser!!.uid)
//
//        query.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                //エラーメッセ―ジを表示する
//                return@addSnapshotListener
//            }
//
//            var buyNotis: List<BuyNoti> = snapshot!!.toObjects(BuyNoti::class.java)
//
//        }
//
//        val options = FirestoreRecyclerOptions.Builder<BuyNoti>()
//                .setQuery(query, BuyNoti::class.java)
//                .build()
//
//
//        var adapter: FirestoreRecyclerAdapter<BuyNoti, NotiViewHolder> = object : FirestoreRecyclerAdapter<BuyNoti, NotiViewHolder>(options) {
//            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
//                return NotiViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.buy_noti_item, parent, false))
//            }
//
//            override fun onBindViewHolder(holder: NotiViewHolder, position: Int, model: BuyNoti) {
//                var
//
//                shimmerContainer.stopShimmer()
//                shimmerContainer.visibility = View.GONE
//            }
//        }
//
        mRecyclerView.adapter = adapter
        adapter.startListening()
    }



//    class NotiViewHolder(itemView: View, var buyNoti: BuyNoti? = null): RecyclerView.ViewHolder(itemView) {
//        fun bind(buyNoti: BuyNoti) {
//            with(buyNoti) {
//                itemView.noti?.text = ""
//                itemView.notiDate?.text = ""
//
//            }
//        }
//
//    }

    class GroupViewHolder(view: View, var viewType: Int? = 0): RecyclerView.ViewHolder(view) {
        var groupImage: ImageView = view.findViewById(R.id.grouplist_item_image)
        var groupTitle: TextView = view.findViewById(R.id.grouplist_item_title)
        var groupDescription: TextView = view.findViewById(R.id.grouplist_item_description)
    }

    override fun onResume() {
        super.onResume()
        shimmerContainer.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerContainer.stopShimmer()
    }

}
