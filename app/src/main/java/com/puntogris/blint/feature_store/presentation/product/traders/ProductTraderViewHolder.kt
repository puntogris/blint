package com.puntogris.blint.feature_store.presentation.product.traders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ProductTraderVhBinding
import com.puntogris.blint.feature_store.domain.model.CheckableTrader

class ProductTraderViewHolder private constructor(val binding: ProductTraderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(trader: CheckableTrader, clickListener: (CheckableTrader) -> Unit) {
        binding.trader = trader
        binding.root.setOnClickListener {
            clickListener(trader)
            binding.supplierView.toggle()
        }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ProductTraderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductTraderVhBinding.inflate(layoutInflater, parent, false)
            return ProductTraderViewHolder(binding)
        }
    }
}
