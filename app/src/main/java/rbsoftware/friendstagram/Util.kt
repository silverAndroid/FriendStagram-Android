package rbsoftware.friendstagram

import android.content.Context
import android.util.Log
import android.widget.Toast

/**
 * Created by silve on 1/18/2018.
 */
fun <T> onError(clazz: Class<T>, context: Context, error: Throwable, message: String? = null) = onError(clazz::class.java.simpleName, context, error, message)

fun onError(tag: String, context: Context, error: Throwable, message: String? = null) {
    Log.e(tag, message ?: "ErrorResponse", error)
    Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
}