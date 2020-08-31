package com.oesvica.appibartiFace.data.service

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oesvica.appibartiFace.buildNotificationBig
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.notificationManager


class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        debug("onMessageReceived()=${remoteMessage.data}")
        val title = remoteMessage.data["title"] ?: return
        val description = remoteMessage.data["description"] ?: return
        val photo = remoteMessage.data["photo"]
        val channel = remoteMessage.data["channel"] ?: return
        if (photo != null) {
            debug("loading stuff $photo")
            loadImg(photo) { resource ->
                displayNotification(title, description, channel, resource, photo)
            }
        } else {
            displayNotification(title, description, channel)
        }
    }

    private fun loadImg(photo: String, func: (Bitmap) -> Unit) {
        Glide.with(this)
            .asBitmap()
            .load(photo)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    debug("onResourceReady")
                    func(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    debug("onLoadCleared")
                }
            })
    }

    private fun displayNotification(
        title: String,
        description: String,
        channel: String,
        photoBitmap: Bitmap? = null,
        photoUrl: String? = null
    ) {
        val notification = buildNotificationBig(
            context = this@MyFirebaseMessagingService,
            title = title,
            description = description,
            channel = channel,
            photoBitmap = photoBitmap,
            photoUrl = photoUrl
        )
        notificationManager().notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        debug("onNewToken(token: String)=$token")
    }
}