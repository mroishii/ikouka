package jp.ac.ecc.sk3a12.ikouka;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Group implements Parcelable {
    private String groupId;
    private String title;
    private String description;
    private String owner;
    private String image;
    private ArrayList<Event> events = new ArrayList<Event>();
    private HashMap<String, HashMap<String, String>> users = new HashMap();

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
                ", users=" + users +
                '}';
    }

    public Group(String groupId, String title, String description, String image) {
        //Constructor for grouplist adapter
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.image = image;
    }

    public Group(String groupId, String title, String description, String owner, String image) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.image = image;
    }

    public Group(String groupId, String title, String description, String owner, String image, DataSnapshot usersDs) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.image = image;
        buildUsersMap(usersDs);
    }

    public void setOwner(String owner) { this.owner = owner; }

    public void setImage(String image) { this.image = image; }

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

    public String getOwner() {return this.owner;}

    public String getImage() {return this.image;}

    public HashMap<String, HashMap<String, String>> getUsers() {
        return this.users;
    }

    public void buildUsersMap(DataSnapshot usersDs) {
        for (DataSnapshot user : usersDs.getChildren()) {
            Log.d("GroupClass", "buildUsersMap/" + user.toString());
            HashMap<String, String> userMap = new HashMap();
            userMap.put("roles", user.child("roles").getValue().toString());
            userMap.put("displayName", user.child("displayName").getValue().toString());
            users.put(user.getKey(), userMap);
        }
    }

    public void buildUserMap(Map<String, Object> usersMap) {
        for (String userKey : usersMap.keySet()) {
            Map<String, Object> user = (Map<String, Object>) usersMap.get(userKey);
            HashMap<String, String> userMap = new HashMap<>();
            userMap.put("displayName", (String) user.get("displayName"));
            userMap.put("image", (String) user.get("image"));
            userMap.put("roles", ((ArrayList<String>) user.get("roles")).toString());
            users.put(userKey, userMap);
        }
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
        Bundle bundle = in.readBundle();
        users = (HashMap <String, HashMap<String, String>>) bundle.getSerializable("users");
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
