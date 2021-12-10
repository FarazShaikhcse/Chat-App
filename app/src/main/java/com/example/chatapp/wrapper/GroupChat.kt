package com.example.chatapp.wrapper

import java.io.Serializable

data class GroupChat(val groupId: String, val participants: ArrayList<String>, val groupName: String,
                     var pfpUri: String = "",
                     var messages: List<Message> = emptyList()): Serializable { }