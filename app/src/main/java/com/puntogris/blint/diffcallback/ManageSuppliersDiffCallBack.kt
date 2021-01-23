package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Supplier

class ManageSuppliersDiffCallBack : DiffUtil.ItemCallback<Supplier>() {
    override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem.supplierId == newItem.supplierId
    }

    override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem == newItem
    }
}