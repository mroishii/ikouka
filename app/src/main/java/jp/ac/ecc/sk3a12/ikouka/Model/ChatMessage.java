package jp.ac.ecc.sk3a12.ikouka.Model;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.HashMap;

public class ChatMessage {
    private String sender;
    private Timestamp timestamp;
    private String message;
    private String type;

    public ChatMessage() {
        //Empty for Firebase
    }

    public ChatMessage(String from, String message, String type) {
        this.sender = from;
        this.message = message;
        this.type = type;
        this.timestamp = Timestamp.now();
    }

    public ChatMessage(String sender, String message, String type, Timestamp timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getType() { return type; }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {this.type = type; }
}
