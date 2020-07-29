package com.oesvica.appibartiFace.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.oesvica.appibartiFace.NOTIFICATION_ID_EVENT
import com.oesvica.appibartiFace.buildNotification
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.notificationManager

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        debug("onMessageReceived()=${remoteMessage.data}")
        val type = remoteMessage.data["type"] ?: return
        val content = remoteMessage.data["content"] ?: return
        val notification = buildNotification(this@MyFirebaseMessagingService, type, content)
        notificationManager().notify(NOTIFICATION_ID_EVENT, notification)
    }

    override fun onNewToken(token: String) {
        debug("onNewToken(token: String)=$token")
    }
}