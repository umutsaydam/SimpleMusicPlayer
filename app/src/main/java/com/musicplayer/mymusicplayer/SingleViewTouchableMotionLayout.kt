package com.musicplayer.mymusicplayer

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.constraintlayout.motion.widget.MotionLayout

class SingleViewTouchableMotionLayout(context: Context, attributeSet: AttributeSet? = null) :
    MotionLayout(context, attributeSet) {
    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.playerBackground)
    }

    private val viewRect = Rect()
    private var touchStarted = false
    private var mStartTime: Long = 0

    init {

        setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout, p1: Int) {
                touchStarted = false
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!touchStarted) {
            viewToDetectTouch?.getHitRect(viewRect)
            touchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return touchStarted && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            mStartTime = event.eventTime
        }
        if ((event?.eventTime?.minus(mStartTime)!! >= ViewConfiguration.getTapTimeout()) && event.action == MotionEvent.ACTION_MOVE) {
            return super.onInterceptTouchEvent(event)
        }
        return false
    }
}