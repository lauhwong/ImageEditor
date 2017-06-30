package com.miracle.view.imageeditor.view

/**
 * Created by lxw
 */
interface EditorModeHandler {

    fun handleScrawlMode(selected: Boolean)

    fun handleStickerMode(selected: Boolean)

    fun handleTextPastingMode(selected: Boolean)

    fun handleMosaicMode(selected: Boolean)

    fun handleCropMode(selected: Boolean)

}