package com.puntogris.blint.ui.product.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageCategoryVhBinding
import com.puntogris.blint.model.Category
import com.puntogris.blint.model.FirestoreCategory

class ProductCategoryViewHolder private constructor(val binding: ManageCategoryVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(category: FirestoreCategory){
        binding.category = Category(name = category.name)
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): ProductCategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageCategoryVhBinding.inflate(layoutInflater,parent, false)
            return ProductCategoryViewHolder(binding)
        }
    }
}
