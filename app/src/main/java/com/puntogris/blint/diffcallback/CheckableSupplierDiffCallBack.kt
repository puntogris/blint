package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.CheckableSupplier

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