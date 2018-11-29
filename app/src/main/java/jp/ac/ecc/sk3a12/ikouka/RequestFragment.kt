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


        mDb.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            Log.d("Firestore", "DocumentSnapshot data: " + task.result!!.data)
                            userDocument = task.result!!

                        } else {
                            Log.d("Firestore", "No such document")
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.exception)
                    }
                }



//        groupList = view!!.findViewById(R.id.grouplist2)
//        mGroupListAdapter = GroupListAdapter(groups, context)
//        groupList!!.adapter = mGroupListAdapter
    }


}
