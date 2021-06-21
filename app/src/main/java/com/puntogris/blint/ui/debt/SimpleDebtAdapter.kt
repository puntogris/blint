package com.puntogris.blint.ui.debt

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.SimpleDebtDiffCallBack
import com.puntogris.blint.model.SimpleDebt
import com.puntogris.blint.ui.client.ManageClientViewHolder

class SimpleDebtAdapter(private val clickListener: (SimpleDebt) -> Unit): PagingDataAdapter<SimpleDebt, SimpleDebtViewHolder>(
    SimpleDebtDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleDebtViewHolder {
        return SimpleDebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SimpleDebtViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}