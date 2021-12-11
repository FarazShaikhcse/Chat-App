package com.example.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.service.FirebaseStorageService
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

    private val _imageUploadedStatus = MutableLiveData<Uri?>()
    val imageUploadedStatus = _imageUploadedStatus as LiveData<Uri?>

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

    fun sendMsgToUser(text: String, peerid: String, msgType: String) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToUserDb(
                        sender,
                        peerid, text, msgType
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

    fun sendMsgToGroup(groupId: String, message: String, msgType: String) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToGroupDb(sender, groupId, message, msgType)
                }
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun uploadImageToStorage(selectedImagePath: Uri?) {
        viewModelScope.launch {
            try {
                _imageUploadedStatus.value = FirebaseStorageService.uploadImage(selectedImagePath)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}