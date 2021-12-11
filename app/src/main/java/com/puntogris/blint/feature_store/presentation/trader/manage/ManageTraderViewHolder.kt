package com.puntogris.blint.feature_store.presentation.trader.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageTradersVhBinding
import com.puntogris.blint.feature_store.domain.model.Trader

class ManageTraderViewHolder private constructor(val binding: ManageTradersVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(trader: Trader, clickListener: (Trader) -> Unit) {
        binding.client = trader
        binding.root.setOnClickListener { clickListener(trader) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ManageTraderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageTradersVhBinding.inflate(layoutInflater, parent, false)
            return ManageTraderViewHolder(binding)
        }
    }
}
