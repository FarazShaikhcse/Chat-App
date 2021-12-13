package com.example.chatapp.notification

data class PushContent(
    val title: String,
    val body: String = "",
    val image: String = ""
)