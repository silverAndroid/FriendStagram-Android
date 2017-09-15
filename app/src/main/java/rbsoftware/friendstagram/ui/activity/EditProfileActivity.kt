package rbsoftware.friendstagram.ui.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.EditProfileFragment

class EditProfileActivity : AppCompatActivity() {
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onResume() {
        super.onResume()
        showEditProfileFragment()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            subscriptions.clear()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            subscriptions.clear()
        }
    }

    private fun showEditProfileFragment() {
        val fragment = EditProfileFragment.newInstance()
        subscriptions.add(
                fragment.updateComplete()
                        .subscribeOn(Schedulers.trampoline())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            finish()
                            Log.d("EditProfileActivity", "Updated password")
                        }, this::onSubscriptionError)
        )
        showFragment(fragment)
    }

    private fun onSubscriptionError(e: Throwable) {
        throw RuntimeException(e)
    }
}
