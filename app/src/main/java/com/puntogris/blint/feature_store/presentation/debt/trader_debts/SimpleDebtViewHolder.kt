package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.SimpleDebtVhBinding
import com.puntogris.blint.feature_store.domain.model.SimpleDebt

class SimpleDebtViewHolder private constructor(val binding: SimpleDebtVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(debt: SimpleDebt, clickListener: (SimpleDebt) -> (Unit)) {
        binding.debt = debt
        binding.root.setOnClickListener { clickListener(debt) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): SimpleDebtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = SimpleDebtVhBinding.inflate(layoutInflater, parent, false)
            return SimpleDebtViewHolder(binding)
        }
    }
}