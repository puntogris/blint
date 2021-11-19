package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.product.ProductWithDetails

class ManageProductsDiffCallback : DiffUtil.ItemCallback<ProductWithDetails>() {
    override fun areItemsTheSame(
        oldItem: ProductWithDetails,
        newItem: ProductWithDetails
    ): Boolean {
        return oldItem.product.productId == newItem.product.productId
    }

    override fun areContentsTheSame(
        oldItem: ProductWithDetails,
        newItem: ProductWithDetails
    ): Boolean {
        return oldItem == newItem
    }
}