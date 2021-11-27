package com.puntogris.blint.feature_store.presentation.supplier

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.Supplier

class SupplierDiffCallBack : DiffUtil.ItemCallback<Supplier>() {
    override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem.supplierId == newItem.supplierId
    }

    override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem == newItem
    }
}