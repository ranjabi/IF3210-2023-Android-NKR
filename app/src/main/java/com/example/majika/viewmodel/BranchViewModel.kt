package com.example.majika.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.majika.model.BranchItem
import com.example.majika.network.MajikaApi
import com.example.majika.network.MajikaApiService
import kotlinx.coroutines.launch

class BranchViewModel : ViewModel() {
    val _branchItem = MutableLiveData<List<BranchItem>>()
    val branchItem: LiveData<List<BranchItem>> = _branchItem

    init {
        getAllBranch()
    }

    private fun getAllBranch() {
        viewModelScope.launch {
            try {
                _branchItem.value = MajikaApi.retrofitService.getAllBranch().data
                Log.d("BranchViewModel","Count loaded: ${branchItem.value?.size}")
                Log.d("COKS", "First: ${branchItem.value?.get(0)?.name}")
            } catch (e: Exception) {
                Log.d("BranchViewModel", "Failure: ${e.message}")
            }
        }
    }
}
