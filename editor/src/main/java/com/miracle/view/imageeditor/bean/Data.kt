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
/**
 * serializable data mark
 */
interface SharableData : Serializable

data class InputTextData(val id: String?, val text: String?, val color: Int?) : SharableData

data class InputStickerData(val sticker: Sticker, val stickerIndex: Int) : SharableData

data class LayerEditResult(val supportMatrix: Matrix, val bitmap: Bitmap?) : SharableData
/**
 * image editor setup data
 * @property originalPath the editorPath's originalPath
 * @property editorPath the image you wanted to edit .
 * @property editor2SavedPath in which path edit image to save.
 */
data class EditorSetup(val originalPath: String?, val editorPath: String?, val editor2SavedPath: String) : SharableData

/**
 * image editor result data.
 * @property editStatus true represent user edit this image false otherwise
 * @property originalPath not equals editorSetup's originalPath ,it shows really edit image called really editImagePath
 * @property editorPath equals editorSetup's editorPath
 * @property editor2SavedPath equals editorSetup's editor2SavedPath
 */
data class EditorResult(val originalPath: String?, val editorPath: String?, val editor2SavedPath: String, val editStatus: Boolean) : SharableData

/**
 * inner editor cache for reEdit and undo or just say recover
 */
data class EditorCacheData(val layerCache: Map<String, SaveStateMarker>) : SharableData

/**
 * mark of image editor's detail function panel's share data structure
 */
interface FuncDetailsMarker

data class ScrawlDetails(val color: Int) : FuncDetailsMarker

data class MosaicDetails(val mosaicMode: MosaicMode) : FuncDetailsMarker

/**
 * it's important for each painting layer,each layer holds it's special dataStructure
 * 1.redraw all view's cache
 * 2.save view's painting data for restore simply called restore info
 */
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

/**
 * Crop func's holding data structure
 * @property originalBitmap bitmap to edit
 * @property originalDisplayRectF the rectF set by editor bitmap and matrix(@originalMatrix map bitmap to imageview)
 * @property originalMatrix    bitmap to fit imageView generated this matrix
 * @property supportMatrix rootView's matrix operation
 * @property cropRect display window's crop rect
 * @property originalCropRation  original view->crop display view ration
 */
data class CropSaveState(var originalBitmap: Bitmap, var originalDisplayRectF: RectF, val originalMatrix: Matrix,
                         var supportMatrix: Matrix,
                         var cropRect: RectF, val originalCropRation: Float)
    : SaveStateMarker() {
    var cropBitmap: Bitmap? = null
    var cropFitCenterMatrix: Matrix = Matrix()
    override fun reset() {
        recycleBitmap(originalBitmap)
        recycleBitmap(cropBitmap)
        cropFitCenterMatrix.reset()
    }

    override fun deepCopy(): SaveStateMarker {
        val state = CropSaveState(originalBitmap, RectF(originalDisplayRectF), Matrix(originalMatrix), Matrix(supportMatrix), RectF(cropRect), originalCropRation)
        state.id = this.id
        return state
    }
}

/**
 * Scrawl func's holding data structure
 */
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

/**
 * TextPasting func's holding data structure
 */
data class TextPastingSaveState(val text: String, val textColor: Int, val initTextRect: RectF, private val initDisplay: RectF, private val display: Matrix) : PastingSaveStateMarker(initDisplay, display) {
    override fun deepCopy(): SaveStateMarker {
        val state = TextPastingSaveState(text, textColor, RectF(initTextRect), RectF(initDisplay), Matrix(display))
        state.id = this.id
        return state
    }

}

/**
 *  Sticker func's holding data structure
 */
data class StickerSaveState(val sticker: Sticker, val stickerIndex: Int, private val initDisplay: RectF, private val display: Matrix) : PastingSaveStateMarker(initDisplay, display) {
    override fun deepCopy(): SaveStateMarker {
        val state = StickerSaveState(sticker, stickerIndex, RectF(initDisplay), Matrix(display))
        state.id = this.id
        return state
    }
}

/**
 * Mosaic func's holding data structure
 */
data class MosaicSaveState(var mode: MosaicMode, var path: Path) : SaveStateMarker() {
    override fun deepCopy(): SaveStateMarker {
        return super.deepCopy()
    }
}