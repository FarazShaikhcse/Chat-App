package com.example.chatapp.wrapper

data class GroupChat(val participants: ArrayList<String>, val groupName: String, val messages: List<Message>) {
}