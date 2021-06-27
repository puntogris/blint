package com.puntogris.blint.ui.supplier

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.StatisticsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.data.remote.SupplierRepository
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SupplierViewModel @Inject constructor(
    private val suppliersDao: SuppliersDao,
    private val ordersDao: OrdersDao,
    private val supplierRepository: SupplierRepository
):ViewModel() {

    private val _currentSupplier = MutableStateFlow(Supplier())
    val currentSupplier : LiveData<Supplier> = _currentSupplier.asLiveData()

    suspend fun getSuppliersPaging() = supplierRepository.getSupplierPagingDataFlow().cachedIn(viewModelScope)

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

    suspend fun getSupplier(id:String) = suppliersDao.getSupplier(id)

    fun updateSupplierData(supplier: Supplier){
        supplier.supplierId = _currentSupplier.value.supplierId
        _currentSupplier.value = supplier
    }

    fun setSupplierData(supplier: Supplier){
        _currentSupplier.value = supplier
    }

    suspend fun deleteSupplierDatabase(supplierId: String) = supplierRepository.deleteSupplierDatabase(supplierId)

    suspend fun saveSupplierDatabase() = supplierRepository.saveSupplierDatabase(_currentSupplier.value)

    fun getSupplierRecords(supplierID: String):Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            ordersDao.getSupplierRecords(supplierID)
        }.flow
    }

}