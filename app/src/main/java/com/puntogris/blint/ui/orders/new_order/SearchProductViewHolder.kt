package com.puntogris.blint.ui.orders.new_order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ProductSearchVhBinding
import com.puntogris.blint.model.Product

class SearchProductViewHolder private constructor(val binding: ProductSearchVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(product: Product, clickListener: (Product)-> Unit) {
        binding.product = product
        binding.root.setOnClickListener { clickListener(product) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): SearchProductViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductSearchVhBinding.inflate(layoutInflater,parent, false)
            return SearchProductViewHolder(binding)
        }
    }
}