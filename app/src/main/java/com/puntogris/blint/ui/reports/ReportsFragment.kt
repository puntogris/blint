package com.puntogris.blint.ui.reports

import android.graphics.Path
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReportsBinding
import com.puntogris.blint.model.DashboardItem
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>(R.layout.fragment_reports) {

    private val viewModel:ReportsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this

        val reportDashboardAdapter = ReportsDashboardAdapter()
        binding.dashboardRv.apply {
            adapter = reportDashboardAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }

        lifecycleScope.launchWhenStarted {
            val statistics = viewModel.getStatistics()
            val data = listOf(
                DashboardItem("Productos", statistics.totalProducts.toString()),
                DashboardItem("Proveedores", statistics.totalSuppliers.toString()),
                DashboardItem("Clientes", statistics.totalClients.toString())
                )
            reportDashboardAdapter.submitList(data)
        }
    }

    fun onProductsReportClicked(){
        showTimeFrameBottomSheet("PRODUCTS_REPORT")
    }

    fun onClientsReportClicked(){
        showTimeFrameBottomSheet("CLIENTS_REPORT")
    }

    fun onSuppliersReportClicked(){
        showTimeFrameBottomSheet("SUPPLIERS_REPORT")
    }

    fun onProductListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = "PRODUCT_LIST_REPORT")
        findNavController().navigate(action)
    }

    fun onClientListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = "CLIENT_LIST_REPORT")
        findNavController().navigate(action)
    }

    fun onSuppliersListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = "SUPPLIER_LIST_REPORT")
        findNavController().navigate(action)
    }

    private fun showTimeFrameBottomSheet(code:String){
        OptionsSheet().build(requireContext()) {
            title("Periodo de tiempo del reporte")
            displayMode(DisplayMode.LIST)
            with(
                Option("Semanal"),
                Option("Mensual"),
                Option("Trimestral"),
                Option("Bianual"),
                Option("Anual"),
                Option("Historico"),
                Option("Periodo de tiempo personalizado")
            )
            onPositive { index: Int, _: Option ->
                val timeCode = when(index){
                    0 -> "WEEKLY"
                    1 -> "MONTHLY"
                    2 -> "QUARTERLY"
                    3 -> "BIANNUAL"
                    4 -> "ANNUAL"
                    5 -> "HISTORICAL"
                    else -> "CUSTOM"
                }
                if (timeCode == "CUSTOM") {
                    CalendarSheet().build(requireContext()) {
                        title("Seleccionar periodo de tiempo.")
                        selectionMode(SelectionMode.RANGE)
                        showButtons(true)
                        onPositive("OK") { dateStart, dateEnd ->
                            val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(
                                reportCode = code,
                                timeCode = timeCode,
                                startTime = dateStart.time.time,
                                endTime = dateEnd!!.time.time
                            )
                            this@ReportsFragment.findNavController().navigate(action)
                        }
                    }.show(parentFragmentManager, "")
                }else{
                    val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = code, timeCode = timeCode)
                    this@ReportsFragment.findNavController().navigate(action)
                }
            }
        }.show(parentFragmentManager, "")
    }
}