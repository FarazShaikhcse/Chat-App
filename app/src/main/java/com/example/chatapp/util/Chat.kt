package com.example.chatapp.util

import com.example.chatapp.wrapper.Message

data class Chat( val participants: ArrayList<String>,
            val message: List<Message>
) {
}