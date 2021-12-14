package com.example.chatapp.viewmodel

import android.net.Uri
import android.util.Log
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

class ChatDetailViewModel : ViewModel() {
    private val _userchatsFromDb = MutableLiveData<MutableList<Message>>()
    val userchatsFromDb = _userchatsFromDb as LiveData<MutableList<Message>>

    private val _groupuserchatsFromDb = MutableLiveData<MutableList<Message>>()
    val groupuserchatsFromDb = _groupuserchatsFromDb as LiveData<MutableList<Message>>

    private val _newChatsFromDb = MutableLiveData<Message?>()
    val newChatsFromDb = _newChatsFromDb as LiveData<Message?>

    private val _newGroupChatsFromDb = MutableLiveData<Message?>()
    val newGroupChatsFromDb = _newGroupChatsFromDb as LiveData<Message?>

    private val _messageSentStatus = MutableLiveData<Boolean>()
    val messageSentStatus = _messageSentStatus as LiveData<Boolean>

    private val _chatCreatedStatus = MutableLiveData<Boolean>()
    val chatCreatedStatus = _chatCreatedStatus as LiveData<Boolean>

    private val _groupMembersTokens = MutableLiveData<List<String>>()
    val groupMembersTokens = _groupMembersTokens as LiveData<List<String>>

    init {
        Log.d("view model", "initialised")
    }

    fun getChatsFromDB(peerid: String, limit: Long) {
        viewModelScope.launch {
            try {
                _userchatsFromDb.value = FirebaseDatabaseService.getChatsofUserFromDB(peerid, limit)
            } catch (ex: Exception) {
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
                if (msgType == Constants.IMAGE)
                    NotificationService.pushNotification(
                        msgToken, SharedPref.get(Constants.USERNAME).toString(),
                        "Sent Image"
                    )
                else
                    NotificationService.pushNotification(
                        msgToken, SharedPref.get(Constants.USERNAME).toString(),
                        text
                    )

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun updateMessages(peerid: String) {
        viewModelScope.launch {
            AuthenticationService.getUserID()?.let { sender ->
                try {
                    FirebaseDatabaseService.getChatUpdates(peerid).collect {
                        if (it?.sentTime != _newGroupChatsFromDb.value?.sentTime)
                            _newChatsFromDb.value = it
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getGroupMessages(groupId: String) {
        viewModelScope.launch {
            try {
                Log.d("groupmessageupdates", "called")
                FirebaseDatabaseService.getGroupChatUpdates(groupId).collect {
                    _newGroupChatsFromDb.value = it
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun getToken(participants: ArrayList<String>) {
        viewModelScope.launch {
            try {
                _groupMembersTokens.value = FirebaseDatabaseService.getTokensOfMembers(participants)
            } catch (ex: Exception) {
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
                var text = message
                if (msgType == Constants.IMAGE) {
                    text = "Sent Image"
                }
                for (token in tokenList) {
                    NotificationService.pushNotification(
                        token, SharedPref.get(Constants.USERNAME).toString(),
                        text
                    )
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun addNewUserChat(peerId: ChatUser) {
        viewModelScope.launch {
            try {
                _chatCreatedStatus.value = FirebaseDatabaseService.addNewUserChat(peerId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun loadNextTenChats(peerid: String, offset: Long, convType: String) {
        viewModelScope.launch {
            try {
                if (convType == Constants.CHATS) {
                    _userchatsFromDb.value = FirebaseDatabaseService.loadNextChats(peerid, offset)
                } else {
                    _groupuserchatsFromDb.value =
                        FirebaseDatabaseService.loadNextChatsGroups(peerid, offset)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("view model", "cleared")
    }

}