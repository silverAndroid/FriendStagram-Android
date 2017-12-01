package rbsoftware.friendstagram

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import rbsoftware.friendstagram.ui.activity.CreatePostActivity
import rbsoftware.friendstagram.ui.activity.MainActivity

/**
 * Created by rushil on 01/12/17.
 */
@RunWith(AndroidJUnit4::class)
class CreatePostActivityTest {
    @get:Rule
    private val intentRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testIntent() {
        val cameraTabMatcher = onView(withId(R.id.tab_camera))

        intentRule.launchActivity(null)
        cameraTabMatcher.check(matches(isDisplayed()))
        cameraTabMatcher.perform(click())

        intended(hasComponent(CreatePostActivity::class.java.name))
    }
}