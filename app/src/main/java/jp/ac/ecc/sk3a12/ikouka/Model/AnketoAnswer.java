package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class AnketoAnswer implements Parcelable {
    private String id;
    private String description;
    private HashMap<String, Boolean> answered = new HashMap();

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
        answered = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeValue(answered);
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
