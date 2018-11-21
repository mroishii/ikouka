package jp.ac.ecc.sk3a12.ikouka;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String userId;
    private String userName;
    private String email;
    private String userGroups;
    private String image;
    private String thumbImage;

    public User() {
        //empty constructor for firebase
    }

    public User(String userId, String userName, String email, String userGroups, String image, String thumbImage) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.userGroups = userGroups;
        this.image = image;
        this.thumbImage = thumbImage;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(String userGroups) {
        this.userGroups = userGroups;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(String thumbImage) {
        this.thumbImage = thumbImage;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", userGroups='" + userGroups + '\'' +
                ", image='" + image + '\'' +
                ", thumbImage='" + thumbImage + '\'' +
                '}';
    }

    protected User(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        email = in.readString();
        userGroups = in.readString();
        image = in.readString();
        thumbImage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(email);
        dest.writeString(userGroups);
        dest.writeString(image);
        dest.writeString(thumbImage);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
