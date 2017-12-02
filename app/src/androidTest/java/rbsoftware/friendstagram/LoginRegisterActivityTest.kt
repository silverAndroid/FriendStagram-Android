package rbsoftware.friendstagram

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import rbsoftware.friendstagram.model.LoginResponse
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.ui.activity.LoginRegisterActivity
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.Response

/**
 * Created by rushil on 01/12/17.
 */
@RunWith(AndroidJUnit4::class)
class LoginRegisterActivityTest {
    @get:Rule
    private val activityRule: ActivityTestRule<LoginRegisterActivity> = ActivityTestRule(LoginRegisterActivity::class.java)
    private val userViewModel: UserViewModel = mock(UserViewModel::class.java)

    @Test
    fun testLoginToRegister() {
        val registerButtonMatcher = onView(withId(R.id.register_button))
        activityRule.launchActivity(null)

        registerButtonMatcher.check(matches(isDisplayed()))
        registerButtonMatcher.perform(click())
        onView(withId(R.id.name_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterToLogin() {
        val registerButtonMatcher = onView(withId(R.id.register_button))
        val loginButtonMatcher = onView(withId(R.id.login_button))
        val nameLayoutMatcher = onView(withId(R.id.name_layout))
        activityRule.launchActivity(null)

        registerButtonMatcher.check(matches(isDisplayed()))
        registerButtonMatcher.perform(click())
        nameLayoutMatcher.check(matches(isDisplayed()))

        loginButtonMatcher.check(matches(isDisplayed()))
        loginButtonMatcher.perform(click())
        nameLayoutMatcher.check(doesNotExist())
    }

    @Test
    fun testLoginToSetup() {
        val username = "banana"
        val password = "banana"

        `when`(userViewModel.login(username, password)).thenReturn(Single.just(
                Response.success(
                        ServerResponse(LoginResponse(profilePictureURL = null))
                )
        ))
        activityRule.launchActivity(null)

        onView(withId(R.id.username)).perform(typeText(username))
        onView(withId(R.id.password)).perform(typeText(password))
        onView(withId(R.id.login_button)).perform(click())

        onView(withId(R.id.setup_profile_title)).check(matches(isDisplayed()))
    }
}