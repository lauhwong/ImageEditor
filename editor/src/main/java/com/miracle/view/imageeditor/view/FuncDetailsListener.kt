package com.miracle.view.imageeditor.view

import com.miracle.view.imageeditor.bean.FuncDetailsMarker

/**
 * ## UI function details result marker
 *
 * Created by lxw
 */
interface FuncDetailsListener {

    fun onReceiveDetails(editorMode: EditorMode, funcDetailsMarker: FuncDetailsMarker)
}