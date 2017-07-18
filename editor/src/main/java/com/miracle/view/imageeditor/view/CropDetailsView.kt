package com.miracle.view.imageeditor.view

import android.view.View
import android.widget.TextView
import com.miracle.view.imageeditor.R
import com.miracle.view.imageeditor.Utils

/**
 * ## Ui details element for crop view
 *
 * Created by lxw
 */
class CropDetailsView(val view: View) {
    var cropListener: OnCropOperationListener? = null
    private val mRestoreView: TextView

    init {
        view.findViewById(R.id.ivCropRotate).setOnClickListener {
            cropListener?.onCropRotation(90f)
        }
        view.findViewById(R.id.ivCropCancel).setOnClickListener {
            cropListener?.onCropCancel()
        }
        mRestoreView = view.findViewById(R.id.tvCropRestore) as TextView
        mRestoreView.setOnClickListener {
            cropListener?.onCropRestore()
        }
        view.findViewById(R.id.ivCropConfirm).setOnClickListener {
            cropListener?.onCropConfirm()
        }
    }

    fun setRestoreTextStatus(restore: Boolean) {
        var color = if (restore) R.color.green_btn else R.color.white_f
        color = Utils.getResourceColor(view.context, color)
        mRestoreView.setTextColor(color)
    }

    fun showOrHide(show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    interface OnCropOperationListener {

        fun onCropRotation(degree: Float)

        fun onCropCancel()

        fun onCropConfirm()

        fun onCropRestore()
    }
}