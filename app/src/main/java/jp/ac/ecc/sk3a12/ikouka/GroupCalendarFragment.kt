package jp.ac.ecc.sk3a12.ikouka


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupDashboardFragment : Fragment() {
    private val TAG = "GrpCalFrag"

    lateinit var currentGroup: Group

    //Firestore
    lateinit var mDb: FirebaseFirestore
    lateinit var groupsDb: CollectionReference
    lateinit var eventsDb: CollectionReference

    var events : ArrayList<Event> = ArrayList()

    lateinit var calendarGridAdapter: GroupCalendarGridAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Initiate firestore
        mDb = FirebaseFirestore.getInstance()
        groupsDb = mDb.collection("Groups")
        eventsDb = mDb.collection("Events")

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupId = activity!!.intent.getStringExtra("groupId")
        groupsDb.document(groupId)
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

    fun doneGetGroup(groupDs: DocumentSnapshot?) {
        currentGroup = Group(groupDs!!.id,
                groupDs!!.getString("title"),
                groupDs!!.getString("description"),
                groupDs!!.getString("owner"),
                groupDs!!.getString("image"))
        var eventsMap: ArrayList<Map<String, Any>> = groupDs!!.get("events") as ArrayList<Map<String, Any>>

        for ((index, e) in eventsMap.withIndex()) {
            val start = Timestamp((e.get("start") as com.google.firebase.Timestamp).seconds *1000)
            val end = Timestamp((e.get("end") as com.google.firebase.Timestamp).seconds * 1000)
            var event = Event(index.toString(),
                    e.get("title") as String,
                    e.get("description") as String,
                    start,
                    end,
                    e.get("owner") as String)

            Log.d(TAG, "Event object created -> " + event)
            currentGroup.addEvent(event)

        }

        currentGroup.buildUserMap(groupDs.get("users") as Map<String, Any>)

        Log.d(TAG, "Current Group Object -> $currentGroup")

        var calendarGrid: GridView = view!!.findViewById(R.id.calendarGrid)
        calendarGridAdapter = GroupCalendarGridAdapter(context, currentGroup.events,2019, 1)
        calendarGrid.adapter = calendarGridAdapter;

    }
}
