package com.puntogris.blint.ui.orders

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ProductRecordsDiffCallBack
import com.puntogris.blint.model.Record

class ProductsRecordsAdapter(private val clickListener: (Record) -> Unit): PagingDataAdapter<Record, ProductsRecordsViewHolder>(
    ProductRecordsDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsRecordsViewHolder {
        return ProductsRecordsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductsRecordsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}