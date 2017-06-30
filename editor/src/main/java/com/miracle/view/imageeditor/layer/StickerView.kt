package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.bean.InputStickerData
import com.miracle.view.imageeditor.bean.StickerSaveState
import com.miracle.view.imageeditor.increase
import com.miracle.view.imageeditor.schedule

/**
 * Created by lxw
 */
class StickerView : BasePastingLayerView<StickerSaveState> {
    private var mFocusRectOffset = 0f

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initSupportView(context: Context) {
        super.initSupportView(context)
        mFocusRectOffset = Utils.dp2px(context, 10f).toFloat()
    }

    fun onStickerPastingChanged(data: InputStickerData) {
        addStickerPasting(data.stickerIndex, data.sticker)
    }

    private fun addStickerPasting(stickerIndex: Int, sticker: Sticker) {
        genDisplayCanvas()
        val state = initStickerSaveState(stickerIndex, sticker)
        state ?: return
        saveStateMap.put(state.id, state)
        currentPastingState = state
        redrawAllCache()
        hideExtraValidateRect()
    }

    private fun initStickerSaveState(stickerIndex: Int, sticker: Sticker, matrix: Matrix = Matrix()): StickerSaveState? {
        val bitmap = StickerUtils.getStickerBitmap(context, sticker, stickerIndex)
        bitmap ?: return null
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        val initDisplayRect = RectF()
        var point = PointF(validateRect.centerX(), validateRect.centerY())
        point = Utils.mapInvertMatrixPoint(drawMatrix, point)
        initDisplayRect.schedule(point.x, point.y, width, height)
        val initTextRect = RectF()
        initTextRect.set(initDisplayRect)
        initDisplayRect.increase(mFocusRectOffset, mFocusRectOffset)
        return StickerSaveState(sticker, stickerIndex, initDisplayRect, matrix)
    }

    override fun drawPastingState(state: StickerSaveState, canvas: Canvas) {
        super.drawPastingState(state, canvas)
        val result = StickerUtils.getStickerBitmap(context, state.sticker, state.stickerIndex)
        result ?: return
        val resultStickerRect = RectF()
        val matrix = Matrix(state.displayMatrix)
        matrix.mapRect(resultStickerRect, state.initDisplayRect)
        canvas.drawBitmap(result, null, resultStickerRect, null)
    }

}