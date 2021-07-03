package com.puntogris.blint.ui.record

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class RecordsViewModel @ViewModelInject constructor(
    private val ordersDao: OrdersDao,
    private val productsDao: ProductsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val userRepository: UserRepository
): ViewModel() {

    private val _currentProduct = MutableStateFlow(Product())
    val currentProduct : LiveData<Product> = _currentProduct.asLiveData()

    private val _barcodeScanned = MutableLiveData<String>()
    val barcodeScanned: LiveData<String> = _barcodeScanned

    private var recordType = "NONE"
    private var externalID = ""
    private var externalName = ""

    fun updateRecordType(code: Int){
        recordType = when(code){
            0 -> "IN"
            else -> "OUT"
        }
    }

    fun updateExternalInfo(id:String, name: String){
        externalID = id
        externalName = name
    }

    fun resetExternalInfo(){
        externalName = ""
        externalID = ""
    }

    fun setProductData(product: Product){
        _currentProduct.value = product
    }

    fun getProductRecords(): Flow<PagingData<Record>> {
        return Pager(
            PagingConfig(
            pageSize = 30,
            enablePlaceholders = true,
            maxSize = 200
        )
        ){
            ordersDao.getAllRecordsPaged()
        }.flow
    }

    fun getOrders(): Flow<PagingData<Order>> {
        return Pager(
            PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
                maxSize = 200
            )
        ){
            ordersDao.getAllOrdersPaged()
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
            ordersDao.getPagedSearch("%${name}%")
        }.flow
    }

    suspend fun getProduct(id: String) = productsDao.getProduct(id)

    suspend fun saveRecordAndUpdateStock(amount: Int): Boolean{
        val record = Record(
            productId = _currentProduct.value.productId,
            productName = _currentProduct.value.name,
            amount = amount,
            type = recordType,
            timestamp = Timestamp.now(),
            traderId = externalID,
            traderName = externalName,
            author = userRepository.getCurrentUser()?.email.toString(),
            businessId = usersDao.getUser().currentBusinessId
        )
        val newAmount = getNewStockAmount(record.type, amount)
        return if (newAmount < 0){
            false
        }else{
            ordersDao.insert(record)
            _currentProduct.value.amount = newAmount
            _currentProduct.value.lastRecordTimestamp = Timestamp.now()
            productsDao.update(_currentProduct.value)
            true
        }
    }

    suspend fun fetchOrderRecords(orderId:String) = ordersDao.getAllOrderRecords(orderId)


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

    fun getCodeScanned() = _barcodeScanned.value.toString()

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllClients() = clientsDao.getAllClients()

}