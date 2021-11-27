package com.puntogris.blint.feature_store.presentation.orders

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.order.Record

class RecordDiffCallBack : DiffUtil.ItemCallback<Record>() {
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.recordId == newItem.recordId
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem == newItem
    }
}