package com.puntogris.blint.ui.scanner

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import com.puntogris.blint.model.FirestoreRecord
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.ProductWithSuppliersCategories
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.ui.orders.OrdersViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.ARG_SCANNING_RESULT
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerResultBottomSheet(private val listener: DialogDismissListener) :
    BaseBottomSheetFragment<ScannerResultDialogBinding>(R.layout.scanner_result_dialog) {

    private val viewModel: OrdersViewModel by viewModels()
    private var returnAndActivateCamera = true
    private var orderType = IN

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this

        val items = resources.getStringArray(R.array.order_type)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.recordType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.recordTypeText.setOnItemClickListener { _, _, i, _ ->
            orderType = if(i == 0) IN else OUT
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        viewModel.updateBarcodeScanned(scannedResult.toString())
        lifecycleScope.launch {
            when(val product = viewModel.getProductWithBarCode(scannedResult.toString())){
                is RepoResult.Error -> showProductNotFoundUI()
                RepoResult.InProgress -> { }
                is RepoResult.Success -> showProductUI(product.data)
            }
        }
    }

    private fun showProductUI(product: ProductWithSuppliersCategories){
        viewModel.setProductData(product)
        binding.productFoundGroup.visible()
    }

    private fun showProductNotFoundUI(){
        binding.productNotFoundGroup.visible()
    }

    fun onGoToProductClicked(){
        returnAndActivateCamera = false
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToProductFragment(viewModel.currentProduct.value)
        findNavController().navigate(action)
    }

    fun onSaveProductClicked(){
        when {
            binding.recordTypeText.text.isNullOrBlank() -> {
                showSackBarAboveBottomSheet(getString(R.string.snack_pick_record_type))
            }
            binding.productAmountText.getInt() == 0  || binding.productAmountText.text.isNullOrBlank() -> {
                showSackBarAboveBottomSheet(getString(R.string.snack_amount_cant_be_empty))
            }
            else -> {
                val amount = binding.productAmountText.getString().toIntOrNull()
                if (amount != null && amount > 0){
                    val order = OrderWithRecords()
                    order.order.type = orderType
                    order.records = listOf(
                        FirestoreRecord(
                            productId = viewModel.currentProduct.value!!.product.productId,
                            productName = viewModel.currentProduct.value!!.product.name,
                            amount = amount,
                            totalOutStock = viewModel.currentProduct.value!!.product.totalOutStock,
                            totalInStock = viewModel.currentProduct.value!!.product.totalInStock
                        )
                    )

                    lifecycleScope.launch {
                        when(viewModel.createSimpleOrder(order)){
                            SimpleResult.Failure -> {
                                dismiss()
                                showSackBarAboveBottomSheet(getString(R.string.snack_order_created_error))
                            }
                            SimpleResult.Success -> {
                                dismiss()
                                showSackBarAboveBottomSheet(getString(R.string.snack_created_order_success))
                            }
                        }
                    }
                }else{ showSackBarAboveBottomSheet(getString(R.string.snack_amount_cant_be_empty)) }
            }
        }
    }

    fun onCreateNewProductClicked(){
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToEditProductFragment(barcodeScanned = viewModel.getCodeScanned())
        findNavController().navigate(action)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(returnAndActivateCamera) listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        if(returnAndActivateCamera) listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }

    companion object {
        fun newInstance(scanningResult: String, listener: DialogDismissListener): ScannerResultBottomSheet =
            ScannerResultBottomSheet(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }
            }
    }
}