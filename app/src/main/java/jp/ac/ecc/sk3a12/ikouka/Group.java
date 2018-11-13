package jp.ac.ecc.sk3a12.ikouka;

public class Group {
    private String groupId;
    private String title;
    private String description;

    public Group(String groupId, String title, String description) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
    }

    public String getGroupId() {return this.groupId;}

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }
}
