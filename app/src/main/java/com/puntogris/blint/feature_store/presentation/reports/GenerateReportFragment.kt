package com.puntogris.blint.feature_store.presentation.reports

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenerateReportFragment :
    BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) { defaultViewModelProviderFactory }
    private lateinit var documentLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this

        setupDocumentLauncher()
        registerToolbarBackButton(binding.toolbar)
    }

    private fun setupDocumentLauncher() {
        documentLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            if (it != null) {
                viewModel.updateReportUri(it)
                startReportGeneration()
            } else {
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
                    is Resource.Error -> {
                        reportTitle.setText(R.string.report_exported_error_title)
                        reportMessage.setText(R.string.report_exported_error_message)
                        animationView.playAnimationOnce(R.raw.error)
                    }
                    is Resource.Success -> {
                        reportTitle.setText(R.string.report_exported_success_title)
                        reportMessage.setText(R.string.report_exported_success_message)
                        animationView.playAnimationOnce(R.raw.done)
                    }
                }
            }
        }
    }

    fun onShareReportClicked() {
        val uri = viewModel.getReportUri()
        if (uri == null)
            UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
        else {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "application/vnd.ms-excel"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, getString(R.string.action_share_report)))
        }
    }

    fun onNavigateOutClicked() {
        findNavController().navigateAndClearStack(R.id.homeFragment)
    }
}
