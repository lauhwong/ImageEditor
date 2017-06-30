package com.miracle.view.imageeditor.layer

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.support.v4.util.ArrayMap
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import com.miracle.view.imageeditor.bean.EditorCacheData
import com.miracle.view.imageeditor.bean.LayerEditResult
import com.miracle.view.imageeditor.bean.SaveStateMarker
import com.miracle.view.imageeditor.recycleBitmap
import com.miracle.view.imageeditor.setInt

/**
 * Created by lxw
 */
abstract class BaseLayerView<T : SaveStateMarker> : View, LayerTransformer
        , OnPhotoRectUpdateListener, LayerCacheNode {
    /*support matrix for drawing layerView*/
    protected open val drawMatrix: Matrix
        get() {
            val matrix = Matrix()
            matrix.set(supportMatrix)
            matrix.postConcat(rootLayerMatrix)
            return matrix
        }
    protected val supportMatrix = Matrix()
    protected val rootLayerMatrix = Matrix()
    protected val validateRect = RectF()
    /*support drawing*/
    protected var displayBitmap: Bitmap? = null
    protected var displayCanvas: Canvas? = null
    /*saveState Info*/
    protected var saveStateMap = ArrayMap<String, T>()
    /*gesture*/
    protected lateinit var gestureDetector: CustomGestureDetector
    protected val adInterpolator = AccelerateDecelerateInterpolator()
    /*paint*/
    protected lateinit var maskPaint: Paint
    /*operation*/
    open var isLayerInEditMode = false
    protected var unitMatrix: Matrix = Matrix()
    protected var viewIsLayout = false

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context)
    }

    @TargetApi(21)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView(context)
    }

    protected fun initView(context: Context) {
        gestureDetector = CustomGestureDetector(context, this)
        //maskPaint
        maskPaint = Paint()
        maskPaint.style = Paint.Style.FILL
        maskPaint.isAntiAlias = true
        maskPaint.color = Color.BLACK
        initSupportView(context)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleBitmap(displayBitmap)
        displayCanvas = null
    }

    override fun onDraw(canvas: Canvas) {
//        displayCanvas?.let {
//            drawMask(it)
//        }
        //drawDisplay
        displayBitmap?.let {
            if (clipRect()) {
                canvas.save()
                canvas.clipRect(validateRect)
                canvas.drawBitmap(it, drawMatrix, null)
                canvas.restore()
            } else {
                canvas.drawBitmap(it, drawMatrix, null)
            }
        }
        //drawExtra
        canvas.save()
        canvas.matrix = drawMatrix
        drawMask(canvas)
        canvas.matrix = unitMatrix
        canvas.restore()
    }

    open fun clipRect() = true

    open fun drawMask(canvas: Canvas) {
//        val layerRect = RectF(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat())
//        val diffs = Utils.diffRect(layerRect, validateRect)
//        for (rect in diffs) {
//            canvas.drawRect(Utils.mapInvertMatrixRect(drawMatrix, rect), maskPaint)
//        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (validateRect.isEmpty) {
            validateRect.setInt(left, top, right, bottom)
        }
        viewIsLayout = true
    }

    override fun onPhotoRectUpdate(rect: RectF, matrix: Matrix) {
        validateRect.set(rect)
        rootLayerMatrix.set(matrix)
        redrawOnPhotoRectUpdate()
    }

    protected fun genDisplayCanvas() {
        displayBitmap ?: let {
            displayBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
            displayCanvas = Canvas(displayBitmap)
        }
    }

    override fun resetEditorSupportMatrix(matrix: Matrix) {
        supportMatrix.set(matrix)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isLayerInEditMode) {
            return checkInterceptedOnTouchEvent(event) && gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    override fun onDrag(dx: Float, dy: Float, x: Float, y: Float, rootLayer: Boolean) {

    }

    override fun onScale(scaleFactor: Float, focusX: Float, focusY: Float, rootLayer: Boolean) {
    }

    /*OverBound translate dx and dy */
    protected inner class OverBoundRunnable(val dx: Float, val dy: Float) : Runnable {
        val mStartTime = System.currentTimeMillis()
        val mZoomDuration = 300
        var mLastDiffX = 0f
        var mLastDiffY = 0f

        override fun run() {
            val t = interpolate()
            val ddx = t * dx - mLastDiffX
            val ddy = t * dy - mLastDiffY
            onDrag(-ddx, -ddy, -1f, -1f, false)
            mLastDiffX = t * dx
            mLastDiffY = t * dy
            if (t < 1f) {
                ViewCompat.postOnAnimation(this@BaseLayerView, this)
            }
        }

        private fun interpolate(): Float {
            var t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration
            t = Math.min(1f, t)
            t = adInterpolator.getInterpolation(t)
            return t
        }

    }

    open fun checkInterceptedOnTouchEvent(event: MotionEvent): Boolean {
        return true
    }

    open fun onStartCompose() {

    }

    open fun redrawOnPhotoRectUpdate() {
        invalidate()
    }

    fun redrawAllCache() {
        if (!saveStateMap.isEmpty) {
            genDisplayCanvas()
        }
        displayCanvas?.let {
            it.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            drawAllCachedState(it)
        }
        postInvalidate()
    }

    abstract fun drawAllCachedState(canvas: Canvas)

    open fun initSupportView(context: Context) {

    }

    open fun revoke() {

    }

    open fun getEditorResult() = LayerEditResult(supportMatrix, displayBitmap)

    //cache layer data.
    override fun saveLayerData(output: MutableMap<String, EditorCacheData>) {
        output.put(getLayerTag(), EditorCacheData(ArrayMap<String, T>(saveStateMap)))
    }

    override fun restoreLayerData(input: MutableMap<String, EditorCacheData>) {
        val lastCache = input[getLayerTag()]
        lastCache?.let {
            val restore = lastCache.layerCache as ArrayMap<String, T>
            for (key in restore.keys) {
                val value = restore[key]
                value?.let {
                    saveStateMap.put(key, it.deepCopy() as T)
                }
            }
            if (viewIsLayout) {
                redrawAllCache()
            } else {
                addOnLayoutChangeListener(OnLayerLayoutListener())
            }
        }

    }


    inner class OnLayerLayoutListener : OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            redrawAllCache()
            removeOnLayoutChangeListener(this)
        }

    }
}