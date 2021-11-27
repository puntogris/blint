package com.puntogris.blint.feature_store.presentation.product.suppliers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ProductSupplierVhBinding
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier

class ProductSupplierViewHolder private constructor(val binding: ProductSupplierVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(supplier: CheckableSupplier, clickListener: (CheckableSupplier) -> Unit) {
        binding.supplier = supplier
        binding.root.setOnClickListener {
            clickListener(supplier)
            binding.supplierView.toggle()
        }

        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ProductSupplierViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductSupplierVhBinding.inflate(layoutInflater, parent, false)
            return ProductSupplierViewHolder(binding)
        }
    }
}
