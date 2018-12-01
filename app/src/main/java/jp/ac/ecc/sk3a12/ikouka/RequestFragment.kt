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
    //Group object array for adapter
    private var groups: ArrayList<Group> = ArrayList()

    private lateinit var mGroupListAdapter: GroupListAdapter
    //Firebase auth
    private lateinit var mAuth: FirebaseAuth

    //Firestore
    private lateinit var mDb: FirebaseFirestore
    lateinit var userDocument: DocumentSnapshot

    //Groups list
    private var groupList: ListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

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

        var uid = mAuth.currentUser!!.uid




//        groupList = view!!.findViewById(R.id.grouplist2)
//        mGroupListAdapter = GroupListAdapter(groups, context)
//        groupList!!.adapter = mGroupListAdapter
    }

    private fun doneGetUser(userDs: DocumentSnapshot) {
        mDb.collection("Groups")
                .document("dummy_group_id")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {

                        } else {
                            Log.d("Firestore", "No such document")
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.exception)
                    }
                }
    }

    private fun doneGetGroup(groupDs: DocumentSnapshot) {
        val document = groupDs

//       Log.d("FIRESTORE", "title:${document.get("title")} ")
//       Log.d("FIRESTORE", "description:${document.get("description")} ")
//       Log.d("FIRESTORE", "owner:${document.get("owner")} ")
//       Log.d("FIRESTORE", "image:${document.get("image")} ")
//       Log.d("FIRESTORE", "users" + document.get("users"))
//       for (user in document.get("users") as Map<String, Object>) {
//           Log.d("FIRESTORE", "user:${user} ")
//       }
        var group: Group = Group(document.id,
                document.getString("title"),
                document.getString("description"),
                document.get("owner").toString(),
                document.getString("image"))
        group.buildUserMap(document.get("users") as Map<String, Object>)

        Log.d("FIRESTORE", "group object created" + group)
    }


}
