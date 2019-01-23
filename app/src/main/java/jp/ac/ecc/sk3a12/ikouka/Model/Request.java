package jp.ac.ecc.sk3a12.ikouka.Model;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Request {
    public static String WAITING = "waiting";
    public static String ACCEPTED = "accepted";
    public static String DENIED = "denied";
    public static String CANCELED = "canceled";

    private String id;
    private String from;
    private String to;
    private String groupId;
    private Timestamp timestamp;
    private String status;

    public Request() {
    }

    public Request(String id, String from, String to, String groupId, Timestamp timestamp, String status) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.groupId = groupId;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getGroupId() {
        return groupId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }
}
