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
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import jp.ac.ecc.sk3a12.ikouka.Magic
import jp.ac.ecc.sk3a12.ikouka.Model.Activity
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

    val mAuth = FirebaseAuth.getInstance()
    val mDb = Magic.getDbInstance()

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

        val emptyListText: TextView = TextView(view.context)
        emptyListText.id = R.id.notFound
        emptyListText.text = "イベントがありません。"

        events = arguments!!.getSerializable("events") as ArrayList<Event>
        rect = arguments!!.getSerializable("rect") as Array<Drawable>
        time = Date(arguments!!.getLong("time"))
        groupId = arguments!!.getString("groupId")

        val fmt = SimpleDateFormat("yyyy年MM月dd日")
        var titleStr =  fmt.format(time) + "のイベント"
        view.findViewById<TextView>(R.id.header_title).text = titleStr

        var rectIndex = 0
        if (events.size == 0) {
            eventList.addView(emptyListText)
        } else {
            for (event in events) {
                val eventItem: View = layoutInflater.inflate(R.layout.calendar_item, null)
                eventItem.background = rect[rectIndex]
                eventItem.findViewById<TextView>(R.id.eventTitle).text = event.title
                eventItem.findViewById<TextView>(R.id.eventDescription).text = event.description

                mDb.collection("Groups")
                        .document(groupId)
                        .get()
                        .addOnSuccessListener {group ->
                            if (mAuth.currentUser!!.uid == event.owner || mAuth.currentUser!!.uid == group.getString("owner")) {
                                eventItem.setOnClickListener {
                                    AlertDialog.Builder(context!!).apply {
                                        setTitle("イベント編集")
                                        var inputForm = layoutInflater.inflate(R.layout.event_input_form, null)
                                        inputForm.findViewById<EditText>(R.id.createEventTitle).text.insert(0, event.title)
                                        inputForm.findViewById<EditText>(R.id.createEventDescription).text.insert(0, event.description)
                                        setView(inputForm)
                                        setNeutralButton("キャンセル") { dialog, which ->
                                            dialog.dismiss()
                                        }
                                        setPositiveButton("変更") { dialog, which ->
                                            var eventTitle = inputForm.findViewById<EditText>(R.id.createEventTitle).text.toString()
                                            var eventDescription = inputForm.findViewById<EditText>(R.id.createEventDescription).text.toString()
                                            mDb.document("Groups/$groupId/Events/${event.eventId}")
                                                    .update("title",  eventTitle,
                                                            "description", eventDescription)
                                                    .addOnSuccessListener {
                                                        event.apply {
                                                            title = eventTitle
                                                            description = eventDescription
                                                        }
                                                        eventItem.apply {
                                                            findViewById<TextView>(R.id.eventTitle).text = eventTitle
                                                            findViewById<TextView>(R.id.eventDescription).text = eventDescription
                                                        }
                                                        Toast.makeText(context!!, "イベントが更新されました。", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context!!, "イベントの更新に失敗しました。", Toast.LENGTH_SHORT).show()
                                                    }
                                        }
                                        create().show()
                                    }
                                }
                            }
                        }



                //Add eventItem to view
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
                            if (eventList.findViewById<TextView>(R.id.notFound) != null) {
                                eventList.removeView(eventList.findViewById<TextView>(R.id.notFound))
                            }

                            val eventItem: View = layoutInflater.inflate(R.layout.calendar_item, null)
                            eventItem.background = rect[rectIndex]
                            eventItem.findViewById<TextView>(R.id.eventTitle).text = eventMap.get("title") as String
                            eventItem.findViewById<TextView>(R.id.eventDescription).text = eventMap.get("description") as String
                            eventList.addView(eventItem)
                            rectIndex = (rectIndex + 1) % 5

                            //write group activity
                            var activityMap = HashMap<String, Any>().apply {
                                put("userId", FirebaseAuth.getInstance().currentUser!!.uid)
                                put("action", Activity.CREATED_EVENT)
                                put("timestamp", Timestamp.now())
                                put("reference", it.id)
                            }

                            FirebaseFirestore.getInstance()
                                    .collection("Groups/$groupId/Activities")
                                    .add(activityMap)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "イベント追加に成功しました。", Toast.LENGTH_SHORT)
                                        dialog.dismiss()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "FAILED AT => ${it.message}", Toast.LENGTH_LONG)
                                    }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this.context!!, "イベントの追加に失敗しました", Toast.LENGTH_SHORT).show()
                            Log.d("AddEvent", "FAILED WITH => ${it.message}")
                        }

            }

            alertDialogBuilder.setNeutralButton("キャンセル") { dialog, which ->
                dialog.dismiss()
            }

            alertDialogBuilder.setCancelable(true)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.setCanceledOnTouchOutside(false)
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
