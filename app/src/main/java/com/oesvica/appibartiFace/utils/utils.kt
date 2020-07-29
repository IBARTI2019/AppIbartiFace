package com.oesvica.appibartiFace.utils

import android.app.NotificationManager
import android.content.Context
import android.os.Build.VERSION
import android.util.Base64
import android.util.DisplayMetrics
import android.view.WindowManager
import com.oesvica.appibartiFace.data.model.JWTDecoded
import java.io.UnsupportedEncodingException

fun Context.screenWidth(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.widthPixels
}

fun Context.screenHeight(): Int {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val dm = DisplayMetrics()
    windowManager.defaultDisplay.getMetrics(dm)
    return dm.heightPixels
}

fun Context.notificationManager(): NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

fun isMarshmallowOrLater(): Boolean {
    return VERSION.SDK_INT >= 23
}

fun isNougatOrLater(): Boolean {
    return VERSION.SDK_INT >= 24
}

fun isOreoOrLater(): Boolean {
    return VERSION.SDK_INT >= 26
}


fun String.decoded(): JWTDecoded {
    val split = split("\\.".toRegex()).toTypedArray()
    return JWTDecoded(
        getJson(split[0]),
        getJson(split[1])
    )
}

@Throws(UnsupportedEncodingException::class)
private fun getJson(strEncoded: String): String {
    val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
    return String(decodedBytes, Charsets.UTF_8)
}