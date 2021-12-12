package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.wrapper.User
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class NewChatViewModel: ViewModel() {


    private val _getUserListStatus = MutableLiveData<ArrayList<User>>()
    val getUserListStatus = _getUserListStatus as LiveData<ArrayList<User>>

    fun getUserListFromDb() {
        viewModelScope.launch {
            try {
                _getUserListStatus.value = FirebaseDatabaseService.getChatUsersFromDb()
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}