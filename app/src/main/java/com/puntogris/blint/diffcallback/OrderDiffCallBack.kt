package com.puntogris.blint.diffcallback


import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Order

class OrderDiffCallBack : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}