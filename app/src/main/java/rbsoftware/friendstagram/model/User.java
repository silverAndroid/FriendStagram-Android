package rbsoftware.friendstagram.model;

/**
 * Created by silver_android on 07/10/16.
 */

public class User {

    private final String username;
    private final String profilePictureURL;
    private String name;
    private String description;

    public User(String username, String profilePictureURL) {
        this.username = username;
        this.profilePictureURL = profilePictureURL;
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
}
