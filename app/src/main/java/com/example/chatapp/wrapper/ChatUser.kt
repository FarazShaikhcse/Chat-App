package com.example.chatapp.wrapper

import java.io.Serializable

data class ChatUser(val userName: String, val userId: String, var recentMsg: String = "",
                    var recentMsgTime: Long = 0L, var pfpUri: String = ""):
    Serializable {}