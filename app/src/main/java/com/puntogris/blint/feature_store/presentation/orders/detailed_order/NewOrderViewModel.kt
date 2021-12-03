package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.common.utils.Constants.IN
import com.puntogris.blint.common.utils.Constants.OUT
import com.puntogris.blint.common.utils.copyAndAdd
import com.puntogris.blint.common.utils.copyAndRemove
import com.puntogris.blint.common.utils.types.RepoResult
import com.puntogris.blint.feature_store.data.data_source.toNewRecord
import com.puntogris.blint.feature_store.domain.model.order.*
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.repository.ClientRepository
import com.puntogris.blint.feature_store.domain.repository.OrdersRepository
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import com.puntogris.blint.feature_store.domain.repository.SupplierRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val orderRepository: OrdersRepository,
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
        _newOrder.value.newRecords = Collections.unmodifiableList(_newOrder.value.newRecords)
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
        return orderRepository.saveOrder(_newOrder.value)
    }

    fun updateOrderTotal(){
        _newOrder.value = _newOrder.value.copy().apply {
            updateOrderTotalValue()
        }
    }

    fun setQuery(editable: Editable) {
        query.value = editable.toString()
    }

    fun updateOrderDebt(amount: Float) {
        _newOrder.value = _newOrder.value.copy(
            newDebt = if (amount != 0F) NewDebt(amount = amount) else null
        )
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

    fun areProductsValid() = _newOrder.value.areRecordsValid()

    fun isDebtValid() = _newOrder.value.newDebt?.amount != 0F
}
