package com.puntogris.blint.ui.orders.detailed_order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.*
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.utils.SearchText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository
): ViewModel() {

    private val order = Order()
    var productWithRecords = mutableListOf<ProductWithRecord>()

    fun getOrder() = order

    fun refreshOrderValue(){
        order.updateOrderValue()
    }

    fun updateRecordType(code: Int){
        order.type = when(code){
            0 -> "IN"
            else -> "OUT"
        }
    }
    fun getOrderType() = order.type

    fun updateOrderValue(newValue:Float){
        order.value = newValue
    }

    fun updateOrderExternalInfo(name:String, id:String){
        order.traderId = id
        order.traderName = name
    }

    fun updateOrdersItems(items:List<ProductWithRecord>){
        order.items = items.map {
            it.record
        }
    }

    suspend fun getSuppliersWithName(name: String) =
        supplierRepository.getSupplierWithNamePagingDataFlow(name).cachedIn(viewModelScope)

    suspend fun getSuppliersPaging() = supplierRepository.getSupplierPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithName(searchText: SearchText) =
        productRepository.getProductsWithNamePagingDataFlow(searchText)
            .map { it.map { it.product } }.cachedIn(viewModelScope)

    fun getCurrentUserEmail() = userRepository.getCurrentUser()?.email

    suspend fun publishOrderDatabase() = orderRepository.saveOrderIntoDatabase(order)

    suspend fun getClientPaging() = clientRepository.getClientPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getClientsWithName(name: String) =
        clientRepository.getClientWithNamePagingDataFlow(name).cachedIn(viewModelScope)
}
