package rbsoftware.friendstagram.model;

/**
 * Created by silver_android on 1/3/2017.
 */

public class Picture {
    private final int id;
    private final String url;

    public Picture(int id, String url) {
        this.id = id;
        this.url = url;
    }

    public int getID() {
        return id;
    }

    public String getURL() {
        return url;
    }
}
