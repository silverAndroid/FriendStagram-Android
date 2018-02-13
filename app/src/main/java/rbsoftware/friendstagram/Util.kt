package rbsoftware.friendstagram

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

/**
 * Created by silve on 1/18/2018.
 */

fun <T> onError(clazz: Class<T>, context: Context, error: Throwable, message: String? = null) = onError(clazz::class.java.simpleName, context, error, message)

fun onError(tag: String, context: Context, error: Throwable, message: String? = null) {
    Log.e(tag, message ?: "ErrorResponse", error)
    Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
}

fun showProgress(show: Boolean, progressView: ProgressBar, layoutView: View) {
    val showView = if (show) progressView else layoutView
    val hideView = if (show) layoutView else progressView

    fadeAnimation(showView, hideView)
}

private fun fadeAnimation(showView: View, hideView: View) {
    val animTime = showView.context.applicationContext.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
    val showAnimation = ObjectAnimator.ofPropertyValuesHolder(
            showView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 1f)
    )
    val hideAnimation = ObjectAnimator.ofPropertyValuesHolder(
            hideView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0f)
    )

    showAnimation.duration = animTime
    hideAnimation.duration = animTime

    AnimationQueue.runAnimations(
            listOf(showAnimation, hideAnimation),
            {
                showView.alpha = 0f
                showView.visibility = View.VISIBLE
            },
            {
                hideView.visibility = View.GONE
            }
    )
}