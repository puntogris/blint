package com.puntogris.blint.ui.orders.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ProductRecordsDiffCallBack
import com.puntogris.blint.model.Record

class RecordsAdapter(private val clickListener: (Record) -> Unit): PagingDataAdapter<Record, RecordsViewHolder>(
    ProductRecordsDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        return RecordsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}