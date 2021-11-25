package com.puntogris.blint.ui.orders.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.puntogris.blint.domain.repository.OrdersRepository
import com.puntogris.blint.model.order.OrderWithRecords
import com.puntogris.blint.model.order.OrdersTableItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrdersRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderArg = savedStateHandle.get<OrderWithRecords>("orderWithRecords")
    private val orderArgId = savedStateHandle.get<String>("orderId") ?: ""

    val tableItems = liveData {
        if (orderArgId.isNotEmpty()) emit(repository.getOrderRecords(orderArgId))
        else emit(orderArg ?: OrderWithRecords())
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