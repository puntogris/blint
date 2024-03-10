package com.puntogris.blint.feature_store.presentation.scanner

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Keys
import com.puntogris.blint.common.utils.isDarkThemeOn
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setDateFromTimestamp
import com.puntogris.blint.common.utils.setTextOrDefault
import com.puntogris.blint.common.utils.showOrderPickerAndNavigate
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ScannerResultBottomSheet :
    BottomSheetDialogFragment() {

    private val binding by viewBinding(ScannerResultDialogBinding::bind)

    private val viewModel: ScannerViewModel by viewModels()

    private var resumeCameraOnNavigationBack = true

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).apply {
            window?.let {
                it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                WindowInsetsControllerCompat(it, it.decorView).isAppearanceLightStatusBars =
                    !isDarkThemeOn()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scanner_result_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    setDateFromTimestamp(textViewProductLastRecord, it.product.lastRecordTimestamp)
                    textViewProductBarcode.text = it.product.barcode
                    setTextOrDefault(textViewProductBuyPrice, it.product.buyPrice)
                    setTextOrDefault(textViewProductSellPrice, it.product.sellPrice)
                    setTextOrDefault(textViewProductSuggestedPrice, it.product.suggestedSellPrice)
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
