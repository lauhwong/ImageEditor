package com.miracle.view.imageeditor.bean

import android.graphics.*
import com.miracle.view.imageeditor.Utils
import com.miracle.view.imageeditor.layer.Sticker
import com.miracle.view.imageeditor.recycleBitmap
import com.miracle.view.imageeditor.view.MosaicMode
import java.io.Serializable

/**
 * Created by lxw
 */
interface SharableData : Serializable

data class InputTextData(val id: String?, val text: String?, val color: Int?) : SharableData

data class InputStickerData(val sticker: Sticker, val stickerIndex: Int) : SharableData

data class LayerEditResult(val supportMatrix: Matrix, val bitmap: Bitmap?) : SharableData

data class EditorSetup(val originalPath: String?, val editorPath: String?, val editor2SavedPath: String) : SharableData

data class EditorResult(val originalPath: String?, val editorPath: String?, val editor2SavedPath: String, val editStatus: Boolean) : SharableData

data class EditorCacheData(val layerCache: Map<String, SaveStateMarker>) : SharableData
/*details*/
interface FuncDetailsMarker

data class ScrawlDetails(val color: Int) : FuncDetailsMarker

data class MosaicDetails(val mosaicMode: MosaicMode) : FuncDetailsMarker

/*SaveStateMarker*/
abstract class SaveStateMarker {
    var id = Utils.randomId()
    override fun equals(other: Any?): Boolean {
        if (other is SaveStateMarker) {
            return id == (other.id)
        }
        return false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    open fun reset() {

    }

    open fun deepCopy(): SaveStateMarker {
        return this
    }
}

data class CropSaveState(var originalBitmap: Bitmap, var originalDisplayRectF: RectF, val originalMatrix: Matrix, var supportMatrix: Matrix, var cropRect: RectF)
    : SaveStateMarker() {
    var cropBitmap: Bitmap? = null
    var cropFitCenterMatrix: Matrix = Matrix()
    override fun reset() {
        recycleBitmap(originalBitmap)
        recycleBitmap(cropBitmap)
        cropFitCenterMatrix.reset()
    }

    override fun deepCopy(): SaveStateMarker {
        val state = CropSaveState(originalBitmap, RectF(originalDisplayRectF), Matrix(originalMatrix), Matrix(supportMatrix), RectF(cropRect))
        state.id = this.id
        return state
    }
}

data class ScrawlSaveState(var paint: Paint, var path: Path) : SaveStateMarker() {
    override fun deepCopy(): SaveStateMarker {
        val state = ScrawlSaveState(Utils.copyPaint(paint), path)
        state.id = this.id
        return state
    }
}

abstract class PastingSaveStateMarker(val initDisplayRect: RectF, val displayMatrix: Matrix) : SaveStateMarker() {
    //for rebound.
    var initEventDisplayMatrix = Matrix()

}

data class TextPastingSaveState(val text: String, val textColor: Int, val initTextRect: RectF, private val initDisplay: RectF, private val display: Matrix) : PastingSaveStateMarker(initDisplay, display) {
    override fun deepCopy(): SaveStateMarker {
        val state = TextPastingSaveState(text, textColor, RectF(initTextRect), RectF(initDisplay), Matrix(display))
        state.id = this.id
        return state
    }

}

data class StickerSaveState(val sticker: Sticker, val stickerIndex: Int, private val initDisplay: RectF, private val display: Matrix) : PastingSaveStateMarker(initDisplay, display) {
    override fun deepCopy(): SaveStateMarker {
        val state = StickerSaveState(sticker, stickerIndex, RectF(initDisplay), Matrix(display))
        state.id = this.id
        return state
    }
}

data class MosaicSaveState(var mode: MosaicMode, var path: Path) : SaveStateMarker() {
    override fun deepCopy(): SaveStateMarker {
        return super.deepCopy()
    }
}