package com.salton123.hook.dex

import android.os.Build
import android.util.Log
import java.io.File

/**
 * User: wujinsheng1@yy.com
 * Date: 2020/5/9 15:24
 * ModifyTime: 15:24
 * Description:
 */
object DumpDexManager {
    private var sdkInit: Int = 0
    private val TAG = "DumpDexManager"

    init {
        sdkInit = Build.VERSION.SDK_INT
    }

    private fun supportNativeHook(): Boolean {
        return sdkInit == 23 || sdkInit == 24 || sdkInit == 25 || sdkInit == 26 || sdkInit == 27
    }

    private fun hightSDKVersion(): Boolean {
        return sdkInit == 26 || sdkInit == 27 || sdkInit == 28 || sdkInit == 29
    }

    fun dumpAction(packageName: String, classLoader: ClassLoader) {
        val path = "/data/data/$packageName/dump"
        val parent = File(path)
        if (!parent.exists() || !parent.isDirectory) {
            parent.mkdirs()
        }
        Log.i(TAG, "sdk version:" + Build.VERSION.SDK_INT)
        if (hightSDKVersion() || supportNativeHook()) {
            NativeDump.dump(packageName)
            Log.i(TAG, "NativeDump")
        } else {
            JavaDump.dump(packageName, classLoader)
            Log.i(TAG, "JavaDump")
        }
    }
}