package com.miracle.view.imageeditor.layer

import android.graphics.Matrix

/**
 * Created by lxw
 */
interface LayerTransformer : GestureDetectorListener {

    fun resetEditorSupportMatrix(matrix: Matrix)

}