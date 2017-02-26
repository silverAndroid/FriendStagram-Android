package rbsoftware.friendstagram.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by silver_android on 07/10/16.
 */

public class User {

    private final String username;
    private final String profilePictureURL = "http://tracara.com/wp-content/uploads/2016/04/aleksandar-radojicic-i-aja-e1461054273916.jpg?fa0c3d";
    private String name;
    private String description;
    @SerializedName("posts")
    private ArrayList<String> postIDs;
    @SerializedName("following")
    private ArrayList<String> followingUserIDs;
    @SerializedName("followers")
    private ArrayList<String> followersUserIDs;

    public User(String username, String profilePictureURL) {
        this.username = username;
//        this.profilePictureURL = profilePictureURL;
        name = "";
        description = "";
    }

    public String getUsername() {
        return username;
    }

    public String getProfilePictureURL() {
        return profilePictureURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getPostIDs() {
        return postIDs;
    }

    public ArrayList<String> getFollowingUserIDs() {
        return followingUserIDs;
    }

    public ArrayList<String> getFollowersUserIDs() {
        return followersUserIDs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (obj instanceof User) {
            User user = (User) obj;
            return user.username.equals(username) && user.profilePictureURL.equals(profilePictureURL) && user.name.equals(name) && user.description.equals(description);
        }
        return false;
    }
}
