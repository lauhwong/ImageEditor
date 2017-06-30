package com.miracle.view.imageeditor.view

import android.graphics.RectF
import android.view.View
import android.widget.TextView
import com.miracle.view.imageeditor.OnLayoutRectChange
import com.miracle.view.imageeditor.R
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.setInt

/**
 * Created by lxw
 */
class DragToDeleteView(private val view: View) {
    var onLayoutRectChange: OnLayoutRectChange? = null
    private val mTextView: TextView

    init {
        view.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            val rect = RectF()
            rect.setInt(left, top, right, bottom)
            onLayoutRectChange?.invoke(view, rect)
        }
        mTextView = view.findViewById(R.id.tvDragDelete) as TextView
    }

    fun showOrHide(show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun setDrag2DeleteText(focus: Boolean) {
        val text = if (focus) Utils.getResourceString(view.context, R.string.editor_drag_to_delete)
        else Utils.getResourceString(view.context, R.string.editor_release_to_delete)
        mTextView.text = text
    }

}