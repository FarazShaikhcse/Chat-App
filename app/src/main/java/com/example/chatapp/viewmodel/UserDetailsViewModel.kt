package com.example.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.service.FirebaseStorageService
import com.example.chatapp.wrapper.User
import kotlinx.coroutines.launch

class UserDetailsViewModel: ViewModel() {
    private val _userDetailAddedStatus = MutableLiveData<Boolean>()
    val userDetailAddedStatus = _userDetailAddedStatus as LiveData<Boolean>

    fun addUserDetails(user: User) {
        viewModelScope.launch {
            _userDetailAddedStatus.value = FirebaseDatabaseService.writeUserDataToDatabase(user)
        }
    }
    fun uploadProfilePic(uri: Uri) {
        viewModelScope.launch {
            FirebaseStorageService.uploadprofile(uri)
        }
    }
}