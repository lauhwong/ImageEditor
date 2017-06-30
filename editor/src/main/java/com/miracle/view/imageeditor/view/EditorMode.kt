package com.miracle.view.imageeditor.view

import com.miracle.view.imageeditor.R

/**
 * Created by lxw
 */
enum class EditorMode {
    //    ZoomMode {
//        override fun getModeBgResource() = -1
//    },
    ScrawlMode {
        override fun canPersistMode() = true

        override fun onHandle(selected: Boolean, handler: EditorModeHandler) = handler.handleScrawlMode(selected)

        override fun getModeBgResource() = R.drawable.selector_edit_image_pen_tool
    },
    StickerMode {
        override fun canPersistMode() = false

        override fun onHandle(selected: Boolean, handler: EditorModeHandler) = handler.handleStickerMode(selected)

        override fun getModeBgResource() = R.drawable.selector_edit_image_emotion_tool
    },
    TextPastingMode {
        override fun canPersistMode() = false

        override fun onHandle(selected: Boolean, handler: EditorModeHandler) = handler.handleTextPastingMode(selected)

        override fun getModeBgResource() = R.drawable.selector_edit_image_text_tool
    },
    MosaicMode {
        override fun canPersistMode() = true

        override fun onHandle(selected: Boolean, handler: EditorModeHandler) = handler.handleMosaicMode(selected)

        override fun getModeBgResource() = R.drawable.selector_edit_image_mosaic_tool
    },
    CropMode {
        override fun canPersistMode() = false

        override fun onHandle(selected: Boolean, handler: EditorModeHandler) = handler.handleCropMode(selected)

        override fun getModeBgResource() = R.drawable.selector_edit_image_crop_tool

    };
//    AllEditMode {
//        override fun getModeBgResource() = -1
//    };

    abstract fun getModeBgResource(): Int

    abstract fun onHandle(selected: Boolean, handler: EditorModeHandler)

    abstract fun canPersistMode(): Boolean
}