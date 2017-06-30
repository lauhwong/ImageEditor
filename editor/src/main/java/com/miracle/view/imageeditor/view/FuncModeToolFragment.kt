package com.miracle.view.imageeditor.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.miracle.view.imageeditor.R
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.bean.MosaicDetails
import com.miracle.view.imageeditor.bean.ScrawlDetails
import java.io.Serializable
import java.util.*

/**
 * Created by lxw
 */
class FuncModeToolFragment : Fragment(), EditorModeHandler {
    private lateinit var mFuncModePanel: LinearLayout
    private lateinit var mFuncDetailsPanel: FrameLayout
    private var mSelectedMode: EditorMode? = null
    private val mFuncModeListeners = ArrayList<FuncModeListener>()
    private val mFuncDetailsListeners = ArrayList<FuncDetailsListener>()
    private val mOnRevokeListeners = ArrayList<OnRevokeListener>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_func, container, false)
        mFuncModePanel = root.findViewById(R.id.llFuncMode) as LinearLayout
        mFuncDetailsPanel = root.findViewById(R.id.flFuncDetails) as FrameLayout
        return root
    }

    //newInstance....
    companion object StaticDelegate {
        fun newInstance(mode: List<EditorMode>): FuncModeToolFragment {
            val result = FuncModeToolFragment()
            val bundle = Bundle()
            bundle.putSerializable("mode", mode as Serializable)
            result.arguments = bundle
            return result
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val modeList = arguments?.getSerializable("mode") as List<EditorMode>
        for (index in modeList.indices) {
            val mode = modeList[index]
            if (mode.getModeBgResource() <= 0) {
                continue
            }
            val item = LayoutInflater.from(context).inflate(R.layout.item_func_mode, mFuncModePanel, false)
            val ivFuncDesc = item.findViewById(R.id.ivFuncDesc) as ImageView
            ivFuncDesc.setImageResource(mode.getModeBgResource())
            item.tag = mode
            mFuncModePanel.addView(item)
            item.setOnClickListener {
                onFuncModeClick(mode, index, item)
            }
        }
    }

    private fun onFuncModeClick(editorMode: EditorMode, position: Int, clickView: View) {
        if (mSelectedMode == editorMode) {
            editorMode.onHandle(false, this)
            Utils.changeSelectedStatus(mFuncModePanel, -1)
            callback2Listeners(mFuncModeListeners) {
                it.onFuncModeUnselected(editorMode)
            }
            mSelectedMode = null
        } else {
            editorMode.onHandle(true, this)
            if (editorMode.canPersistMode()) {
                Utils.changeSelectedStatus(mFuncModePanel, position)
                mSelectedMode = editorMode
            }
            callback2Listeners(mFuncModeListeners) {
                it.onFuncModeSelected(editorMode)
            }
        }

    }

    private fun <T> callback2Listeners(listeners: List<T>, callback: (T) -> Unit) {
        listeners.forEach {
            callback(it)
        }
    }

    fun addFuncModeListener(funcModeListener: FuncModeListener) {
        mFuncModeListeners.add(funcModeListener)
    }

    fun addFuncModeDetailsListener(funcDetailsListener: FuncDetailsListener) {
        mFuncDetailsListeners.add(funcDetailsListener)
    }

    fun addOnRevokeListener(onRevokeListener: OnRevokeListener) {
        mOnRevokeListeners.add(onRevokeListener)
    }

    private fun showOrHideDetails(show: Boolean) {
        mFuncDetailsPanel.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    /*handle mode*/
    override fun handleScrawlMode(selected: Boolean) {
        if (selected) {
            val scrawlDetails = ScrawlDetailsView(context);
            scrawlDetails.onColorChangeListener = object : ColorSeekBar.OnColorChangeListener {
                override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                    callback2Listeners(mFuncDetailsListeners) {
                        it.onReceiveDetails(EditorMode.ScrawlMode, ScrawlDetails(color))
                    }
                }
            }
            scrawlDetails.onRevokeListener = object : OnRevokeListener {
                override fun revoke(editorMode: EditorMode) {
                    callback2Listeners(mOnRevokeListeners) {
                        it.revoke(EditorMode.ScrawlMode)
                    }
                }
            }
            showOrHideDetailsView(EditorMode.ScrawlMode, scrawlDetails)
        }
        showOrHideDetails(selected)
    }

    override fun handleStickerMode(selected: Boolean) {

    }

    override fun handleTextPastingMode(selected: Boolean) {

    }

    override fun handleMosaicMode(selected: Boolean) {
        if (selected) {
            val listener = object : MosaicDetailsView.OnMosaicChangeListener {
                override fun onChange(mosaicMode: MosaicMode) {
                    callback2Listeners(mFuncDetailsListeners) {
                        it.onReceiveDetails(EditorMode.MosaicMode, MosaicDetails(mosaicMode))
                    }
                }
            }
            val mosaicDetails = MosaicDetailsView(context,listener)
            mosaicDetails.onRevokeListener = object : OnRevokeListener {
                override fun revoke(editorMode: EditorMode) {
                    callback2Listeners(mOnRevokeListeners) {
                        it.revoke(EditorMode.MosaicMode)
                    }
                }
            }
            showOrHideDetailsView(EditorMode.MosaicMode, mosaicDetails)
        }
        showOrHideDetails(selected)
    }

    override fun handleCropMode(selected: Boolean) {

    }

    private fun showOrHideDetailsView(editorMode: EditorMode, view: View) {
        val count = mFuncDetailsPanel.childCount
        var toRemoveView: View? = null
        var handled = false
        if (count > 0) {
            val topView = mFuncDetailsPanel.getChildAt(count - 1)
            val tag = topView.tag
            if (tag != editorMode) {
                toRemoveView = topView
            } else {
                handled = true
            }
        }
        if (!handled) {
            mFuncDetailsPanel.addView(view)
            toRemoveView?.let {
                mFuncDetailsPanel.removeView(toRemoveView)
            }
        }
    }

}