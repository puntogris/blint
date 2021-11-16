package com.puntogris.blint.ui.orders

import androidx.lifecycle.*
import com.puntogris.blint.data.repository.orders.OrderRepository
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
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

    suspend fun fetchOrderRecords(orderId:String) = orderRepository.getOrderRecords(orderId)

    suspend fun getProductWithBarCode(barcode:String) = productRepository.getProductWithBarcode(barcode)

    fun getProductID() = _currentProduct.value.product.productId

    fun updateBarcodeScanned(barcode: String){
        _barcodeScanned.value = barcode
    }

    fun getCodeScanned() = _barcodeScanned.value.toString()

    suspend fun createSimpleOrder(orderWithRecords: OrderWithRecords): SimpleResult{
        orderWithRecords.order.value = orderWithRecords.records.first().value
        return orderRepository.saveOrderIntoDatabase(orderWithRecords)
    }

}