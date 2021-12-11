package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.presentation.trader.TraderDiffCallBack

class TraderDebtAdapter(private val clickListener: (Trader) -> Unit) :
    PagingDataAdapter<Trader, TraderDebtViewHolder>(
        TraderDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TraderDebtViewHolder {
        return TraderDebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TraderDebtViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}