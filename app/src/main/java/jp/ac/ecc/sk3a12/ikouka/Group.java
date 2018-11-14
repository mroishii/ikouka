package jp.ac.ecc.sk3a12.ikouka;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Group implements Parcelable {
    private String groupId;
    private String title;
    private String description;
    private String owner;
    private String image;
    private ArrayList<Event> events = new ArrayList<Event>();

    public Group() {
        //empty constructor for firebase
    }

    public Group(String groupId, String title, String description, String owner, String image) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.image = image;
    }

    public void setTitle(String title) {this.title = title;}

    public void setGroupId(String groupId) {this.groupId = groupId;}

    public void setDescription(String description) {this.description = description;}

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        this.events.add(event);
    }

    public String getGroupId() {return this.groupId;}

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    protected Group(Parcel in) {
        groupId = in.readString();
        title = in.readString();
        description = in.readString();
        owner = in.readString();
        image = in.readString();
        if (in.readByte() == 0x01) {
            events = new ArrayList<Event>();
            in.readList(events, Event.class.getClassLoader());
        } else {
            events = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(groupId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(owner);
        dest.writeString(image);
        if (events == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(events);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel in) {
            return new Group(in);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
}
