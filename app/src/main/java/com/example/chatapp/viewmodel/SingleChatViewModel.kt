package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.wrapper.Chat
import com.example.chatapp.wrapper.User
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

class SingleChatViewModel: ViewModel() {

    private val _userchatsFromDb = MutableLiveData<MutableList<Chat>>()
    val userchatsFromDb = _userchatsFromDb as LiveData<MutableList<Chat>>

    private val _readUsersFromDb = MutableLiveData<MutableList<User>>()
    val readUsersFromDb = _readUsersFromDb as LiveData<MutableList<User>>

//    fun getChatsFromDB(limit: Long) {
//        viewModelScope.launch {
//            _userchatsFromDb.value = FirebaseDatabaseService.getChatsFromDB(limit)
//        }
//    }

    fun getAllUsers() {
        viewModelScope.launch {
            _readUsersFromDb.value = FirebaseDatabaseService.getAllUsersFromDb()
        }
    }

}