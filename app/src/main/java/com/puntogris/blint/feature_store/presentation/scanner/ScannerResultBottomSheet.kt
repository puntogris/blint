package com.puntogris.blint.feature_store.presentation.scanner

import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.databinding.ScannerResultDialogBinding
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
        viewModel.scannedProduct.value?.let {
            val action = ScannerResultBottomSheetDirections.actionGlobalProductFragment(it)
            findNavController().navigate(action)
        }
    }

    fun onCreateSimpleOrder() {
        resumeCameraOnNavigationBack = false
        dismiss()
        showOrderPickerAndNavigate()
    }

    fun onCreateNewProduct() {
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
