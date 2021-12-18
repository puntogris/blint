package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.text.Editable
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.copyAndAdd
import com.puntogris.blint.common.utils.copyAndRemove
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.common.utils.types.TraderQuery
import com.puntogris.blint.feature_store.data.data_source.toNewRecord
import com.puntogris.blint.feature_store.domain.model.order.*
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.repository.OrderRepository
import com.puntogris.blint.feature_store.domain.repository.ProductRepository
import com.puntogris.blint.feature_store.domain.repository.TraderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val traderRepository: TraderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val query = MutableLiveData("")

    private val orderType = savedStateHandle.get<String>("orderType") ?: Constants.IN

    private val _newOrder = MutableStateFlow(NewOrder(type = orderType))
    val newOrder = _newOrder.asStateFlow()

    val productsLiveData = query.switchMap {
        liveData {
            if (it.isNotBlank()) emit(productRepository.getProductsWithQuery(it))
            else emit(emptyList())
        }
    }

    val tradersLiveData = Transformations.switchMap(query) {
        traderRepository.getTradersPaged(TraderQuery(query = it)).asLiveData()
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

    fun publishOrder(): Flow<ProgressResource<Unit>> {
        return orderRepository.saveOrder(_newOrder.value)
    }

    fun updateOrderTotal() {
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

    fun updateOrderTrader(traderName: String, traderId: String) {
        _newOrder.value.traderId = traderId
        _newOrder.value.traderName = traderName
        query.value = ""
    }

    fun areProductsValid() = _newOrder.value.areRecordsValid()

    fun isDebtValid() = _newOrder.value.newDebt?.amount != 0F
}
