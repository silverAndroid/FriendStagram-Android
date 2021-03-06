package rbsoftware.friendstagram

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.LayoutRes
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.*
import android.widget.EditText
import io.reactivex.Observable
import rbsoftware.friendstagram.model.Validator
import rbsoftware.friendstagram.ui.fragment.SearchFragment

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

fun SearchView.listenForQueries(emitEmptyString: Boolean = true): Observable<String> {
    var currentQuery = ""
    return Observable.create {
        this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                currentQuery = newText
                it.onNext(currentQuery)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                if (currentQuery != query) {
                    currentQuery = query
                    it.onNext(query)
                }
                return true
            }
        })
    }
}

fun ViewGroup.inflate(@LayoutRes resource: Int, attachToRoot: Boolean = false, inflater: LayoutInflater = LayoutInflater.from(context)): View = inflater.inflate(resource, this, attachToRoot)

fun MenuItem.setTint(context: Context, menu: Menu, @ColorRes color: Int) {
    var drawable = menu.findItem(itemId).icon
    drawable = DrawableCompat.wrap(drawable)

    DrawableCompat.setTint(drawable, ContextCompat.getColor(context, color))
    menu.findItem(itemId).icon = drawable
}