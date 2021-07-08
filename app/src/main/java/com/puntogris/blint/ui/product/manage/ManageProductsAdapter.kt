package com.puntogris.blint.ui.product.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ManageProductsDiffCallback
import com.puntogris.blint.model.ProductWithSuppliersCategories

class ManageProductsAdapter(
    private val shortClickListener: (ProductWithSuppliersCategories) -> Unit,
    private val longClickListener: (ProductWithSuppliersCategories) -> Unit
): PagingDataAdapter<ProductWithSuppliersCategories, ManageProductsViewHolder>(ManageProductsDiffCallback()) {

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProductsViewHolder {
        return ManageProductsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageProductsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, shortClickListener, longClickListener)
    }

}