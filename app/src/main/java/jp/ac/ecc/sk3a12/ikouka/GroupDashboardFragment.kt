package jp.ac.ecc.sk3a12.ikouka


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class GroupDashboardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentGroup: Group = activity!!.intent.getParcelableExtra("group")

        var groupString = "Group id: " + currentGroup.groupId
        groupString += System.lineSeparator() + "Group title: " + currentGroup.title
        groupString += System.lineSeparator() + "Group description: " +currentGroup.description
        groupString += System.lineSeparator() + "Group owner: " +currentGroup.owner
        groupString += System.lineSeparator() + "Group image: " +currentGroup.image
        groupString += System.lineSeparator() + "Events: "
        for (event in currentGroup.events) {
            groupString += System.lineSeparator() + " Event id:" + event.eventId
            groupString += System.lineSeparator() + "    Event title:" + event.title
            groupString += System.lineSeparator() + "    Event owner:" + event.owner
            groupString += System.lineSeparator() + "    Event description:" + event.description
            groupString += System.lineSeparator() + "    Event start and end:" + event.start + "," + event.end
            groupString += System.lineSeparator() + ""
        }
        Log.d("group info", groupString)

        var calendarGrid: GridView = view!!.findViewById(R.id.calendarGrid)
        var calendarGridAdapter: GroupCalendarGridAdapter = GroupCalendarGridAdapter(context, currentGroup.events, 12)
        calendarGrid.adapter = calendarGridAdapter;
    }


}
