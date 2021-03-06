package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_register.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.addValidation
import rbsoftware.friendstagram.model.Validator
import rbsoftware.friendstagram.validate

/**
 * Created by Rushil on 8/21/2017.
 */
class RegisterFragment : Fragment(), ErrorDisplay {
    private var nameInputLayout: TextInputLayout? = null
    private var emailInputLayout: TextInputLayout? = null
    private var usernameInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private lateinit var inputValidators: Map<TextInputLayout?, List<Validator>>
    private lateinit var loadLoginPage: () -> Unit
    private lateinit var register: (String, String, String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton: Button? = login_button
        val registerButton: Button? = register_button
        nameInputLayout = name_layout
        emailInputLayout = email_layout
        usernameInputLayout = username_layout
        passwordInputLayout = password_layout
        inputValidators = mapOf(
                Pair(nameInputLayout,
                        listOf(
                                Validator.empty()
                        )
                ),
                Pair(emailInputLayout,
                        listOf(
                                Validator.empty(),
                                Validator.emailValid()
                        )
                ),
                Pair(usernameInputLayout,
                        listOf(
                                Validator.empty()
                        )
                ),
                Pair(passwordInputLayout,
                        listOf(
                                Validator.empty(),
                                Validator.passwordValid()
                        )
                )
        )

        passwordInputLayout?.editText?.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_register_id || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            } else {
                false
            }
        })
        loginButton?.setOnClickListener { loadLoginPage() }
        registerButton?.setOnClickListener { attemptRegister() }
        inputValidators.forEach {
            val inputLayout = it.key
            val validators = it.value

            validators.forEach {
                inputLayout?.addValidation(it.validate, it.errorMessage)
            }
        }
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
        inputValidators.keys.forEach { textInputLayout ->
            focusView = if (textInputLayout?.validate() == false) {
                focusView ?: textInputLayout
            } else {
                focusView
            }
        }
        return focusView
    }

    companion object {
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}