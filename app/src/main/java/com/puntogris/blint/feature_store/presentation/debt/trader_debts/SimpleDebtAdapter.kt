package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.SimpleDebt
import com.puntogris.blint.feature_store.presentation.debt.SimpleDebtDiffCallBack

class SimpleDebtAdapter(private val clickListener: (SimpleDebt) -> Unit) :
    PagingDataAdapter<SimpleDebt, SimpleDebtViewHolder>(
        SimpleDebtDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleDebtViewHolder {
        return SimpleDebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SimpleDebtViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}