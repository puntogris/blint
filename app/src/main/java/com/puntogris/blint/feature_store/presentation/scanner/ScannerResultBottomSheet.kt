package com.puntogris.blint.feature_store.presentation.scanner

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import com.puntogris.blint.feature_store.domain.model.product.Product
import com.puntogris.blint.feature_store.domain.model.product.ProductWithDetails
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScannerResultBottomSheet :
    BaseBottomSheetFragment<ScannerResultDialogBinding>(R.layout.scanner_result_dialog) {

    private val viewModel: ScannerViewModel by viewModels()
    private var resumeCameraOnNavigationBack = true

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this
    }

    fun onNavigateToProductClicked() {
        val action = ScannerResultBottomSheetDirections.actionScannerResultDialogToProductFragment(
            viewModel.scannedProduct.value!!
        )
        findNavController().navigate(action)
    }

    fun onCreateSimpleOrder() {
        resumeCameraOnNavigationBack = false
        dismiss()
        val action = ScannerResultBottomSheetDirections.actionGlobalSimpleOrderBottomSheet(
            viewModel.scannedProduct.value!!.product
        )
        findNavController().navigate(action)
    }

    fun onCreateNewProduct() {
        val action =
            ScannerResultBottomSheetDirections.actionScannerResultDialogToEditProductFragment(
                viewModel.scannedProduct.value!!
            )
        findNavController().navigate(action)
    }

    override fun dismiss() {
        super.dismiss()
        setFragmentResult(
            Constants.SCANNER_FRAGMENT_KEY,
            bundleOf(Constants.RESUME_CAMERA_KEY to resumeCameraOnNavigationBack)
        )
    }
}