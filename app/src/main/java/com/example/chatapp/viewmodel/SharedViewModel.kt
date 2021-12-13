package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import kotlinx.coroutines.launch

class SharedViewModel: ViewModel() {

    private val _gotoWelcomePageStatus = MutableLiveData<Boolean>()
    val gotoWelcomePageStatus = _gotoWelcomePageStatus as LiveData<Boolean>

    private val _gotoOTPPageStatus = MutableLiveData<Boolean>()
    val gotoOTPPageStatus = _gotoOTPPageStatus as LiveData<Boolean>

    private val _gotoHomePageStatus = MutableLiveData<Boolean>()
    val gotoHomePageStatus = _gotoHomePageStatus as LiveData<Boolean>

    private val _gotoUserDetailsPageStatus = MutableLiveData<Boolean>()
    val gotoUserDetailsPageStatus = _gotoUserDetailsPageStatus as LiveData<Boolean>

    private val _gotoEditProfilePageStatus = MutableLiveData<Boolean>()
    val gotoEditProfilePageStatus = _gotoEditProfilePageStatus as LiveData<Boolean>

    private val _gotoChatDetailsPageStatus = MutableLiveData<Boolean>()
    val gotoChatDetailsPageStatus = _gotoChatDetailsPageStatus as LiveData<Boolean>

    private val _gotoSplashPageStatus = MutableLiveData<Boolean>()
    val gotoSplashPageStatus = _gotoSplashPageStatus as LiveData<Boolean>

    fun setGoToWelcomePageStatus(status: Boolean) {
        _gotoWelcomePageStatus.value = status
    }

    fun setGoToOTPageStatus(status: Boolean) {
        _gotoOTPPageStatus.value = status
    }

    fun setGotoHomePageStatus(status: Boolean) {
        _gotoHomePageStatus.value = status
    }

    fun setGotoUserDetailsPageStatus(status: Boolean) {
        _gotoUserDetailsPageStatus.value = status
    }

    fun setGotoEditProfilePageStatus(status: Boolean) {
        _gotoEditProfilePageStatus.value = status
    }

    fun setGoToChatDetailsPageStatus(status: Boolean) {
        _gotoChatDetailsPageStatus.value = status
    }

    fun setGotoSplashScreen(status: Boolean) {
        _gotoSplashPageStatus.value = status
    }

    fun updateToken(token: String) {
        viewModelScope.launch {
            try {
                FirebaseDatabaseService.updateTokentoDB(token)
            }
         catch (ex: Exception) {
             ex.printStackTrace()
         }
        }

    }

}