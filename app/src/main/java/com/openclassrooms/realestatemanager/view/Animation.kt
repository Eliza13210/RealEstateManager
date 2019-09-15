package com.openclassrooms.realestatemanager.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import com.openclassrooms.realestatemanager.view.CircularRevealAnimation

class Animation {

    /**
     *  Start Activity with a circular reveal animation
     * **/
    fun start(savedInstanceState: Bundle?, root_layout: FrameLayout) {
        if (savedInstanceState == null) {

            root_layout.visibility = View.INVISIBLE

            val viewTreeObserver = root_layout.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    @SuppressLint("ObsoleteSdkInt")
                    override fun onGlobalLayout() {
                        circularRevealActivity(root_layout)
                        root_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private fun circularRevealActivity(root_layout: FrameLayout) {
        CircularRevealAnimation.startAnimation(root_layout)
    }
}