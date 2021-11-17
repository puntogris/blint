package com.puntogris.blint.ui.debt

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.DebtDiffCallBack
import com.puntogris.blint.model.Debt

class DebtsAdapter(private val clickListener: (Debt) -> (Unit)) : ListAdapter<Debt, DebtViewHolder>(
    DebtDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DebtViewHolder {
        return DebtViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DebtViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }
}