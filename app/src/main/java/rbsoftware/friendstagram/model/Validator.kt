package rbsoftware.friendstagram.model

/**
 * Created by Rushil on 8/24/2017.
 */
data class Validator(val validate: (String) -> Boolean, val errorMessage: String) {
    companion object {
        fun empty(): Validator = Validator({ it.isNotBlank() }, "This field is required")
    }
}