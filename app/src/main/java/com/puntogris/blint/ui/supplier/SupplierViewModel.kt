package com.puntogris.blint.ui.supplier

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.dao.RecordsDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SupplierViewModel @ViewModelInject constructor(
    private val suppliersDao: SuppliersDao,
    private val recordsDao: RecordsDao,
    private val usersDao: UsersDao,
    private val statisticsDao: StatisticsDao
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
        supplier.supplierId = _currentSupplier.value.supplierId
        _currentSupplier.value = supplier
    }

    fun setSupplierData(supplier: Supplier){
        _currentSupplier.value = supplier
    }

    fun deleteSupplierDatabase(id: Int){
        viewModelScope.launch {
            suppliersDao.delete(id)
            statisticsDao.decrementTotalSuppliers()
        }
    }

    fun saveSupplierDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            _currentSupplier.value.businessId = usersDao.getUser().currentBusinessId
            suppliersDao.insert(_currentSupplier.value)
            if (_currentSupplier.value.supplierId == 0) statisticsDao.incrementTotalSuppliers()
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