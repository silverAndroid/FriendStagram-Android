package rbsoftware.friendstagram.model;

/**
 * Created by silver_android on 1/6/2017.
 */

public class Error {
    private boolean error;
    private String data;

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return data;
    }
}
