package jp.ac.ecc.sk3a12.ikouka


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList
import com.google.firebase.firestore.FirebaseFirestoreSettings




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RequestFragment : Fragment() {
    private val TAG = "GroupFrag"

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
        mDb.collection("User")
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
        currentUser = User(userDs!!.id,
                userDs!!["userName"] as String,
                userDs!!["email"] as String,
                userDs!!["groups"] as ArrayList<String>,
                userDs!!["image"] as String,
                userDs!!["thumgImage"] as String)

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
        val document = groupDs

//       Log.d("FIRESTORE", "title:${document.get("title")} ")
//       Log.d("FIRESTORE", "description:${document.get("description")} ")
//       Log.d("FIRESTORE", "owner:${document.get("owner")} ")
//       Log.d("FIRESTORE", "image:${document.get("image")} ")
//       Log.d("FIRESTORE", "users" + document.get("users"))
//       for (user in document.get("users") as Map<String, Object>) {
//           Log.d("FIRESTORE", "user:${user} ")
//       }
        var group: Group = Group(document!!.id,
                document!!["title"] as String,
                document!!["description"] as String,
                document!!["image"] as String)

        Log.d(TAG, "Group object created" + group.title)

        groups.add(group)
        mGroupListAdapter.notifyDataSetChanged()
    }


}
