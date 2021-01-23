package com.puntogris.blint.ui.record

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentCreateRecordBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateRecordFragment : BaseFragment<FragmentCreateRecordBinding>(R.layout.fragment_create_record) {

    private val args: CreateRecordFragmentArgs by navArgs()
    private val viewModel: RecordsViewModel by viewModels()

    override fun initializeViews() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        binding.viewModel = viewModel

        lifecycleScope.launchWhenStarted {
            if (args.productID != 0){
                lifecycleScope.launch {
                    val product = viewModel.getProduct(args.productID)
                    viewModel.setProductData(product)
                }
            }
        }

        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                when {
                    binding.recordTypeText.text.isNullOrEmpty() -> {
                        showLongSnackBarAboveFab("Especifica el tipo de movimiento.")
                    }
                    binding.productAmountText.getInt() == 0 -> {
                        showLongSnackBarAboveFab("El campo de cantidad no puede estar vacio.")
                    }
                    else -> {
                        lifecycleScope.launchWhenStarted {
                            if(viewModel.saveRecordAndUpdateStock(binding.productAmountText.getInt())){
                                showLongSnackBarAboveFab("Se actualizo el producto correctamente.")
                                findNavController().navigateUp()
                            }
                            else{
                                showLongSnackBarAboveFab("No dispone del stock para realizar esta accion.")
                            }
                        }
                    }
                }
            }
        }

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
                showLongSnackBarAboveFab("No se encontraron clientes registrados.")
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
                showLongSnackBarAboveFab("No se encontraron proveedores registrados.")
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
}