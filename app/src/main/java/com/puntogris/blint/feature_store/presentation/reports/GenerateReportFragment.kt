package com.puntogris.blint.feature_store.presentation.reports

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.types.SimpleResult
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenerateReportFragment :
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) { defaultViewModelProviderFactory }
    private lateinit var documentLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_share_24) {
            val uri = viewModel.getReportUri()
            if (uri == null)
                UiInterface.showSnackBar(getString(R.string.snack_report_generating_or_error))
            else shareFileIntent(uri)
        }
        setupDocumentLauncher()
    }

    private fun setupDocumentLauncher() {
        documentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            if (it != null) {
                viewModel.updateReportUri(it)
                startReportGeneration()
            }
            else {
                findNavController().navigateUp()
            }
        }

        val fileName = viewModel.getReportName(requireContext())
        documentLauncher.launch("$fileName.xls")
    }

    private fun startReportGeneration() {
        lifecycleScope.launch {
            with(binding) {
                when (viewModel.generateReport()) {
                    SimpleResult.Failure -> {
                        reportTitle.setText(R.string.report_exported_error_title)
                        reportMessage.setText(R.string.report_exported_error_message)
                        animationView.playAnimationOnce(R.raw.error)
                    }
                    SimpleResult.Success -> {
                        reportTitle.setText(R.string.report_exported_success_title)
                        reportMessage.setText(R.string.report_exported_success_message)
                        animationView.playAnimationOnce(R.raw.done)
                    }
                }
            }
        }
    }

    private fun shareFileIntent(uri: Uri) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "application/vnd.ms-excel"
        share.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(share, getString(R.string.action_share_report)))
    }
}
