package com.puntogris.blint.feature_store.presentation.product

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.CheckableTrader

class CheckableTraderDiffCallBack : DiffUtil.ItemCallback<CheckableTrader>() {

    override fun areItemsTheSame(oldItem: CheckableTrader, newItem: CheckableTrader): Boolean {
        return oldItem.trader.traderId == newItem.trader.traderId
    }

    override fun areContentsTheSame(
        oldItem: CheckableTrader,
        newItem: CheckableTrader
    ): Boolean {
        return oldItem == newItem
    }
}
