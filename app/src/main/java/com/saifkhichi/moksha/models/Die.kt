package com.saifkhichi.moksha.models

import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import com.saifkhichi.moksha.R
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * A regular six-sided die.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Die(val view: ImageView) {

    private val frames = arrayOf(
        R.drawable.one,
        R.drawable.two,
        R.drawable.three,
        R.drawable.four,
        R.drawable.five,
        R.drawable.six,
    )

    private val sides = frames.size

    fun animateDie(value: Int, onFinished: (value: Int) -> Unit, i: Int = 1) {
        val maxMovement = 25
        val dx = Random.Default.nextInt(-maxMovement..maxMovement).toFloat()
        val dy = Random.Default.nextInt(-maxMovement..maxMovement).toFloat()
        val move = TranslateAnimation(0f, dx, 0f, dy)
        move.duration = SHAKE_DURATION
        move.setAnimationListener(ShakeAndRollListener(value, i, onFinished))
        view.startAnimation(move)
    }

    fun roll(seed: Long? = null, onFinished: (value: Int) -> Unit) {
        view.isEnabled = false
        val random: Random = when (seed) {
            null -> Random.Default
            else -> Random(seed)
        }

        val value = random.nextInt(1..sides)
        animateDie(value, onFinished)
    }

    inner class ShakeAndRollListener(
        private val value: Int,
        private val n: Int,
        private val onFinished: (value: Int) -> Unit,
    ) : Animation.AnimationListener {

        private var roll: AnimationDrawable

        init {
            view.setBackgroundResource(R.drawable.die)
            roll = view.background as AnimationDrawable
        }

        override fun onAnimationStart(animation: Animation?) {
            if (n == 1) {
                roll.start()
            }
        }

        override fun onAnimationEnd(animation: Animation?) {
            if (n == NUM_OF_SHAKES) {
                roll.stop()

                view.setBackgroundResource(frames[value - 1])
                onFinished(value)
            } else {
                animateDie(value, onFinished, n + 1)
            }
        }

        override fun onAnimationRepeat(animation: Animation?) {}
    }

    fun hide() {
        view.visibility = View.GONE
    }

    fun show() {
        view.visibility = View.VISIBLE
        view.isEnabled = true
    }

    companion object {
        private const val NUM_OF_SHAKES = 3
        private const val SHAKE_DURATION = 200L
    }

}