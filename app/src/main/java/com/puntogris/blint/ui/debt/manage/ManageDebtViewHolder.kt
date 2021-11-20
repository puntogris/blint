package com.puntogris.blint.ui.debt.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.DebtPagingVhBinding
import com.puntogris.blint.model.Debt

class ManageDebtViewHolder private constructor(val binding: DebtPagingVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(debt: Debt, clickListener: (Debt) -> (Unit)) {
        binding.debt = debt
        binding.root.setOnClickListener { clickListener(debt) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ManageDebtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DebtPagingVhBinding.inflate(layoutInflater, parent, false)
            return ManageDebtViewHolder(binding)
        }
    }
}
