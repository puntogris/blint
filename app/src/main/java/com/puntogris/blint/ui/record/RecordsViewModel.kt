package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RecordsViewModel @ViewModelInject constructor(
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao): ViewModel() {

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct : LiveData<Product> = _currentProduct.asLiveData()

    private var initialAmount = 0


    fun setProductData(product: Product){
        _currentProduct.value = product
        initialAmount = product.amount
    }

    fun getProductRecords(): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
        ){
            recordsDao.getAllPaged()
        }.flow
    }

    fun getRecordsWithName(name: String): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ) {
            recordsDao.getPagedSearch("%${name}%")
        }.flow
    }

    suspend fun getProduct(id: Int) =
        productsDao.getProduct(id)

}