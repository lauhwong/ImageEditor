package com.miracle.view.imageeditor

import android.graphics.Bitmap
import android.graphics.BitmapFactory


/**
 * Created by lxw
 * @see https://github.com/Curzibn/Luban/blob/master/library/src/main/java/top/zibin/luban/Engine.java
 */
object EditorCompressUtils {

    private fun computeSize(inputWidth: Int, inputHeight: Int): Int {
        val mSampleSize: Int
        var srcWidth = if (inputWidth % 2 == 1) inputWidth + 1 else inputWidth
        var srcHeight = if (inputHeight % 2 == 1) inputHeight + 1 else inputHeight
        srcWidth = if (srcWidth > srcHeight) srcHeight else srcWidth
        srcHeight = if (srcWidth > srcHeight) srcWidth else srcHeight
        val scale = srcWidth * 1.0 / srcHeight
        if (scale <= 1 && scale > 0.5625) {
            if (srcHeight < 1664) {
                mSampleSize = 1
            } else if (srcHeight in 1666 until 4990) {
                mSampleSize = 2
            } else if (srcHeight in 4990 until 10240) {
                mSampleSize = 4
            } else {
                mSampleSize = if (srcHeight / 1280 == 0) 1 else srcHeight / 1280
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            mSampleSize = if (srcHeight / 1280 == 0) 1 else srcHeight / 1280
        } else {
            mSampleSize = Math.ceil(srcHeight / (1280.0 / scale)).toInt()
        }
        return mSampleSize
    }

    fun getImageBitmap(filePath: String): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(filePath, options)
        val outWidth = options.outWidth
        val outHeight = options.outHeight
        options.inSampleSize = computeSize(outWidth, outHeight)*2
        options.inJustDecodeBounds = false
        logD1("options.inSampleSize=${options.inSampleSize}")
        return BitmapFactory.decodeFile(filePath, options)
    }

}