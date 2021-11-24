package com.puntogris.blint.ui.orders.detailed_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CreateRecordItemVhBinding
import com.puntogris.blint.model.order.NewRecord

class OrderItemViewHolder private constructor(val binding: CreateRecordItemVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(newRecord: NewRecord, dataChangedListener: () -> Unit) {
        binding.item = newRecord

        binding.recordAmount.addTextChangedListener { editable ->
            editable.toString().toIntOrNull()?.let {
                newRecord.amount = it
                dataChangedListener()
            }
        }
        binding.recordValue.addTextChangedListener { editable ->
            editable.toString().toFloatOrNull()?.let {
                newRecord.productUnitPrice = it
                dataChangedListener()
            }
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): OrderItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CreateRecordItemVhBinding.inflate(layoutInflater, parent, false)
            return OrderItemViewHolder(binding)
        }
    }
}
