package com.puntogris.blint.feature_store.presentation.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ProductCategoryVhBinding
import com.puntogris.blint.feature_store.domain.model.CheckableCategory

class ProductCategoryViewHolder private constructor(val binding: ProductCategoryVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: CheckableCategory, clickListener: (CheckableCategory) -> Unit) {
        with(binding) {
            textViewCategoryName.text = category.category.categoryName
            textViewCategoryName.isChecked = category.isChecked
            root.setOnClickListener {
                clickListener(category)
                textViewCategoryName.toggle()
            }
        }
    }

    companion object {
        fun from(parent: ViewGroup): ProductCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ProductCategoryVhBinding.inflate(layoutInflater, parent, false)
            return ProductCategoryViewHolder(binding)
        }
    }
}
