package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.ClientsDao
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.data.local.dao.RecordsDao
import com.puntogris.blint.data.local.dao.SuppliersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RecordsViewModel @ViewModelInject constructor(
    private val recordsDao: RecordsDao,
    private val productsDao: ProductsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val userRepository: UserRepository): ViewModel() {

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct : LiveData<Product> = _currentProduct.asLiveData()

    private val _barcodeScanned = MutableLiveData<String>()
    val barcodeScanned: LiveData<String> = _barcodeScanned

    private var recordType = "NONE"
    private var externalID = 0
    private var externalName = ""

    fun updateExternalInfo(id:Int, name: String){
        externalID = id
        externalName = name
    }

    fun resetExternalInfo(){
        externalName = ""
        externalID = 0
    }

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

    suspend fun saveRecordAndUpdateStock(amount: Int): Boolean{
        val record = Record(
            productID = _currentProduct.value.productId,
            productName = _currentProduct.value.name,
            amount = amount,
            type = recordType,
            timestamp = Timestamp.now(),
            externalID = externalID,
            externalName = externalName,
            author = userRepository.getCurrentUser()?.email.toString()
        )
        val newAmount = getNewStockAmount(record.type, amount)
        return if (newAmount < 0){
            false
        }else{
            recordsDao.insert(record)
            _currentProduct.value.amount = newAmount
            _currentProduct.value.lastRecordTimestamp = Timestamp.now()
            productsDao.update(_currentProduct.value)
            true
        }
    }

    private fun getNewStockAmount(type:String, amount:Int): Int{
        val newAmount :Int
        when(type){
            "IN" -> {
                _currentProduct.value.totalInStock += amount
                newAmount = _currentProduct.value.amount + amount
            }
            else -> {
                _currentProduct.value.totalOutStock += amount
                newAmount = _currentProduct.value.amount - amount
            }
        }
        return newAmount
    }

    suspend fun getProductWithBarCode(barcode:String) = productsDao.getProductWithBarcode(barcode)

    fun getProductID() = _currentProduct.value.productId

    fun updateBarcodeScanned(barcode: String){
        _barcodeScanned.value = barcode
    }

    fun getBarcodeScanned() = _barcodeScanned.value.toString()

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllClients() = clientsDao.getAllClients()
}