package com.puntogris.blint.ui.product

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ManageProductsDiffCallback
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories

class ManageProductsAdapter(private val clickListener: (ProductWithSuppliersCategories) -> Unit): PagingDataAdapter<ProductWithSuppliersCategories, ManageProductsViewHolder>(ManageProductsDiffCallback()) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProductsViewHolder {
        return ManageProductsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageProductsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}