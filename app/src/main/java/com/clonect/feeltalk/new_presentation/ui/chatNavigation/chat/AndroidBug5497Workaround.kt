package com.clonect.feeltalk.new_presentation.ui.chatNavigation.chat

import android.app.Activity
import android.graphics.Rect
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class AndroidBug5497Workaround constructor(activity: Activity) {
    private val contentContainer = activity.findViewById(android.R.id.content) as ViewGroup
    private val rootView = contentContainer.getChildAt(0)
    private val rootViewLayout = rootView.layoutParams as FrameLayout.LayoutParams
    private val viewTreeObserver = rootView.viewTreeObserver
    private val listener = ViewTreeObserver.OnGlobalLayoutListener { possiblyResizeChildOfContent() }

    private val contentAreaOfWindowBounds = Rect()
    private var usableHeightPrevious = 0

    // I call this in "onResume()" of my fragment
    fun addListener() {
        viewTreeObserver.addOnGlobalLayoutListener(listener)
    }

    // I call this in "onPause()" of my fragment
    fun removeListener() {
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    private fun possiblyResizeChildOfContent() {
        contentContainer.getWindowVisibleDisplayFrame(contentAreaOfWindowBounds)
        val usableHeightNow = contentAreaOfWindowBounds.height()
        if (usableHeightNow != usableHeightPrevious) {
            rootViewLayout.height = usableHeightNow
            // Change the bounds of the root view to prevent gap between keyboard and content, and top of content positioned above top screen edge.
            rootView.layout(contentAreaOfWindowBounds.left, contentAreaOfWindowBounds.top, contentAreaOfWindowBounds.right, contentAreaOfWindowBounds.bottom)
            rootView.requestLayout()

            usableHeightPrevious = usableHeightNow
        }
    }
}