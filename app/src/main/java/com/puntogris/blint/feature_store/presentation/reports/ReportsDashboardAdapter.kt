package com.puntogris.blint.feature_store.presentation.reports

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.puntogris.blint.feature_store.domain.model.DashboardItem
import com.puntogris.blint.feature_store.presentation.main.DashboardItemDiffCallBack

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