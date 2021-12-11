package com.puntogris.blint.feature_store.presentation.trader

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.Trader

class ClientDiffCallBack : DiffUtil.ItemCallback<Trader>() {
    override fun areItemsTheSame(oldItem: Trader, newItem: Trader): Boolean {
        return oldItem.traderId == newItem.traderId
    }

    override fun areContentsTheSame(oldItem: Trader, newItem: Trader): Boolean {
        return oldItem == newItem
    }
}