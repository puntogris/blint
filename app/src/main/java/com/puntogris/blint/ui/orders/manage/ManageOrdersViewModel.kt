package com.puntogris.blint.ui.orders.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.puntogris.blint.data.repository.OrdersRepositoryImpl
import com.puntogris.blint.domain.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageOrdersViewModel @Inject constructor(
    private val repository: OrdersRepository
) : ViewModel() {

    fun getOrders() = repository.getOrdersPaged().cachedIn(viewModelScope)

    fun getRecords() = repository.getRecordsPaged().cachedIn(viewModelScope)
}