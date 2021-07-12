package com.puntogris.blint.ui.reports

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.maxkeppeler.sheets.calendar.CalendarSheet
import com.maxkeppeler.sheets.calendar.SelectionMode
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentReportsBinding
import com.puntogris.blint.model.DashboardItem
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.utils.Constants.SUPPLIERS_LIST
import com.puntogris.blint.utils.RepoResult
import com.puntogris.blint.utils.setUpUi
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment: BaseFragment<FragmentReportsBinding>(R.layout.fragment_reports) {

    private val viewModel: ReportsViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        setUpUi(showFab = false)

        val reportDashboardAdapter = ReportsDashboardAdapter()
        binding.dashboardRv.apply {
            adapter = reportDashboardAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
        }

        lifecycleScope.launchWhenStarted {
            when(val result =  viewModel.getStatistics()){
                is RepoResult.Error -> {}
                RepoResult.InProgress -> {}
                is RepoResult.Success -> {
                    val data = listOf(
                        DashboardItem(getString(R.string.products_label), result.data.totalProducts.toString()),
                        DashboardItem(getString(R.string.suppliers_label), result.data.totalSuppliers.toString()),
                        DashboardItem(getString(R.string.clients_label), result.data.totalClients.toString())
                    )
                    reportDashboardAdapter.submitList(data)
                }
            }
        }
    }

    fun onProductsReportClicked(){ showTimeFrameBottomSheet(PRODUCTS_RECORDS) }
    //fun onClientsReportClicked(){ showTimeFrameBottomSheet(CLIENTS_RECORDS) }
    //fun onSuppliersReportClicked(){ showTimeFrameBottomSheet(SUPPLIERS_RECORDS) }

    fun onProductListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = PRODUCTS_LIST)
        findNavController().navigate(action)
    }

    fun onClientListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = CLIENTS_LIST)
        findNavController().navigate(action)
    }

    fun onSuppliersListClicked(){
        val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = SUPPLIERS_LIST)
        findNavController().navigate(action)
    }

    @Suppress("SameParameterValue")
    private fun showTimeFrameBottomSheet(code: Int){
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
                        //showButtons(true)
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