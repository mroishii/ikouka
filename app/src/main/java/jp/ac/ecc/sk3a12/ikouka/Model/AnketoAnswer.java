package jp.ac.ecc.sk3a12.ikouka.Model;

import java.util.HashMap;

public class AnketoAnswer {

    private String description;
    private HashMap<String, Boolean> answered = new HashMap();

    public AnketoAnswer(String description, HashMap<String, Boolean> answered) {
        this.description = description;
        this.answered = answered;
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
}
