package com.miracle.view.imageeditor.view

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.miracle.view.imageeditor.EditorTextInputActivity
import com.miracle.view.imageeditor.LayerViewProvider
import com.miracle.view.imageeditor.bean.*
import com.miracle.view.imageeditor.layer.*
import com.miracle.view.imageeditor.logD1

/**
 * Created by lxw
 */
class FuncHelper(private val mProvider: LayerViewProvider, private val mDragToDeleteView: DragToDeleteView) : FuncModeListener, FuncDetailsListener, OnRevokeListener {
    private val mTextInputResultCode = 301
    private val mContext = mProvider.getActivityContext()
    private val mFuncAndActionBarAnimHelper = mProvider.getFuncAndActionBarAnimHelper()
    private val mCropHelper = mProvider.getCropHelper()
    private var mStickerDetailsView: StickerDetailsView? = null
    private var mStickerDetailsShowing = false

    init {
        mDragToDeleteView.onLayoutRectChange = {
            _, rect ->
            getView<TextPastingView>(EditorMode.TextPastingMode)?.dragViewRect = rect
            getView<StickerView>(EditorMode.StickerMode)?.dragViewRect = rect
        }
        getView<TextPastingView>(EditorMode.TextPastingMode)?.let {
            setUpPastingView(it)
            it.onLayerViewDoubleClick = {
                _, sharableData ->
                go2InputView(sharableData as InputTextData)
            }
        }
        getView<StickerView>(EditorMode.StickerMode)?.let {
            setUpPastingView(it)
        }
    }

    private fun setUpPastingView(layer: BasePastingLayerView<*>) {
        layer.showOrHideDragCallback = {
            showOrHideDrag2Delete(it)
        }
        layer.setOrNotDragCallback = {
            mDragToDeleteView.setDrag2DeleteText(it)
        }
    }

    private inline fun <reified T : View> getView(editorMode: EditorMode) = mProvider.findLayerByEditorMode(editorMode) as? T


    private fun setScrawlDetails(details: ScrawlDetails) {
        getView<ScrawlView>(EditorMode.ScrawlMode)?.setPaintColor(details.color)
    }

    private fun setMosaicDetails(details: MosaicDetails) {
        getView<MosaicView>(EditorMode.MosaicMode)?.setMosaicMode(details.mosaicMode, null)
    }

    private fun showOrHideDrag2Delete(show: Boolean) {
        mFuncAndActionBarAnimHelper.showOrHideFuncAndBarView(!show)
        mDragToDeleteView.showOrHide(show)
    }

    private fun go2InputView(prepareData: InputTextData?) {
        mFuncAndActionBarAnimHelper.showOrHideFuncAndBarView(false)
        val intent = EditorTextInputActivity.intent(mContext, prepareData)
        (mContext as Activity).startActivityForResult(intent, mTextInputResultCode)
        mContext.overridePendingTransition(com.miracle.view.imageeditor.R.anim.animation_bottom_to_top, 0)
    }

    private fun resultFromInputView(resultCode: Int, data: Intent?) {
        val result = data?.getSerializableExtra(resultCode.toString()) as? InputTextData
        logD1("resultFromInputView is $result")
        result?.let {
            getView<TextPastingView>(EditorMode.TextPastingMode)?.onTextPastingChanged(it)
        }
        mFuncAndActionBarAnimHelper.showOrHideFuncAndBarView(true)
    }

    private fun enableOrDisableEditorMode(editorMode: EditorMode, enable: Boolean) {
        val view = mProvider.findLayerByEditorMode(editorMode)
        if (view is BaseLayerView<*>) {
            view.isLayerInEditMode = enable
        }
    }

    private fun go2StickerPanel() {
        mFuncAndActionBarAnimHelper.showOrHideFuncAndBarView(false)
        mStickerDetailsView ?: let {
            mStickerDetailsView = StickerDetailsView(mContext)
            mStickerDetailsView!!.onStickerClickListener = object : StickerDetailsView.OnStickerClickResult {
                override fun onResult(stickerData: InputStickerData) {
                    getView<StickerView>(EditorMode.StickerMode)?.onStickerPastingChanged(stickerData)
                    closeStickerPanel()
                }
            }
            mFuncAndActionBarAnimHelper.addFunBarAnimateListener(object : FuncAndActionBarAnimHelper.OnFunBarAnimationListener {
                override fun onFunBarAnimate(show: Boolean) {
                    if (show && mStickerDetailsShowing) {
                        hideStickerPanel()
                    }
                }
            })
        }
        val layoutParam = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)
        ((mContext as Activity).window.decorView as ViewGroup).addView(mStickerDetailsView!!, layoutParam)
        mStickerDetailsShowing = true
    }

    private fun closeStickerPanel() {
        mFuncAndActionBarAnimHelper.showOrHideFuncAndBarView(true)
        hideStickerPanel()
    }

    private fun hideStickerPanel() {
        mStickerDetailsView?.let {
            ((mContext as Activity).window.decorView as ViewGroup).removeView(it)
            mStickerDetailsShowing = false
        }
    }

    override fun onFuncModeSelected(editorMode: EditorMode) {
        when (editorMode) {
            EditorMode.CropMode -> mCropHelper.showCropDetails()
            EditorMode.ScrawlMode -> {
                enableOrDisableEditorMode(EditorMode.ScrawlMode, true)
                enableOrDisableEditorMode(EditorMode.MosaicMode, false)
            }
            EditorMode.TextPastingMode -> go2InputView(null)
            EditorMode.MosaicMode -> {
                enableOrDisableEditorMode(EditorMode.ScrawlMode, false)
                enableOrDisableEditorMode(EditorMode.MosaicMode, true)
            }
            EditorMode.StickerMode -> go2StickerPanel()
        }
    }

    override fun onFuncModeUnselected(editorMode: EditorMode) {
        when (editorMode) {
            EditorMode.ScrawlMode -> enableOrDisableEditorMode(EditorMode.ScrawlMode, false)
            EditorMode.MosaicMode -> enableOrDisableEditorMode(EditorMode.MosaicMode, false)
            else -> logD1("editorMode=$editorMode,Unselected !")
        }
    }

    override fun onReceiveDetails(editorMode: EditorMode, funcDetailsMarker: FuncDetailsMarker) {
        when (editorMode) {
            EditorMode.ScrawlMode -> setScrawlDetails(funcDetailsMarker as ScrawlDetails)
            EditorMode.MosaicMode -> setMosaicDetails(funcDetailsMarker as MosaicDetails)
            else -> logD1("editorMode=$editorMode,onReceiveDetails !")
        }
    }

    override fun revoke(editorMode: EditorMode) {
        val view = mProvider.findLayerByEditorMode(editorMode)
        if (view is BaseLayerView<*>) {
            view.revoke()
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mTextInputResultCode) {
            resultFromInputView(resultCode, data)
        }
    }

    fun onBackPress(): Boolean {
        if (mStickerDetailsShowing) {
            closeStickerPanel()
            return true
        }
        return false
    }

}