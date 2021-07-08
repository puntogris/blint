package com.puntogris.blint.ui.orders.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.OrderDiffCallBack
import com.puntogris.blint.model.Order

class OrdersAdapter(private val clickListener: (Order) -> Unit): PagingDataAdapter<Order, OrdersViewHolder>(
    OrderDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}