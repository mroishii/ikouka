package jp.ac.ecc.sk3a12.ikouka.Model;

import java.util.HashMap;

public class Anketo2 {
    private String id;
    private Long created;
    private String type;
    private String title;
    private String description;
    private String owner;
    private Long due;
    private HashMap<String, Object> answers;

    public Anketo2() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
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

    public Long getDue() {
        return due;
    }

    public void setDue(Long due) {
        this.due = due;
    }

    public HashMap<String, Object> getAnswers() {
        return answers;
    }

    public void setAnswers(HashMap<String, Object> answers) {
        this.answers = answers;
    }


}
