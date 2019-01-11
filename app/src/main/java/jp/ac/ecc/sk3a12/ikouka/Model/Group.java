package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group implements Parcelable {
    private String groupId;
    private String title;
    private String description;
    private String owner;
    private String image;

    private ArrayList<String> usersId = new ArrayList();
    private ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<String> eventsId = new ArrayList<String>();
    private ArrayList<String> anketosId = new ArrayList<String>();
    private HashMap<String, Object> users = new HashMap();

    public Group() {
        //empty constructor for firebase
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupId='" + groupId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", image='" + image + '\'' +
                ", events=" + events +
                ", eventsId=" + eventsId +
                ", users=" + users +
                '}';
    }

    public Group(String groupId, String title, String description, String owner, String image) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.image = image;
    }

    public Group(String groupId, String title, String description, String owner, String image, HashMap<String, Object> users) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.image = image;
        this.users = users;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public String getImage() {
        return image;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public ArrayList<String> getEventsId() {
        return eventsId;
    }

    public ArrayList<String> getAnketosId() {
        return anketosId;
    }

    public void setAnketosId(ArrayList<String> anketosId) {
        this.anketosId = anketosId;
    }

    public HashMap<String, Object> getUsers() {
        return users;
    }


    public void addEventId(String eventId) {
        this.eventsId.add(eventId);
    }

    public void putEvent(Event event) {
        this.events.add(event);
    }

    public void addAnketoId(String anketoId) {
        this.anketosId.add(anketoId);
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
        eventsId = (ArrayList<String>) in.readSerializable();
        anketosId = (ArrayList<String>) in.readSerializable();
        Bundle bundle = in.readBundle();
        users = (HashMap <String, Object>) bundle.getSerializable("users");
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
        dest.writeSerializable(eventsId);
        dest.writeSerializable(anketosId);
        Bundle bundle = new Bundle();
        bundle.putSerializable("users", users);
        dest.writeBundle(bundle);
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
