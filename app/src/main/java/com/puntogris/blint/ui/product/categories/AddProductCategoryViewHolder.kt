package com.puntogris.blint.ui.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.AddCategoryProductVhBinding
import com.puntogris.blint.databinding.ManageCategoryVhBinding
import com.puntogris.blint.model.Category

class AddProductCategoryViewHolder private constructor(val binding: AddCategoryProductVhBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(category: Category, clickListener: (Category) -> Unit) {
        binding.category = category
        binding.categoryButton.setOnClickListener {
            clickListener(category)
        }
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): AddProductCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = AddCategoryProductVhBinding.inflate(layoutInflater, parent, false)
            return AddProductCategoryViewHolder(binding)
        }
    }
}
