package com.puntogris.blint.feature_store.presentation.trader.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageClientsVhBinding
import com.puntogris.blint.feature_store.domain.model.Trader

class ManageClientViewHolder private constructor(val binding: ManageClientsVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(trader: Trader, clickListener: (Trader) -> Unit) {
        binding.client = trader
        binding.root.setOnClickListener { clickListener(trader) }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ManageClientViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageClientsVhBinding.inflate(layoutInflater, parent, false)
            return ManageClientViewHolder(binding)
        }
    }
}
