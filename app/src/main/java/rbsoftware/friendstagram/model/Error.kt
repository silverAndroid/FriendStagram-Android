package rbsoftware.friendstagram.model

/**
 * Created by Rushil on 11/3/2017.
 */
data class Error(private val title: String = "", val code: String = "", val status: String = "") {
    private val detail: String = ""

    fun getMessage(): String {
        return if (detail.isEmpty()) {
            title
        } else {
            detail
        }
    }
}