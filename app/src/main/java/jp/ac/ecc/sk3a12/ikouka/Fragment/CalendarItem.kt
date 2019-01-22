package jp.ac.ecc.sk3a12.ikouka.Fragment


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.Model.Event

import jp.ac.ecc.sk3a12.ikouka.R
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 *
 */
class CalendarItem : DialogFragment() {
    companion object {
        fun getInstance(events: ArrayList<Event>, rect: Array<Drawable>, time: Long, groupId: String): CalendarItem {
            var args = Bundle()
            args.putSerializable("events", events)
            args.putSerializable("rect", rect)
            args.putLong("time", time)
            args.putString("groupId", groupId)

            var calendarItem = CalendarItem()
            calendarItem.arguments = args

            return calendarItem
        }
    }

    var events: ArrayList<Event> = ArrayList()
    lateinit var rect: Array<Drawable>
    lateinit var time: Date
    var groupId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val eventList: LinearLayout = view.findViewById(R.id.eventList)

        events = arguments!!.getSerializable("events") as ArrayList<Event>
        rect = arguments!!.getSerializable("rect") as Array<Drawable>
        time = Date(arguments!!.getLong("time"))
        groupId = arguments!!.getString("groupId")

        val fmt = SimpleDateFormat("yyyy年MM月dd日")
        var titleStr =  fmt.format(time) + "のイベント"
        view.findViewById<TextView>(R.id.header_title).text = titleStr

        var rectIndex = 0
        if (events.size == 0) {
            val textView: TextView = TextView(view.context)
            textView.text = "イベントがありません。"
            eventList.addView(textView)
        } else {
            for (event in events) {
                val eventItem: View = layoutInflater.inflate(R.layout.calendar_item, null)
                eventItem.background = rect[rectIndex]
                eventItem.findViewById<TextView>(R.id.eventTitle).text = event.title
                eventItem.findViewById<TextView>(R.id.eventDescription).text = event.description
                eventList.addView(eventItem)
                rectIndex = (rectIndex + 1) % 5
            }
        }

        view.findViewById<Button>(R.id.header_button).setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this.context!!)
            alertDialogBuilder.setTitle("イベント追加")
            alertDialogBuilder.setMessage("イベント日付： ${fmt.format(time)}")
            val inputForm: View = LayoutInflater.from(this.context!!).inflate(R.layout.event_input_form, null)
            alertDialogBuilder.setView(inputForm)

            alertDialogBuilder.setPositiveButton("追加") { dialog, which ->
                var path ="/Groups/$groupId/Events"
                Log.d("AddEvent", "Path => $path")

                var eventMap = HashMap<String, Any>()
                eventMap.put("title", inputForm.findViewById<EditText>(R.id.createEventTitle).text.toString())
                eventMap.put("description", inputForm.findViewById<EditText>(R.id.createEventDescription).text.toString())
                eventMap.put("date", time)
                eventMap.put("owner", FirebaseAuth.getInstance().currentUser!!.uid)
                Log.d("AddEvent", "EventMap => $eventMap")

                FirebaseFirestore.getInstance()
                        .collection(path)
                        .add(eventMap)
                        .addOnSuccessListener {
                            Toast.makeText(this.context!!, "イベントが追加されました。", Toast.LENGTH_SHORT).show()

                            val eventItem: View = layoutInflater.inflate(R.layout.calendar_item, null)
                            eventItem.background = rect[rectIndex]
                            eventItem.findViewById<TextView>(R.id.eventTitle).text = eventMap.get("title") as String
                            eventItem.findViewById<TextView>(R.id.eventDescription).text = eventMap.get("description") as String
                            eventList.addView(eventItem)
                            rectIndex = (rectIndex + 1) % 5
                        }
                        .addOnFailureListener {
                            Toast.makeText(this.context!!, "イベントの追加が失敗しました", Toast.LENGTH_SHORT).show()
                            Log.d("AddEvent", "FAILED WITH => ${it.message}")
                        }

            }
            alertDialogBuilder.setCancelable(true)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        view.findViewById<Button>(R.id.header_exit).setOnClickListener {
            this.dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        val params = dialog.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = params as android.view.WindowManager.LayoutParams
    }


}
