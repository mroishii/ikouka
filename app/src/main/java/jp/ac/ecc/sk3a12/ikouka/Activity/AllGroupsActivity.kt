package jp.ac.ecc.sk3a12.ikouka.Activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.grouplist_item.view.*

class AllGroupsActivity : AppCompatActivity() {
    //ToolBar
    private lateinit var mToolbar: Toolbar
    //RecycleView
    private lateinit var  mRecyclerView: RecyclerView
    //Database Reference
    private lateinit var mDatabase: DatabaseReference

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
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        //Database Reference
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Groups")
    }

    override fun onStart() {
        super.onStart()

        var adapterOptions: FirebaseRecyclerOptions<Group> = FirebaseRecyclerOptions.Builder<Group>()
                .setQuery(mDatabase, Group::class.java)
                .build()

        var adapter: FirebaseRecyclerAdapter<Group, GroupViewHolder> = object : FirebaseRecyclerAdapter<Group, GroupViewHolder>(adapterOptions) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
                return GroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.grouplist_item, parent, false))
            }

            override fun onBindViewHolder(holder: GroupViewHolder, position: Int, model: Group) {
                holder.itemView.grouplist_item_title.text = model.title
                holder.itemView.grouplist_item_description.text = model.description
            }
        }

        mRecyclerView.adapter = adapter
        adapter.startListening()
    }

    class GroupViewHolder(itemView: View, var group: Group? = null): RecyclerView.ViewHolder(itemView) {
        fun bind(group: Group) {
            with(group) {
                itemView.grouplist_item_title?.text = group.title
                itemView.grouplist_item_description?.text = group.description
            }
        }

    }
}
