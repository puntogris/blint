package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.CheckableCategory

class CheckableCategoryDiffCallBack : DiffUtil.ItemCallback<CheckableCategory>() {
    override fun areItemsTheSame(oldItem: CheckableCategory, newItem: CheckableCategory): Boolean {
        return oldItem.category.categoryName == newItem.category.categoryName
    }

    override fun areContentsTheSame(
        oldItem: CheckableCategory,
        newItem: CheckableCategory
    ): Boolean {
        return oldItem == newItem
    }
}