package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.SimpleDebt

class SimpleDebtDiffCallBack : DiffUtil.ItemCallback<SimpleDebt>() {
    override fun areItemsTheSame(oldItem: SimpleDebt, newItem: SimpleDebt): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SimpleDebt, newItem: SimpleDebt): Boolean {
        return oldItem == newItem
    }
}