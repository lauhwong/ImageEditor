package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout


/**
 * Created by lxw
 */
class ActionFrameLayout : FrameLayout {
    var actionListener: ActionListener? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    private fun initView(context: Context) {

    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP) {
            actionListener?.actionUp()
        } else if (ev.action == MotionEvent.ACTION_MOVE) {
            actionListener?.actionMove()
        }
        return super.dispatchTouchEvent(ev)
    }

    interface ActionListener {
        fun actionUp()
        fun actionMove()
    }
}