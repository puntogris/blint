package com.puntogris.blint.ui.orders.detailed_order

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.data.data_source.toNewRecord
import com.puntogris.blint.data.repository.clients.ClientRepository
import com.puntogris.blint.data.repository.orders.OrderRepository
import com.puntogris.blint.data.repository.products.ProductRepository
import com.puntogris.blint.data.repository.supplier.SupplierRepository
import com.puntogris.blint.model.order.NewDebt
import com.puntogris.blint.model.order.NewOrder
import com.puntogris.blint.model.order.NewRecord
import com.puntogris.blint.model.order.updateOrderTotalValue
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import com.puntogris.blint.utils.copyAndAdd
import com.puntogris.blint.utils.copyAndRemove
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val supplierRepository: SupplierRepository,
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val query = MutableLiveData("")

    private val _newOrder = MutableStateFlow(NewOrder())
    val newOrder = _newOrder.asStateFlow()

    val productsLiveData = query.switchMap {
        liveData {
            if (it.isNotBlank()) emit(productRepository.getProductsWithQuery(it))
            else emit(emptyList())
        }
    }

    val clientsLiveData = Transformations.switchMap(query) {
        clientRepository.getClientsPaged(it).asLiveData()
    }.cachedIn(viewModelScope)

    val suppliersLiveData = Transformations.switchMap(query) {
        supplierRepository.getSuppliersPaged(query.value).asLiveData()
    }.cachedIn(viewModelScope)

    fun addProduct(product: Product) {
        if (!newOrder.value.newRecords.any { it.productId == product.productId }) {
            _newOrder.value = newOrder.value.copy(
                newRecords = newOrder.value.newRecords.copyAndAdd(product.toNewRecord())
            )
        }
    }

    fun addProduct(newRecord: NewRecord) {
        if (!newOrder.value.newRecords.any { it.productId == newRecord.productId }) {
            _newOrder.value = newOrder.value.copy(
                newRecords = newOrder.value.newRecords.copyAndAdd(newRecord)
            )
        }
    }

    fun removeProduct(newRecord: NewRecord) {
        _newOrder.value = newOrder.value.copy(
            newRecords = newOrder.value.newRecords.copyAndRemove(newRecord)
        )
    }

    fun publishOrder(): Flow<RepoResult<Unit>> {
        _newOrder.value.updateOrderTotalValue()
        return orderRepository.saveOrder(_newOrder.value)
    }

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }

    fun updateOrderDebt(amount: Float) {
        _newOrder.value.newDebt = if (amount != 0F) NewDebt(amount = amount) else null
    }
    fun updateOrderDebt(editable: Editable) {
        updateOrderDebt(editable.toString().toFloat())
    }

    fun updateOrderType(code: Int) {
        _newOrder.value.type = if (code == 0) IN else OUT
    }

    fun updateOrderTrader(traderName: String, traderId: String) {
        _newOrder.value.traderId = traderId
        _newOrder.value.traderName = traderName
        query.value = ""
    }

    fun areProductsValid() = _newOrder.value.newRecords.isNotEmpty()

    fun isDebtValid() = if (_newOrder.value.newDebt != null) _newOrder.value.newDebt?.amount != 0F else true
}
