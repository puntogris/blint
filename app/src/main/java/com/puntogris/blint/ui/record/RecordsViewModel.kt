package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.clients.ClientsDao
import com.puntogris.blint.data.local.products.ProductsDao
import com.puntogris.blint.data.local.records.RecordsDao
import com.puntogris.blint.data.local.suppliers.SuppliersDao
import com.puntogris.blint.data.remote.UserRepository
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import com.puntogris.blint.model.Supplier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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

    private var clientID = 0
    private var supplierID = 0

    fun updateClientID(id:Int){
        clientID = id
    }

    fun updateSupplierID(id:Int){
        supplierID = id
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

    suspend fun saveRecordAndUpdateStock(amount: Int):Boolean{
        val record = Record(
            productID = _currentProduct.value.id,
            productName = _currentProduct.value.name,
            amount = amount,
            type = recordType,
            timestamp = Timestamp.now(),
            clientID = clientID,
            supplierID = supplierID,
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

    fun getProductID() = _currentProduct.value.id

    fun updateBarcodeScanned(barcode: String){
        _barcodeScanned.value = barcode
    }

    fun getBarcodeScanned() = _barcodeScanned.value.toString()

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllClients() = clientsDao.getAllClients()
}