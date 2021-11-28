package com.puntogris.blint.feature_store.presentation.orders.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.OrdersTableItem
import com.puntogris.blint.feature_store.domain.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val orderArg = handle.getLiveData<OrderWithRecords>("orderWithRecords").asFlow()
    private val orderArgId = handle.getLiveData<String>("orderId").asFlow()

    val tableItems = combine(orderArg, orderArgId) { order, orderId ->
        order ?: repository.getOrderRecords(orderId)
    }.map { order ->
        order.records.map {
            OrdersTableItem(
                it.productName,
                it.amount,
                it.value
            )
        }
    }

    private val _order = MutableStateFlow(OrderWithRecords())
    val order = _order.asStateFlow()
}