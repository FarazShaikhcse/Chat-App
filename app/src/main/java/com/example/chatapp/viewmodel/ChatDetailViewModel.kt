package com.example.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.notification.NotificationService
import com.example.chatapp.service.AuthenticationService
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.service.FirebaseStorageService
import com.example.chatapp.util.Constants
import com.example.chatapp.util.SharedPref
import com.example.chatapp.wrapper.ChatUser
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

    private val _chatCreatedStatus = MutableLiveData<Boolean>()
    val chatCreatedStatus = _chatCreatedStatus as LiveData<Boolean>

    private val _groupMembersTokens = MutableLiveData<List<String>>()
    val groupMembersTokens = _groupMembersTokens as LiveData<List<String>>

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

    fun sendMsgToUser(text: String, peerid: String, msgType: String, msgToken: String) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToUserDb(
                        sender,
                        peerid, text, msgType
                    )
                }
                val imagePattern = Regex("^https://firebasestorage.googleapis.com")
                if (imagePattern.containsMatchIn(text) )
                    NotificationService.pushNotification(msgToken, SharedPref.get(Constants.USERNAME).toString(),
                        "Sent Image")
                else
                    NotificationService.pushNotification(msgToken, SharedPref.get(Constants.USERNAME).toString(),
                        text)

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

    fun getToken(participants: ArrayList<String>) {
        viewModelScope.launch {
            try {
                _groupMembersTokens.value =  FirebaseDatabaseService.getTokensOfMembers(participants)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun sendMsgToGroup(groupId: String, message: String, msgType: String, tokenList: List<String>) {
        viewModelScope.launch {
            try {
                _messageSentStatus.value = AuthenticationService.getUserID()?.let { sender ->
                    FirebaseDatabaseService.sendTextToGroupDb(sender, groupId, message, msgType)
                }
                val imagePattern = Regex("^https://firebasestorage.googleapis.com")
                var text = message
                if (imagePattern.containsMatchIn(message)) {
                     text = "Sent Image"
                }
                for (token in tokenList) {
                        NotificationService.pushNotification(token, SharedPref.get(Constants.USERNAME).toString(),
                            text)
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

    fun addNewUserChat(peerId: ChatUser) {
        viewModelScope.launch {
            try {
                _chatCreatedStatus.value = FirebaseDatabaseService.addNewUserChat(peerId)
            }
            catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}