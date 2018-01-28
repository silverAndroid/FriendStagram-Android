package rbsoftware.friendstagram.model

import android.util.Patterns
import rbsoftware.friendstagram.Errors

/**
 * Created by Rushil on 8/24/2017.
 */
data class Validator(val validate: (String) -> Boolean, val errorMessage: String) {
    companion object {
        fun empty() = Validator({ it.isNotBlank() }, Errors.ERROR_FIELD_REQUIRED)
        fun emailValid() = Validator({ Patterns.EMAIL_ADDRESS.matcher(it).matches() }, Errors.ERROR_INVALID_EMAIL)
        fun passwordValid() = Validator({ it.length > 4 }, Errors.ERROR_INVALID_PASSWORD)
        fun notRequired() = Validator({ true }, "")
    }
}