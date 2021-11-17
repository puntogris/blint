package com.puntogris.blint.ui.supplier.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ManageSuppliersDiffCallBack
import com.puntogris.blint.model.Supplier

class ManageSuppliersAdapter(private val clickListener: (Supplier) -> Unit) :
    PagingDataAdapter<Supplier, ManageSupplierViewHolder>(
        ManageSuppliersDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageSupplierViewHolder {
        return ManageSupplierViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageSupplierViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}