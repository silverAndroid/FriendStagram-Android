package rbsoftware.friendstagram

import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import rbsoftware.friendstagram.model.Validator

/**
 * Created by Rushil on 8/23/2017.
 */
fun AppCompatActivity.showFragment(fragment: Fragment, addToStack: Boolean = false, name: String? = "", setAnimations: (FragmentTransaction) -> Unit = {}) {
    val transaction = supportFragmentManager.beginTransaction()

    setAnimations(transaction)
    transaction.replace(R.id.container, fragment)
    if (addToStack)
        transaction.addToBackStack(name)
    transaction.commit()
}

fun TextInputLayout.addValidation(validate: (String) -> Boolean, errorMessage: String) {
    Validators.addValidation(this.id, Validator(validate, errorMessage))
}

fun TextInputLayout.validate(): Boolean {
    val error = Validators.validate(this.id, this.editText?.text.toString())
    this.error = error
    return error == null
}

fun EditText.setInputView(inputType: Int) {
    if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD) {
        this.inputType = inputType
        this.transformationMethod = PasswordTransformationMethod.getInstance()
    } else {
        this.inputType = inputType
    }
}