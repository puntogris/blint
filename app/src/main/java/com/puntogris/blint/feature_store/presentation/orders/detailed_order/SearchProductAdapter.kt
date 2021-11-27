package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.presentation.product.ProductDiffCallBack

class SearchProductAdapter(private val clickListener: (Product) -> Unit) :
    ListAdapter<Product, SearchProductViewHolder>(
        ProductDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductViewHolder {
        return SearchProductViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchProductViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}
