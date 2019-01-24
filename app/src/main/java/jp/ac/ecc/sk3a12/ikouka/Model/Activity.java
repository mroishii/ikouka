package jp.ac.ecc.sk3a12.ikouka.Model;

import com.google.firebase.Timestamp;

public class Activity {
    public final static String JOINED_GROUP = "joinedGroup";
    public final static String LEFT_GROUP = "leftGroup";
    public final static String CREATED_ANKETO = "createdAnketo";
    public final static String CREATED_EVENT = "createdEvent";
    public final static String CREATED_TODO = "createdTodo";
    public final static String NOREF = "none";

    private String id;
    private String userId;
    private String action;
    private String reference;
    private Timestamp timestamp;

    public Activity() {
    }

    public Activity(String id, String userId, String action, String reference, Timestamp timestamp) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.reference = reference;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getAction() {
        return action;
    }

    public String getReference() {
        return reference;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }


}
