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

    private val _userDetailFetchedStatus = MutableLiveData<User>()
    val userDetailFetchedStatus = _userDetailFetchedStatus as LiveData<User>

    private val _userPFPfetchedStatus = MutableLiveData<Uri?>()
    val userPFPfetchedStatus = _userPFPfetchedStatus as LiveData<Uri?>

    fun addUserDetails(user: User) {
        viewModelScope.launch {
            _userDetailAddedStatus.value = FirebaseDatabaseService.writeUserDataToDatabase(user)
        }
    }

    fun readUserDetails() {
        viewModelScope.launch {
            _userDetailFetchedStatus.value = FirebaseDatabaseService.readUserDataFromDatabase()
        }
    }

    fun uploadProfilePic(uri: Uri) {
        viewModelScope.launch {
            FirebaseStorageService.uploadprofile(uri)
        }
    }

    fun getProfilePic() {
        viewModelScope.launch {
            _userPFPfetchedStatus.value = FirebaseStorageService.fetchProfile()
        }
    }

    fun updatePfpUri(it: Uri) {
        viewModelScope.launch {
            try {
                FirebaseDatabaseService.addUriToProfile(it)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}