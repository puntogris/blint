package com.puntogris.blint.feature_store.presentation.orders.simple_order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.feature_store.domain.model.order.NewOrder
import com.puntogris.blint.feature_store.domain.model.order.NewRecord
import com.puntogris.blint.feature_store.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SimpleOrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    handle: SavedStateHandle
) : ViewModel() {

    private val product = SimpleOrderDialogArgs.fromSavedStateHandle(handle).product

    private var newOrder = NewOrder()

    fun createSimpleOrder(amount: Int): Flow<ProgressResource<Unit>> {

        newOrder.newRecords = listOf(
            NewRecord(
                amount = amount,
                productId = product.productId,
                productName = product.name,
                historicalInStock = product.historicInStock,
                historicalOutStock = product.historicOutStock,
                sku = product.sku,
                barcode = product.barcode,
                currentStock = product.amount
            )
        )
        return repository.saveOrder(newOrder)
    }

    fun updateOrderType(orderType: String) {
        newOrder.type = orderType
    }
}
