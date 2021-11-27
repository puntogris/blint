package com.puntogris.blint.feature_store.presentation.product.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import com.puntogris.blint.feature_store.presentation.product.ProductWithDetailsDiffCallback

class ManageProductsAdapter(
    private val shortClickListener: (ProductWithDetails) -> Unit,
    private val longClickListener: (ProductWithDetails) -> Unit
) : PagingDataAdapter<ProductWithDetails, ManageProductsViewHolder>(
    ProductWithDetailsDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageProductsViewHolder {
        return ManageProductsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageProductsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, shortClickListener, longClickListener)
    }

}