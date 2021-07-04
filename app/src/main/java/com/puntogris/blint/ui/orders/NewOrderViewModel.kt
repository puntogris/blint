package com.puntogris.blint.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.local.dao.*
import com.puntogris.blint.data.repo.OrderRepository
import com.puntogris.blint.data.repo.ProductRepository
import com.puntogris.blint.data.repo.UserRepository
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.utils.SearchText
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val clientsDao: ClientsDao,
    private val suppliersDao: SuppliersDao,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
): ViewModel() {

    private val order = Order()
    var productWithRecords = listOf<ProductWithRecord>()

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

    suspend fun getClientsWithName(name: String) = clientsDao.getClientWithName(name)

    suspend fun getSuppliersWithName(name: String) = suppliersDao.getSupplierWithName(name)

    suspend fun getProductWithName(searchText: SearchText) = productRepository.getProductsWithNamePagingDataFlow(searchText).cachedIn(viewModelScope)

    fun getCurrentUserEmail() = userRepository.getCurrentUser()?.email

    suspend fun publishOrderDatabase() = orderRepository.saveOrderIntoDatabase(order)

}
