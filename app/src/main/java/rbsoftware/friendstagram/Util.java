package rbsoftware.friendstagram;

/**
 * Created by silver_android on 06/10/16.
 */

public class Util {

    public static boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public static boolean isUsernameValid(String username) {
        return username.length() <= 25;
    }
}
