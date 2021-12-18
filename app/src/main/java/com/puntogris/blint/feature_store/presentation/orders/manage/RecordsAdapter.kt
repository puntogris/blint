package com.puntogris.blint.feature_store.presentation.orders.manage

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.feature_store.domain.model.order.Record
import com.puntogris.blint.feature_store.presentation.orders.RecordDiffCallBack

class RecordsAdapter(private val clickListener: (Record) -> Unit) :
    PagingDataAdapter<Record, RecordsViewHolder>(RecordDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        return RecordsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}