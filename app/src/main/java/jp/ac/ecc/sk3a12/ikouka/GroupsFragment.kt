package jp.ac.ecc.sk3a12.ikouka


import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
    //Group object array for adapter
    private var groups: ArrayList<Group> = ArrayList()

    //Firebase Auth
    private lateinit var auth: FirebaseAuth
    //Database Reference
    private lateinit var dbRoot: DatabaseReference
    private lateinit var dbUsers: DatabaseReference
    private lateinit var dbGroups: DatabaseReference
    //Database Listener
        //for user
    var dbUsersListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            dbUsersListenerCallback(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("database error", databaseError.message)
        }
    }
        //for group
    var dbGroupsListener = object: ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var title = dataSnapshot.child("title").value.toString()
            var description = dataSnapshot.child("description").value.toString()
            var group : Group = Group(title, description)
            groups.add(group)

        }
        override fun onCancelled(databaseError: DatabaseError) {
            Log.e("database error", databaseError.message)
        }

    }
    //GroupListView
    private var grouplist: ListView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Initialize variable
        auth = FirebaseAuth.getInstance()
        dbRoot = FirebaseDatabase.getInstance().getReference()
        dbUsers = dbRoot.child("Users")
        dbGroups = dbRoot.child("Groups")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var uid = auth.currentUser!!.uid

        dbUsers.child(uid).addListenerForSingleValueEvent(dbUsersListener)

    }


    private var GRABBING = true
    private fun dbUsersListenerCallback (dataSnapshot: DataSnapshot) {
        var temp = dataSnapshot.child("groups").value.toString()
        var userGroups = temp.split(",") as ArrayList<String>

        for (gid in userGroups) {
            Log.d("groupId", gid)
            dbGroups.child(gid).addListenerForSingleValueEvent(dbGroupsListener)
        }

    }

    private fun buildGroupList() {
        grouplist = view!!.findViewById(R.id.grouplist)
        var groupListAdapter : GroupListAdapter = GroupListAdapter(groups, context)
        grouplist!!.setAdapter(groupListAdapter)
    }
}
