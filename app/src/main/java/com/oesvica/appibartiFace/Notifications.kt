package com.oesvica.appibartiFace

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

const val NOTIFICATION_ID_EVENT = 1
const val CHANNEL_ID = "ChannelIbartiApp"

fun buildNotification(
    context: Context,
    type: String,
    content: String
): Notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    setContentTitle(type)
    setContentText(content)
    val intent = Intent(Intent.ACTION_VIEW)
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    setSound(soundUri)
    val v = longArrayOf(200, 100)
    setVibrate(v)
    setAutoCancel(true)
}.build()