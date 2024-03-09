package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.databinding.ProductSearchVhBinding
import com.puntogris.blint.feature_store.domain.model.product.Product

class SearchProductViewHolder private constructor(val binding: ProductSearchVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(product: Product, clickListener: (Product) -> Unit) {
        with(binding) {
            textViewProductName.text = product.name.capitalizeFirstChar()
            textViewProductStock.text = product.stock.toString()
            root.setOnClickListener { clickListener(product) }
        }
    }

    companion object {
        fun from(parent: ViewGroup): SearchProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductSearchVhBinding.inflate(layoutInflater, parent, false)
            return SearchProductViewHolder(binding)
        }
    }
}
