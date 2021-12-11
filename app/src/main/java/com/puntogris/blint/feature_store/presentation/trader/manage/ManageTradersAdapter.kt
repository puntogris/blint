package com.puntogris.blint.feature_store.presentation.trader.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.presentation.trader.TraderDiffCallBack

class ManageTradersAdapter(private val clickListener: (Trader) -> Unit) :
    PagingDataAdapter<Trader, ManageTraderViewHolder>(
        TraderDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageTraderViewHolder {
        return ManageTraderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageTraderViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}