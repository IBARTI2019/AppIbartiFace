package com.oesvica.appibartiFace

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.oesvica.appibartiFace.utils.debug

const val NOTIFICATION_ID_EVENT = 1
const val CHANNEL_ID = "ChannelIbartiApp"

fun buildNotification(
    context: Context,
    title: String,
    description: String,
    img: Bitmap? = null,
    channel: String
): Notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    setContentTitle(title)
    setContentText(description)
    //val intent = Intent(Intent.ACTION_VIEW)
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.)
    img?.let { setLargeIcon(it) }
    setSound(soundUri)
    val v = longArrayOf(200, 100)
    setVibrate(v)
    setAutoCancel(true)
}.build()

fun buildNotificationBig(
    context: Context,
    title: String,
    description: String,
    img: Bitmap? = null,
    channel: String
): Notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
    setContentTitle(title)
    setContentText(description)
    //val intent = Intent(Intent.ACTION_VIEW)
    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.)
    img?.let { setStyle(NotificationCompat.BigPictureStyle().bigPicture(it)) }
    setSound(soundUri)
    val v = longArrayOf(200, 100)
    setVibrate(v)
    setAutoCancel(true)
}.build()