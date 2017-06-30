package com.miracle.imageeditor

import android.app.Application
import com.miracle.view.imageeditor.logE1

/**
 * Created by lxw
 */
class ExampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            logE1(e)
            System.exit(0)
            android.os.Process.killProcess(android.os.Process.myPid())

        }
    }
}