package rbsoftware.friendstagram.ui.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.showFragment
import rbsoftware.friendstagram.ui.fragment.EditProfileFragment

class EditProfileActivity : AppCompatActivity() {
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

    private fun showEditProfileFragment() {
        val fragment = EditProfileFragment.newInstance()
        showFragment(fragment)
    }
}
