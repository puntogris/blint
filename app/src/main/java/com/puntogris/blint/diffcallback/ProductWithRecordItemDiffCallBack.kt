package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.product.ProductWithRecord


class ProductWithRecordItemDiffCallBack : DiffUtil.ItemCallback<ProductWithRecord>() {
    override fun areItemsTheSame(oldItem: ProductWithRecord, newItem: ProductWithRecord): Boolean {
        return oldItem.product == newItem.product
    }

    override fun areContentsTheSame(
        oldItem: ProductWithRecord,
        newItem: ProductWithRecord
    ): Boolean {
        return oldItem == newItem
    }
}