package com.puntogris.blint.feature_store.presentation.reports

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.common.utils.Constants.CLIENTS_RECORDS
import com.puntogris.blint.common.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.common.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.common.utils.Constants.SUPPLIERS_LIST
import com.puntogris.blint.common.utils.Constants.SUPPLIERS_RECORDS
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentReportsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>(R.layout.fragment_reports) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        UiInterface.registerUi(showFab = false)
    }

    fun onProductsReportClicked() {
        showTimeFrameBottomSheet()
        viewModel.updateReportType(PRODUCTS_RECORDS)
    }

    fun onClientsReportClicked() {
        showTimeFrameBottomSheet()
        viewModel.updateReportType(CLIENTS_RECORDS)

    }

    fun onSuppliersReportClicked() {
        showTimeFrameBottomSheet()
        viewModel.updateReportType(SUPPLIERS_RECORDS)
    }

    fun onProductListClicked() {
        viewModel.updateReportType(PRODUCTS_LIST)
        findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
    }

    fun onClientListClicked() {
        viewModel.updateReportType(CLIENTS_LIST)
        findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
    }

    fun onSuppliersListClicked() {
        viewModel.updateReportType(SUPPLIERS_LIST)
        findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
    }

    private fun showTimeFrameBottomSheet() {
        val timeFrames = TimeFrame.values()
        OptionsSheet().show(requireParentFragment().requireContext()) {
            title(R.string.ask_report_time_frame_title)
            displayMode(DisplayMode.LIST)
            with(*timeFrames.map { Option(it.res) }.toTypedArray())
            onPositive { index: Int, _ ->
                viewModel.updateTimeFrame(timeFrames[index])
                findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
            }
        }
    }
}

enum class TimeFrame(val res: Int, val days: Int) {
    WEEKLY(R.string.weekly, 7),
    MONTHLY(R.string.monthly, 30),
    QUARTERLY(R.string.quarterly, 90),
    BIANNUAL(R.string.biannual, 180),
    ANNUAL(R.string.annual, 360),
    HISTORICAL(R.string.historical, 0)
}