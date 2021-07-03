package com.puntogris.blint.ui.orders

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.google.firebase.Timestamp
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.OrderRepository
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Record
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val ordersDao: OrdersDao,
    private val productsDao: ProductsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val usersDao: UsersDao,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
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

    suspend fun getBusinessOrders() = orderRepository.getBusinessOrdersPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getBusinessRecords() = orderRepository.getBusinessRecordsPagingDataFlow().cachedIn(viewModelScope)

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