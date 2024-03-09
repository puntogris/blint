package com.puntogris.blint.feature_store.presentation.orders.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.setAmountSymbolWithRecordType
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.setRecordType
import com.puntogris.blint.databinding.ProductRecordsVhBinding
import com.puntogris.blint.feature_store.domain.model.order.Record

class RecordsViewHolder private constructor(val binding: ProductRecordsVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(record: Record, clickListener: (Record) -> Unit) {
        with(binding) {
            viewRecordTypeIcon.setRecordType(record.type)
            textViewProductName.text = record.productName.capitalizeFirstChar()
            textViewRecordDate.setDateFromTimestamp(record.timestamp)
            textViewProductStock.text = record.amount.toString()
            textViewRecordTypeSign.setAmountSymbolWithRecordType(record.type)
            root.setOnClickListener { clickListener(record) }
        }
    }

    companion object {
        fun from(parent: ViewGroup): RecordsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductRecordsVhBinding.inflate(layoutInflater, parent, false)
            return RecordsViewHolder(binding)
        }
    }
}
