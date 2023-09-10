package com.puntogris.blint.feature_store.presentation.trader.detail

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.presentation.debt.DebtDiffCallBack

class DebtStatusAdapter(private val clickListener: (Debt) -> (Unit)) :
    PagingDataAdapter<Debt, DebtStatusViewHolder>(
        DebtDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtStatusViewHolder {
        return DebtStatusViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DebtStatusViewHolder, position: Int) {
        holder.bind(getItem(position) ?: return, clickListener)
    }
}
