package com.puntogris.blint.feature_store.presentation.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.CategoryProductVhBinding
import com.puntogris.blint.feature_store.domain.model.CheckableCategory

class ProductCategoryViewHolder private constructor(val binding: CategoryProductVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: CheckableCategory, clickListener: (CheckableCategory) -> Unit) {
        binding.category = category
        binding.categoryView.isChecked = category.isChecked
        binding.root.setOnClickListener {
            clickListener(category)
            binding.categoryView.toggle()
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

