package com.puntogris.blint.feature_store.presentation.reports

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.databinding.DashboardReportVhBinding
import com.puntogris.blint.feature_store.domain.model.DashboardItem

class ReportDashboardViewHolder private constructor(val binding: DashboardReportVhBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(dashboardItem: DashboardItem) {
        binding.dashboardItem = dashboardItem
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ReportDashboardViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = DashboardReportVhBinding.inflate(layoutInflater, parent, false)
            return ReportDashboardViewHolder(binding)
        }
    }
}
