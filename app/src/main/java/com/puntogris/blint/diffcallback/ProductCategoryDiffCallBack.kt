package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.FirestoreCategory

class ProductCategoryDiffCallBack : DiffUtil.ItemCallback<FirestoreCategory>() {
    override fun areItemsTheSame(oldItem: FirestoreCategory, newItem: FirestoreCategory): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: FirestoreCategory, newItem: FirestoreCategory): Boolean {
        return oldItem == newItem
    }
}