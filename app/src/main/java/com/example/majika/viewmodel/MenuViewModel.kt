package com.example.majika.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.majika.model.MenuItem
import com.example.majika.network.MajikaApi
import kotlinx.coroutines.launch

private const val TAG = "MenuViewModel"

class MenuViewModel : ViewModel() {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String> = _status
    private val _menuItem = MutableLiveData<List<MenuItem>>()
    val menuItem: LiveData<List<MenuItem>> = _menuItem

    init {
        getAllMenu()
    }

    private fun getAllMenu() {
        viewModelScope.launch {
            try {
                _menuItem.value = MajikaApi.retrofitService.getAllMenu().data
                Log.d(TAG, menuItem.toString())
                Log.d(TAG, "api loaded")
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
                Log.d(TAG, "Failure: ${e.message}")
            }
        }
    }
}