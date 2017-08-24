package rbsoftware.friendstagram

import android.support.annotation.StringRes
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
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

fun TextInputLayout.addValidation(validate: (String) -> Boolean, @StringRes errorMessage: Int) {
    Validators.addValidation(this.id, Validator(validate, context.getString(errorMessage)))
}

fun TextInputLayout.validate(): Boolean {
    val error = Validators.validate(this.id, this.editText?.text.toString())
    this.error = error
    return error == null
}