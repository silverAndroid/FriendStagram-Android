package rbsoftware.friendstagram.model

/**
 * Created by silver_android on 1/7/2017.
 */

class NetResponse<out T> {
    val isError: Boolean = false
    val data: T? = null
}
