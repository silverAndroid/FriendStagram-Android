package rbsoftware.friendstagram.ui.activity

import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_alt_toolbar.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.onError
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.EditProfileFragment

class EditProfileActivity : AppCompatActivity() {
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val user = intent.getParcelableExtra<User>("user")

        showEditProfileFragment(user)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    private fun showEditProfileFragment(user: User) {
        val fragment = EditProfileFragment.newInstance(user)
        subscriptions.add(
                fragment.updateComplete()
                        .subscribeOn(Schedulers.trampoline())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            finish()
                            Log.d("EditProfileActivity", "Updated profile info")
                        }, { onError(EditProfileActivity::class.java, this, it) })
        )
        showFragment(fragment)
    }
}
