package com.puntogris.blint.ui.debt

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.DebtDiffCallBack
import com.puntogris.blint.model.Debt

class DebtPagingAdapter(private val clickListener: (Debt) -> Unit) :
    PagingDataAdapter<Debt, DebtPagingViewHolder>(
        DebtDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtPagingViewHolder {
        return DebtPagingViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DebtPagingViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}