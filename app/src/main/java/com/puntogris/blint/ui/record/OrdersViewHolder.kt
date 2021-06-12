package com.puntogris.blint.ui.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.OrderVhBinding
import com.puntogris.blint.model.Order

class OrdersViewHolder private constructor(val binding: OrderVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(order: Order, clickListener: (Order)-> Unit) {
        binding.order = order
        binding.root.setOnClickListener { clickListener(order) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): OrdersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = OrderVhBinding.inflate(layoutInflater,parent, false)
            return OrdersViewHolder(binding)
        }
    }
}
