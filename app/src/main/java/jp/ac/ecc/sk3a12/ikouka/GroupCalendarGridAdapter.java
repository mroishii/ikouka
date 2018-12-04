package jp.ac.ecc.sk3a12.ikouka;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.storage.internal.Util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GroupCalendarGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Event> events;
    private final Calendar cal;
    private int selectedMonth;

    private ArrayList<String> calendarGrid = new ArrayList<>();
    private HashMap<String, String> eventMap = new HashMap<>();

    public GroupCalendarGridAdapter(Context context, ArrayList<Event> events) {
        this.mContext = context;
        this.events = events;
        this.cal = Calendar.getInstance();
        this.prepareCalendar();
    }

    public GroupCalendarGridAdapter(Context context, ArrayList<Event> events,int selectedYear, int selectedMonth) {
        this.mContext = context;
        this.events = events;
        this.selectedMonth = selectedMonth;
        this.cal = Calendar.getInstance();
        cal.set(Calendar.YEAR , selectedYear);
        cal.set(Calendar.MONTH, selectedMonth - 1);
        this.prepareCalendar();
    }

    @Override
    public int getCount() {
        return calendarGrid.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.groupcalendar_item, null);
        }

        final TextView day = convertView.findViewById(R.id.day);
        final TextView event1 = convertView.findViewById(R.id.event1);
        final TextView event2 = convertView.findViewById(R.id.event2);
        final TextView event3 = convertView.findViewById(R.id.event3);
        final TextView[] eventTexts = {event1, event2, event3};

        if (TextUtils.isEmpty(calendarGrid.get(position))) {
            day.setVisibility(View.INVISIBLE);
        } else {
            day.setText(calendarGrid.get(position));
        }

        if (!TextUtils.isEmpty(day.getText()) && eventMap.containsKey(day.getText())) {
            String eventsString = eventMap.get(day.getText());
            String splitedEvents[] = eventsString.split("//");
            for (int i = 0; i < splitedEvents.length; i++) {
                if (i > 2) {
                    break;
                }
                eventTexts[i].setText(splitedEvents[i]);
                eventTexts[i].setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    private void prepareCalendar() {
//        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//        fmt.setTimeZone(cal.getTimeZone());
//        Log.d("calendar", fmt.format(cal.getTime()));
        cal.set(Calendar.DATE, 1);
        int daysCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstRowOffset = cal.get(Calendar.DAY_OF_WEEK);
        //cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        //int prevMonthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH) - firstRowOffset + 2;
//        Log.d("calendar grid", "currentMonth: " + cal.get(Calendar.MONTH) + ", daysCount: " + daysCount + ", offset:"+ firstRowOffset);

        //first round
//        String days[] = {"日", "月", "火", "水", "木", "金", "土"};
//        for (int i = 0; i < days.length; i++) {
//            calendarGrid.add(days[i]);
//        }

        for (Event event : events) {
            String eventMonth = event.getEventMonth().get(0);
            String eventYear = event.getEventYear().get(0);
            if (cal.get(Calendar.YEAR) != Integer.parseInt(eventYear) || Integer.parseInt(eventMonth) != selectedMonth) {
                Log.d("eventMap", "continued, event detail: " + eventYear + "," +  eventMonth + ", current cal: " + cal.get(Calendar.YEAR) + "," + selectedMonth);
                continue;
            } else {
                String eventDate = event.getEventDate().get(0);
                String eventTitle = event.getTitle();
                if (!eventMap.containsKey(eventDate)) {
                    eventMap.put(eventDate, eventTitle);
                    Log.d("eventMap", "newly created");
                } else {
                    String existedDateEvent = eventMap.get(eventDate);
                    existedDateEvent += "//" + eventTitle;
                    eventMap.remove(eventDate);
                    eventMap.put(eventDate, existedDateEvent);
                    Log.d("eventMap", "merged");
                }
                Log.d("eventMap", eventDate + ":" + eventMap.get(eventDate));
            }
        }

        //offset row
        for (int i = 0; i < firstRowOffset - 1; i++) {
            calendarGrid.add("");
        }
        //onward
        for (int i = 1; i <= daysCount; i++) {
            calendarGrid.add(Integer.toString(i));

        }

        //log
        String log = "";
        for (int i = 0; i < calendarGrid.size(); i++) {
            log += calendarGrid.get(i) + ",";
        }
        Log.d("CalendarGrid", log);


    }


}
