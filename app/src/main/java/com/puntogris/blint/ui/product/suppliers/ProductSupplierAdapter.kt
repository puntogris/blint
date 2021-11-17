package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.FirestoreSupplierDiffCallBack
import com.puntogris.blint.model.FirestoreSupplier

class ProductSupplierAdapter(private val clickListener: (FirestoreSupplier) -> Unit) :
    PagingDataAdapter<FirestoreSupplier, ProductSupplierViewHolder>(
        FirestoreSupplierDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSupplierViewHolder {
        return ProductSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductSupplierViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}

