package com.miracle.view.imageeditor.layer

import android.graphics.Matrix
import android.graphics.RectF

/**
 * ## Root layer's matrix changed callback
 *  it's very important to  coordinate with other layer view
 *
 * Created by lxw
 */
interface OnPhotoRectUpdateListener {

    fun onPhotoRectUpdate(rect: RectF, matrix: Matrix)

}