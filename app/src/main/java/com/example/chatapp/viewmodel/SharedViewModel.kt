package com.example.chatapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.util.Chat
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

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

    private val _userchatsFromDb = MutableLiveData<MutableList<Chat>>()
    val userchatsFromDb = _userchatsFromDb as LiveData<MutableList<Chat>>

    private val _gotoChatDetailsPageStatus = MutableLiveData<Boolean>()
    val gotoChatDetailsPageStatus = _gotoChatDetailsPageStatus as LiveData<Boolean>

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

    fun getChatsFromDB() {
        viewModelScope.launch {
            _userchatsFromDb.value = FirebaseDatabaseService.getChatsFromDB(1)
        }
    }

    fun setGoToChatDetailsPageStatus(status: Boolean) {
        _gotoChatDetailsPageStatus.value = status
    }

}