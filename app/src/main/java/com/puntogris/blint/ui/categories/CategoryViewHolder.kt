package com.puntogris.blint.ui.categories

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.ManageCategoryVhBinding
import com.puntogris.blint.model.Category

class CategoryViewHolder private constructor(val binding: ManageCategoryVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(category: Category, clickListener: ()-> Unit) {
        binding.category = category
        binding.root.setOnClickListener {
            category.selected = !category.selected
            clickListener()
        }
        binding.executePendingBindings()
    }

    companion object{
        fun from(parent: ViewGroup): CategoryViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ManageCategoryVhBinding.inflate(layoutInflater,parent, false)
            return CategoryViewHolder(binding)
        }
    }

}
