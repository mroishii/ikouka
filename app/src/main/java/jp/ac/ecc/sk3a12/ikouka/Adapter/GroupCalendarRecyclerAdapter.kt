package jp.ac.ecc.sk3a12.ikouka.Adapter

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils.isToday
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import jp.ac.ecc.sk3a12.ikouka.Model.DayItem
import jp.ac.ecc.sk3a12.ikouka.Model.Event
import jp.ac.ecc.sk3a12.ikouka.R
import kotlinx.android.synthetic.main.groupcalendar_item.view.*
import java.util.*

class GroupCalendarRecyclerAdapter(context: Context, year: Int, month: Int ): RecyclerView.Adapter<GroupCalendarRecyclerAdapter.DayItemViewHolder>() {
    companion object {
        val TAG = "GrpCalenAdapter"
        val HIDDEN = 0
        val NORMAL = 1
        val SATURDAY = 2
        val SUNDAY = 3
        val TODAY = 4
        val YOUBI = arrayOf("月", "火", "水", "木", "金", "土", "日")
    }

    var context: Context
    var cal: Calendar = Calendar.getInstance()
    var offset: Int = 0

    init {
        this.context = context
        this.cal.set(year, month, 1)
        this.offset = cal.get(Calendar.DAY_OF_WEEK) - 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupCalendarRecyclerAdapter.DayItemViewHolder {
        return GroupCalendarRecyclerAdapter.DayItemViewHolder(LayoutInflater.from(context).inflate(R.layout.groupcalendar_item, parent, false))
    }

    override fun onBindViewHolder(viewholder: GroupCalendarRecyclerAdapter.DayItemViewHolder, position: Int, payloads: List<Any>) {
        Log.d(TAG, "payloads => ${payloads}")
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
        var cal = this.cal.clone() as Calendar
        cal.set(Calendar.DATE, position - offset)

        when (viewholder.itemViewType) {
            HIDDEN -> {
                viewholder.day.visibility = TextView.INVISIBLE
                viewholder.event1.visibility = TextView.INVISIBLE
                viewholder.event2.visibility = TextView.INVISIBLE
                viewholder.event3.visibility = TextView.INVISIBLE
            }

            NORMAL -> {
                viewholder.day.text = "${position - offset + 1} (${YOUBI[cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
            }

            SATURDAY -> {
                viewholder.day.text = "${position - offset + 1} (${YOUBI[cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
                viewholder.day.setBackgroundColor(context.resources.getColor(R.color.md_blue_400))
            }

            SUNDAY -> {
                viewholder.day.text = "${position - offset + 1} (${YOUBI[cal.get(Calendar.DAY_OF_WEEK) - 1]} )"
                viewholder.day.setBackgroundColor(context.resources.getColor(R.color.md_red_200))
            }
        }

        if (isToday(cal)) {
            viewholder.container.background = context.resources.getDrawable(R.drawable.layout_border)
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

        init {
            view.setOnClickListener {
                Toast.makeText(view.context, "events: $events", Toast.LENGTH_LONG ).show()
                Log.d(TAG, "clicked")
            }
        }
    }
}