package com.puntogris.blint.ui.orders.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.orders.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageOrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
): ViewModel() {

    suspend fun getBusinessOrders() = orderRepository.getBusinessOrdersPagingDataFlow().cachedIn(viewModelScope)

    suspend fun getBusinessRecords() = orderRepository.getBusinessRecordsPagingDataFlow().cachedIn(viewModelScope)
}