package com.puntogris.blint.ui.supplier

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.data.local.suppliers.SuppliersDao
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SupplierViewModel @ViewModelInject constructor(
    private val suppliersDao: SuppliersDao,
    private val recordsDao: RecordsDao
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

    fun getSuppliersWithName(name: String): Flow<PagingData<Supplier>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            suppliersDao.getPagedSearch("%${name}%")
        }.flow
    }

    suspend fun getSupplier(id:Int) = suppliersDao.getSupplier(id)

    fun updateSupplierData(supplier: Supplier){
        supplier.id = _currentSupplier.value.id
        _currentSupplier.value = supplier
    }

    fun setSupplierData(supplier: Supplier){
        _currentSupplier.value = supplier
    }

    fun deleteSupplierDatabase(id: Int){
        viewModelScope.launch {
            suppliersDao.delete(id)
        }
    }

    fun saveSupplierDatabase(){
        viewModelScope.launch {
            suppliersDao.insert(_currentSupplier.value)
        }
    }

    fun getSupplierRecords(supplierID:Int):Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            recordsDao.getSupplierRecords(supplierID)
        }.flow
    }
}