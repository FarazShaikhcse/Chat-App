package com.example.chatapp.wrapper

import com.example.chatapp.wrapper.Message
import java.io.Serializable

data class Chat(
    val participants: ArrayList<String>,
    val message: List<Message>
) : Serializable { }