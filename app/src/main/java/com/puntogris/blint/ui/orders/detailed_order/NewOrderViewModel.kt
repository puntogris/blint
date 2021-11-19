package com.puntogris.blint.ui.orders.detailed_order

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.clients.ClientRepository
import com.puntogris.blint.data.repository.orders.OrderRepository
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.data.repository.user.UserRepository
import com.puntogris.blint.model.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private var job: Job? = null

    private val _order = MutableStateFlow(Order())
    val order: LiveData<Order> = _order.asLiveData()
    var productWithRecords = mutableListOf<ProductWithRecord>()

    private var debt: FirestoreDebt? = null

    fun updateOrderDebt(amount: Float) {
        debt = FirestoreDebt(amount = -amount)
    }

    fun updateOrderDiscount(amount: Float) {
        _order.value.value = _order.value.value - amount
        _order.value.discount = amount
    }

    fun updateRecordType(code: Int) {
        _order.value.type = when (code) {
            0 -> IN
            else -> OUT
        }
    }

    fun getOrderType() = _order.value.type

    fun updateOrderValue(newValue: Float) {
        _order.value.value = newValue
    }

    fun updateOrderExternalInfo(name: String, id: Int) {
        _order.value.traderId = id
        _order.value.traderName = name
    }

    fun updateOrdersItems(items: List<ProductWithRecord>) {
        _order.value.items = items.map {
            it.record
        }
    }

    fun getSuppliersWithName(name: String) =
        supplierRepository.getSuppliersPaged(name).cachedIn(viewModelScope)

    fun getSuppliersPaging() = supplierRepository.getSuppliersPaged().cachedIn(viewModelScope)

    private val _productsLiveData = MutableLiveData<List<Product>>()
    val productsLiveData: LiveData<List<Product>> get() = _productsLiveData

    fun setQuery(query: String) {
        job?.cancel()
        if (query.isEmpty()) {
            _productsLiveData.value = emptyList()
        } else {
            job = viewModelScope.launch {
                val products = productRepository.getProductsWithQuery(query)
                _productsLiveData.value = products
            }
        }
    }

    suspend fun currentUserEmail() = userRepository.getCurrentUser().email

    suspend fun publishOrderDatabase(): SimpleResult {
        val newOrder = OrderWithRecords(
            _order.value,
            _order.value.items.map {
                FirestoreRecord(
                    amount = it.amount,
                    productId = it.productId,
                    productName = it.productName,
                    recordId = it.recordId,
                    value = it.value,
                    sku = it.sku,
                    barcode = it.barcode,
                    totalInStock = it.totalInStock,
                    totalOutStock = it.totalOutStock
                )
            },
            debt
        )
        return orderRepository.saveOrder(newOrder)
    }

    fun getClientPaging() = clientRepository.getClientsPaged().cachedIn(viewModelScope)

    fun getClientsWithName(name: String) =
        clientRepository.getClientsPaged(name).cachedIn(viewModelScope)

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}
