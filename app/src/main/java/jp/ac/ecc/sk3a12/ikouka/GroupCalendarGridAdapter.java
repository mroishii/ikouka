package jp.ac.ecc.sk3a12.ikouka;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class GroupCalendarGridAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Event> events;
    private final int selectedMonth;

    private ArrayList<String> calendarGrid = new ArrayList<String>();

    public GroupCalendarGridAdapter(Context context, ArrayList<Event> events) {
        this.mContext = context;
        this.events = events;
        this.selectedMonth = Calendar.getInstance().MONTH;
        this.prepareCalendar();
    }

    public GroupCalendarGridAdapter(Context context, ArrayList<Event> events, int selectedMonth) {
        this.mContext = context;
        this.events = events;
        this.selectedMonth = selectedMonth - 1;
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
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(calendarGrid.get(position));
        return dummyTextView;
    }

    private void prepareCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(2018, 10, 1, 0, 0, 0);
        int daysCount = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int firstRowOffset = cal.MONTH;
        Log.d("calendar grid", selectedMonth + ", " + daysCount + ", "+ firstRowOffset);
        //first round
        for (int i = 0; i < firstRowOffset; i++) {
            calendarGrid.add("");
        }
        //onward
        for (int i = 1; i <= daysCount; i++) {
            calendarGrid.add(Integer.toString(i));
        }

        String log = "";
        for (int i = 0; i < calendarGrid.size(); i++) {
            log += calendarGrid.get(i) + ", ";
        }
        Log.d("calendar grid", log);
    }
}
