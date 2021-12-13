package com.example.chatapp.notification

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PushNotificationApi {
    @Headers("Accept: application/json")
    @POST("fcm/send")
    suspend fun sendPushNotification(
        @Header("Authorization") authorizationToken: String,
        @Body message: PushMessage
    ): PushResponse

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com/"
    }
}