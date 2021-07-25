package com.puntogris.blint.ui.orders

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.OrderTableItemDiffCallBack
import com.puntogris.blint.model.OrdersTableItem

class OrdersTableAdapter: ListAdapter<OrdersTableItem, OrdersTableItemViewHolder>(
    OrderTableItemDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersTableItemViewHolder {
        return OrdersTableItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersTableItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}