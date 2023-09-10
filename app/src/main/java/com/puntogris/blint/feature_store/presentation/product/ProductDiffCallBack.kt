package com.puntogris.blint.feature_store.presentation.product

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.product.Product

class ProductDiffCallBack : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem.productId == newItem.productId
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
        return oldItem == newItem
    }
}
