package com.puntogris.blint.feature_store.presentation.trader.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.presentation.trader.ClientDiffCallBack

class ManageTradersAdapter(private val clickListener: (Trader) -> Unit) :
    PagingDataAdapter<Trader, ManageClientViewHolder>(
        ClientDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageClientViewHolder {
        return ManageClientViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageClientViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}