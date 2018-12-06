package jp.ac.ecc.sk3a12.ikouka.Model;

import java.util.HashMap;

public class Anketo {
    private String title;
    private String description;
    private String owner;
    private HashMap<String, AnketoAnswer> answers = new HashMap();

    public Anketo(String title, String description, String owner) {
        this.title = title;
        this.description = description;
        this.owner = owner;
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
}
