package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.util.Chat
import kotlinx.coroutines.launch

class SingleChatViewModel: ViewModel() {

    private val _userchatsFromDb = MutableLiveData<MutableList<Chat>>()
    val userchatsFromDb = _userchatsFromDb as LiveData<MutableList<Chat>>

    fun getChatsFromDB() {
        viewModelScope.launch {
            _userchatsFromDb.value = FirebaseDatabaseService.getChatsFromDB(1)
        }
    }
}