package com.serdar.whell.wheel

import android.animation.Animator

abstract class WheelContract : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) {}

    override fun onAnimationEnd(animation: Animator) {}

    override fun onAnimationCancel(animation: Animator) {}

    override fun onAnimationRepeat(animation: Animator) {}
}