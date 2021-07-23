package com.puntogris.blint.ui.orders

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.orders.OrderRepository
import com.puntogris.blint.data.repo.products.ProductRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val productsDao: ProductsDao,
    private val suppliersDao: SuppliersDao,
    private val clientsDao: ClientsDao,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val _currentProduct = MutableStateFlow(ProductWithSuppliersCategories())
    val currentProduct : LiveData<ProductWithSuppliersCategories> = _currentProduct.asLiveData()

    private val _barcodeScanned = MutableLiveData<String>()
    val barcodeScanned: LiveData<String> = _barcodeScanned

    private val _order = MutableLiveData(OrderWithRecords())
    val order: LiveData<OrderWithRecords> = _order

    fun updateOrder(orderWithRecords: OrderWithRecords){
        _order.value = orderWithRecords
    }

    fun setProductData(product: ProductWithSuppliersCategories){
        _currentProduct.value = product
    }

    suspend fun getBusinessOrders() = orderRepository.getBusinessOrdersPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getBusinessRecords() = orderRepository.getBusinessRecordsPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProduct(id: String) = productsDao.getProduct(id)

    suspend fun fetchOrderRecords(orderId:String) =
        orderRepository.getOrderRecords(orderId)


    suspend fun getProductWithBarCode(barcode:String) = productRepository.getProductWithBarcode(barcode)

    fun getProductID() = _currentProduct.value.product.productId

    fun updateBarcodeScanned(barcode: String){
        _barcodeScanned.value = barcode
    }

    fun getCodeScanned() = _barcodeScanned.value.toString()

    suspend fun getAllSuppliers() = suppliersDao.getAllSuppliers()

    suspend fun getAllClients() = clientsDao.getAllClients()

    suspend fun createSimpleOrder(orderWithRecords: OrderWithRecords): SimpleResult{
        orderWithRecords.order.value = orderWithRecords.records.first().value
        return orderRepository.saveOrderIntoDatabase(orderWithRecords)
    }

}