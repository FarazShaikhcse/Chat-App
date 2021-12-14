package com.example.chatapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.service.FirebaseStorageService
import kotlinx.coroutines.launch

class ImageViewModel: ViewModel() {
    private val _imageUploadedStatus = MutableLiveData<Uri?>()
    val imageUploadedStatus = _imageUploadedStatus as LiveData<Uri?>

    fun uploadImageToStorage(selectedImagePath: Uri?) {
        viewModelScope.launch {
            try {
                _imageUploadedStatus.value = FirebaseStorageService.uploadImage(selectedImagePath)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}