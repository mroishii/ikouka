package jp.ac.ecc.sk3a12.ikouka;

import android.os.Parcel;
import android.os.Parcelable;

public class Group implements Parcelable {
    private String groupId;
    private String title;
    private String description;

    public Group() {
        //empty constructor for firebase
    }

    public Group(String groupId, String title, String description) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
    }

    public void setTitle(String title) {this.title = title;}

    public void setGroupId(String groupId) {this.groupId = groupId;}

    public void setDescription(String description) {this.description = description;}

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
