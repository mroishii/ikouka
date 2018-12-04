package jp.ac.ecc.sk3a12.ikouka


import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import de.hdodenhof.circleimageview.CircleImageView
import jp.ac.ecc.sk3a12.ikouka.R.id.groups
import java.nio.file.Files.find
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupsFragment : Fragment() {
    private val TAG = "GroupsFrag"

    //Group object array for adapter
    private var groups: ArrayList<Group> = ArrayList()
    private lateinit var mGroupListAdapter: GroupListAdapter

    //Groups listview
    private var groupList: ListView? = null

    //Firebase auth
    private lateinit var mAuth: FirebaseAuth

    //Firestore
    private lateinit var mDb: FirebaseFirestore
    lateinit var currentUser: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //init firebase
        mAuth = FirebaseAuth.getInstance()
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentUser = mAuth.currentUser!!.uid

        //group listview
        groupList = view!!.findViewById(R.id.grouplist)
        mGroupListAdapter = GroupListAdapter(groups, context)
        groupList!!.adapter = mGroupListAdapter

        //Get current user document
        mDb.collection("Users")
                .document(currentUser)
                .get()
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        if (task.result == null) {
                            Log.d(TAG, "FIRESTORE -> CANNOT FIND CURRENT USER DOCUMENT")
                        }
                        else {
                            Log.d(TAG, "FIRESTORE -> CURRENT USER DOCUMENT: " + task.result)
                            doneGetUser(task.result)
                        }

                    } else {
                        Log.d(TAG, "FIRESTORE -> GET FAILED WITH ", task.exception)
                    }
                }
    }

    private fun doneGetUser(userDs: DocumentSnapshot?) {
        Log.d(TAG, "userDs" + userDs)
        currentUser = User(
                userDs!!.id,
                userDs!!.getString("userName"),
                userDs!!.getString("email"),
                userDs!!.get("groups") as ArrayList<String>,
                userDs!!.getString("image") ,
                userDs!!.getString("thumbImage"))

        Log.d(TAG, "Current user object created -> $currentUser")

        for (groupId in currentUser.userGroups) {
            mDb.collection("Groups")
                    .document(groupId)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (task.result == null) {
                                Log.d(TAG, "FIRESTORE -> CANNOT FIND THIS GROUP DOCUMENT -> $groupId")
                            }
                            else {
                                Log.d(TAG, "FIRESTORE -> CURRENT GROUP DOCUMENT: " + task.result)
                                doneGetGroup(task.result)
                            }

                        } else {
                            Log.d(TAG, "FIRESTORE -> GET FAILED WITH ", task.exception)
                        }
                    }
        }
    }

    private fun doneGetGroup(groupDs: DocumentSnapshot?) {
        Log.d(TAG, "groupDs -> " + groupDs)

        var group = Group(groupDs!!.id,
                groupDs.getString("title"),
                groupDs.getString("description"),
                groupDs.getString("owner"),
                groupDs.getString("image"))

        Log.d(TAG, "Group object created -> " + group.title)

        groups.add(group)
        mGroupListAdapter.notifyDataSetChanged()
    }

}
