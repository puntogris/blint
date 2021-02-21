package com.puntogris.blint.ui.backup

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.BusinessDiffCallback
import com.puntogris.blint.model.Employee

class BackupAdapter(private val clickListener: (Employee)->Unit): ListAdapter<Employee, BackupViewHolder>(BusinessDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BackupViewHolder {
        return BackupViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: BackupViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }


}