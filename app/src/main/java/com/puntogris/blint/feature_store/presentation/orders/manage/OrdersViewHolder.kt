package com.puntogris.blint.feature_store.presentation.orders.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.setAmountSymbolWithRecordType
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.setOrderNumberTitle
import com.puntogris.blint.common.utils.setRecordType
import com.puntogris.blint.databinding.OrderVhBinding
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords

class OrdersViewHolder private constructor(val binding: OrderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(order: OrderWithRecords, clickListener: (OrderWithRecords) -> Unit) {
        with(binding) {
            viewOrderTypeIcon.setRecordType(order.order.type)
            textViewOrderNumber.setOrderNumberTitle(order.order.number)
            textViewOrderDate.setDateFromTimestamp(order.order.timestamp)
            textViewOrderTotal.text = order.order.total.toString()
            textViewOrderTypeSign.setAmountSymbolWithRecordType(order.order.type)
            root.setOnClickListener {
                clickListener(order)
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): OrdersViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = OrderVhBinding.inflate(layoutInflater, parent, false)
            return OrdersViewHolder(binding)
        }
    }
}
