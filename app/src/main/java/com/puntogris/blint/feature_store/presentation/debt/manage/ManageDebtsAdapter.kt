package com.puntogris.blint.feature_store.presentation.debt.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.order.Debt
import com.puntogris.blint.feature_store.presentation.debt.DebtDiffCallBack

class ManageDebtsAdapter :
    PagingDataAdapter<Debt, ManageDebtViewHolder>(
        DebtDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageDebtViewHolder {
        return ManageDebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageDebtViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}
