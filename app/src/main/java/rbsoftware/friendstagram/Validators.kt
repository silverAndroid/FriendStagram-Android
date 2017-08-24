package rbsoftware.friendstagram

import android.support.annotation.IdRes
import rbsoftware.friendstagram.model.Validator

/**
 * Created by Rushil on 8/24/2017.
 */
class Validators {
    companion object {
        private val validators: MutableMap<Int, MutableList<Validator>> = mutableMapOf()

        fun addValidation(@IdRes inputID: Int, validator: Validator) {
            val validatorList = validators.getOrDefault(inputID, mutableListOf())
            validatorList.add(validator)
            validators[inputID] = validatorList
        }

        fun validate(@IdRes inputID: Int, text: String): String? {
            val errors = validators
                    .getOrDefault(inputID, mutableListOf())
                    .filter { !it.validate(text) }
            return if (errors.isNotEmpty()) {
                errors[0].errorMessage
            } else {
                null
            }
        }
    }
}