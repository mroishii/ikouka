package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.HashMap;

public class Anketo implements Parcelable {
    private String id;
    private Long created;
    private String type;
    private String title;
    private String description;
    private String owner;
    private Long due;
    private HashMap<String, Object> answers = new HashMap();

    public Anketo() {
        //Empty constructor for firebase
    }

    public Anketo(String id, Long created, String type, String title, String description, String owner, Long due) {
        this.id = id;
        this.created = created;
        this.type = type;
        this.title = title;
        this.description = description;
        this.owner = owner;
        this.due = due;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public String getType() {return type;}

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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Long getDue() {
        return due;
    }

    public void setDue(Long due) {
        this.due = due;
    }

    public HashMap<String, Object> getAnswerAt(String id) {
        if (!answers.containsKey(id)) {
            return null;
        }
        return (HashMap<String, Object>) answers.get(id);

    }

    public void setAnswers(HashMap<String, Object> answers ) {
        this.answers = answers;
    }

    public HashMap<String, Object> getAnswers() {
        return answers;
    }

    public void putAnswer(String key, HashMap<String, Object> answer) {
        answers.put(key, answer);
    }

    public boolean isAnswered (String uid) {
        for (String key : answers.keySet()) {
            HashMap<String, Object> answer = (HashMap<String, Object>) answers.get(key);
            HashMap<String, Boolean> answered = (HashMap<String, Boolean>) answer.get("answered");
            if (answered.get(uid)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "Anketo{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", answers=" + answers +
                '}';
    }

    protected Anketo(Parcel in) {
        id = in.readString();
        created = in.readLong();
        title = in.readString();
        description = in.readString();
        owner = in.readString();
        due =in.readLong();

        Bundle bundle = in.readBundle();
        answers = (HashMap<String, Object>) bundle.getSerializable("answers");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(created);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(owner);
        dest.writeLong(due);

        Bundle bundle = new Bundle();
        bundle.putSerializable("answers", answers);
        dest.writeBundle(bundle);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Anketo> CREATOR = new Parcelable.Creator<Anketo>() {
        @Override
        public Anketo createFromParcel(Parcel in) {
            return new Anketo(in);
        }

        @Override
        public Anketo[] newArray(int size) {
            return new Anketo[size];
        }
    };
}
