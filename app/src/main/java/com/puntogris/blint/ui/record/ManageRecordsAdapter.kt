package com.puntogris.blint.ui.record

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.puntogris.blint.diffcallback.ProductRecordsDiffCallBack
import com.puntogris.blint.model.Record

class ManageRecordsAdapter(private val clickListener: (Record) -> Unit): PagingDataAdapter<Record, ManageRecordsViewHolder>(
    ProductRecordsDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ManageRecordsViewHolder {
        return ManageRecordsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ManageRecordsViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }
}