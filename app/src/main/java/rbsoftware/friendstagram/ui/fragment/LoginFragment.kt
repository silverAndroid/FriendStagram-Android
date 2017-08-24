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
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.Util
import rbsoftware.friendstagram.addValidation
import rbsoftware.friendstagram.validate

/**
 * Created by Rushil on 8/21/2017.
 */
class LoginFragment : Fragment(), ErrorDisplay {
    private var usernameInputLayout: TextInputLayout? = null
    private var passwordInputLayout: TextInputLayout? = null
    private lateinit var loadRegisterPage: () -> Unit
    private lateinit var login: (String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginButton: Button? = view?.findViewById(R.id.login_button)
        val registerButton: Button? = view?.findViewById(R.id.register_button)
        usernameInputLayout = view?.findViewById(R.id.username_layout)
        passwordInputLayout = view?.findViewById(R.id.password_layout)

        passwordInputLayout?.editText?.setOnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_login_id || id == EditorInfo.IME_ACTION_UNSPECIFIED) {
                attemptLogin()
                return@setOnEditorActionListener true
            }
            false
        }
        loginButton?.setOnClickListener { attemptLogin() }
        registerButton?.setOnClickListener { loadRegisterPage() }
        usernameInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        passwordInputLayout?.addValidation({ !TextUtils.isEmpty(it) }, R.string.error_field_required)
        passwordInputLayout?.addValidation({ Util.isPasswordValid(it) }, R.string.error_invalid_password)
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
        if (usernameInputLayout?.validate() == false) {
            focusView = usernameInputLayout
        }
        if (passwordInputLayout?.validate() == false) {
            focusView = focusView ?: passwordInputLayout
        }
        return focusView
    }

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }
}