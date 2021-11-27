package com.puntogris.blint.feature_store.presentation.orders.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.OrderTableItemVhBinding
import com.puntogris.blint.feature_store.domain.model.order.OrdersTableItem

class OrdersTableItemViewHolder private constructor(val binding: OrderTableItemVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(ordersTableItem: OrdersTableItem) {
        binding.item = ordersTableItem
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): OrdersTableItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = OrderTableItemVhBinding.inflate(layoutInflater, parent, false)
            return OrdersTableItemViewHolder(binding)
        }
    }
}
