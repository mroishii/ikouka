package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.Model.User
import jp.ac.ecc.sk3a12.ikouka.R


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupDashboardFragment : Fragment() {
    private val TAG = "GroupDashboard"

    private var groupId = ""

    private var mAuth = FirebaseAuth.getInstance()
    private var mDb = FirebaseFirestore.getInstance()

    private lateinit var memberListView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupId = arguments!!.getString("groupId")

        val groupImage: ImageView = view.findViewById(R.id.groupMenuImage)
        mDb.collection("Groups")
                .document(groupId)
                .get()
                .addOnSuccessListener {
                    if (it.getString("image") != "default") {
                        Glide.with(context!!)
                                .load(it.getString("image"))
                                .into(groupImage)
                        view.findViewById<TextView>(R.id.groupDescription).text = it.getString("description")
                    }
                }

        memberListView = view.findViewById(R.id.member_list)
        memberListView.setHasFixedSize(true)
        memberListView.layoutManager = LinearLayoutManager(context, LinearLayout.HORIZONTAL, false)

        val path = "Groups/$groupId/Users"
        val query = mDb.collection(path)
                .whereEqualTo("status", "active")

        query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "DATABASE ERROR => ${exception.message}")
                return@addSnapshotListener
            }
        }

        val options = FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, object: SnapshotParser<User> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): User {
                        Log.d(TAG, snapshot.toString())
                        return User(snapshot.id,
                                (snapshot.get("roles") as ArrayList<String>).get(0))
                    }
                })
                .build()

        val memberListAdapter: FirestoreRecyclerAdapter<User, UserViewHolder> = object : FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.memberlist_item, parent, false))
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                mDb.collection("Users")
                        .document(model.userId)
                        .get()
                        .addOnSuccessListener {
                            holder.username.text = it.getString("userName")

                            when (model.roles) {
                                "member" -> {holder.userrole.text = "メンバー"}
                                "admin" -> {
                                    holder.userrole.text = "管理者"
                                    holder.userrole.setTextColor(resources.getColor(R.color.md_blue_600))
                                }
                                "owner" -> {
                                    holder.userrole.text = "所有者"
                                    holder.userrole.setTextColor(resources.getColor(R.color.md_red_400))
                                }
                                else -> {holder.userrole.text = model.roles}
                            }

                            if (it.getString("image") != "default") {
                                Glide.with(context!!)
                                        .load(it.getString("image"))
                                        .into(holder.image)
                            } else {
                                Glide.with(context!!)
                                        .load(resources.getDrawable(R.drawable.default_avatar))
                                        .into(holder.image)
                            }
                        }

            }
        }

        memberListView.adapter = memberListAdapter
        memberListAdapter.startListening()

    }

    class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<CircleImageView>(R.id.userimage)
        val username = view.findViewById<TextView>(R.id.username)
        val userrole = view.findViewById<TextView>(R.id.userrole)
    }


}
