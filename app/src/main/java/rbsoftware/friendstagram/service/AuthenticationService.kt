package rbsoftware.friendstagram.service

import android.content.Context
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by silver_android on 1/7/2017.
 */
@Singleton
class AuthenticationService @Inject constructor(context: Context) {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    var token: String?
        get() = preferences.getString(tokenKey, null)
        set(value) = saveToken(value)
    var username: String
        get() = preferences.getString(usernameKey, null)
        set(value) = saveUsername(value)
    var isSetup: Boolean
        get() = preferences.contains(setupKey) && preferences.getBoolean(setupKey, false)
        set(value) = setSetupBoolean(value)
    val isLoggedIn: Boolean
        get() = hasToken() && isSetup

    fun logout() {
        deleteToken()
        deleteSavedUsername()
    }

    fun hasToken(): Boolean = preferences.contains(tokenKey)

    fun hasSavedUsername(): Boolean = preferences.contains(usernameKey)

    private fun saveToken(token: String?) {
        token?.let { preferences.edit().putString(tokenKey, it).apply() }
    }

    private fun saveUsername(username: String?) {
        username?.let { preferences.edit().putString(usernameKey, it).apply() }
    }

    private fun setSetupBoolean(hasSetup: Boolean) {
        preferences.edit().putBoolean(setupKey, hasSetup).apply()
    }

    private fun deleteToken() {
        preferences.edit().remove(tokenKey).apply()
    }

    private fun deleteSavedUsername() {
        preferences.edit().remove(usernameKey).apply()
    }

    companion object {
        private val tokenKey = "auth"
        private val usernameKey = "username"
        private val setupKey = "setup"
    }
}
