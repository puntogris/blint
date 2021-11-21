package com.puntogris.blint.ui.debt.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.DebtDiffCallBack
import com.puntogris.blint.model.order.Debt

class ManageDebtsAdapter(private val clickListener: (Debt) -> Unit) :
    PagingDataAdapter<Debt, ManageDebtViewHolder>(
        DebtDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageDebtViewHolder {
        return ManageDebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageDebtViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}