package rbsoftware.friendstagram

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

/**
 * Created by Rushil on 8/23/2017.
 */
fun AppCompatActivity.showFragment(fragment: Fragment, addToStack: Boolean = false, name: String? = "", setAnimations: (FragmentTransaction) -> Unit = {}) {
    val transaction = supportFragmentManager.beginTransaction()

    setAnimations(transaction)
    transaction.replace(R.id.container, fragment)
    if (addToStack)
        transaction.addToBackStack(name)
    transaction.commit()
}