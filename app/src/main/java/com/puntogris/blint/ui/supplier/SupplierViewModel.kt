package com.puntogris.blint.ui.supplier

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.suppliers.SuppliersDao
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow

class SupplierViewModel @ViewModelInject constructor(
    private val suppliersDao: SuppliersDao
):ViewModel() {

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
}