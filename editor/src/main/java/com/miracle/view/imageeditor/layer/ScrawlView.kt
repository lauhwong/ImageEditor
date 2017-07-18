package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.bean.ScrawlSaveState

/**
 * ## ScrawlView show to user
 *
 * Created by lxw
 */
class ScrawlView : BasePaintLayerView<ScrawlSaveState> {
    private lateinit var mDrawPaint: Paint

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initSupportView(context: Context) {
        super.initSupportView(context)
        mDrawPaint = Paint()
        mDrawPaint.isAntiAlias = true
        mDrawPaint.color = Color.RED
        mDrawPaint.strokeJoin = Paint.Join.ROUND
        mDrawPaint.strokeCap = Paint.Cap.ROUND
        mDrawPaint.style = Paint.Style.STROKE
        mDrawPaint.strokeWidth = Utils.dp2px(context, 3f).toFloat()
    }

    override fun drawDragPath(paintPath: Path) {
        super.drawDragPath(paintPath)
        displayCanvas?.drawPath(paintPath, mDrawPaint)
        invalidate()
    }

    override fun savePathOnFingerUp(paintPath: Path) = ScrawlSaveState(Utils.copyPaint(mDrawPaint), paintPath)

    override fun drawAllCachedState(canvas: Canvas) {
        for ((paint, path) in saveStateMap.values) {
            canvas.drawPath(path, paint)
        }
    }

    fun setPaintColor(color: Int) {
        mDrawPaint.color = color
    }


}