package rbsoftware.friendstagram.model;

/**
 * Created by silver_android on 1/7/2017.
 */

public class Response<T> {
    private boolean error;
    private T data;

    public boolean isError() {
        return error;
    }

    public T getData() {
        return data;
    }
}
