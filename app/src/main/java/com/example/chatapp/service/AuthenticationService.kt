package com.example.chatapp.service

import com.google.firebase.auth.FirebaseAuth

object AuthenticationService {
    fun getUserID(): String?{
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}