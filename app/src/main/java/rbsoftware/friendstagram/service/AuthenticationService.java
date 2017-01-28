package rbsoftware.friendstagram.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by silver_android on 1/7/2017.
 */

public class AuthenticationService {

    private static final String tokenKey = "auth";
    private static final String usernameKey = "username";
    private SharedPreferences preferences;
    private static AuthenticationService instance;

    private AuthenticationService(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void newInstance(Context context) {
        instance = new AuthenticationService(context);
    }

    public static AuthenticationService getInstance() {
        return instance;
    }

    public void saveToken(String token) {
        preferences.edit().putString(tokenKey, token).apply();
    }

    public String getToken() {
        return preferences.getString(tokenKey, null);
    }

    public boolean hasToken() {
        return preferences.contains(tokenKey);
    }

    public void deleteToken() {
        preferences.edit().remove(tokenKey).apply();
    }

    public void saveUsername(String username) {
        preferences.edit().putString(usernameKey, username).apply();
    }

    public String getUsername() {
        return preferences.getString(usernameKey, null);
    }

    public boolean hasSavedUsername() {
        return preferences.contains(usernameKey);
    }

    public void deleteSavedUsername() {
        preferences.edit().remove(usernameKey).apply();
    }

    public boolean isLoggedIn() {
        return hasToken();
    }
}
