package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
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

    //Group object array for adapter
    private var groups: ArrayList<Group> = ArrayList()
    private lateinit var mGroupListAdapter: GroupListAdapter

    //Groups listview
    private var groupList: ListView? = null

    //Firebase auth
    private lateinit var mAuth: FirebaseAuth

    //Firestore
    private lateinit var mDb: FirebaseFirestore

    private lateinit var currentUser: User
    private var currentUserId: String = ""
    private var oldGroupsList: ArrayList<String> = ArrayList()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //init firebase
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        currentUserId = mAuth.currentUser!!.uid


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //group listview
        groupList = view!!.findViewById(R.id.grouplist)
        mGroupListAdapter = GroupListAdapter(groups, context)
        groupList!!.adapter = mGroupListAdapter

        mDb.collection("Users")
                .document(currentUserId)
                .addSnapshotListener(EventListener<DocumentSnapshot>  { snapshot, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@EventListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, "Current data: " + snapshot.data)
                        doneGetUser(snapshot)
                    } else {
                        Log.d(TAG, "Current data: null")
                    }
                })

    }

    private fun doneGetUser(userDs: DocumentSnapshot?) {
        currentUser = User(
                userDs!!.id,
                userDs!!.getString("userName"),
                userDs!!.getString("email"),
                userDs!!.get("groups") as ArrayList<String>,
                userDs!!.getString("image"),
                userDs!!.getString("thumbImage"),
                (userDs!!.get("joined") as Timestamp).seconds * 1000)

        Log.d(TAG, "Current user object created -> $currentUser")

        for (groupId in currentUser.userGroups) {
            if (oldGroupsList.contains(groupId)) {
                Log.d(TAG, "GROUP ALREADY SHOWN")
            } else {
                mDb.collection("Groups")
                        .document(groupId)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                if (task.result == null) {
                                    Log.d(TAG, "FIRESTORE -> CANNOT FIND THIS GROUP DOCUMENT -> $groupId")
                                } else {
                                    val groupDs = task.result
                                    Log.d(TAG, "FIRESTORE -> CURRENT GROUP DOCUMENT: " + groupDs)

                                    Log.d(TAG, "NEW GROUP FOUND")
                                    doneGetGroup(task.result)


                                }

                            } else {
                                Log.d(TAG, "FIRESTORE -> GET FAILED WITH ", task.exception)
                            }
                        }
            }
        }
    }

    private fun doneGetGroup(groupDs: DocumentSnapshot?) {
        var group = Group(groupDs!!.id,
                groupDs.getString("title"),
                groupDs.getString("description"),
                groupDs.getString("owner"),
                groupDs.getString("image"))

        Log.d(TAG, "Group object created -> " + group.title)

        groups.add(group)
        oldGroupsList.add(group.groupId)
        mGroupListAdapter.notifyDataSetChanged()
    }

}
