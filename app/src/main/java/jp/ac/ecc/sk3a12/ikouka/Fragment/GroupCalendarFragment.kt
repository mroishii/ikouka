package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupCalendarGridAdapter
import jp.ac.ecc.sk3a12.ikouka.Adapter.GroupCalendarRecyclerAdapter
import jp.ac.ecc.sk3a12.ikouka.Model.Event
import jp.ac.ecc.sk3a12.ikouka.Model.Group
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.fragment_group_calendar.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GroupCalendarFragment : Fragment() {
    private val TAG = "GrpCalFrag"

    lateinit var groupId: String

    //Firebase
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var mDb: FirebaseFirestore

    private lateinit var calendarGrid: RecyclerView

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

        val groupId = arguments!!.getString("groupId")

        calendarGrid = view!!.findViewById(R.id.calendarGrid)
        calendarGrid.setHasFixedSize(true)
        val layoutManager: GridLayoutManager = GridLayoutManager(this.context!!, 7)
        calendarGrid.layoutManager = layoutManager
        val calendarGridAdapter = GroupCalendarRecyclerAdapter(this.context!!, 2019, 1)
        calendarGrid.adapter = calendarGridAdapter


    }
}
