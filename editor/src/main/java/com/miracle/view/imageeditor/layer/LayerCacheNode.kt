package com.miracle.view.imageeditor.layer

import com.miracle.view.imageeditor.bean.EditorCacheData

/**
 * Created by lxw
 */
interface LayerCacheNode {

    fun getLayerTag(): String = this::class.java.simpleName

    fun restoreLayerData(input: MutableMap<String, EditorCacheData>)

    fun saveLayerData(output: MutableMap<String, EditorCacheData>)
}