package com.puntogris.blint.ui.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.RemoveCategoryProductVhBinding
import com.puntogris.blint.model.Category

class RemoveProductCategoryViewHolder private constructor(val binding: RemoveCategoryProductVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(category: Category, clickListener: (Category) -> Unit) {
        binding.category = category
        binding.categoryButton.setOnClickListener {
            clickListener(category)
        }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): RemoveProductCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RemoveCategoryProductVhBinding.inflate(layoutInflater,parent, false)
            return RemoveProductCategoryViewHolder(binding)
        }
    }
}
