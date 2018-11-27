package jp.ac.ecc.sk3a12.ikouka;

import java.util.Calendar;
import java.util.HashMap;

public class ChatMessage {
    private String sender;
    private String timestamp;
    private String message;

    public ChatMessage() {
        //Empty for Firebase
    }

    public ChatMessage(String from, String message) {
        this.sender = from;
        this.message = message;
        this.timestamp = Long.toString(Calendar.getInstance().getTimeInMillis());
    }

    public ChatMessage(String sender, String message, String timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public Long getTimestamp() {
        return Long.parseLong(timestamp);
    }

    public String getMessage() {
        return message;
    }

    public HashMap<String, String> toMap() {
        HashMap<String, String> messageMap = new HashMap();
        messageMap.put("sender", sender);
        messageMap.put("timestamp", timestamp.toString());
        return messageMap;
    }
}
