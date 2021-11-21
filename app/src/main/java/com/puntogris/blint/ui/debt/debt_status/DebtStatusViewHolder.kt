package com.puntogris.blint.ui.debt.debt_status

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.DebtVhBinding
import com.puntogris.blint.model.order.Debt

class DebtStatusViewHolder private constructor(val binding: DebtVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(debt: Debt, clickListener: (Debt) -> (Unit)) {
        binding.debt = debt
        binding.root.setOnClickListener { clickListener(debt) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): DebtStatusViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DebtVhBinding.inflate(layoutInflater, parent, false)
            return DebtStatusViewHolder(binding)
        }
    }
}
