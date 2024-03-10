package com.puntogris.blint.feature_store.presentation.scanner

import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.setTextOrDefault
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScannerResultBottomSheet :
    BaseBottomSheetFragment<ScannerResultDialogBinding>(R.layout.scanner_result_dialog) {

    private val viewModel: ScannerViewModel by viewModels()

    private var resumeCameraOnNavigationBack = true

    override fun initializeViews() {
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.scannedProduct.collectLatest {
                if (it == null) {
                    return@collectLatest
                }
                with(binding) {
                    groupProductFound.isVisible = it.product.productId.isNotEmpty()
                    groupProductNotFound.isVisible = it.product.productId.isEmpty()
                    textViewProductName.text = it.product.name
                    textViewProductStock.text = it.product.stock.toString()
                    textViewProductLastRecord.setDateFromTimestamp(it.product.lastRecordTimestamp)
                    textViewProductBarcode.text = it.product.barcode
                    textViewProductBuyPrice.setTextOrDefault(it.product.buyPrice)
                    textViewProductSellPrice.setTextOrDefault(it.product.sellPrice)
                    textViewProductSuggestedPrice.setTextOrDefault(it.product.suggestedSellPrice)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.buttonCreateProduct.setOnClickListener {
            onCreateNewProduct()
        }
        binding.buttonEditProduct.setOnClickListener {
            onCreateSimpleOrder()
        }
        binding.buttonOpenProduct.setOnClickListener {
            onNavigateToProductClicked()
        }
    }

    private fun onNavigateToProductClicked() {
        viewModel.scannedProduct.value?.let {
            val action = ScannerResultBottomSheetDirections.actionGlobalProductFragment(it)
            findNavController().navigate(action)
        }
    }

    private fun onCreateSimpleOrder() {
        resumeCameraOnNavigationBack = false
        dismiss()
        showOrderPickerAndNavigate()
    }

    private fun onCreateNewProduct() {
        viewModel.scannedProduct.value?.let {
            val action =
                ScannerResultBottomSheetDirections.actionScannerResultDialogToEditProductFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun dismiss() {
        super.dismiss()
        setFragmentResult(
            Keys.SCANNER_FRAGMENT_KEY,
            bundleOf(Keys.RESUME_CAMERA_KEY to resumeCameraOnNavigationBack)
        )
    }
}
