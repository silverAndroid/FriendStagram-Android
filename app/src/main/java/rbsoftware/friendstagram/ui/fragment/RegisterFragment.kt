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
import android.widget.TextView
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.Util

/**
 * Created by Rushil on 8/21/2017.
 */
class RegisterFragment : Fragment(), ErrorDisplay {
    private var editName: EditText? = null
    private var editEmail: EditText? = null
    private var editUsername: EditText? = null
    private var editPassword: EditText? = null
    private lateinit var loadLoginPage: () -> Unit
    private lateinit var register: (String, String, String, String) -> Unit

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editName = view?.findViewById(R.id.name)
        editEmail = view?.findViewById(R.id.email)
        editUsername = view?.findViewById(R.id.username)
        editPassword = view?.findViewById(R.id.password)
        val loginButton: Button? = view?.findViewById(R.id.login_button)
        val registerButton: Button? = view?.findViewById(R.id.register_button)

        editPassword?.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == R.integer.action_register_id || id == EditorInfo.IME_NULL) {
                attemptRegister()
                return@OnEditorActionListener true
            }
            false
        })
        loginButton?.setOnClickListener { loadLoginPage() }
        registerButton?.setOnClickListener { attemptRegister() }
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

    fun setLoadLoginCallback(loadLogin: () -> Unit) {
        this.loadLoginPage = loadLogin
    }

    fun setRegisterCallback(register: (String, String, String, String) -> Unit) {
        this.register = register
    }

    private fun attemptRegister() {
        editName?.error = null
        editEmail?.error = null
        editUsername?.error = null
        editPassword?.error = null

        val name = editName?.text.toString()
        val email = editEmail?.text.toString()
        val username = editUsername?.text.toString()
        val password = editPassword?.text.toString()
        val focusView = validate(name, email, username, password)

        if (focusView != null) {
            focusView.requestFocus()
        } else {
            register(name, email, username, password)
        }
    }

    private fun validate(name: String, email: String, username: String, password: String): EditText? {
        if (TextUtils.isEmpty(name)) {
            editName?.error = getString(R.string.error_field_required)
            return editName
        } else if (TextUtils.isEmpty(email)) {
            editEmail?.error = getString(R.string.error_field_required)
            return editEmail
        } else if (!Util.isEmailValid(email)) {
            editEmail?.error = getString(R.string.error_invalid_email)
            return editEmail
        } else if (TextUtils.isEmpty(username)) {
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
        fun newInstance(): RegisterFragment = RegisterFragment()
    }
}