package com.puntogris.blint.feature_store.presentation.orders.details

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.order.OrdersTableItem
import com.puntogris.blint.feature_store.presentation.orders.OrderTableItemDiffCallBack

class OrdersTableAdapter : ListAdapter<OrdersTableItem, OrdersTableItemViewHolder>(
    OrderTableItemDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersTableItemViewHolder {
        return OrdersTableItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: OrdersTableItemViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}