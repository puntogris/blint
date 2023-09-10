package com.puntogris.blint.feature_store.presentation.product

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails

class ProductWithDetailsDiffCallback : DiffUtil.ItemCallback<ProductWithDetails>() {
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
