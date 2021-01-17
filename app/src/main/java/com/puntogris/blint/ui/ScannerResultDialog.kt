package com.puntogris.blint.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import com.puntogris.blint.ui.product.ProductViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.ARG_SCANNING_RESULT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ScannerResultDialog(private val listener: DialogDismissListener) : BottomSheetDialogFragment() {

    private lateinit var binding: ScannerResultDialogBinding
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScannerResultDialogBinding.inflate(
            inflater, container, false
        )
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        lifecycleScope.launch {
            val product = viewModel.getProductWithBarCode(scannedResult.toString())
            viewModel.setProductData(product)
            binding.product = product
        }
    }

    companion object {
        fun newInstance(scanningResult: String, listener: DialogDismissListener): ScannerResultDialog =
            ScannerResultDialog(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }
            }
    }

    fun onGoToProductClicked(){
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToEditProductFragment(viewModel.getProductID())
        findNavController().navigate(action)
    }

    fun onSaveProductClicked(){
        viewModel.setNewProductStock(binding.productAmountText.getInt())
        binding.productAmount.text = binding.productAmountText.getString()
        binding.productLastEdited.text = SimpleDateFormat("dd / MM / yyyy",Locale.getDefault()).format(Date())
        showSackBarAboveBotomSheet("Se actualizo el producto correctamente.")
    }

    fun onIncreaseAmountButtonClicked(){
        binding.productAmountText.apply {
            setText(getInt().inc().toString())
        }
    }
    fun onDecreaseAmountButtonClicked(){
        binding.productAmountText.apply {
            setText(getInt().dec().toString())
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        listener.onDismiss()
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        listener.onDismiss()
    }

    interface DialogDismissListener {
        fun onDismiss()
    }
}