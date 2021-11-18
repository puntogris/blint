package com.puntogris.blint.ui.product.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ManageProductsDiffCallback
import com.puntogris.blint.model.ProductWithDetails

class ManageProductsAdapter(
    private val shortClickListener: (ProductWithDetails) -> Unit,
    private val longClickListener: (ProductWithDetails) -> Unit
) : PagingDataAdapter<ProductWithDetails, ManageProductsViewHolder>(
    ManageProductsDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProductsViewHolder {
        return ManageProductsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageProductsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, shortClickListener, longClickListener)
    }

}