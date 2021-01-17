package com.puntogris.blint.ui.backup

import android.view.ViewGroup
import android.widget.Toolbar
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.BusinessDiffCallback
import com.puntogris.blint.model.Business
import com.puntogris.blint.ui.business.JoinBusinessViewHolder

class BackupAdapter(private val clickListener: (Business)->Unit): ListAdapter<Business, BackupViewHolder>(BusinessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        return BackupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


}