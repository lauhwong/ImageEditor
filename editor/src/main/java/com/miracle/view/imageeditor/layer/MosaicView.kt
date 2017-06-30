package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.bean.MosaicSaveState
import com.miracle.view.imageeditor.recycleBitmap
import com.miracle.view.imageeditor.saveEntireLayer
import com.miracle.view.imageeditor.view.MosaicMode

/**
 * Created by lxw
 */
class MosaicView : BasePaintLayerView<MosaicSaveState> {
    private var mGridMosaicCover: Bitmap? = null
    private var mBlurMosaicCover: Bitmap? = null
    private var mMosaicMode: MosaicMode? = null
    private var mLastBitmapId: Int = 0
    private lateinit var mMosaicPaint: Paint
    private lateinit var mMosaicPaintMode: Xfermode
    //initial
    var initializeMatrix: Matrix = Matrix()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun initSupportView(context: Context) {
        super.initSupportView(context)
        mMosaicPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMosaicPaint.style = Paint.Style.STROKE
        mMosaicPaint.color = Color.BLACK
        mMosaicPaint.isAntiAlias = true
        mMosaicPaint.strokeJoin = Paint.Join.ROUND
        mMosaicPaint.strokeCap = Paint.Cap.ROUND
        mMosaicPaint.pathEffect = CornerPathEffect(10f)
        mMosaicPaint.strokeWidth = Utils.dp2px(context, 30f).toFloat()
        mMosaicPaintMode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleBitmap(mGridMosaicCover)
        recycleBitmap(mBlurMosaicCover)
    }

    private fun getMosaicCover(mosaicMode: MosaicMode?): Bitmap? {
        return if (mosaicMode == null) null
        else if (mosaicMode == MosaicMode.Grid) mGridMosaicCover
        else if (mosaicMode == MosaicMode.Blur) mBlurMosaicCover
        else null
    }

    override fun interceptDrag(x: Float, y: Float): Boolean {
        return !validateRect.contains(x, y)
    }

    override fun drawDragPath(paintPath: Path) {
        super.drawDragPath(paintPath)
        displayCanvas?.let {
            if (drawMosaicLayer(it, mMosaicMode, paintPath)) invalidate()
        }
    }

    private fun drawMosaicLayer(canvas: Canvas, mode: MosaicMode?, paintPath: Path): Boolean {
        val cover = getMosaicCover(mode)
        cover ?: return false
        val count = canvas.saveEntireLayer()
        canvas.drawPath(paintPath, mMosaicPaint)
        mMosaicPaint.xfermode = mMosaicPaintMode
        canvas.drawBitmap(cover, initializeMatrix, mMosaicPaint)
        mMosaicPaint.xfermode = null
        canvas.restoreToCount(count)
        return true
    }

    override fun savePathOnFingerUp(paintPath: Path): MosaicSaveState? {
        mMosaicMode?.let {
            return MosaicSaveState(it, paintPath)
        } ?: return null

    }

    override fun drawAllCachedState(canvas: Canvas) {
        for ((mode, path) in saveStateMap.values) {
            drawMosaicLayer(canvas, mode, path)
        }
    }

    fun setMosaicMode(mosaicMode: MosaicMode, mosaicBitmap: Bitmap?) {
        mMosaicMode = mosaicMode
        mosaicBitmap ?: return
        val bitmapId = mosaicBitmap.hashCode()
        val sameFromLast = mLastBitmapId == bitmapId
        if (mosaicMode == MosaicMode.Grid) {
            if (sameFromLast) {
                mGridMosaicCover ?: let {
                    mGridMosaicCover = MosaicUtils.getGridMosaic(mosaicBitmap)
                }
            } else {
                recycleBitmap(mGridMosaicCover)
                mGridMosaicCover = MosaicUtils.getGridMosaic(mosaicBitmap)
            }
        } else if (mosaicMode == MosaicMode.Blur) {
            if (sameFromLast) {
                mBlurMosaicCover ?: let {
                    mBlurMosaicCover = MosaicUtils.getBlurMosaic(mosaicBitmap)
                }
            } else {
                recycleBitmap(mBlurMosaicCover)
                mBlurMosaicCover = MosaicUtils.getBlurMosaic(mosaicBitmap)
            }
        }
        mLastBitmapId = bitmapId
    }

    fun setupForMosaicView(mosaicBitmap: Bitmap) {
        mBlurMosaicCover = MosaicUtils.getBlurMosaic(mosaicBitmap)
        mGridMosaicCover = MosaicUtils.getGridMosaic(mosaicBitmap)
    }

}