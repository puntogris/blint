package com.puntogris.blint.feature_store.presentation.categories

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.Category

class CategoryDiffCallBack : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.categoryName == newItem.categoryName
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}