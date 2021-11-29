package com.puntogris.blint.feature_store.presentation.reports

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.Constants.CLIENTS_LIST
import com.puntogris.blint.common.utils.Constants.PRODUCTS_LIST
import com.puntogris.blint.common.utils.Constants.PRODUCTS_RECORDS
import com.puntogris.blint.common.utils.Constants.SUPPLIERS_LIST
import com.puntogris.blint.common.utils.types.ExportResult
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class GenerateReportFragment :
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) { defaultViewModelProviderFactory }
    private val args: GenerateReportFragmentArgs by navArgs()
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_share_24) {
            val uri = viewModel.getDownloadUri()
            if (uri == null)
                UiInterface.showSnackBar(getString(R.string.snack_report_generating_or_error))
            else shareFileIntent(uri)
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.exportingState.collect {
                when (it) {
                    is ExportResult.Error -> showErrorUi()
                    ExportResult.InProgress -> showInProgressUi()
                    ExportResult.Success -> showSuccessUi()
                }
            }
        }

        val date = Date().getDateFormattedStringUnderLine()
        val fileName = when (args.reportCode) {
            SUPPLIERS_LIST -> getString(R.string.report_list_suppliers, date)
            CLIENTS_LIST -> getString(R.string.report_list_clients, date)
            PRODUCTS_LIST -> getString(R.string.report_list_products, date)
            PRODUCTS_RECORDS -> getString(R.string.report_product_records, date)
            else -> ""
        }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
                if (uri != null) {
                    println(args.reportCode)
                    when (args.reportCode) {
                        SUPPLIERS_LIST -> exportSupplierList(uri)
                        CLIENTS_LIST -> exportClientList(uri)
                        PRODUCTS_LIST -> exportProductList(uri)
                        PRODUCTS_RECORDS -> exportProductRecords(uri)
//                SUPPLIERS_RECORDS -> {
//                    fileName = "proveedores_movimientos_${Date().getDateFormattedString()}"
//                    exportSupplierRecords(uri)
//                }
//                CLIENTS_RECORDS -> {
//                    fileName = "clientes_movimientos_${Date().getDateFormattedString()}"
//                    exportClientRecords(uri)
//                }
                    }
                } else findNavController().navigateUp()
            }
        activityResultLauncher.launch("${fileName}.xls")
    }

    private fun showInProgressUi() {}

    private fun showSuccessUi() {
        binding.reportTitle.text = getString(R.string.report_exported_success_title)
        binding.reportMessage.text = getString(R.string.report_exported_success_message)
        binding.progressBar.gone()
        binding.animationView.visible()
        binding.animationView.playAnimation()
    }

    private fun showErrorUi() {
        binding.progressBar.gone()
        binding.reportTitle.text = getString(R.string.report_exported_error_title)
        UiInterface.showSnackBar(getString(R.string.report_exported_error_message))
    }

    private fun exportProductRecords(downloadFileUri: Uri) {

    }


    private fun exportSupplierList(downloadFileUri: Uri) {

    }

    private fun exportClientList(downloadFileUri: Uri) {

    }

    private fun exportProductList(downloadFileUri: Uri) {

    }

    private fun shareFileIntent(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "application/vnd.ms-excel"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, getString(R.string.action_share_report)))
    }

}
