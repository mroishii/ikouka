package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Activity.GroupActivity
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupCalendarGridAdapter
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupCalendarRecyclerAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Event
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.fragment_group_calendar.view.*
import java.text.SimpleDateFormat
import java.util.*

open class GroupCalendarFragment : Fragment() {
    private val TAG = "GrpCalFrag"

    lateinit var groupId: String

    private lateinit var calendarItemFrame: FrameLayout

    //Firebase
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var mDb: FirebaseFirestore

    //Calendar Button
    private lateinit var nextMonthBtn: ImageButton
    private lateinit var prevMonthBtn: ImageButton
    private lateinit var todayBtn: ImageButton
    private lateinit var setMonthBtn: Button

    private lateinit var calendarGrid: RecyclerView
    private lateinit var calendarGridAdapter: GroupCalendarRecyclerAdapter

    private var cal: Calendar = Calendar.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //Initiate firestore
        mDb = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        mDb.firestoreSettings = settings

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarItemFrame = view.findViewById(R.id.calendarItem)

        //calendar button
        nextMonthBtn = view.findViewById(R.id.calendarNextMonth)
        prevMonthBtn = view.findViewById(R.id.calendarPrevMonth)
        todayBtn = view.findViewById(R.id.calendarToday)
        setMonthBtn = view.findViewById(R.id.calendarSet)

        prevMonthBtn.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            calendarItemFrame.removeAllViews()
            setCalendar()
            getEvents()
        }

        nextMonthBtn.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            calendarItemFrame.removeAllViews()
            setCalendar()
            getEvents()
        }

        todayBtn.setOnClickListener {
            cal = Calendar.getInstance()
            calendarItemFrame.removeAllViews()
            setCalendar()
            getEvents()
        }

        groupId = arguments!!.getString("groupId")

        calendarGrid = view!!.findViewById(R.id.calendarGrid)
        calendarGrid.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(this.context!!, 7)
        calendarGrid.layoutManager = layoutManager
        setCalendar()
        getEvents()

    }

    private fun setCalendar() {
        calendarGridAdapter = GroupCalendarRecyclerAdapter(this.context!!,this.activity as GroupActivity , calendarItemFrame,  cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), groupId)
        calendarGrid.adapter = calendarGridAdapter
        var mDateFormat = SimpleDateFormat("yyyy年MM月")
        setMonthBtn.text = mDateFormat.format(Date(cal.timeInMillis))
    }

    private fun getEvents() {
        val cal = Calendar.getInstance()
        //Create date range to use in query
        cal.set(this.cal.get(Calendar.YEAR), this.cal.get(Calendar.MONTH), 1)
        val startDate = Date(cal.timeInMillis)
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
        val endDate = Date(cal.timeInMillis)

        mDb.collection("Groups")
                .document(groupId)
                .collection("Events")
                .whereGreaterThanOrEqualTo("date", startDate)
                .whereLessThanOrEqualTo("date", endDate)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        Log.d(TAG, "LISTENING TO EVENTS FAIL => ${exception.message}")
                        return@addSnapshotListener
                    }

                    var monthEvents: HashMap<Int, ArrayList<Event>> = HashMap()
                    //Build a hashmap of this month event
                    for (document in snapshot!!.documents) {
                        val event = Event(document.id,
                                document.getString("title"),
                                document.getString("description"),
                                document.get("date") as Timestamp,
                                document.getString("owner"))
                        cal.timeInMillis = event.date.seconds * 1000

                        //If the date already in the hashmap, just add the event to array,
                        //else, create a new key and add the event into array
                        if (monthEvents.containsKey(cal.get(Calendar.DATE))) {
                            monthEvents.get(cal.get(Calendar.DATE))!!.add(event)
                        } else {
                            var events: ArrayList<Event> = ArrayList()
                            events.add(event)
                            monthEvents.put(cal.get(Calendar.DATE), events)
                        }
                    }

                    Log.d(TAG, "eventsMap => $monthEvents")
                    //For each key(as event date) in the hashmap, notify adapter to show event title on selected date
                    for (date in monthEvents.keys) {
                        var events = monthEvents.get(date)
                        calendarGridAdapter.notifyItemChanged(date + calendarGridAdapter.offset - 1, events)
                    }
                }

    }
}
