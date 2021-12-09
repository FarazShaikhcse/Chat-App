package com.example.chatapp.wrapper

import java.io.Serializable

data class User(val userName: String, val about: String, val userId: String, var pfpUri: String = ""): Serializable {}