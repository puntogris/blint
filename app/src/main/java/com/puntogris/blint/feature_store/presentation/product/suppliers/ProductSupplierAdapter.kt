package com.puntogris.blint.feature_store.presentation.product.suppliers

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.CheckableSupplier
import com.puntogris.blint.feature_store.presentation.product.CheckableSupplierDiffCallBack

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

