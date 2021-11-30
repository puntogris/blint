package com.puntogris.blint.feature_store.presentation.orders.details

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentOrderBinding
import com.rajat.pdfviewer.PdfViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.File

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>(R.layout.fragment_order) {

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var mediaStorageLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        UiInterface.registerUi(showAppBar = false)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupOrderTableAdapter()
        setupMediaStorageLauncher()
    }

    private fun setupOrderTableAdapter() {
        OrdersTableAdapter().let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: OrdersTableAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.tableItems.collect {
                adapter.submitList(it)
            }
        }
    }

    private fun setupMediaStorageLauncher() {
        mediaStorageLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
                try {
                    requireActivity().contentResolver.openOutputStream(uri)?.let {
                        viewModel.createPdf(it)
                    }
                    UiInterface.showSnackBar(getString(R.string.snack_invoice_saved_success))
                } catch (e: Exception) {
                    UiInterface.showSnackBar(getString(R.string.snack_invoice_save_error))
                }
            }
    }

    fun onGenerateOrderReceiptClicked() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            displayMode(DisplayMode.GRID_HORIZONTAL)
            with(
                Option(R.drawable.ic_baseline_article_24, R.string.see),
                Option(R.drawable.ic_baseline_share_24, R.string.share),
                Option(R.drawable.ic_baseline_archive_24, R.string.action_save)
            )
            onPositive { index: Int, _: Option ->
                when (index) {
                    0 -> onSeeReceipt()
                    1 -> onShareReceipt()
                    2 -> onSaveReceipt()
                }
            }
        }
    }

    private fun onSeeReceipt() {
        val file = File(
            requireContext().filesDir.absolutePath + "/${
                getString(
                    R.string.invoice_file_name,
                    viewModel.orderWithRecords.value.order.number
                )
            }"
        )
        viewModel.createPdf(file.outputStream())
        startActivity(
            PdfViewerActivity.launchPdfFromPath(
                context,
                file.path,
                getString(
                    R.string.order_number_invoice,
                    viewModel.orderWithRecords.value.order.number
                ),
                "pdf",
                enableDownload = false
            )
        )
    }

    private fun onShareReceipt() {
        val file = File(
            requireContext().filesDir.absolutePath + "/${
                getString(
                    R.string.invoice_file_name,
                    viewModel.orderWithRecords.value.order.number
                )
            }"
        )
        viewModel.createPdf(file.outputStream())
        val uri = FileProvider.getUriForFile(requireContext(), requireActivity().packageName, file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(intent)
    }

    private fun onSaveReceipt() {
        mediaStorageLauncher.launch(
            getString(
                R.string.invoice_file_name,
                viewModel.orderWithRecords.value.order.number
            )
        )
    }

    fun onExternalChipClicked() {
        when (viewModel.orderWithRecords.value.order.type) {
            "IN" -> {
                val action =
                    OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(
                        supplierId = viewModel.orderWithRecords.value.order.traderId
                    )
                findNavController().navigate(action)
            }
            "OUT" -> {
                val action =
                    OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToClientFragment(
                        clientId = viewModel.orderWithRecords.value.order.traderId
                    )
                findNavController().navigate(action)
            }
        }
    }
}