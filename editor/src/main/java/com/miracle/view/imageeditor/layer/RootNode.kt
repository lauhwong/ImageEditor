package com.miracle.view.imageeditor.layer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF

/**
 * ## Root node marker
 *
 * @see com.miracle.view.imageeditor.layer.photoview.PhotoView
 *
 * Created by lxw
 */
interface RootNode<out RootView> {

    fun addOnMatrixChangeListener(listener: OnPhotoRectUpdateListener)

    fun addGestureDetectorListener(listener: GestureDetectorListener)
    //setter
    fun setRotationBy(degree: Float)

    fun force2Scale(scale: Float, animate: Boolean)

    fun setSupportMatrix(matrix: Matrix)

    fun setDisplayBitmap(bitmap: Bitmap)
    //getter
    fun getSupportMatrix(): Matrix

    fun getDisplayBitmap(): Bitmap?

    fun getRooView(): RootView

    fun getDisplayingRect(): RectF?

    fun getDisplayMatrix(): Matrix

    fun getBaseLayoutMatrix(): Matrix

    fun getOriginalRect(): RectF?

}