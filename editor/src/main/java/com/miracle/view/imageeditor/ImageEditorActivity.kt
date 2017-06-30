package com.miracle.view.imageeditor

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.miracle.view.imageeditor.bean.CropSaveState
import com.miracle.view.imageeditor.bean.EditorCacheData
import com.miracle.view.imageeditor.bean.EditorResult
import com.miracle.view.imageeditor.bean.EditorSetup
import com.miracle.view.imageeditor.layer.BaseLayerView
import com.miracle.view.imageeditor.layer.LayerCacheNode
import com.miracle.view.imageeditor.layer.LayerComposite
import com.miracle.view.imageeditor.layer.RootEditorDelegate
import com.miracle.view.imageeditor.view.*
import kotlinx.android.synthetic.main.action_bar_editor.*
import kotlinx.android.synthetic.main.activity_image_editor.*
import java.io.File
import java.util.concurrent.Executors

/**
 * Created by lxw
 */
class ImageEditorActivity : AppCompatActivity(), LayerViewProvider {
    private lateinit var mRootEditorDelegate: RootEditorDelegate
    private lateinit var mFuncAndActionBarAnimHelper: FuncAndActionBarAnimHelper
    private lateinit var mFuncHelper: FuncHelper
    private lateinit var mCropHelper: CropHelper
    private lateinit var mEditorSetup: EditorSetup
    private lateinit var mEditorId: String
    private lateinit var mEditorPath: String

    companion object {
        private val intentKey = "editorSetup"
        private val RESULT_OK_CODE = Activity.RESULT_OK
        private val RESULT_CANCEL_CODE = Activity.RESULT_CANCELED

        fun intent(context: Context, editorSetup: EditorSetup): Intent {
            val intent = Intent(context, ImageEditorActivity::class.java)
            intent.putExtra(intentKey, editorSetup)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        //window flag.
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_image_editor)
        // reset placeHolder height...
        viewPlaceHolder.layoutParams.height = Utils.getStatusBarHeight(this)
        initData()
        initView()
        initActionBarListener()
        Utils.showStatusBar(this)

    }

    private fun initData() {
        val intent = intent
        intent ?: let {
            finish()
            return
        }
        val editorSetup = intent.getSerializableExtra(intentKey) as? EditorSetup
        editorSetup ?: let {
            logD1("editorSetup=null")
            finish()
            return
        }
        mEditorSetup = editorSetup
        val op = mEditorSetup.originalPath
        val ep = mEditorSetup.editorPath
        if (op == null && ep == null) {
            logD1("originalPath,editorPath are both null")
            finish()
            return
        }
    }

    /*init for different kinds of EditorMode usage in java code...<xml code viewStub...>*/
    private fun initView() {
        val functionalModeList = listOf(EditorMode.ScrawlMode, EditorMode.StickerMode, EditorMode.TextPastingMode, EditorMode.MosaicMode, EditorMode.CropMode)
        val toolFragment = FuncModeToolFragment.StaticDelegate.newInstance(functionalModeList)
        supportFragmentManager.beginTransaction().add(com.miracle.view.imageeditor.R.id.flFunc, toolFragment).commit()
        mRootEditorDelegate = RootEditorDelegate(layerImageView, layerEditorParent)
        mFuncAndActionBarAnimHelper = FuncAndActionBarAnimHelper(layerActionView, editorBar, flFunc, this)
        mCropHelper = CropHelper(layerCropView, CropDetailsView(layoutCropDetails), this)
        mFuncHelper = FuncHelper(this, DragToDeleteView(layoutDragDelete))
        toolFragment.addFuncModeListener(mFuncHelper)
        toolFragment.addFuncModeDetailsListener(mFuncHelper)
        toolFragment.addOnRevokeListener(mFuncHelper)
        //restore
        restoreData()
    }

    private fun restoreData() {
        val op = mEditorSetup.originalPath
        val ep = mEditorSetup.editorPath
        var cacheData: MutableMap<String, EditorCacheData>? = null
        if (op != null) {
            mEditorId = op + (ep ?: "")
            cacheData = LayerCache.getCacheDataById(mEditorId)
        }
        if ((cacheData == null || cacheData.isEmpty()) && ep != null) {
            mEditorId = (op ?: "") + ep
            cacheData = LayerCache.getCacheDataById(mEditorId)
            //set up layer cache with ep...
            mEditorPath = ep
        } else {
            //op has extra data or not
            mEditorPath = op!!
        }
        logD1("edtiorId=${mEditorId},editorPath=${mEditorPath},init cached data=$cacheData")
        if (!File(mEditorPath).exists()) {
            toastShort("文件不存在！")
            finish()
            return
        }
        val imageBitmap = EditorCompressUtils.getImageBitmap(mEditorPath)
        mCropHelper.restoreLayerData(cacheData!!)
        val cropBitmap = mCropHelper.restoreCropData(imageBitmap)
        layerImageView.setImageBitmap(cropBitmap)
        val cropState = mCropHelper.getSavedCropState()
        layerImageView.addOnLayoutChangeListener(LayerImageOnLayoutChangeListener(cropState))
//        Executors.newSingleThreadExecutor().execute {
        val data = cacheData[layerMosaicView.getLayerTag()]
        data?.let {
            layerMosaicView.setupForMosaicView(imageBitmap)
        }
        Utils.callChildren(LayerCacheNode::class.java, layerComposite) {
            it.restoreLayerData(cacheData!!)
        }
        data ?: Executors.newSingleThreadExecutor().execute {
            layerMosaicView.setupForMosaicView(imageBitmap)
        }
        //    }
    }

