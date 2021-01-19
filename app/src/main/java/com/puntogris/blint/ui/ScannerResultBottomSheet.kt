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
import com.maxkeppeler.bottomsheets.options.DisplayMode
import com.maxkeppeler.bottomsheets.options.Option
import com.maxkeppeler.bottomsheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.ScannerResultDialogBinding
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.record.RecordsViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.ARG_SCANNING_RESULT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerResultBottomSheet(private val listener: DialogDismissListener) : BottomSheetDialogFragment() {

    private lateinit var binding: ScannerResultDialogBinding
    private val viewModel: RecordsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScannerResultDialogBinding.inflate(
            inflater, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this

        val items = listOf("Entrada", "Salida")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.recordType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.recordTypeText.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0 -> {
                    binding.supplierChipGroup.visible()
                    binding.clientsChipGroup.gone()
                }
                1 -> {
                    binding.supplierChipGroup.gone()
                    binding.clientsChipGroup.visible()
                }
            }
        }
        return binding.root
    }

    fun openBottomSheetForClients(){
        OptionsSheet().build(requireContext()){
            title("Agregar Clientes")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Cliente 1"),
                Option("Cliente 2"),
                Option("Cliente 3")
            )
            onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                selectedIndices.forEach {
                    createNewChipAndAddItToGroup(it.toString(), binding.clientsChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }

    fun openBottomSheetForSuppliers(){
        OptionsSheet().build(requireContext()){
            title("Agregar Proveedores")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Proveedor 1"),
                Option("Proveedor 2"),
                Option("Proveedor 3")
            )
            onPositiveMultiple("Agregar") { selectedIndices: MutableList<Int>, _ ->
                selectedIndices.forEach {
                    createNewChipAndAddItToGroup(it.toString(), binding.supplierChipGroup)
                }
            }
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val scannedResult = arguments?.getString(ARG_SCANNING_RESULT)
        viewModel.updateBarcodeScanned(scannedResult.toString())
        lifecycleScope.launch {
            val product = viewModel.getProductWithBarCode(scannedResult.toString())
            if (product!= null){
                viewModel.setProductData(product)
                showProductUI(product)
            }else showProductNotFoundUI()

        }
    }

    private fun showProductUI(product: Product){
        binding.productFoundGroup.visible()
        viewModel.setProductData(product)
        binding.product = product
    }

    private fun showProductNotFoundUI(){
        binding.productNotFoundGroup.visible()
    }

    fun onGoToProductClicked(){
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToEditProductFragment(viewModel.getProductID())
        findNavController().navigate(action)
    }

    fun onSaveProductClicked(){
        when {
            binding.recordTypeText.text.isNullOrEmpty() -> {
                showLongSnackBarAboveFab("Especifica el tipo de movimiento.")
            }
            binding.productAmountText.getInt() == 0 -> {
                showLongSnackBarAboveFab("El campo de cantidad no puede estar vacio.")
            }
            else -> {
                viewModel.saveRecordAndUpdateStock(binding.productAmountText.getInt())
                dismiss()
                showLongSnackBarAboveFab("Se actualizo el producto correctamente.")
            }
        }
    }

    fun onCreateNewProductClicked(){
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToEditProductFragment(barcodeScanned = viewModel.getBarcodeScanned())
        findNavController().navigate(action)
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

    companion object {
        fun newInstance(scanningResult: String, listener: DialogDismissListener): ScannerResultBottomSheet =
            ScannerResultBottomSheet(listener).apply {
                arguments = Bundle().apply {
                    putString(ARG_SCANNING_RESULT, scanningResult)
                }
            }
    }
}