package com.miracle.view.imageeditor.view

import com.miracle.view.imageeditor.R

/**
 * Created by lxw
 */
enum class MosaicMode {
    Grid {
        override fun getModeBgResource() = R.drawable.selector_edit_image_traditional_mosaic
    },
    Blur {
        override fun getModeBgResource() = R.drawable.selector_edit_image_brush_mosaic
    };

    abstract fun getModeBgResource(): Int
}