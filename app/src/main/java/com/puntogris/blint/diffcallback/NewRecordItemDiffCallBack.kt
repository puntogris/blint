package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.order.NewRecord

class NewRecordItemDiffCallBack : DiffUtil.ItemCallback<NewRecord>() {
    override fun areItemsTheSame(oldItem: NewRecord, newItem: NewRecord): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: NewRecord, newItem: NewRecord): Boolean {
        return oldItem == newItem
    }
}