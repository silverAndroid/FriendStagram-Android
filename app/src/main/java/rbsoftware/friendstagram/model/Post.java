package rbsoftware.friendstagram.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by silver_android on 06/10/16.
 */

public class Post {
    private int id;
    @SerializedName("image_url")
    private String imageURL;
    @SerializedName("description")
    private String description;
    @SerializedName("tags")
    private ArrayList<String> hashTags;
    @SerializedName("user")
    private User user;

    public Post(String imageURL, String description, ArrayList<String> tags) {
        this.imageURL = imageURL;
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

    public int getID() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Post) {
            Post post = (Post) obj;
            return post.id == id && post.imageURL.equals(imageURL) && post.description.equals(description) && post.user.equals(user) && post.hashTags.equals(hashTags);
        }
        return false;
    }
}