    /*for initialize mosaic view's matrix*/
    inner class LayerImageOnLayoutChangeListener(val state: CropSaveState?) : android.view.View.OnLayoutChangeListener {
        override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
            val matrix = state?.originalMatrix ?: layerImageView.getBaseLayoutMatrix()
            state?.let {
                mCropHelper.resetEditorSupportMatrix(it)
            }
            layerMosaicView.initializeMatrix = matrix
            layerImageView.removeOnLayoutChangeListener(this)
        }

    }

    private fun initActionBarListener() {
        ivBack.setOnClickListener {
            onImageComposeCancel()
        }
        tvComplete.setOnClickListener {
            imageCompose()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFuncHelper.onActivityResult(requestCode, resultCode, data)
    }

    override fun findLayerByEditorMode(editorMode: EditorMode): View? {
        when (editorMode) {
            EditorMode.ScrawlMode -> return layerScrawlView
            EditorMode.StickerMode -> return layerStickerView
            EditorMode.TextPastingMode -> return layerTextPastingView
            EditorMode.MosaicMode -> return layerMosaicView
            EditorMode.CropMode -> return layerCropView
            else -> return null
        }
    }

    override fun getActivityContext(): Context {
        return this
    }

    override fun onBackPressed() {
        if (!mFuncHelper.onBackPress()) {
            super.onBackPressed()
        }
    }

    override fun getFuncAndActionBarAnimHelper(): FuncAndActionBarAnimHelper = mFuncAndActionBarAnimHelper

    override fun getCropHelper(): CropHelper = mCropHelper

    override fun getRootEditorDelegate(): RootEditorDelegate = mRootEditorDelegate

    override fun getLayerCompositeView(): LayerComposite = layerComposite

    override fun getSetupEditorId() = mEditorId

    override fun getResultEditorId() = mEditorPath + mEditorSetup.editor2SavedPath

    private var imageComposeTask: ImageComposeTask? = null

    private fun imageCompose() {
        val path = mEditorSetup.editor2SavedPath
        val parentFile = File(path).parentFile
        parentFile?.mkdirs()
        imageComposeTask?.cancel(true)
        imageComposeTask = ImageComposeTask(this)
        imageComposeTask?.execute(path)
    }

    private fun onImageComposeCancel() {
        supportRecycle()
        val intent = Intent()
        setResult(RESULT_CANCEL_CODE, intent)
        finish()
    }

    private fun onImageComposeResult(editStatus: Boolean) {
        supportRecycle()
        val intent = Intent()
        val resultData = EditorResult(mEditorPath, mEditorSetup.editorPath, mEditorSetup.editor2SavedPath, editStatus)
        intent.putExtra(RESULT_OK_CODE.toString(), resultData)
        setResult(RESULT_OK_CODE, intent)
        finish()
    }

    private fun supportRecycle() {
        recycleBitmap(mRootEditorDelegate.getDisplayBitmap())
        mCropHelper.getSavedCropState()?.reset()
    }

    inner class ImageComposeTask(private val mProvider: LayerViewProvider) : AsyncTask<String, Void, Boolean>() {
        private var mDialog = ProgressDialog(mProvider.getActivityContext())
        private var mPath: String? = null
        private val layerComposite = mProvider.getLayerCompositeView()
        private val mEditorId = mProvider.getResultEditorId()

        init {
            mDialog.isIndeterminate = true
            mDialog.setCancelable(false)
            mDialog.setCanceledOnTouchOutside(false)
            mDialog.setMessage(Utils.getResourceString(mProvider.getActivityContext(), R.string.editor_handle))

        }

        override fun doInBackground(vararg params: String): Boolean {
            mPath = params[0]
            val cropState = mProvider.getCropHelper().getSavedCropState()
            val delegate = mProvider.getRootEditorDelegate()
            val rootBit = cropState?.cropBitmap ?: delegate.getDisplayBitmap()
            val compose = Bitmap.createBitmap(layerComposite.width, layerComposite.height, Bitmap.Config.RGB_565)
            val canvas = Canvas(compose)
            canvas.drawBitmap(rootBit, delegate.getBaseLayoutMatrix(), null)
            Utils.callChildren(BaseLayerView::class.java, layerComposite) {
                val (supportMatrix, bitmap) = it.getEditorResult()
                bitmap?.let {
                    val matrix = Matrix()
                    matrix.set(supportMatrix)
                    canvas.drawBitmap(bitmap, matrix, null)
                }
            }
            val rect = delegate.getOriginalRect()!!
            val result = Bitmap.createBitmap(compose, rect.left.toInt(), rect.top.toInt(), rect.width().toInt(), rect.height().toInt())
            result.compress(Bitmap.CompressFormat.JPEG, 85, File(mPath).outputStream())
            recycleBitmap(compose)
            recycleBitmap(result)
            recycleBitmap(rootBit)
            //Save cached data.
            val cacheData = LayerCache.getCacheDataById(mEditorId)
            Utils.callChildren(BaseLayerView::class.java, layerComposite) {
                it.saveLayerData(cacheData)
            }
            mProvider.getCropHelper().saveLayerData(cacheData)
            return true
        }

        override fun onPreExecute() {
            super.onPreExecute()
//            Utils.callChildren(BaseLayerView::class.java, layerComposite) {
//                it.onStartCompose()
//            }
            mDialog.show()
        }

        override fun onCancelled() {
            super.onCancelled()
            mDialog.dismiss()
        }

        override fun onPostExecute(result: Boolean) {
            super.onPostExecute(result)
            logD1("ImageComposeTask:result=$mPath")
            //this@ImageEditorActivity.toastShort( "合成图片完成")
            mDialog.dismiss()
            onImageComposeResult(result)
        }

    }

}
