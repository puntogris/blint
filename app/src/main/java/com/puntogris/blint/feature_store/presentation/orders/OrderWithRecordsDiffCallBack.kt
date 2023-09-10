package com.puntogris.blint.feature_store.presentation.orders


import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords

class OrderWithRecordsDiffCallBack : DiffUtil.ItemCallback<OrderWithRecords>() {
    override fun areItemsTheSame(oldItem: OrderWithRecords, newItem: OrderWithRecords): Boolean {
        return oldItem.order.orderId == newItem.order.orderId
    }

    override fun areContentsTheSame(oldItem: OrderWithRecords, newItem: OrderWithRecords): Boolean {
        return oldItem == newItem
    }
}
