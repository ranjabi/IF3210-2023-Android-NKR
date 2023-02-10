package com.example.majika.viewmodel

import androidx.lifecycle.*
import com.example.majika.model.Fnb
import com.example.majika.model.FnbDao
import kotlinx.coroutines.launch

class CartViewModel(private val fnbDao: FnbDao) : ViewModel() {
    val allFnbs: LiveData<List<Fnb>> = fnbDao.getFnbs().asLiveData()

    // get table row length that will call FnbDao
    fun getTableRowLength(): LiveData<Int> {
        return fnbDao.getRowLength().asLiveData()
    }
    // retrieve first fnb data that will call FnbDao
    fun retrieveFirstFnb(): LiveData<Fnb> {
        return fnbDao.getFnb().asLiveData()
    }
    // insert particular fnb instance to call FnbDao
    private fun insertFnb (fnb: Fnb) {
        viewModelScope.launch { fnbDao.insert(fnb) }
    }
    // create new Fnb instance
    private fun newFnbEntry (fnbName: String, fnbPrice: String, fnbQuantity: String): Fnb {
        return Fnb (
            fnbName = fnbName,
            fnbPrice = fnbPrice.toInt(),
            fnbQuantity = fnbQuantity.toInt()
        )
    }
    // add new fnb that to be used in CartFragment
    fun addNewFnb (fnbName: String, fnbPrice: String, fnbQuantity: String) {
        val newFnb: Fnb = newFnbEntry(fnbName, fnbPrice, fnbQuantity)
        insertFnb(newFnb)
    }
    // add particular fnb qty that will call FnbDao update
    // and to be used in CartFragment
    fun addFnbQuantity (fnb: Fnb) {
        val updatedFnb: Fnb = fnb.copy(fnbQuantity = fnb.fnbQuantity + 1)
        viewModelScope.launch { fnbDao.update(updatedFnb) }
    }
    // delete particular fnb instance to call FnbDao
    private fun deleteFnb (fnb: Fnb) {
        viewModelScope.launch { fnbDao.delete(fnb) }
    }
    // remove fnb qty if qty > 0, otherwise delete the fnb instance
    fun removeFnbQuantity (fnb: Fnb) {
        if(fnb.fnbQuantity - 1 > 0) {
            val updatedFnb: Fnb = fnb.copy(fnbQuantity = fnb.fnbQuantity - 1)
            viewModelScope.launch { fnbDao.update(updatedFnb) }
        } else {
            deleteFnb(fnb)
        }
    }
}

class CartViewModelFactory(private val fnbDao: FnbDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(fnbDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
