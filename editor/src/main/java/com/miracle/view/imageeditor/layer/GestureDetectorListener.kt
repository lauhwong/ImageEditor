package com.miracle.view.imageeditor.layer

/**
 * Created by lxw
 */
interface GestureDetectorListener {

    fun onFingerDown(downX: Float, downY: Float) {

    }

    fun onFingerUp(upX: Float, upY: Float) {

    }
    fun onFingerCancel() {

    }

    fun onDrag(dx: Float, dy: Float, x: Float, y: Float, rootLayer: Boolean = false)

    fun onFling(startX: Float, startY: Float, velocityX: Float, velocityY: Float, rootLayer: Boolean = false) {

    }

    fun cancelFling(rootLayer: Boolean = false) {

    }

    fun onScale(scaleFactor: Float, focusX: Float, focusY: Float, rootLayer: Boolean = false)

    fun onRotate(rotateDegree: Float, focusX: Float, focusY: Float, rootLayer: Boolean = false) {

    }

}