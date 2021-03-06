package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import kotlinx.android.synthetic.main.fragment_login.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.addValidation
import rbsoftware.friendstagram.model.Validator
import rbsoftware.friendstagram.validate

/**
 * Created by Rushil on 8/21/2017.
 */
class LoginFragment : Fragment(), ErrorDisplay {
    private var usernameInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private lateinit var inputValidators: Map<TextInputLayout?, List<Validator>>
    private lateinit var loadRegisterPage: () -> Unit
    private lateinit var login: (String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton = login_button
        val registerButton = register_button
        usernameInputLayout = username_layout
        passwordInputLayout = password_layout
        inputValidators = mapOf(
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

        passwordInputLayout?.editText?.setOnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_login_id || id == EditorInfo.IME_ACTION_UNSPECIFIED) {
                attemptLogin()
                return@setOnEditorActionListener true
            } else {
                false
            }
        }
        loginButton?.setOnClickListener { attemptLogin() }
        registerButton?.setOnClickListener { loadRegisterPage() }
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

    fun setLoadRegisterCallback(loadRegister: () -> Unit) {
        this.loadRegisterPage = loadRegister
    }

    fun setLoginCallback(login: (String, String) -> Unit) {
        this.login = login
    }

    private fun attemptLogin() {
        val focusView = validate()

        if (focusView != null) {
            focusView.requestFocus()
        } else {
            val username = usernameInputLayout?.editText?.text.toString()
            val password = passwordInputLayout?.editText?.text.toString()
            login(username, password)
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
        fun newInstance(): LoginFragment = LoginFragment()
    }
}