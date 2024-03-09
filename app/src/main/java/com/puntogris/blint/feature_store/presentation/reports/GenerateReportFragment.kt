package com.puntogris.blint.feature_store.presentation.reports

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.navigateAndClearStack
import com.puntogris.blint.common.utils.playAnimationOnce
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GenerateReportFragment : Fragment(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by navGraphViewModels(R.id.reportsNavGraph) {
        defaultViewModelProviderFactory
    }

    private val binding by viewBinding(FragmentGenerateReportBinding::bind)

    private lateinit var documentLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupListeners()
        setupDocumentLauncher()
    }

    private fun setupListeners() {
        binding.buttonDone.setOnClickListener {
            findNavController().navigateAndClearStack(R.id.homeFragment)
        }
        binding.buttonShare.setOnClickListener {
            onShareReportClicked()
        }
    }

    private fun setupDocumentLauncher() {
        documentLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("image/*")) {
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
                        textViewReportTitle.setText(R.string.report_exported_error_title)
                        textViewReportAlert.setText(R.string.report_exported_error_message)
                        viewAnimation.playAnimationOnce(R.raw.error)
                    }

                    is Resource.Success -> {
                        textViewReportTitle.setText(R.string.report_exported_success_title)
                        textViewReportAlert.setText(R.string.report_exported_success_message)
                        viewAnimation.playAnimationOnce(R.raw.done)
                    }
                }
            }
        }
    }

    private fun onShareReportClicked() {
        val uri = viewModel.getReportUri()
        if (uri == null) {
            UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
        } else {
            val share = Intent(Intent.ACTION_SEND)
            share.type = "application/vnd.ms-excel"
            share.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(share, getString(R.string.action_share_report)))
        }
    }
}
