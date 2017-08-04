package com.miracle.view.imageeditor

import android.graphics.RectF
import android.view.View
import com.miracle.view.imageeditor.bean.SharableData

/**
 * Created by lxw
 */
typealias ShowOrHideDragCallback = (Boolean) -> Unit

typealias SetOrNotDragCallback = (Boolean) -> Unit

typealias DragViewRectCallback = (RectF) -> Unit

typealias OnLayerViewDoubleClick = (View, SharableData) -> Unit

typealias OnLayoutRectChange = (View, RectF) -> Unit

typealias ImageComposeCallback = (Boolean) -> Unit

typealias PreDrawSizeListener = (Int, Int) -> Unit




