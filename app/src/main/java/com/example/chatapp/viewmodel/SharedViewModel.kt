package com.example.chatapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    private val _gotoWelcomePageStatus = MutableLiveData<Boolean>()
    val gotoWelcomePageStatus = _gotoWelcomePageStatus as LiveData<Boolean>

    private val _gotoOTPPageStatus = MutableLiveData<Boolean>()
    val gotoOTPPageStatus = _gotoOTPPageStatus as LiveData<Boolean>

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    fun setGoToWelcomePageStatus(status: Boolean) {
        _gotoWelcomePageStatus.value = status
    }

    fun setGoToOTPageStatus(status: Boolean) {
        _gotoOTPPageStatus.value = status
    }

    fun setGotoHomePageStatus(status: Boolean) {
        _gotoHomePageStatus.value = status
    }

}