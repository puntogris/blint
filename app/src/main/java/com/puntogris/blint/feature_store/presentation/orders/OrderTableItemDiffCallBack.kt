package com.puntogris.blint.feature_store.presentation.orders

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.order.OrdersTableItem

class OrderTableItemDiffCallBack : DiffUtil.ItemCallback<OrdersTableItem>() {
    override fun areItemsTheSame(oldItem: OrdersTableItem, newItem: OrdersTableItem): Boolean {
        return oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: OrdersTableItem, newItem: OrdersTableItem): Boolean {
        return oldItem == newItem
    }
}