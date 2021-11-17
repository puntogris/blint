package com.puntogris.blint.ui.backup

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.BusinessDiffCallBack
import com.puntogris.blint.model.Business

class BackupAdapter : ListAdapter<Business, BackupViewHolder>(BusinessDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        return BackupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}