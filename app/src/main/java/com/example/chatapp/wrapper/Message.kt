package com.example.chatapp.wrapper

data class Message(
    val messageId: String,
    val senderId: String,
    val sentTime: Long,
    val text: String,
    val messageType: String
) {}