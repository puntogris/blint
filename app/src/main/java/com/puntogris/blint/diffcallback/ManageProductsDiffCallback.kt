package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories

class ManageProductsDiffCallback : DiffUtil.ItemCallback<ProductWithSuppliersCategories>() {
    override fun areItemsTheSame(oldItem: ProductWithSuppliersCategories, newItem: ProductWithSuppliersCategories): Boolean {
        return oldItem.product.productId == newItem.product.productId
    }

    override fun areContentsTheSame(oldItem: ProductWithSuppliersCategories, newItem: ProductWithSuppliersCategories): Boolean {
        return oldItem == newItem
    }
}