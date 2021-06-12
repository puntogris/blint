package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.OrdersTableItem

class OrderTableItemDiffCallBack : DiffUtil.ItemCallback<OrdersTableItem>() {
    override fun areItemsTheSame(oldItem: OrdersTableItem, newItem: OrdersTableItem): Boolean {
        return oldItem.productName == newItem.productName
    }

    override fun areContentsTheSame(oldItem: OrdersTableItem, newItem: OrdersTableItem): Boolean {
        return oldItem == newItem
    }
}