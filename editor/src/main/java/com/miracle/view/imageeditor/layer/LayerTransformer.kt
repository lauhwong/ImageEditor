package com.miracle.view.imageeditor.layer

import android.graphics.Matrix

/**
 * ## Important for layer's gesture data handling marker
 *
 *@see RootEditorDelegate
 *
 * Created by lxw
 */
interface LayerTransformer : GestureDetectorListener {

    fun resetEditorSupportMatrix(matrix: Matrix)

}