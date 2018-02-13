package rbsoftware.friendstagram

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.util.Log

/**
 * Created by silve on 2/12/2018.
 */
object AnimationQueue {
    private val animationBuilder = AnimatorSet()

    private var isAnimationRunning = false
    private lateinit var lastAnimator: Animator

    fun runAnimation(animator: Animator, onStart: () -> Unit = {}, onEnd: () -> Unit = {}) {
        runAnimations(listOf(animator), onStart, onEnd)
    }

    fun runAnimations(animators: List<Animator>, onStart: () -> Unit = {}, onEnd: () -> Unit = {}) {
        animationBuilder.removeAllListeners()
        animators.first().addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                onStart.invoke()
            }
        })

        if (isAnimationRunning) {
            animators.last().removeAllListeners()
        }

        animators.last().addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                isAnimationRunning = false
                onEnd.invoke()
                animation.removeAllListeners()
            }
        })

        if (isAnimationRunning) {
            animationBuilder.playTogether(animators)
            animationBuilder.start()
        } else {
            isAnimationRunning = true
            animationBuilder.playTogether(animators)
            animationBuilder.start()
        }
        lastAnimator = animators.last()
        animators.first().removeAllListeners()
    }
}