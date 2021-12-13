package com.example.chatapp.notification

data class PushMessage(
    val to: String,
    val notification: PushContent
)