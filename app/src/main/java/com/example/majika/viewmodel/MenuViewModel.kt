package com.example.majika.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.majika.network.MajikaApi
import kotlinx.coroutines.launch

class MenuViewModel : ViewModel() {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status

    init {
        getAllMenu()
    }

    private fun getAllMenu() {
        viewModelScope.launch {
            try {
                val listResult = MajikaApi.retrofitService.getAllMenu()
                _status.value = listResult
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }
}