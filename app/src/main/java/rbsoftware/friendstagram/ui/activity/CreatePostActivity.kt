package rbsoftware.friendstagram.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.dagger.component.DaggerServicesComponent
import rbsoftware.friendstagram.dagger.module.AppModule
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.service.NetworkService
import rbsoftware.friendstagram.ui.fragment.SelectFilterFragment
import rbsoftware.friendstagram.ui.fragment.SelectImageFragment
import rbsoftware.friendstagram.ui.fragment.SharePostFragment
import rbsoftware.friendstagram.viewmodel.PostViewModel
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.net.URISyntaxException

/**
 * Created by Rushil on 8/18/2017.
 */
class CreatePostActivity : AppCompatActivity() {
    private var imageURI: Uri? = null
    private var caption: String? = null
    private lateinit var authService: AuthenticationService
    private lateinit var postViewModel: PostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        val toolbar: Toolbar? = findViewById(R.id.toolbar)
        val closeIndicator = ContextCompat.getDrawable(this, R.drawable.ic_close)

        val daggerComponent = DaggerServicesComponent
                .builder()
                .appModule(AppModule(this))
                .build()
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
        postViewModel = daggerComponent.postViewModel()

        setSupportActionBar(toolbar)
        DrawableCompat.setTint(closeIndicator, Color.WHITE)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(closeIndicator)
    }

    override fun onResume() {
        super.onResume()
        showImageSelectFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_create_post, menu)
        return imageURI != null
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_next -> {
                nextClicked()
                return true
            }
        }
        return false
    }

    private fun nextClicked() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is SelectImageFragment) {
            showFilterSelectFragment()
        } else if (fragment is SelectFilterFragment) {
            showSharePostFragment()
        } else if (fragment is SharePostFragment) {
            if (isValidForm(fragment)) {
                uploadImage()
            }
        }
    }

    private fun showImageSelectFragment() {
        val fragment = SelectImageFragment.newInstance()
        fragment.setOnImageSelectListener {
            imageURI = it
            invalidateOptionsMenu()
        }
        showFragment(fragment)
    }

    private fun showFilterSelectFragment() {
        imageURI?.let { showFragment(SelectFilterFragment.newInstance(it), true, "SelectFilter") }
    }

    private fun showSharePostFragment() {
        imageURI?.let { showFragment(SharePostFragment.newInstance(it), true, "SharePost") }
    }

    private fun isValidForm(fragment: Fragment): Boolean {
        if (fragment is SharePostFragment) {
            val sharePostFragment: SharePostFragment = fragment
            if (TextUtils.isEmpty(sharePostFragment.getCaption())) {
                sharePostFragment.showRequiredCaption()
                return false
            }
            caption = sharePostFragment.getCaption()
            return true
        }
        throw IllegalArgumentException("Fragment does not have form!")
    }

    private fun uploadImage() {
        val dialog = ProgressDialog.show(this, "", "Uploading image...")
        try {
            val inputStream = FileInputStream(File(URI(imageURI.toString().replace(" ", "%20"))))
            postViewModel.uploadImage(inputStream, authService.username)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        val publicURL = response["secure_url"] as String
                        caption?.let { createPost(publicURL, it) }
                        dialog.dismiss()
                    }, { err ->
                        Toast.makeText(applicationContext, getString(R.string.error_image_upload), Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Failed to upload image", err)
                        dialog.dismiss()
                    })
        } catch (e: URISyntaxException) {
            Toast.makeText(applicationContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            Log.e(TAG, "uploadImage: Invalid URI", e)
            dialog.dismiss()
        }
    }

    private fun createPost(imageURL: String, caption: String) {
        val dialog = ProgressDialog.show(this, "", "Sharing post...")
        postViewModel.createPost(Post(imageURL, caption))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, getString(R.string.success_image_upload), Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        finish()
                    } else {
                        val error: String? = NetworkService.parseError(response.errorBody())
                        handleError(error)
                        dialog.dismiss()
                    }
                }, this::onNetworkError)
    }

    private fun showFragment(fragment: Fragment, addToStack: Boolean = false, name: String? = "") {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.container, fragment)
        if (addToStack)
            transaction.addToBackStack(name)
        transaction.commit()
    }

    private fun handleError(error: String?) {
        when {
            error == null -> Toast.makeText(applicationContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            error == "bad token" || error.contains("User ID is Null") -> {
                Toast.makeText(applicationContext, getString(R.string.error_login_invalid), Toast.LENGTH_LONG).show()
                authService.logout()

                val intent = Intent(this, LoginRegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }
        }
    }

    private fun onNetworkError(error: Throwable) {
        Log.e(TAG, "Network error", error)
        Toast.makeText(applicationContext, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "CreatePostActivity"
    }
}