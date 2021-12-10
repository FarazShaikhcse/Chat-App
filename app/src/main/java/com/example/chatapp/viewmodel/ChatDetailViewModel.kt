package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.wrapper.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.Exception

class ChatDetailViewModel: ViewModel() {
    private val _userchatsFromDb = MutableLiveData<MutableList<Message>>()
        val userchatsFromDb = _userchatsFromDb as LiveData<MutableList<Message>>

    private val _messageSentStatus = MutableLiveData<Boolean>()
    val messageSentStatus = _messageSentStatus as LiveData<Boolean>

    fun getChatsFromDB(peerid: String, limit: Long) {
        viewModelScope.launch {
            try {
                _userchatsFromDb.value = FirebaseDatabaseService.getChatsofUserFromDB(peerid, limit)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun sendMsgToUser(text: String, peerid: String) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToUserDb(
                        sender,
                        peerid, text
                    )
                }
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun updateMessages(peerid: String) {
        viewModelScope.launch {
            AuthenticationService.getUserID()?.let { sender ->
                try {
                    FirebaseDatabaseService.getUpdatedChatsFromDb(sender, peerid).collect{
                        _userchatsFromDb.value = it
                    }
                }
                 catch (ex: Exception) {
                     ex.printStackTrace()
                 }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getGroupMessages(groupId: String) {
        viewModelScope.launch {
            try {
                FirebaseDatabaseService.getUpdatedGroupChatsFromDb(groupId).collect{
                    _userchatsFromDb.value = it
            }
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun sendMsgToGroup(groupId: String, message: String) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToGroupDb(sender, groupId, message)
                }
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}