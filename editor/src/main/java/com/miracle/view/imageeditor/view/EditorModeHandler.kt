package com.miracle.view.imageeditor.view

/**
 * ## Mode handler :like change selected bar
 *
 * Created by lxw
 */
interface EditorModeHandler {

    fun handleScrawlMode(selected: Boolean)

    fun handleStickerMode(selected: Boolean)

    fun handleTextPastingMode(selected: Boolean)

    fun handleMosaicMode(selected: Boolean)

    fun handleCropMode(selected: Boolean)

}