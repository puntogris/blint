package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.TraderDebtVhBinding
import com.puntogris.blint.feature_store.domain.model.Trader

class TraderDebtViewHolder private constructor(val binding: TraderDebtVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(trader: Trader, clickListener: (Trader) -> (Unit)) {
        binding.trader = trader
        binding.root.setOnClickListener { clickListener(trader) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): TraderDebtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TraderDebtVhBinding.inflate(layoutInflater, parent, false)
            return TraderDebtViewHolder(binding)
        }
    }
}
