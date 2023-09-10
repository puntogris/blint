package com.puntogris.blint.feature_store.presentation.orders.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import com.puntogris.blint.feature_store.presentation.orders.OrderWithRecordsDiffCallBack

class OrdersAdapter(private val clickListener: (OrderWithRecords) -> Unit) :
    PagingDataAdapter<OrderWithRecords, OrdersViewHolder>(
        OrderWithRecordsDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}
