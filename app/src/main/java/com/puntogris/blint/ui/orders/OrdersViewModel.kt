package com.puntogris.blint.ui.orders

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.orders.OrderRepository
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.model.order.NewOrder
import com.puntogris.blint.model.order.OrderWithRecords
import com.puntogris.blint.model.product.ProductWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {


    private val barcode = savedStateHandle.get<String>("barcode") ?: ""

    private val _currentProduct = MutableStateFlow(ProductWithDetails())
    val currentProduct = _currentProduct.asStateFlow()

    private val _barcodeScanned = MutableStateFlow("")
    val barcodeScanned = _barcodeScanned.asStateFlow()

    private val _order = MutableStateFlow(OrderWithRecords())
    val order = _order.asStateFlow()

    fun updateOrder(orderWithRecords: OrderWithRecords) {
        _order.value = orderWithRecords
    }

    suspend fun fetchOrderRecords(orderId: String) = orderRepository.getOrderRecords(orderId)

}