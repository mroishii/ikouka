package jp.ac.ecc.sk3a12.ikouka;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Event implements Parcelable {

    private String eventId;
    private String title;
    private String description;
    private String start;
    private String end;
    private String owner;

    private ArrayList<String> eventDate = new ArrayList<>();
    private ArrayList<String> eventMonth = new ArrayList<>();
    private ArrayList<String> eventYear = new ArrayList<>();
    private ArrayList<String> eventHour = new ArrayList<>();
    private ArrayList<String> eventMinute = new ArrayList<>();

    public Event() {
        //Empty Construtor
    }

    public Event(String eventId, String title, String description, String start, String end, String owner) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        this.owner = owner;
        processTimeStamp();
    }

    public String getEventId() { return eventId;}

    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getEventDate() {
        return eventDate;
    }

    public ArrayList<String> getEventMonth() {
        return eventMonth;
    }

    public ArrayList<String> getEventYear() {
        return eventYear;
    }

    public ArrayList<String> getEventHour() {
        return eventHour;
    }

    public ArrayList<String> getEventMinute() {
        return eventMinute;
    }

    private void processTimeStamp() {
        Timestamp startTs = new Timestamp(Long.parseLong(start)*1000);
        Timestamp endTs = new Timestamp(Long.parseLong(end)*1000);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+9"));

        cal.setTimeInMillis(startTs.getTime());
        //Log
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.JAPAN);
        Log.d("Event.processTimeStamp", fmt.format(cal.getTime()));
        //Logend
        eventDate.add(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        eventMonth.add(Integer.toString(cal.get(Calendar.MONTH + 1)));
        eventYear.add(Integer.toString(cal.get(Calendar.YEAR)));
        eventHour.add(Integer.toString(cal.get(Calendar.HOUR)));
        eventMinute.add(Integer.toString(cal.get(Calendar.MINUTE)));

        cal.setTimeInMillis(endTs.getTime());
        //cal.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        eventDate.add(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        eventMonth.add(Integer.toString(cal.get(Calendar.MONTH + 1)));
        eventYear.add(Integer.toString(cal.get(Calendar.YEAR)));
        eventHour.add(Integer.toString(cal.get(Calendar.HOUR)));
        eventMinute.add(Integer.toString(cal.get(Calendar.MINUTE)));
    }

    protected Event(Parcel in) {
        eventId = in.readString();
        title = in.readString();
        description = in.readString();
        start = in.readString();
        end = in.readString();
        owner = in.readString();
        if (in.readByte() == 0x01) {
            eventDate = new ArrayList<String>();
            in.readList(eventDate, String.class.getClassLoader());
        } else {
            eventDate = null;
        }
        if (in.readByte() == 0x01) {
            eventMonth = new ArrayList<String>();
            in.readList(eventMonth, String.class.getClassLoader());
        } else {
            eventMonth = null;
        }
        if (in.readByte() == 0x01) {
            eventYear = new ArrayList<String>();
            in.readList(eventYear, String.class.getClassLoader());
        } else {
            eventYear = null;
        }
        if (in.readByte() == 0x01) {
            eventHour = new ArrayList<String>();
            in.readList(eventHour, String.class.getClassLoader());
        } else {
            eventHour = null;
        }
        if (in.readByte() == 0x01) {
            eventMinute = new ArrayList<String>();
            in.readList(eventMinute, String.class.getClassLoader());
        } else {
            eventMinute = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(eventId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(start);
        dest.writeString(end);
        dest.writeString(owner);
        if (eventDate == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(eventDate);
        }
        if (eventMonth == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(eventMonth);
        }
        if (eventYear == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(eventYear);
        }
        if (eventHour == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(eventHour);
        }
        if (eventMinute == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(eventMinute);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}