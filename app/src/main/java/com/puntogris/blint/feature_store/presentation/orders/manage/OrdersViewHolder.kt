package com.puntogris.blint.feature_store.presentation.orders.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.OrderVhBinding
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords

class OrdersViewHolder private constructor(val binding: OrderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderWithRecords, clickListener: (OrderWithRecords) -> Unit) {
        binding.order = order
        binding.root.setOnClickListener { clickListener(order) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): OrdersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = OrderVhBinding.inflate(layoutInflater, parent, false)
            return OrdersViewHolder(binding)
        }
    }
}
