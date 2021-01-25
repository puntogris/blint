package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Record

class ProductRecordsDiffCallBack : DiffUtil.ItemCallback<Record>() {
    override fun areItemsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem.recordId == newItem.recordId
    }

    override fun areContentsTheSame(oldItem: Record, newItem: Record): Boolean {
        return oldItem == newItem
    }
}