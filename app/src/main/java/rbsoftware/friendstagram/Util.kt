package rbsoftware.friendstagram

/**
 * Created by silver_android on 06/10/16.
 */

object Util {

    fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }

    fun isUsernameValid(username: String): Boolean {
        return username.length <= 25
    }
}
