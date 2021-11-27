package com.puntogris.blint.feature_store.presentation.product

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier

class CheckableSupplierDiffCallBack : DiffUtil.ItemCallback<CheckableSupplier>() {

    override fun areItemsTheSame(oldItem: CheckableSupplier, newItem: CheckableSupplier): Boolean {
        return oldItem.supplier.supplierId == newItem.supplier.supplierId
    }

    override fun areContentsTheSame(
        oldItem: CheckableSupplier,
        newItem: CheckableSupplier
    ): Boolean {
        return oldItem == newItem
    }
}