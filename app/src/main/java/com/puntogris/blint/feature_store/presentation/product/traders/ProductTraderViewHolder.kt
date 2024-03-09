package com.puntogris.blint.feature_store.presentation.product.traders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.databinding.ProductTraderVhBinding
import com.puntogris.blint.feature_store.domain.model.CheckableTrader

class ProductTraderViewHolder private constructor(val binding: ProductTraderVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(trader: CheckableTrader, clickListener: (CheckableTrader) -> Unit) {
        with(binding) {
            textViewTraderName.isChecked = trader.isChecked
            textViewTraderName.text = trader.trader.name.capitalizeFirstChar()
            root.setOnClickListener {
                clickListener(trader)
                textViewTraderName.toggle()
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ProductTraderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductTraderVhBinding.inflate(layoutInflater, parent, false)
            return ProductTraderViewHolder(binding)
        }
    }
}
