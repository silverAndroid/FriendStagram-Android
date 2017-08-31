package rbsoftware.friendstagram.model

import android.util.Patterns
import rbsoftware.friendstagram.Constants

/**
 * Created by Rushil on 8/24/2017.
 */
data class Validator(val validate: (String) -> Boolean, val errorMessage: String) {
    companion object {
        fun empty(): Validator = Validator({ it.isNotBlank() }, Constants.Error.ERROR_FIELD_REQUIRED)
        fun emailValid(): Validator = Validator({ Patterns.EMAIL_ADDRESS.matcher(it).matches() }, Constants.Error.ERROR_INVALID_EMAIL)
        fun passwordValid(): Validator = Validator({ it.length > 4 }, Constants.Error.ERROR_INVALID_PASSWORD)
    }
}