package com.puntogris.blint.feature_store.presentation.supplier.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.presentation.supplier.SupplierDiffCallBack

class ManageSuppliersAdapter(private val clickListener: (Supplier) -> Unit) :
    PagingDataAdapter<Supplier, ManageSupplierViewHolder>(
        SupplierDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSupplierViewHolder {
        return ManageSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageSupplierViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}