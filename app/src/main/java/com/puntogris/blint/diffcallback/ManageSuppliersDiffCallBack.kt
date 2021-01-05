package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier

class ManageSuppliersDiffCallBack : DiffUtil.ItemCallback<Supplier>() {
    override fun areItemsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Supplier, newItem: Supplier): Boolean {
        return oldItem == newItem
    }
}