package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.Util
import rbsoftware.friendstagram.addValidation
import rbsoftware.friendstagram.validate

/**
 * Created by Rushil on 8/21/2017.
 */
class RegisterFragment : Fragment(), ErrorDisplay {
    private var nameInputLayout: TextInputLayout? = null
    private var emailInputLayout: TextInputLayout? = null
    private var usernameInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private lateinit var loadLoginPage: () -> Unit
    private lateinit var register: (String, String, String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val nameInputLayout: TextInputLayout? = view?.findViewById(R.id.name_layout)
        val emailInputLayout: TextInputLayout? = view?.findViewById(R.id.email_layout)
        val usernameInputLayout: TextInputLayout? = view?.findViewById(R.id.username_layout)
        val passwordInputLayout: TextInputLayout? = view?.findViewById(R.id.password_layout)*/
        nameInputLayout = view?.findViewById(R.id.name_layout)
        emailInputLayout = view?.findViewById(R.id.email_layout)
        usernameInputLayout = view?.findViewById(R.id.username_layout)
        passwordInputLayout = view?.findViewById(R.id.password_layout)
        val loginButton: Button? = view?.findViewById(R.id.login_button)
        val registerButton: Button? = view?.findViewById(R.id.register_button)

        passwordInputLayout?.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_register_id || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            }
            false
        })
        loginButton?.setOnClickListener { loadLoginPage() }
        registerButton?.setOnClickListener { attemptRegister() }
        nameInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        emailInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        emailInputLayout?.addValidation(Util::isEmailValid, R.string.error_invalid_email)
        usernameInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        passwordInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        passwordInputLayout?.addValidation(Util::isPasswordValid, R.string.error_invalid_password)
    }

    override fun showError(error: String?) {
        if (error == "Username is Null") {
            usernameInputLayout?.error = getString(R.string.error_field_required)
            usernameInputLayout?.requestFocus()
        } else if (error == "Password is Null") {
            passwordInputLayout?.error = getString(R.string.error_field_required)
            passwordInputLayout?.requestFocus()
        }
    }

    fun setLoadLoginCallback(loadLogin: () -> Unit) {
        this.loadLoginPage = loadLogin
    }

    fun setRegisterCallback(register: (String, String, String, String) -> Unit) {
        this.register = register
    }

    private fun attemptRegister() {
        val focusView = validate()

        if (focusView != null) {
            focusView.requestFocus()
        } else {
            val name = nameInputLayout?.editText?.text.toString()
            val email = emailInputLayout?.editText?.text.toString()
            val username = usernameInputLayout?.editText?.text.toString()
            val password = passwordInputLayout?.editText?.text.toString()
            register(name, email, username, password)
        }
    }

    private fun validate(): TextInputLayout? {
        var focusView: TextInputLayout? = null
        if (nameInputLayout?.validate() == false) {
            focusView = nameInputLayout
        }
        if (emailInputLayout?.validate() == false) {
            focusView = focusView ?: emailInputLayout
        }
        if (usernameInputLayout?.validate() == false) {
            focusView = focusView ?: usernameInputLayout
        }
        if (passwordInputLayout?.validate() == false) {
            focusView = focusView ?: passwordInputLayout
        }
        return focusView
    }

    companion object {
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}