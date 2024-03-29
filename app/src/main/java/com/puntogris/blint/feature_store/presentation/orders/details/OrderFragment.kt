package com.puntogris.blint.feature_store.presentation.orders.details

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getUriFromProvider
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.setDateFromTimestampWithTime
import com.puntogris.blint.common.utils.setExternalChipName
import com.puntogris.blint.common.utils.setRecordTypeString
import com.puntogris.blint.common.utils.setTotalOrderWithDetails
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentOrderBinding
import com.rajat.pdfviewer.PdfViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var mediaStorageLauncher: ActivityResultLauncher<String>

    private val binding by viewBinding(FragmentOrderBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerToolbarBackButton(binding.orderToolbar)
        setupOrderTableAdapter()
        setupMediaStorageLauncher()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.orderWithRecords.collectLatest {
                binding.chipOrderTrader.setExternalChipName(it.order.traderName)
                setTotalOrderWithDetails(binding.textViewOrderTotal, it)
                setDateFromTimestampWithTime(binding.textViewOrderDate, it.order.timestamp)
                setRecordTypeString(binding.textViewOrderType, it.order.type)
            }
        }
    }

    private fun setupListeners() {
        binding.buttonGenerateInvoice.setOnClickListener {
            onGenerateOrderReceiptClicked()
        }
        binding.chipOrderTrader.setOnClickListener {
            onExternalChipClicked()
        }
    }

    private fun setupOrderTableAdapter() {
        val adapter = OrdersRecordsAdapter()
        binding.recyclerViewOrder.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: OrdersRecordsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.orderWithRecords.collect {
                adapter.submitList(it.records)
            }
        }
    }

    private fun setupMediaStorageLauncher() {
        mediaStorageLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument("image/*")) {
                val message = when (val result = viewModel.getOrderPDF(it)) {
                    is Resource.Error -> result.error
                    is Resource.Success -> R.string.snack_invoice_saved_success
                }
                UiInterface.showSnackBar(getString(message))
            }
    }

    fun onGenerateOrderReceiptClicked() {
        OptionsSheet().show(requireParentFragment().requireContext()) {
            displayMode(DisplayMode.LIST)
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
        when (val result = viewModel.getOrderPDF()) {
            is Resource.Error -> {
                UiInterface.showSnackBar(getString(result.error))
            }

            is Resource.Success -> {
                startActivity(
                    PdfViewerActivity.launchPdfFromPath(
                        context,
                        result.data.path,
                        getString(
                            R.string.order_number_invoice,
                            viewModel.orderWithRecords.value.order.number
                        ),
                        "pdf",
                        enableDownload = false
                    )
                )
            }
        }
    }

    private fun onShareReceipt() {
        when (val result = viewModel.getOrderPDF()) {
            is Resource.Error -> {
                UiInterface.showSnackBar(getString(result.error))
            }

            is Resource.Success -> {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, result.data.getUriFromProvider(requireContext()))
                }
                startActivity(intent)
            }
        }
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
        val action = OrderFragmentDirections.actionGlobalTraderFragment(
            traderId = viewModel.orderWithRecords.value.order.traderId
        )
        findNavController().navigate(action)
    }
}
