package com.puntogris.blint.feature_store.presentation.orders.details

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.domain.model.order.OrdersTableItem
import com.puntogris.blint.feature_store.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val repository: OrderRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val orderArg = handle.getLiveData<OrderWithRecords>("order").asFlow()
    private val orderArgId = handle.getLiveData<String>("orderId").asFlow()

    val orderWithRecords = combine(orderArg, orderArgId) { order, orderId ->
        order ?: repository.getOrderRecords(orderId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), OrderWithRecords())

    val tableItems = orderWithRecords.mapLatest { order ->
        order.records.map {
            OrdersTableItem(
                it.productName,
                it.amount,
                it.value
            )
        }
    }

    fun getOrderPDF(uri: Uri? = null) = repository.generateOrderPDF(uri, orderWithRecords.value)

}