package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RecordsViewModel @ViewModelInject constructor(
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao): ViewModel() {

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct : LiveData<Product> = _currentProduct.asLiveData()

    private val _barcodeScanned = MutableLiveData<String>()
    val barcodeScanned: LiveData<String> = _barcodeScanned

    private var recordType = "NONE"

    fun setProductData(product: Product){
        _currentProduct.value = product
    }

    fun updateRecordType(code: Int){
        recordType = when(code){
            0 -> "IN"
            else -> "OUT"
        }
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

    suspend fun getProduct(id: Int) = productsDao.getProduct(id)

    fun saveRecordAndUpdateStock(amount: Int){
        val record = Record(
            productID = _currentProduct.value.id,
            productName = _currentProduct.value.name,
            amount = amount,
            type = recordType,
            timestamp = Timestamp.now(),
            clients = listOf(),
            suppliers = listOf()
        )
        viewModelScope.launch {
            recordsDao.insert(record)
            val newAmount = when(record.type){
                "IN" -> _currentProduct.value.amount + amount
                else -> _currentProduct.value.amount - amount
            }

            productsDao.updateProductAmount(_currentProduct.value.id, newAmount)
        }

    }

    suspend fun getProductWithBarCode(barcode:String) = productsDao.getProductWithBarcode(barcode)

    fun getProductID() = _currentProduct.value.id

    fun updateBarcodeScanned(barcode: String){
        _barcodeScanned.value = barcode
    }

    fun getBarcodeScanned() = _barcodeScanned.value.toString()

}