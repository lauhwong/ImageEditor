package com.miracle.view.imageeditor.view

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.miracle.view.imageeditor.R

/**
 * Created by lxw
 */
class ScrawlDetailsView(ctx: Context) : FrameLayout(ctx) {
    var onRevokeListener: OnRevokeListener? = null
    var onColorChangeListener: ColorSeekBar.OnColorChangeListener? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.scralw_func_details, this, true)
        findViewById(R.id.ivRevoke).setOnClickListener {
            onRevokeListener?.revoke(EditorMode.ScrawlMode)
        }
        val ckb = findViewById(R.id.colorBarScrawl) as ColorSeekBar
        ckb.setOnColorChangeListener(object : ColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(colorBarPosition: Int, alphaBarPosition: Int, color: Int) {
                onColorChangeListener?.onColorChangeListener(colorBarPosition, alphaBarPosition, color)
            }
        })
    }
}