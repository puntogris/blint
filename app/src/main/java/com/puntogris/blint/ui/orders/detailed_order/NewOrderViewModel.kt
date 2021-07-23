package com.puntogris.blint.ui.orders.detailed_order

import androidx.lifecycle.*
import androidx.paging.cachedIn
import androidx.paging.map
import com.puntogris.blint.data.repo.clients.ClientRepository
import com.puntogris.blint.data.repo.orders.OrderRepository
import com.puntogris.blint.data.repo.products.ProductRepository
import com.puntogris.blint.data.repo.supplier.SupplierRepository
import com.puntogris.blint.data.repo.user.UserRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import com.puntogris.blint.utils.SearchText
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _order = MutableStateFlow(Order())
    val order:LiveData<Order> = _order.asLiveData()
    var productWithRecords = mutableListOf<ProductWithRecord>()

    private var debt :FirestoreDebt? = null

    fun refreshOrderValue(){
        _order.value.updateOrderValue()
    }

    fun updateOrderDebt(amount: Float){
        debt = if (amount == 0f) null
        else FirestoreDebt(amount = amount)
    }

    fun updateRecordType(code: Int){
        _order.value.type = when(code){
            0 -> IN
            else -> OUT
        }
    }
    fun getOrderType() = _order.value.type

    fun updateOrderValue(newValue:Float){
        _order.value.value = newValue
    }

    fun updateOrderExternalInfo(name:String, id:String){
        _order.value.traderId = id
        _order.value.traderName = name
    }

    fun updateOrdersItems(items:List<ProductWithRecord>){
        _order.value.items = items.map {
            it.record
        }
    }

    suspend fun getSuppliersWithName(name: String) =
        supplierRepository.getSupplierWithNamePagingDataFlow(name).cachedIn(viewModelScope)

    suspend fun getSuppliersPaging() = supplierRepository.getSupplierPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getProductWithName(searchText: SearchText) =
        productRepository.getProductsWithNamePagingDataFlow(searchText)
            .map { pagingData ->pagingData.map { it.product } }.cachedIn(viewModelScope)

    fun getCurrentUserEmail() = userRepository.getCurrentUser()?.email

    suspend fun publishOrderDatabase(): SimpleResult{
        val newOrder = OrderWithRecords(
            _order.value,
            _order.value.items.map { FirestoreRecord(
                amount = it.amount,
                productId = it.productId,
                productName = it.productName,
                recordId = it.recordId,
                value = it.value,
                sku = it.sku,
                barcode = it.barcode,
                totalInStock = it.totalInStock,
                totalOutStock = it.totalOutStock)
                            },
                debt)
        return orderRepository.saveOrderIntoDatabase(newOrder)
    }

    suspend fun getClientPaging() = clientRepository.getClientPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getClientsWithName(name: String) =
        clientRepository.getClientWithNamePagingDataFlow(name).cachedIn(viewModelScope)
}
