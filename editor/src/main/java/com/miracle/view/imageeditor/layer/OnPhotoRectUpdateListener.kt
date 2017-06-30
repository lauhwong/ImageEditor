package com.miracle.view.imageeditor.layer

import android.graphics.Matrix
import android.graphics.RectF

/**
 * Created by lxw
 */
interface OnPhotoRectUpdateListener {

    fun onPhotoRectUpdate(rect: RectF,matrix: Matrix)

}