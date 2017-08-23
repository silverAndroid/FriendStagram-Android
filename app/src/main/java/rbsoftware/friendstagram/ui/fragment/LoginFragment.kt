package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.Util

/**
 * Created by Rushil on 8/21/2017.
 */
class LoginFragment : Fragment(), ErrorDisplay {
    private var editUsername: EditText? = null
    private var editPassword: EditText? = null
    private lateinit var loadRegisterPage: () -> Unit
    private lateinit var login: (String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editUsername = view?.findViewById(R.id.username)
        editPassword = view?.findViewById(R.id.password)
        val loginButton: Button? = view?.findViewById(R.id.login_button)
        val registerButton: Button? = view?.findViewById(R.id.register_button)

        editPassword?.setOnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_login_id || id == EditorInfo.IME_ACTION_UNSPECIFIED) {
                attemptLogin()
                return@setOnEditorActionListener true
            }
            false
        }
        loginButton?.setOnClickListener { attemptLogin() }
        registerButton?.setOnClickListener { loadRegisterPage() }
    }

    override fun showError(error: String?) {
        if (error == "Username is Null") {
            editUsername?.error = getString(R.string.error_field_required)
            editUsername?.requestFocus()
        } else if (error == "Password is Null") {
            editPassword?.error = getString(R.string.error_field_required)
            editPassword?.requestFocus()
        }
    }

    fun setLoadRegisterCallback(loadRegister: () -> Unit) {
        this.loadRegisterPage = loadRegister
    }

    fun setLoginCallback(login: (String, String) -> Unit) {
        this.login = login
    }

    private fun attemptLogin() {
        editUsername?.error = null
        editPassword?.error = null

        val username = editUsername?.text.toString()
        val password = editPassword?.text.toString()
        val focusView = validate(username, password)

        if (focusView != null) {
            focusView.requestFocus()
        } else {
            login(username, password)
        }
    }

    private fun validate(username: String, password: String): EditText? {
        if (TextUtils.isEmpty(username)) {
            editUsername?.error = getString(R.string.error_field_required)
            return editUsername
        } else if (!Util.isUsernameValid(username)) {
            editUsername?.error = getString(R.string.error_invalid_username)
            return editUsername
        } else if (!Util.isPasswordValid(password)) {
            editPassword?.error = getString(R.string.error_invalid_password)
            return editPassword
        }
        return null
    }

    companion object {
        fun newInstance(): LoginFragment = LoginFragment()
    }
}