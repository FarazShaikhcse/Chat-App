package com.example.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseDatabaseService
import kotlinx.coroutines.launch

class SetGroupViewModel: ViewModel() {

    private val _grpCreatedStatus = MutableLiveData<Boolean>()
    val grpCreatedStatus = _grpCreatedStatus as LiveData<Boolean>

    fun createGrp(name: String,list: ArrayList<String>?) {
        viewModelScope.launch {
            val status = FirebaseDatabaseService.createGrp(name,list)
            _grpCreatedStatus.value = status
        }
    }
}