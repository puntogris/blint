package com.puntogris.blint.ui.product

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ProductRecordsDiffCallBack
import com.puntogris.blint.model.Record

class ProductRecordsAdapter(private val clickListener: (Record) -> Unit): PagingDataAdapter<Record, ProductRecordViewHolder>(
    ProductRecordsDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRecordViewHolder {
        return ProductRecordViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProductRecordViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}