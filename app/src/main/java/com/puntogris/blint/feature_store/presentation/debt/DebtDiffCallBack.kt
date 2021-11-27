package com.puntogris.blint.feature_store.presentation.debt

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.order.Debt

class DebtDiffCallBack : DiffUtil.ItemCallback<Debt>() {
    override fun areItemsTheSame(oldItem: Debt, newItem: Debt): Boolean {
        return oldItem.debtId == newItem.debtId
    }

    override fun areContentsTheSame(oldItem: Debt, newItem: Debt): Boolean {
        return oldItem == newItem
    }
}