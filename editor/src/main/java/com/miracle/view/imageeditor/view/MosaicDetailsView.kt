package com.miracle.view.imageeditor.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.miracle.view.imageeditor.R
import com.miracle.view.imageeditor.Utils

/**
 * ## UI element show mosaic details view
 *
 * Created by lxw
 */
class MosaicDetailsView(ctx: Context, onMosaicChangeListener: OnMosaicChangeListener?) : FrameLayout(ctx) {
    constructor(ctx: Context) : this(ctx, null)

    var onMosaicChangeListener: OnMosaicChangeListener? = null
    var onRevokeListener: OnRevokeListener? = null

    init {
        this.onMosaicChangeListener = onMosaicChangeListener
        LayoutInflater.from(ctx).inflate(R.layout.mosaic_func_details, this, true)
        val rootFunc = findViewById(R.id.llMosaicDetails) as LinearLayout
        val values = MosaicMode.values()
        for (index in 0 until values.size) {
            val mode = values[index]
            if (mode.getModeBgResource() <= 0) {
                continue
            }
            val item = LayoutInflater.from(context).inflate(R.layout.mosaic_item_func_details, rootFunc, false)
            val ivFuncDesc = item.findViewById(R.id.ivMosaicDesc) as ImageView
            ivFuncDesc.setImageResource(mode.getModeBgResource())
            item.tag = mode
            rootFunc.addView(item)
            item.setOnClickListener {
                onMosaicClick(mode, index, item, rootFunc)
            }
            if (index == 0) {
                item.isSelected = true
                onMosaicClick(mode, 0, item, rootFunc)
            }
        }
        findViewById(R.id.ivRevoke).setOnClickListener {
            onRevokeListener?.revoke(EditorMode.MosaicMode)
        }
    }

    private fun onMosaicClick(mosaicMode: MosaicMode, position: Int, clickView: View, rootView: ViewGroup) {
        Utils.changeSelectedStatus(rootView, position)
        onMosaicChangeListener?.onChange(mosaicMode)
    }

    interface OnMosaicChangeListener {
        fun onChange(mosaicMode: MosaicMode)
    }

}