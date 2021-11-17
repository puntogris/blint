package com.puntogris.blint.ui.product.manage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageProductsVhBinding
import com.puntogris.blint.model.ProductWithSuppliersCategories

class ManageProductsViewHolder private constructor(val binding: ManageProductsVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(
        product: ProductWithSuppliersCategories,
        shortClickListener: (ProductWithSuppliersCategories) -> Unit,
        longClickListener: (ProductWithSuppliersCategories) -> Unit
    ) {
        binding.product = product
        binding.root.setOnClickListener { shortClickListener(product) }
        binding.root.setOnLongClickListener {
            longClickListener(product)
            true
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ManageProductsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageProductsVhBinding.inflate(layoutInflater, parent, false)
            return ManageProductsViewHolder(binding)
        }
    }
}
