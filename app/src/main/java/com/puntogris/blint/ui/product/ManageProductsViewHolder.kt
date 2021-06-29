package com.puntogris.blint.ui.product

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageProductsVhBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories

class ManageProductsViewHolder private constructor(val binding: ManageProductsVhBinding) : RecyclerView.ViewHolder(binding.root){
    fun bind(product: ProductWithSuppliersCategories, clickListener: (ProductWithSuppliersCategories)-> Unit) {
        binding.product = product
        binding.root.setOnClickListener { clickListener(product) }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ManageProductsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageProductsVhBinding.inflate(layoutInflater,parent, false)
            return ManageProductsViewHolder(binding)
        }
    }
}
