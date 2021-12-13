package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.wrapper.GroupChat
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GroupChatViewModel: ViewModel() {

    private val _readGroupsFromDb = MutableLiveData<MutableList<GroupChat>>()
    val readGroupsFromDb = _readGroupsFromDb as LiveData<MutableList<GroupChat>>

    fun getGroupsFromDb() {
        viewModelScope.launch {
            FirebaseDatabaseService.getGroupsFromDb().collect {
                _readGroupsFromDb.value = it
            }
        }
    }
}