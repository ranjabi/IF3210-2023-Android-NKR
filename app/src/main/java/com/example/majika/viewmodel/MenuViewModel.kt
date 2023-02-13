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
    private val _foodItem = MutableLiveData<List<MenuItem>>()
    val foodItem: LiveData<List<MenuItem>> = _foodItem
    private val _drinkItem = MutableLiveData<List<MenuItem>>()
    val drinkItem: LiveData<List<MenuItem>> = _drinkItem

    init {
        getAllMenu()
    }

    private fun getAllMenu() {
        viewModelScope.launch {
            try {
                _foodItem.value = MajikaApi.retrofitService.getAllFood().data
                _drinkItem.value = MajikaApi.retrofitService.getAllDrink().data
                Log.d(TAG, "api loaded")
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
                Log.d(TAG, "Failure: ${e.message}")
            }
        }
    }
}