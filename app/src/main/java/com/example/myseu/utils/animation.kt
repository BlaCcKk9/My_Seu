package com.example.myseu.utils

import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.example.myseu.R
import kotlinx.android.synthetic.main.activity_auth.*

fun View.slideDown(duration: Int = 300) {
    val animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down)
    this.visibility = View.VISIBLE
    this.startAnimation(animation)
}
