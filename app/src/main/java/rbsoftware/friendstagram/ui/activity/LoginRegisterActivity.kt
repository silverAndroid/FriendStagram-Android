package rbsoftware.friendstagram.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.facebook.imagepipeline.request.ImageRequest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login_register.*
import rbsoftware.friendstagram.BuildConfig
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.service.ImageService
import rbsoftware.friendstagram.service.NetworkService
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.ErrorDisplay
import rbsoftware.friendstagram.ui.fragment.LoginFragment
import rbsoftware.friendstagram.ui.fragment.RegisterFragment
import rbsoftware.friendstagram.ui.fragment.SetupFragment
import rbsoftware.friendstagram.viewmodel.UserViewModel


/**
 * Created by Rushil on 8/19/2017.
 */
class LoginRegisterActivity : AppCompatActivity() {
    private val TAG: String = "LoginRegisterActivity"
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    private lateinit var fragmentContainer: View
    private lateinit var progressView: View
    private lateinit var userViewModel: UserViewModel
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        fragmentContainer = container
        progressView = login_progress

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
        userViewModel = daggerComponent.userViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = Color.TRANSPARENT
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                loadLoginBackground()
            }
        }

        if (authService.isLoggedIn) {
            onLoginSuccess()
        } else {
            loadLoginPage()
        }
    }

    private fun login(username: String, password: String) {
        showProgress(true)
        subscriptions.add(
                userViewModel.login(username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                response.body()?.data?.let {
                                    val (token, _, profilePictureURL) = it
                                    authService.token = token
                                    if (profilePictureURL == null) {
                                        subscriptions.add(
                                                ImageService.prefetchImage(
                                                        ImageRequest.fromUri(Uri.parse("${BuildConfig.BASE_URL}/users/default_profile_picture?username=$username"))
                                                )
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribeOn(Schedulers.io())
                                                        .subscribe({
                                                            Log.d(TAG, "Cached image successfully")
                                                            authService.username = username
                                                            loadSetupPage()
                                                        }, {
                                                            onNetworkError(it)
                                                            authService.logout()
                                                        })
                                        )
                                    }
                                }
                            } else {
                                val error = NetworkService.parseError(response.errorBody())
                                this.handleError(error)
                            }
                            showProgress(false)
                        }, this::onNetworkError)
        )
    }

    private fun onLoginSuccess() {
        Toast.makeText(applicationContext, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun loadLoginPage() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            val fragment = LoginFragment.newInstance()
            fragment.setLoginCallback(this::login)
            fragment.setLoadRegisterCallback(this::loadRegisterPage)
            showFragment(fragment, setAnimations = this::showFragmentAnimations)
        } else {
            supportFragmentManager.popBackStack()
        }

        loadLoginBackground()
    }

    private fun register(name: String, email: String, username: String, password: String) {
        showProgress(true)
        subscriptions.add(
                userViewModel.register(name, email, username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                Toast.makeText(applicationContext, getString(R.string.success_register), Toast.LENGTH_SHORT).show()
                                loadLoginPage()
                            } else {
                                val error: String? = NetworkService.parseError(response.errorBody())
                                this.handleError(error)
                            }
                            showProgress(false)
                        }, this::onNetworkError)
        )
    }

    private fun loadRegisterPage() {
        val fragment = RegisterFragment.newInstance()
        fragment.setRegisterCallback(this::register)
        fragment.setLoadLoginCallback(this::loadLoginPage)
        showFragment(fragment, true, "Register", setAnimations = this::showFragmentAnimations)
        loadRegisterBackground()
    }

    private fun loadSetupPage() {
        val fragment = SetupFragment.newInstance(authService.username)
        subscriptions.addAll(
                fragment.setupComplete
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.trampoline())
                        .subscribe({
                            onLoginSuccess()
                        }, this::onNetworkError),
                fragment.showLoading
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.trampoline())
                        .subscribe({ show ->
                            showProgress(show)
                        }, this::onNetworkError)
        )
        showFragment(fragment, false, setAnimations = this::showFragmentAnimations)
        loadSetupBackground()
    }

    private fun loadLoginBackground() {
        val backgroundURI = "res:/${R.drawable.bg_login}"
        background.setImageURI(Uri.parse(backgroundURI))
    }

    private fun loadRegisterBackground() {
        val backgroundURI = "res:/${R.drawable.bg_register}"
        background.setImageURI(Uri.parse(backgroundURI))
    }

    private fun loadSetupBackground() {
        val backgroundURI = "res:/${R.drawable.bg_setup}"
        background.setImageURI(Uri.parse(backgroundURI))
    }

    private fun showFragmentAnimations(transaction: FragmentTransaction) {
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
    }

    private fun onNetworkError(error: Throwable) {
        Log.e(TAG, "Network error", error)
        showProgress(false)
        Toast.makeText(applicationContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
    }

    private fun showProgress(show: Boolean) {
        val showView = if (show) progressView else fragmentContainer
        val hideView = if (show) fragmentContainer else progressView

        fadeAnimation(showView, hideView)
    }

    private fun handleError(error: String?) {
        when {
            error == null -> Toast.makeText(applicationContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            error.contains("is Null") -> {
                val fragment = supportFragmentManager.findFragmentById(R.id.container) as ErrorDisplay
                fragment.showError(error)
            }
            else -> Toast.makeText(applicationContext, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fadeAnimation(showView: View, hideView: View) {
        val animTime: Long = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        showView.alpha = 0f
        showView.visibility = View.VISIBLE

        showView.animate()
                .alpha(1f)
                .setDuration(animTime)
                .setListener(null)

        hideView.animate()
                .alpha(0f)
                .setDuration(animTime)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        hideView.visibility = View.GONE
                    }
                })
    }
}