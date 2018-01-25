package rbsoftware.friendstagram.ui.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.ProfileFragment

class UserProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (intent != null) {
            val username = intent.getStringExtra(ARG_USERNAME)

            showFragment(ProfileFragment.newInstance(username))
        } else {
            Toast.makeText(this, getString(R.string.error_invalid_username), Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val ARG_USERNAME = "username"
    }
}
