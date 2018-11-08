package jp.ac.ecc.sk3a12.ikouka;

public class Group {
    private String title;
    private String description;

    public Group(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }
}
