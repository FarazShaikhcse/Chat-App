package com.example.chatapp.notification

import com.example.chatapp.notification.PushNotificationApi.Companion.BASE_URL
import com.example.chatapp.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PushNotificationSenderService {

    suspend fun sendPushNotification(message: PushMessage): PushResponse {
        val api = Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create<PushNotificationApi>()

        return  api.sendPushNotification(
            Constants.API_KEY,
            message
        )
    }
}