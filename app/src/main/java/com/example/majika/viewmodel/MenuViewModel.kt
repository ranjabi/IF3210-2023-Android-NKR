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

    val _filteredFoodItem = MutableLiveData<List<MenuItem>>()
    val filteredFoodItem: LiveData<List<MenuItem>> = _filteredFoodItem
    val _filteredDrinkItem = MutableLiveData<List<MenuItem>>()
    val filteredDrinkItem: LiveData<List<MenuItem>> = _filteredDrinkItem


    init {
        getAllMenu()
    }

    private fun getAllMenu() {
        viewModelScope.launch {
            try {
                _foodItem.value = MajikaApi.retrofitService.getAllFood().data
                _drinkItem.value = MajikaApi.retrofitService.getAllDrink().data

                _filteredFoodItem.value = foodItem.value
                _filteredDrinkItem.value = drinkItem.value
                Log.d(TAG, "api loaded")
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
                Log.d(TAG, "Failure: ${e.message}")
            }
        }
    }

    fun filterData(query: String) {
        _filteredFoodItem.value = foodItem.value?.filter{
            it.name.toLowerCase().contains(query);
        }
        _filteredDrinkItem.value = drinkItem.value?.filter{
            it.name.toLowerCase().contains(query);
        }
    }
}