package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.Model.Activity
import jp.ac.ecc.sk3a12.ikouka.Model.AnketoAnswer
import jp.ac.ecc.sk3a12.ikouka.Model.User
import jp.ac.ecc.sk3a12.ikouka.R
import java.text.SimpleDateFormat
import java.util.*


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
    private var currentRoles = "member"

    private var mAuth = FirebaseAuth.getInstance()
    private var mDb = FirebaseFirestore.getInstance()

    private lateinit var memberListView: RecyclerView
    private lateinit var activityList: RecyclerView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupId = arguments!!.getString("groupId")

        view.findViewById<Button>(R.id.invite).visibility = Button.INVISIBLE

        mDb.collection("Groups/$groupId/Users")
                .document(mAuth.currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    currentRoles = (it.get("roles") as ArrayList<String>).get(0)
                    if (currentRoles == "owner" || currentRoles == "admin") {
                        //User invite button
                        view.findViewById<Button>(R.id.invite).visibility = Button.VISIBLE
                        view.findViewById<Button>(R.id.invite).setOnClickListener {
                            val fragment = UserInviteFragment.newInstance(groupId)
                            fragment.showNow(activity!!.supportFragmentManager, "USER_INVITE")
                        }

                    }
                }


        //Load group image
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

        //--------------MEMBER LIST RECYCLER VIEW------------------------------------------------------------------------------------------
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

        //--------------MEMBER LIST RECYCLER VIEW END-------------------------------------------------------------------------------------------

        //--------------------------GROUP ACTIVITY RECYCLER VIEW START--------------------------------------------------------------------------

        activityList = view.findViewById(R.id.activity_list)
        activityList.setHasFixedSize(true)
        activityList.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, true)

        val path2 = "Groups/$groupId/Activities"
        val query2 = mDb.collection(path2)

        query2.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Log.d(TAG, "DATABASE ERROR => ${exception.message}")
                return@addSnapshotListener
            }
        }

        val options2 = FirestoreRecyclerOptions.Builder<Activity>()
                .setQuery(query2, object: SnapshotParser<Activity> {
                    override fun parseSnapshot(snapshot: DocumentSnapshot): Activity {
                        Log.d(TAG, snapshot.toString())
                        return Activity(snapshot.id,
                                snapshot.getString("userId"),
                                snapshot.getString("action"),
                                snapshot.getString("reference"),
                                snapshot.get("timestamp") as Timestamp)
                    }
                })
                .build()

        val activityListAdapter: FirestoreRecyclerAdapter<Activity, ActivityViewHolder> = object : FirestoreRecyclerAdapter<Activity, ActivityViewHolder>(options2) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
                return ActivityViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.group_activity_list_item, parent, false))
            }

            override fun onBindViewHolder(holder: ActivityViewHolder, position: Int, model: Activity) {
                mDb.collection("Users").document(model.userId).get()
                        .addOnSuccessListener {
                            var activityText = "【${it.getString("userName")}】さんが"
                            when (model.action) {
                                Activity.JOINED_GROUP -> {
                                    activityText += "グループに参加しました。"
                                    holder.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_members_black_24dp))
                                }

                                Activity.CREATED_ANKETO -> {
                                    activityText += "新しいアンケートを作成しました。"
                                    holder.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_anketo_black_24dp))
                                }

                                Activity.CREATED_EVENT -> {
                                    activityText += "新しいイベントを追加しました。"
                                    holder.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_event_black_24dp))
                                }

                                Activity.CREATED_TODO -> {
                                    activityText += "新しいタスクを追加しました。"
                                    holder.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_task_black_24dp))
                                }

                                Activity.LEFT_GROUP -> {
                                    activityText += "退会しました。"
                                    holder.icon.setImageDrawable(resources.getDrawable(R.drawable.ic_members_black_24dp))
                                }
                            }

                            holder.activityTxt.text = activityText

                            if (it.getString("image") != "default" ) {
                                Glide.with(context!!)
                                        .load(it.getString("image"))
                                        .into(holder.image)
                            }
                        }

                val date = Date(model.timestamp.seconds * 1000)
                val fmt = SimpleDateFormat("yyyy年MM月dd日 HH:mm")
                holder.date.text = fmt.format(date)

            }
        }
        activityList.adapter = activityListAdapter
        activityListAdapter.startListening()

        //--------------------------GROUP ACTIVITY RECYCLER VIEW END--------------------------------------------------------------------------

    }

    class UserViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<CircleImageView>(R.id.userimage)
        val username = view.findViewById<TextView>(R.id.username)
        val userrole = view.findViewById<TextView>(R.id.userrole)
    }

    class ActivityViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image = view.findViewById<CircleImageView>(R.id.activityImage)
        val icon = view.findViewById<ImageView>(R.id.icon)
        val activityTxt = view.findViewById<TextView>(R.id.activityText)
        val date = view.findViewById<TextView>(R.id.activityDate)
    }


}
