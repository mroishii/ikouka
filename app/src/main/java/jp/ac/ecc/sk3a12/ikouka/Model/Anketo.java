package jp.ac.ecc.sk3a12.ikouka.Model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

public class Anketo {
    private String id;
    private Date created;
    private String type;
    private String title;
    private String description;
    private String owner;
    private Date due;

    public Anketo() {
        //Empty constructor for firebase
    }

    public Anketo(String id, Date created, String type, String title, String description, String owner, Date due) {
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getDue() {
        return due;
    }

    public void setDue(Date due) {
        this.due = due;
    }

    @Override
    public String toString() {
        return "Anketo{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", owner='" + owner + '\'' +
                ", due=" + due +
                '}';
    }
}
