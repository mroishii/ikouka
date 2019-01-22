package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class AnketoAnswer{
    private String id;
    private String description;
    private String[] answered;

    public AnketoAnswer() {

    }

    public AnketoAnswer(String id, String description, String[] answered) {
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

    public String[] getAnswered() {
        return answered;
    }

    public void setAnswered(String[] answered) {
        this.answered = answered;
    }
}
