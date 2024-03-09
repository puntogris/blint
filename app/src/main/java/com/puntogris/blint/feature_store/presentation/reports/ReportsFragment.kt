package com.puntogris.blint.feature_store.presentation.reports

import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentReportsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReportsFragment : BaseFragment<FragmentReportsBinding>(R.layout.fragment_reports) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) {
        defaultViewModelProviderFactory
    }

    override fun initializeViews() {
        registerToolbarBackButton(binding.toolbar)
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonProductsRecords.setOnClickListener {
            showTimeFrameBottomSheet()
            viewModel.updateReportType(ReportType.ProductRecords)
        }
        binding.buttonTradersRecords.setOnClickListener {
            showTimeFrameBottomSheet()
            viewModel.updateReportType(ReportType.ClientsRecords)
        }
        binding.buttonProductsList.setOnClickListener {
            viewModel.updateReportType(ReportType.ProductsList)
            findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
        }
        binding.buttonTraderList.setOnClickListener {
            viewModel.updateReportType(ReportType.ClientsList)
            findNavController().navigate(R.id.action_reportsFragment_to_generateReportFragment)
        }
    }

    private fun showTimeFrameBottomSheet() {
        val timeFrames = TimeFrame.entries
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
