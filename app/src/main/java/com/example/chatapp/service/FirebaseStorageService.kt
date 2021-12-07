package com.example.chatapp.service

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.suspendCoroutine

object FirebaseStorageService {
    suspend fun uploadprofile(profile: Uri?): Boolean {
        return suspendCoroutine { cont ->
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null && profile != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                storageRef.child("users_PFP/" + uid + ".jpg")
                    .putFile(profile)
                    .addOnSuccessListener {
                        it.storage.downloadUrl.addOnSuccessListener {
                            cont.resumeWith(Result.success(true))
                        }.addOnFailureListener {
                            cont.resumeWith(Result.failure(it))
                        }
                    }.addOnFailureListener {
                        cont.resumeWith(Result.failure(it))
                    }
            }
        }
    }
}