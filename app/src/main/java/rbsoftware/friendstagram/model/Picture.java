package rbsoftware.friendstagram.model;

import android.net.Uri;

/**
 * Created by silver_android on 1/3/2017.
 */

public class Picture {
    private final int id;
    private final Uri uri;

    public Picture(int id, Uri uri) {
        this.id = id;
        this.uri = uri;
    }

    public int getID() {
        return id;
    }

    public Uri getURI() {
        return uri;
    }
}
