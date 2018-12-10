package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class AnketoAnswer implements Parcelable {
    private String id;
    private String description;
    private HashMap<String, Boolean> answered;

    public AnketoAnswer(String id, String description, HashMap<String, Boolean> answered) {
        this.id = id;
        this.description = description;
        this.answered = answered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Boolean> getAnswered() {
        return answered;
    }

    public void setAnswered(HashMap<String, Boolean> answered) {
        this.answered = answered;
    }

    public boolean isAnswered (String uid) {
        if (answered.get(uid)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "AnketoAnswer{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", answered=" + answered +
                '}';
    }

    protected AnketoAnswer(Parcel in) {
        id = in.readString();
        description = in.readString();

        Bundle bundle = in.readBundle();
        answered = (HashMap<String, Boolean>) bundle.getSerializable("answered");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);

        Bundle bundle = new Bundle();
        bundle.putSerializable("answered", answered);
        dest.writeBundle(bundle);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AnketoAnswer> CREATOR = new Parcelable.Creator<AnketoAnswer>() {
        @Override
        public AnketoAnswer createFromParcel(Parcel in) {
            return new AnketoAnswer(in);
        }

        @Override
        public AnketoAnswer[] newArray(int size) {
            return new AnketoAnswer[size];
        }
    };
}