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
import com.puntogris.blint.utils.Constants.ANNUAL
import com.puntogris.blint.utils.Constants.BIANNUAL
import com.puntogris.blint.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.utils.Constants.HISTORICAL
import com.puntogris.blint.utils.Constants.MONTHLY
import com.puntogris.blint.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.utils.Constants.QUARTERLY
import com.puntogris.blint.utils.Constants.SUPPLIERS_LIST
import com.puntogris.blint.utils.Constants.WEEKLY
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
            title(getString(R.string.ask_report_time_frame_title))
            displayMode(DisplayMode.LIST)
            with(
                Option(getString(R.string.weekly)),
                Option(getString(R.string.monthly)),
                Option(getString(R.string.quarterly)),
                Option(getString(R.string.biannual)),
                Option(getString(R.string.annual)),
                Option(getString(R.string.historical))
            )
            onPositive { index: Int, _: Option ->
                val timeCode = when(index){
                    0 -> WEEKLY
                    1 -> MONTHLY
                    2 -> QUARTERLY
                    3 -> BIANNUAL
                    4 -> ANNUAL
                    else -> HISTORICAL
                }
            val action = ReportsFragmentDirections.actionReportsFragmentToGenerateReportFragment(reportCode = code, timeCode = timeCode)
            this@ReportsFragment.findNavController().navigate(action)
            }
        }.show(parentFragmentManager, "")
    }
}