package com.puntogris.blint.ui.reports

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.diffcallback.DashboardItemDiffCallBack
import com.puntogris.blint.model.DashboardItem

class ReportsDashboardAdapter : ListAdapter<DashboardItem, ReportDashboardViewHolder>(
    DashboardItemDiffCallBack()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportDashboardViewHolder {
        return ReportDashboardViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReportDashboardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}