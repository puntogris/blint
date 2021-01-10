package com.puntogris.blint.ui.supplier

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.suppliers.SuppliersDao
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SupplierViewModel @ViewModelInject constructor(
    private val suppliersDao: SuppliersDao
):ViewModel() {

    private val _currentSupplier = MutableStateFlow(Supplier())
    val currentSupplier : LiveData<Supplier> = _currentSupplier.asLiveData()

    fun getAllSuppliers(): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            suppliersDao.getAllPaged()
        }.flow
    }

    fun updateCurrentSupplierData(supplier: Supplier){
        supplier.id = _currentSupplier.value.id
        _currentSupplier.value = supplier
    }

    fun setCurrentSupplierData(supplier: Supplier){
        _currentSupplier.value = supplier
    }

    fun saveCurrentSupplierToDatabase(){
        viewModelScope.launch {
            suppliersDao.insert(_currentSupplier.value)
        }
    }
}