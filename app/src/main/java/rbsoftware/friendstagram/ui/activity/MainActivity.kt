package rbsoftware.friendstagram.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.widget.ImageView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import rbsoftware.friendstagram.Actions
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Action
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.HomeFragment
import rbsoftware.friendstagram.ui.fragment.PostFragment
import rbsoftware.friendstagram.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val bottomNavItems: List<BottomNavItem> = listOf(
            BottomNavItem(R.id.tab_home, this::showHomeFragment),
            BottomNavItem(R.id.tab_camera, this::showCameraActivity),
            BottomNavItem(R.id.tab_account, this::showProfileFragment)
    )
    private val uiSubscriptions: CompositeDisposable = CompositeDisposable()

    private var currentTab: Int = 0
    private var bottomNav: BottomNavigationView? = null
    private var homeFragment: HomeFragment? = null
    private var profileFragment: ProfileFragment? = null
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = bottom_bar

        bottomNav?.setOnNavigationItemSelectedListener({ item ->
            val id = item.itemId
            val bottomNavItem = bottomNavItems.find { it.itemID == id }
            if (bottomNavItem == null)
                false
            else {
                bottomNavItem.loadFragment()
                true
            }
        })

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
    }

    override fun onPostResume() {
        super.onPostResume()
        bottomNav?.selectedItemId = bottomNavItems[currentTab].itemID
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uiSubscriptions.clear()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uiSubscriptions.clear()
        }
    }

    private fun showHomeFragment() {
        currentTab = 0
        if (homeFragment == null)
            homeFragment = HomeFragment.newInstance()

        homeFragment?.let {
            uiSubscriptions.add(
                    it.getToolbarManipulator()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.trampoline())
                            .subscribe(this::setToolbar, this::onSubscriptionError)
            )
            showFragment(it)
        }
    }

    private fun showCameraActivity() {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivity(intent)
    }

    private fun showProfileFragment() {
        Log.d(TAG, "show profile fragment")
        currentTab = 2
        if (profileFragment == null)
            profileFragment = ProfileFragment.newInstance(authService.username)

        profileFragment?.let {
            uiSubscriptions.add(
                    it.isAdapterInitialized()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.trampoline())
                            .subscribe({
                                uiSubscriptions.add(
                                        it.getToolbarManipulator()
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.trampoline())
                                                .subscribe(this::setToolbar, this::onSubscriptionError)
                                )
                                it.getOnPostSelected()?.let { subject ->
                                    uiSubscriptions.add(
                                            subject.observeOn(AndroidSchedulers.mainThread())
                                                    .subscribeOn(Schedulers.trampoline())
                                                    .subscribe(this::showPostFragment, this::onSubscriptionError)
                                    )
                                }
                                it.getOnActionExecuted()?.let { subject ->
                                    uiSubscriptions.add(
                                            subject.observeOn(AndroidSchedulers.mainThread())
                                                    .subscribeOn(Schedulers.trampoline())
                                                    .subscribe(this::onActionExecution, this::onSubscriptionError)
                                    )
                                }
                            }, this::onSubscriptionError)
            )
            showFragment(it)
        }
    }

    private fun showPostFragment(post: Post) {
        val fragment = PostFragment.newInstance(post)
        uiSubscriptions.add(
                fragment.getToolbarManipulator()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.trampoline())
                        .subscribe(this::setToolbar, this::onSubscriptionError)
        )
        showFragment(fragment, true, "PostFragment")
    }

    private fun showEditProfileActivity(user: User) {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
    }

    private fun showSearchActivity() {
        val intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }

    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val searchIcon: ImageView? = toolbar.findViewById(R.id.left_icon)
        searchIcon?.setOnClickListener {
            showSearchActivity()
        }
    }

    private fun onActionExecution(action: Action) {
        when (action.action) {
            Actions.EDIT_PROFILE -> showEditProfileActivity(action.data["user"] as User)
        }
    }

    private fun onSubscriptionError(e: Throwable) {
        throw RuntimeException(e)
    }

    private data class BottomNavItem(@IdRes val itemID: Int, val loadFragment: () -> Unit)
}