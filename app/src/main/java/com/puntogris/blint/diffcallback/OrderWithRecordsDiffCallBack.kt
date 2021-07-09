package com.puntogris.blint.diffcallback


import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Order
import com.puntogris.blint.model.OrderWithRecords

class OrderWithRecordsDiffCallBack : DiffUtil.ItemCallback<OrderWithRecords>() {
    override fun areItemsTheSame(oldItem: OrderWithRecords, newItem: OrderWithRecords): Boolean {
        return oldItem.order.orderId == newItem.order.orderId
    }

    override fun areContentsTheSame(oldItem: OrderWithRecords, newItem: OrderWithRecords): Boolean {
        return oldItem == newItem
    }
}