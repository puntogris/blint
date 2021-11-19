package com.puntogris.blint.ui.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CategoryProductVhBinding
import com.puntogris.blint.model.Category

class ProductCategoryViewHolder private constructor(val binding: CategoryProductVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category, clickListener: (Category) -> Unit) {
        binding.category = category
        binding.root.setOnClickListener {
            clickListener(category)
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ProductCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CategoryProductVhBinding.inflate(layoutInflater, parent, false)
            return ProductCategoryViewHolder(binding)
        }
    }
}
