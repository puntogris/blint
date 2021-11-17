package com.puntogris.blint.ui.orders.detailed_order

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.ProductDiffCallBack
import com.puntogris.blint.model.Product

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
