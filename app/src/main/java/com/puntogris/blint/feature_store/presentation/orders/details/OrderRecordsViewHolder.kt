package com.puntogris.blint.feature_store.presentation.orders.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.setImageFullSize
import com.puntogris.blint.common.utils.setNumberToMoneyString
import com.puntogris.blint.databinding.OrderRecordVhBinding
import com.puntogris.blint.feature_store.domain.model.order.Record

class OrderRecordsViewHolder private constructor(val binding: OrderRecordVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(record: Record) {
        with(binding) {
            setImageFullSize(imageViewProductImage, record.productImage)
            textViewProductName.text = record.productName.capitalizeFirstChar()
            setNumberToMoneyString(textViewRecordTotal, record.total)
            textViewRecordAmount.text = record.amount.toString()
        }
    }

    companion object {
        fun from(parent: ViewGroup): OrderRecordsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = OrderRecordVhBinding.inflate(layoutInflater, parent, false)
            return OrderRecordsViewHolder(binding)
        }
    }
}
