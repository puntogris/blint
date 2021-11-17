package com.puntogris.blint.diffcallback

import androidx.recyclerview.widget.DiffUtil
import com.puntogris.blint.model.FirestoreSupplier

class FirestoreSupplierDiffCallBack : DiffUtil.ItemCallback<FirestoreSupplier>() {
    override fun areItemsTheSame(oldItem: FirestoreSupplier, newItem: FirestoreSupplier): Boolean {
        return oldItem.supplierId == newItem.supplierId
    }

    override fun areContentsTheSame(
        oldItem: FirestoreSupplier,
        newItem: FirestoreSupplier
    ): Boolean {
        return oldItem == newItem
    }
}