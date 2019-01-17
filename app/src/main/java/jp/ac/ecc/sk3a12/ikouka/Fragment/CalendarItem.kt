package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.content.Context
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import jp.ac.ecc.sk3a12.ikouka.Model.Event

import jp.ac.ecc.sk3a12.ikouka.R
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class CalendarItem : DialogFragment() {
    companion object {
        fun getInstance(events: ArrayList<Event>): CalendarItem {
            var args: Bundle = Bundle()
            args.putSerializable("events", events)

            var calendarItem = CalendarItem()
            calendarItem.arguments = args

            return calendarItem
        }
    }

    var events: ArrayList<Event> = ArrayList()
    var rect = arrayOf(resources.getDrawable(R.drawable.rounded_rect1),
            resources.getDrawable(R.drawable.rounded_rect2),
            resources.getDrawable(R.drawable.rounded_rect3),
            resources.getDrawable(R.drawable.rounded_rect4),
            resources.getDrawable(R.drawable.rounded_rect5))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventList: LinearLayout = view.findViewById(R.id.eventList)

        events = arguments!!.getSerializable("events") as ArrayList<Event>

        view.findViewById<TextView>(R.id.header_title).text = Date(events.get(0).date.seconds * 1000).toLocaleString()

        var rectIndex = 0
        for (event in events) {
            val eventItem: View = layoutInflater.inflate(R.layout.calendar_item, null)
            eventItem.background = rect[rectIndex]
            eventList.addView(eventItem)
            rectIndex = (rectIndex + 1) % 5
        }
    }


}
