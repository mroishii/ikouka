package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class Anketo implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String owner;
    private HashMap<String, AnketoAnswer> answers = new HashMap();

    public Anketo(String id, String title, String description, String owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public HashMap<String, AnketoAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<String, AnketoAnswer> answers) {
        this.answers = answers;
    }

    public void putAnswer(AnketoAnswer answer) {
        String key = Integer.toString(answers.size() + 1);
        answers.put(key, answer);
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
        title = in.readString();
        description = in.readString();
        owner = in.readString();
        answers = (HashMap) in.readValue(HashMap.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(owner);
        dest.writeValue(answers);
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
