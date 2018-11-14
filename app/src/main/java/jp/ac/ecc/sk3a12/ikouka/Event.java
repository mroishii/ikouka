package jp.ac.ecc.sk3a12.ikouka;

import android.os.Parcel;
import android.os.Parcelable;

public class Event implements Parcelable {
    private String eventId;
    private String title;
    private String description;
    private String start;
    private String end;
    private String owner;

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

    protected Event(Parcel in) {
        eventId = in.readString();
        title = in.readString();
        description = in.readString();
        start = in.readString();
        end = in.readString();
        owner = in.readString();
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