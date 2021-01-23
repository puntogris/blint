package com.puntogris.blint.ui

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
import com.puntogris.blint.model.Product
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.ui.record.RecordsViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.ARG_SCANNING_RESULT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScannerResultBottomSheet(private val listener: DialogDismissListener): BaseBottomSheetFragment<ScannerResultDialogBinding>(R.layout.scanner_result_dialog) {

    private val viewModel: RecordsViewModel by viewModels()
    private var returnAndActivateCamera = true

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.fragment = this

        val items = listOf("Entrada", "Salida")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.recordType.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        binding.externalChip.setOnCloseIconClickListener {
            it.gone()
            viewModel.resetExternalInfo()
        }

        binding.recordTypeText.setOnItemClickListener { adapterView, view, i, l ->
            when(i){
                0 -> {
                    setUpExternalChipGroup(i)
                    binding.addExternalChip.setOnClickListener {
                        openBottomSheetForSuppliers()
                    }
                }
                1 -> {
                    setUpExternalChipGroup(i)
                    binding.addExternalChip.setOnClickListener {
                        openBottomSheetForClients()
                    }
                }
            }
            viewModel.updateRecordType(i)
        }
    }

    private fun setUpExternalChipGroup(position:Int){
        binding.addExternalChip.text =
            when(position){
                0-> "Agregar proveedor"
                1-> "Agregar cliente"
                else -> ""
            }
        viewModel.resetExternalInfo()
        binding.externalChip.gone()
        binding.externalChipGroup.visible()
    }

    private fun openBottomSheetForClients(){
        lifecycleScope.launch {
            val clients = viewModel.getAllClients()
            if (clients.isNullOrEmpty()){
                showSackBarAboveBotomSheet("No se encontraron clientes registrados.")
            }else{
                val optionClients = clients.map { Option(it.name) }.toMutableList()
                OptionsSheet().build(requireContext()) {
                    title("Agregar Clientes")
                    displayMode(DisplayMode.LIST)
                    multipleChoices(false)
                    with(optionClients)
                    onPositive { index: Int, _: Option ->
                        viewModel.updateExternalInfo(clients[index].id, clients[index].name)
                        binding.externalChip.text = clients[index].name
                        binding.externalChip.visible()
                    }
                    onNegative("Cancelar")
                }.show(parentFragmentManager, "")
            }
        }
    }

    private fun openBottomSheetForSuppliers(){
        lifecycleScope.launch {
            val suppliers = viewModel.getAllSuppliers()
            if (suppliers.isNullOrEmpty()){
                showSackBarAboveBotomSheet("No se encontraron proveedores registrados.")
            }else{
                val optionSuppliers = suppliers.map { Option(it.companyName) }.toMutableList()
                OptionsSheet().build(requireContext()){
                    title("Agregar Proveedores")
                    displayMode(DisplayMode.LIST)
                    multipleChoices(false)
                    with(optionSuppliers)
                    onPositive { index: Int, _: Option ->
                        viewModel.updateExternalInfo(suppliers[index].supplierId, suppliers[index].companyName)
                        binding.externalChip.text = suppliers[index].companyName
                        binding.externalChip.visible()
                    }
                    onNegative("Cancelar")
                }.show(parentFragmentManager, "")
            }
        }
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
        returnAndActivateCamera = false
        dismiss()
        val action = ScannerFragmentDirections.actionScannerFragmentToProductFragment(viewModel.getProductID())
        findNavController().navigate(action)
    }

    fun onSaveProductClicked(){
        when {
            binding.recordTypeText.text.isNullOrBlank() -> {
                showSackBarAboveBotomSheet("Especifica el tipo de movimiento.")
            }
            binding.productAmountText.getInt() == 0  || binding.productAmountText.text.isNullOrBlank() -> {
                showSackBarAboveBotomSheet("El campo de cantidad no puede estar vacio.")
            }
            else -> {
                lifecycleScope.launchWhenStarted {
                    if(viewModel.saveRecordAndUpdateStock(binding.productAmountText.getInt())){
                        dismiss()
                        showLongSnackBarAboveFab("Se actualizo el producto correctamente.")
                    }
                    else{
                        showSackBarAboveBotomSheet("No dispone del stock para realizar esta accion.")
                    }
                }
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