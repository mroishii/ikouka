package jp.ac.ecc.sk3a12.ikouka.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils.isToday
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import jp.ac.ecc.sk3a12.ikouka.Activity.GroupActivity
import jp.ac.ecc.sk3a12.ikouka.Fragment.CalendarItem
import jp.ac.ecc.sk3a12.ikouka.Model.DayItem
import jp.ac.ecc.sk3a12.ikouka.Model.Event
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.groupcalendar_item.view.*
import java.util.*

class GroupCalendarRecyclerAdapter(context: Context,parentActivity: GroupActivity, calendarItemFrame: FrameLayout, year: Int, month: Int, groupId: String ): RecyclerView.Adapter<GroupCalendarRecyclerAdapter.DayItemViewHolder>() {
    companion object {
        val TAG = "GrpCalenAdapter"
        val HIDDEN = 0
        val NORMAL = 1
        val SATURDAY = 2
        val SUNDAY = 3
        val TODAY = 4
        val YOUBI = arrayOf("日","月", "火", "水", "木", "金", "土")
    }

    var context: Context
    var parentActivity: GroupActivity
    var calendarItemFrame: FrameLayout
    var cal: Calendar = Calendar.getInstance()
    var offset: Int = 0
    var rect: Array<Drawable>
    var lastClicked: DayItemViewHolder?
    var groupId = ""


    init {
        this.context = context
        this.parentActivity = parentActivity
        this.calendarItemFrame = calendarItemFrame
        this.cal.set(year, month, 1)
        this.offset = cal.get(Calendar.DAY_OF_WEEK) - 1

        rect = arrayOf(parentActivity.applicationContext.resources.getDrawable(R.drawable.rounded_rect1),
                parentActivity.applicationContext.resources.getDrawable(R.drawable.rounded_rect2),
                parentActivity.applicationContext.resources.getDrawable(R.drawable.rounded_rect3),
                parentActivity.applicationContext.resources.getDrawable(R.drawable.rounded_rect4),
                parentActivity.applicationContext.resources.getDrawable(R.drawable.rounded_rect5))

        lastClicked = null
        this.groupId = groupId
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupCalendarRecyclerAdapter.DayItemViewHolder {
        return GroupCalendarRecyclerAdapter.DayItemViewHolder(LayoutInflater.from(context).inflate(R.layout.groupcalendar_item, parent, false))
    }

    override fun onBindViewHolder(viewholder: GroupCalendarRecyclerAdapter.DayItemViewHolder, position: Int, payloads: List<Any>) {
        if (!payloads.isEmpty()) {
            viewholder.events = payloads.get(0) as ArrayList<Event>
            Log.d(TAG, "eventsMap => ${viewholder.events}")

            when (viewholder.events.size) {
                0 -> {}
                1 -> {
                    viewholder.event1.text = viewholder.events.get(0).title
                    viewholder.event1.visibility = TextView.VISIBLE
                }
                2 -> {
                    viewholder.event1.text = viewholder.events.get(0).title
                    viewholder.event1.visibility = TextView.VISIBLE
                    viewholder.event2.text = viewholder.events.get(1).title
                    viewholder.event2.visibility = TextView.VISIBLE
                }
                3 -> {
                    viewholder.event1.text = viewholder.events.get(0).title
                    viewholder.event1.visibility = TextView.VISIBLE
                    viewholder.event2.text = viewholder.events.get(1).title
                    viewholder.event2.visibility = TextView.VISIBLE
                    viewholder.event3.text = viewholder.events.get(2).title
                    viewholder.event3.visibility = TextView.VISIBLE
                }
                else -> {
                    viewholder.event1.text = viewholder.events.get(0).title
                    viewholder.event1.visibility = TextView.VISIBLE
                    viewholder.event2.text = viewholder.events.get(1).title
                    viewholder.event2.visibility = TextView.VISIBLE
                    viewholder.event3.text = "後${viewholder.events.size - 2}個"
                    viewholder.event3.visibility = TextView.VISIBLE
                    viewholder.event3.setBackgroundColor(context.resources.getColor(R.color.md_white_1000))
                }
            }

        } else {
            super.onBindViewHolder(viewholder, position, payloads)
        }
    }

    override fun onBindViewHolder(viewholder: GroupCalendarRecyclerAdapter.DayItemViewHolder, position: Int) {
        viewholder.cal = this.cal.clone() as Calendar
        viewholder.cal.set(Calendar.DATE, position - offset + 1)

        when (viewholder.itemViewType) {
            HIDDEN -> {
                viewholder.day.visibility = TextView.INVISIBLE
                viewholder.event1.visibility = TextView.INVISIBLE
                viewholder.event2.visibility = TextView.INVISIBLE
                viewholder.event3.visibility = TextView.INVISIBLE
            }

            NORMAL -> {
                viewholder.day.text = "${viewholder.cal.get(Calendar.DATE)} (${YOUBI[viewholder.cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
            }

            SATURDAY -> {
                viewholder.day.text = "${viewholder.cal.get(Calendar.DATE)} (${YOUBI[viewholder.cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
                viewholder.day.setBackgroundColor(context.resources.getColor(R.color.md_blue_400))
            }

            SUNDAY -> {
                viewholder.day.text = "${viewholder.cal.get(Calendar.DATE)} (${YOUBI[viewholder.cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
                viewholder.day.setBackgroundColor(context.resources.getColor(R.color.md_red_200))
            }
        }

        if (isToday(cal)) {
            viewholder.day.background = context.resources.getDrawable(R.drawable.layout_border)
        }

        viewholder.itemView.setOnClickListener {
            if (viewholder.itemViewType != HIDDEN) {
                if (lastClicked != null) {
                    lastClicked!!.container.setBackgroundColor(Color.WHITE)
                }
                viewholder.container.setBackgroundColor(context.resources.getColor(R.color.md_yellow_300))

                val calendarItemFragment = CalendarItem.getInstance(viewholder.events, rect, viewholder.cal.timeInMillis, groupId)
//                parentActivity.supportFragmentManager.beginTransaction()
//                        .replace(calendarItemFrame.id, calendarItemFragment)
//                        .commit()
                calendarItemFragment.showNow(parentActivity.supportFragmentManager, "CALENDAR_ITEM")

                lastClicked = viewholder
            }
        }

    }

    override fun getItemCount(): Int {
        var count = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + offset
        var lastOffset = 7 - (count % 7)
        return  count + lastOffset
    }

    override fun getItemViewType(position: Int): Int {
        if (position < offset || position > cal.getActualMaximum(Calendar.DAY_OF_MONTH) + offset - 1) {
            return HIDDEN
        } else {
            var cal = this.cal.clone() as Calendar
            cal.set(Calendar.DATE, position - offset +1)

            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> {
                    return SATURDAY
                }

                Calendar.SUNDAY -> {
                    return SUNDAY
                }

                else -> {
                    return NORMAL
                }
            }

        }
    }

    private fun isToday(cal: Calendar): Boolean {
        val today = Calendar.getInstance()
        if (cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                cal.get(Calendar.DATE) == today.get(Calendar.DATE)) {
            return true
        }

        return false
    }

    class DayItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val day: TextView = view.findViewById(R.id.day)
        val event1: TextView = view.findViewById(R.id.event1)
        val event2: TextView = view.findViewById(R.id.event2)
        val event3: TextView = view.findViewById(R.id.event3)
        val container: ConstraintLayout = view.findViewById(R.id.container)
        var events = ArrayList<Event>()
        var cal = Calendar.getInstance()
    }
}