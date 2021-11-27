package com.puntogris.blint.feature_store.presentation.debt

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.SimpleDebt

class SimpleDebtDiffCallBack : DiffUtil.ItemCallback<SimpleDebt>() {
    override fun areItemsTheSame(oldItem: SimpleDebt, newItem: SimpleDebt): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SimpleDebt, newItem: SimpleDebt): Boolean {
        return oldItem == newItem
    }
}