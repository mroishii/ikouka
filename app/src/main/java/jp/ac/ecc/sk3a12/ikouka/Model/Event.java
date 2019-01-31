package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Event {
    private String eventId;
    private String title;
    private String description;
    private Timestamp date;
    private String owner;

    public Event() {
        //Empty Construtor
    }

    public Event(String eventId, String title, String description, Timestamp date, String owner) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.owner = owner;
    }

    public String getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Timestamp getDate() {
        return date;
    }

    public String getOwner() {
        return owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", owner='" + owner + '\'' +
                '}';
    }
}