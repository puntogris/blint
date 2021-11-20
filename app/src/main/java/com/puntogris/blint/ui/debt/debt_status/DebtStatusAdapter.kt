package com.puntogris.blint.ui.debt.debt_status

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.DebtDiffCallBack
import com.puntogris.blint.model.Debt

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