package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.bean.InputTextData
import com.miracle.view.imageeditor.bean.TextPastingSaveState
import com.miracle.view.imageeditor.increase
import com.miracle.view.imageeditor.schedule


/**
 * Created by lxw
 */

class TextPastingView : BasePastingLayerView<TextPastingSaveState> {
    private var mFocusRectOffset = 0f
    private lateinit var mTextPaint: Paint
    private lateinit var mTempTextPaint: Paint

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initSupportView(context: Context) {
        super.initSupportView(context)
        mFocusRectOffset = Utils.dp2px(context, 10f).toFloat()
        //textPaint
        mTextPaint = Paint()
        mTextPaint.textSize = Utils.sp2px(context, 25f).toFloat()
        mTextPaint.isAntiAlias = true
        mTempTextPaint = Utils.copyPaint(mTextPaint)

    }

    fun onTextPastingChanged(data: InputTextData) {
        if (data.text == null || data.text.isBlank() || data.color == null) return
        addTextPasting(data.id, data.text, data.color)
    }

    private fun addTextPasting(id: String?, text: String, color: Int) {
        genDisplayCanvas()
        //old matrix info
        var displayMatrix = Matrix()
        id?.let {
            val result = saveStateMap[id]
            if (result != null) {
                displayMatrix = result.displayMatrix
            }
        }
        val state = initTextPastingSaveState(text, color, displayMatrix)
        id?.let {
            state.id = it
        }
        saveStateMap.put(state.id, state)
        currentPastingState = state
        redrawAllCache()
        //hideBorder...
        hideExtraValidateRect()
    }

    private fun initTextPastingSaveState(text: String, color: Int, matrix: Matrix = Matrix()): TextPastingSaveState {
        mTextPaint.color = color
        val width = mTextPaint.measureText(text)
        val height = mTextPaint.descent() - mTextPaint.ascent()
        val initDisplayRect = RectF()
        var point = PointF(validateRect.centerX(), validateRect.centerY())
        point = Utils.mapInvertMatrixPoint(drawMatrix, point)
        initDisplayRect.schedule(point.x, point.y, width, height)
        val initTextRect = RectF()
        initTextRect.set(initDisplayRect)
        initDisplayRect.increase(mFocusRectOffset, mFocusRectOffset)
        return TextPastingSaveState(text, color, initTextRect, initDisplayRect, matrix)
    }


    override fun drawPastingState(state: TextPastingSaveState, canvas: Canvas) {
        super.drawPastingState(state, canvas)
        val resultTextRect = RectF()
        val matrix = Matrix(state.displayMatrix)
        matrix.mapRect(resultTextRect, state.initTextRect)
        mTempTextPaint.textSize = mTextPaint.textSize * Utils.geMatrixScale(matrix)
        mTempTextPaint.color = state.textColor
        val result = PointF(resultTextRect.left, resultTextRect.bottom - mTempTextPaint.descent())
        canvas.drawText(state.text, result.x, result.y, mTempTextPaint)
    }

    override fun onPastingDoubleClick(state: TextPastingSaveState) {
        super.onPastingDoubleClick(state)
        onLayerViewDoubleClick?.invoke(this, InputTextData(state.id, state.text, state.textColor))
    }

}


