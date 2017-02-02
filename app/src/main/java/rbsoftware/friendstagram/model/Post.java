package rbsoftware.friendstagram.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by silver_android on 06/10/16.
 */

public class Post {
    private int id;
    @SerializedName("url")
    private String imageID;
    @SerializedName("description")
    private String description;
    @SerializedName("tags")
    private ArrayList<String> hashTags;
    private User user;
    private String imageURL; // Temporary until server is up

    public Post(String imageID, String description, ArrayList<String> tags) {
        this.imageID = imageID;
        this.description = description;
        hashTags = new ArrayList<>(tags);
    }

    /**
     * Temporary constructor until server is up
     * @param imageURL
     * @param description
     */
    public Post(String imageURL, String description, User user) {
        this.imageURL = imageURL;
        this.description = description;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return user;
    }

    /**
     * Temporary until server is up
     * @return
     */
    public String getImageURL() {
        return imageURL;
    }

    /**
     * Temporary until server is up
     * @param imageURL
     */
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
