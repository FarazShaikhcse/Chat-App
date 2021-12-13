package com.example.chatapp.notification

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationService {
    suspend fun pushNotification(token: String?, userName: String, textMessage: String) {
        withContext(Dispatchers.IO){
            if(token != null) {
                Log.d("token",token.toString())
                val pushContent = PushContent(userName, textMessage, "")
                val pushMessage = PushMessage(token, pushContent)
                val pushNotificationSenderService = PushNotificationSenderService()
                Log.d("push",pushNotificationSenderService.sendPushNotification(pushMessage).toString())
            }

        }
    }

}