package com.example.chatapp.viewmodel

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.util.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTPViewModel: ViewModel() {
    val auth = FirebaseAuth.getInstance()
    private val _isNewUserStatus = MutableLiveData<Boolean>()
    val isNewUserStatus = _isNewUserStatus as LiveData<Boolean>

    fun verifyOTP(otp: String, activity: Activity, context: Context) {
        val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
            SharedPref.get("storedVerificationId").toString(), otp
        )
        signInWithPhoneAuthCredential(credential, activity, context)
    }

    // verifies if the code matches sent by firebase
    // if success start the new activity in our case it is main Activity
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, activity: Activity, context: Context) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    _isNewUserStatus.value = task.result?.additionalUserInfo?.isNewUser == true
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(context, "Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}