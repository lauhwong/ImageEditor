package com.miracle.view.imageeditor.layer

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.view.ViewGroup
import android.widget.ImageView
import com.miracle.view.imageeditor.Utils.callChildren

/**
 * ## A delegate for Editor just simplify the callback
 *
 * Created by lxw
 */
class RootEditorDelegate(val rootNode: RootNode<ImageView>, val delegateParent: ViewGroup) : RootNode<ImageView>, LayerTransformer, OnPhotoRectUpdateListener {
    init {
        addGestureDetectorListener(this)
        addOnMatrixChangeListener(this)
    }

    override fun onPhotoRectUpdate(rect: RectF, matrix: Matrix) {
        callChildren(OnPhotoRectUpdateListener::class.java, delegateParent) {
            it.onPhotoRectUpdate(rect, matrix)
        }
    }

    override fun onDrag(dx: Float, dy: Float, x: Float, y: Float, rootLayer: Boolean) {
        callChildren(LayerTransformer::class.java, delegateParent) {
            it.onDrag(dx, dy, x, y, true)
        }
    }

    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float, rootLayer: Boolean) {
        callChildren(LayerTransformer::class.java, delegateParent) {
            it.onScale(scaleFactor, focusX, focusY, true)
        }
    }

    override fun resetEditorSupportMatrix(matrix: Matrix) {
        callChildren(LayerTransformer::class.java, delegateParent) {
            it.resetEditorSupportMatrix(matrix)
        }
    }

    override fun setRotationBy(degree: Float) {
        rootNode.setRotationBy(degree)
    }

    override fun force2Scale(scale: Float, animate: Boolean) {
        rootNode.force2Scale(scale, animate)
    }

    override fun addOnMatrixChangeListener(listener: OnPhotoRectUpdateListener) {
        rootNode.addOnMatrixChangeListener(listener)
    }

    override fun addGestureDetectorListener(listener: GestureDetectorListener) {
        rootNode.addGestureDetectorListener(listener)
    }

    override fun getSupportMatrix() = rootNode.getSupportMatrix()

    override fun getDisplayBitmap() = rootNode.getDisplayBitmap()

    override fun setSupportMatrix(matrix: Matrix) {
        rootNode.setSupportMatrix(matrix)
    }

    override fun setDisplayBitmap(bitmap: Bitmap) {
        rootNode.setDisplayBitmap(bitmap)
    }


    override fun getRooView(): ImageView {
        return rootNode.getRooView()
    }

    override fun getDisplayingRect() = rootNode.getDisplayingRect()

    override fun getDisplayMatrix() = rootNode.getDisplayMatrix()

    override fun getBaseLayoutMatrix() = rootNode.getBaseLayoutMatrix()

    override fun getOriginalRect() = rootNode.getOriginalRect()
}