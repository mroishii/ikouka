package jp.ac.ecc.sk3a12.ikouka;

import java.util.Calendar;
import java.util.HashMap;

public class ChatMessage {
    private String sender;
    private Long timestamp;
    private String message;
    private String type;

    public ChatMessage() {
        //Empty for Firebase
    }

    public ChatMessage(String from, String message, String type) {
        this.sender = from;
        this.message = message;
        this.type = type;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public ChatMessage(String sender, String message, String type, Long timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getType() { return type; }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = Long.parseLong(timestamp);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {this.type = type; }
}
