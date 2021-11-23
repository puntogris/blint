package com.puntogris.blint.ui.orders.simple_order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repository.orders.OrderRepository
import com.puntogris.blint.model.order.NewOrder
import com.puntogris.blint.model.order.NewRecord
import com.puntogris.blint.model.product.Product
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SimpleOrderViewModel @Inject constructor(
    private val repository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val product = savedStateHandle.get<Product>("product")!!
    private var newOrder = NewOrder()

    fun createSimpleOrder(amount: Int): Flow<RepoResult<Unit>> {

        newOrder.newRecords = listOf(
            NewRecord(
                amount = amount,
                productId = product.productId,
                productName = product.name,
                totalInStock = product.historicInStock,
                totalOutStock = product.historicOutStock,
                sku = product.sku,
                barcode = product.barcode
            )
        )

        return repository.saveOrder(newOrder)
    }

    fun updateOrderType(orderType: String) {
        newOrder.type = orderType
    }

    // todo think a better way to do this, maybe move it to the repository
    fun amountIsValid(amount: Int): Boolean {
        return amount > 0 &&
                if (newOrder.type == Constants.IN) true
                else amount <= product.amount
    }

}