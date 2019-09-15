package com.openclassrooms.realestatemanager.view;

import android.animation.Animator;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.FrameLayout;

public class CircularRevealAnimation {

    public static void startAnimation(FrameLayout rootLayout, boolean isTablet) {

        int cx;
        int cy;
        if (isTablet) {
            cx = rootLayout.getWidth() - 40;
            cy = rootLayout.getHeight() - 40;
        } else {
            cx = rootLayout.getWidth()/2 ;
            cy = rootLayout.getHeight() - 150;
        }


        float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator circularReveal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
        }
        assert circularReveal != null;
        circularReveal.setDuration(1000);

        // make the view visible and start the animation
        rootLayout.setVisibility(View.VISIBLE);
        circularReveal.start();
    }
}
