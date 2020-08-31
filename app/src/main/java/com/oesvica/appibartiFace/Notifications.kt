package com.oesvica.appibartiFace

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.oesvica.appibartiFace.ui.personAsistencia.personImage.PersonImgDialogArgs
import java.lang.IllegalStateException

//fun buildNotification(
//    context: Context,
//    title: String,
//    description: String,
//    img: Bitmap? = null,
//    channel: String
//): Notification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
//    setSmallIcon(R.mipmap.ic_launcher)
//    setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//    setContentTitle(title)
//    setContentText(description)
//    //val intent = Intent(Intent.ACTION_VIEW)
//    val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//    //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.)
//    img?.let { setLargeIcon(it) }
//    setSound(soundUri)
//    val v = longArrayOf(200, 100)
//    setVibrate(v)
//    setAutoCancel(true)
//}.build()

data class ChannelData(
    val name: String,
    val description: String,
    val importance: Importance,
    val color: Int,
    val sound: Uri,
    val idDestination: Int? = null
)

enum class Importance(private val priority: Int) {
    MAX(5),
    HIGH(4),
    LOW(3),
    MIN(2);

    @RequiresApi(Build.VERSION_CODES.N)
    fun asChannelPriority(): Int {
        return when (priority) {
            MAX.priority -> NotificationManager.IMPORTANCE_MAX
            HIGH.priority -> NotificationManager.IMPORTANCE_HIGH
            LOW.priority -> NotificationManager.IMPORTANCE_LOW
            MIN.priority -> NotificationManager.IMPORTANCE_MIN
            else -> throw IllegalStateException("")
        }
    }

    fun asNotificationPriority(): Int {
        return when (priority) {
            MAX.priority -> NotificationCompat.PRIORITY_MAX
            HIGH.priority -> NotificationCompat.PRIORITY_HIGH
            LOW.priority -> NotificationCompat.PRIORITY_LOW
            MIN.priority -> NotificationCompat.PRIORITY_MIN
            else -> throw IllegalStateException("")
        }
    }
}

val CHANNELS = mapOf(
    "Aptos" to ChannelData(
        "Aptos",
        "Notificaciones para listado de aptos",
        Importance.HIGH,
        Color.rgb(0, 89, 255),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        R.id.dialog_person_img_aptos
    ),
    "NoAptos" to ChannelData(
        "NoAptos",
        "Notificaciones para listado de no aptos",
        Importance.HIGH,
        Color.rgb(224, 0, 0),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        R.id.dialog_person_img_no_aptos
    ),
    "Solicitado" to ChannelData(
        "Solicitado",
        "Notificaciones para listado de no aptos",
        Importance.HIGH,
        Color.rgb(224, 0, 0),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        R.id.dialog_person_img_no_aptos
    ),
    "Sospechoso" to ChannelData(
        "Sospechoso",
        "Notificaciones para listado de no aptos",
        Importance.HIGH,
        Color.rgb(224, 0, 0),
        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
        R.id.dialog_person_img_no_aptos
    )
)

fun buildNotificationBig(
    context: Context,
    title: String,
    description: String,
    channel: String,
    photoBitmap: Bitmap? = null,
    photoUrl: String? = null,
): Notification = NotificationCompat.Builder(context, channel).apply {
    setSmallIcon(R.mipmap.ic_launcher)
    setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
    setContentTitle(title)
    setContentText(description)

    val channelData = CHANNELS[channel] ?: throw IllegalStateException("There is no channel for id = $channel")
    color = channelData.color
    setSound(channelData.sound)
    if (photoUrl != null && channelData.idDestination != null){
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(channelData.idDestination)
            .setArguments(
                PersonImgDialogArgs(photoUrl = photoUrl).toBundle()
            )
            .createPendingIntent()
        setContentIntent(pendingIntent)
    }

    photoBitmap?.let {
        setLargeIcon(it)
        setStyle(
            NotificationCompat.BigPictureStyle()
                .bigPicture(it)
                .bigLargeIcon(null)
        )
    }

//    val v = longArrayOf(200, 100)
//    setVibrate(v)
    priority = channelData.importance.asNotificationPriority()
    setAutoCancel(true)
}.build()