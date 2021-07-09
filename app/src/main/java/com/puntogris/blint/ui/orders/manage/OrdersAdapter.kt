package com.puntogris.blint.ui.orders.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.OrderWithRecordsDiffCallBack
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.OrderWithRecords

class OrdersAdapter(private val clickListener: (OrderWithRecords) -> Unit): PagingDataAdapter<OrderWithRecords, OrdersViewHolder>(
    OrderWithRecordsDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}