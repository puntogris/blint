package com.puntogris.blint.feature_store.presentation.debt.trader_debts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.setDebtColor
import com.puntogris.blint.databinding.TraderDebtVhBinding
import com.puntogris.blint.feature_store.domain.model.Trader

class TraderDebtViewHolder private constructor(val binding: TraderDebtVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(trader: Trader, clickListener: (Trader) -> (Unit)) {
        with(binding) {
            textViewTraderName.text = trader.name.capitalizeFirstChar()
            textViewTraderDebt.setDebtColor(trader.debt)
            root.setOnClickListener { clickListener(trader) }
        }
    }

    companion object {
        fun from(parent: ViewGroup): TraderDebtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = TraderDebtVhBinding.inflate(layoutInflater, parent, false)
            return TraderDebtViewHolder(binding)
        }
    }
}
