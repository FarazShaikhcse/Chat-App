package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import com.example.chatapp.wrapper.User
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CreateGroupViewModel: ViewModel() {
    val userList = ArrayList<User>()

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    @InternalCoroutinesApi
    fun getUserListFromDb() {
        viewModelScope.launch {
            FirebaseDatabaseService.getAllUsersFromDb().collect {
                userList.clear()
                userList.addAll(it as ArrayList<User>)
                _getUserListStatus.postValue(true)
            }
        }
    }
}