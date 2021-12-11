package com.puntogris.blint.feature_store.presentation.product.traders

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.CheckableTrader
import com.puntogris.blint.feature_store.presentation.product.CheckableTraderDiffCallBack

class ProductTraderAdapter(private val clickListener: (CheckableTrader) -> Unit) :
    PagingDataAdapter<CheckableTrader, ProductTraderViewHolder>(
        CheckableTraderDiffCallBack()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductTraderViewHolder {
        return ProductTraderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductTraderViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

}

