package rbsoftware.friendstagram.ui.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import rbsoftware.friendstagram.Constants
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.dagger.component.DaggerServicesComponent
import rbsoftware.friendstagram.dagger.module.AppModule
import rbsoftware.friendstagram.dagger.module.ServicesModule
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.ui.fragment.HomeFragment
import rbsoftware.friendstagram.ui.fragment.PostFragment
import rbsoftware.friendstagram.ui.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {
    private val bottomNavItems: List<BottomNavItem> = listOf(
            BottomNavItem(R.id.tab_home, this::showHomeFragment),
            BottomNavItem(R.id.tab_camera, this::showCameraActivity),
            BottomNavItem(R.id.tab_account, this::showProfileFragment)
    )
    private var currentTab: Int = 0
    private var bottomNav: BottomNavigationView? = null
    private var homeFragment: HomeFragment? = null
    private var profileFragment: ProfileFragment? = null
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_bar)

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

        val daggerComponent = DaggerServicesComponent
                .builder()
                .appModule(AppModule(this))
                .servicesModule(ServicesModule())
                .build()
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
    }

    override fun onResume() {
        super.onResume()
        bottomNav?.selectedItemId = bottomNavItems[currentTab].itemID
        showFragment(currentTab)
    }

    private fun showHomeFragment() {
        currentTab = 0
        if (homeFragment == null)
            homeFragment = HomeFragment.newInstance()

        homeFragment?.let {
            it.setToolbarManipulator(this::setToolbar)
            showFragment(it)
        }
    }

    private fun showCameraActivity() {
        val intent = Intent(this, CreatePostActivity::class.java)
        startActivity(intent)
    }

    private fun showProfileFragment() {
        currentTab = 2
        // TODO: Add callback for when edit profile
        if (profileFragment == null)
            profileFragment = ProfileFragment.newInstance(authService.username)

        profileFragment?.let {
            it.setToolbarManipulator(this::setToolbar)
            it.setOnPostSelectListener(this::showPostFragment)
            it.setOnActionExecuteListener(this::onActionExecution)
            showFragment(it)
        }
    }

    private fun showPostFragment(post: Post) {
        val fragment = PostFragment.newInstance(post)
        fragment.setToolbarManipulator(this::setToolbar)
        showFragment(fragment, true, "PostFragment")
    }

    private fun showEditProfileActivity() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun showFragment(currentTab: Int) {
        bottomNavItems[currentTab].loadFragment()
    }

    private fun showFragment(fragment: Fragment, addToStack: Boolean = false, name: String? = "") {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment)
        if (addToStack)
            transaction.addToBackStack(name)
        transaction.commit()
    }

    private fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun onActionExecution(action: String) {
        when (action) {
            Constants.Action.EDIT_PROFILE -> showEditProfileActivity()
        }
    }

    private data class BottomNavItem(@IdRes val itemID: Int, val loadFragment: () -> Unit)
}