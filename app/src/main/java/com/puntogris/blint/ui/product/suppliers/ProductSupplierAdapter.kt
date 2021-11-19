package com.puntogris.blint.ui.product.suppliers

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.CheckableSupplierDiffCallBack
import com.puntogris.blint.model.CheckableSupplier

class ProductSupplierAdapter(private val clickListener: (CheckableSupplier) -> Unit) :
    PagingDataAdapter<CheckableSupplier, ProductSupplierViewHolder>(
        CheckableSupplierDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductSupplierViewHolder {
        return ProductSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductSupplierViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}

