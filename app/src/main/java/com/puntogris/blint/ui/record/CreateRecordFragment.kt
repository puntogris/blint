package com.puntogris.blint.ui.record

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.bottomsheets.options.DisplayMode
import com.maxkeppeler.bottomsheets.options.Option
import com.maxkeppeler.bottomsheets.options.OptionsSheet
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
                        viewModel.saveRecordAndUpdateStock(binding.productAmountText.getInt())
                        showLongSnackBarAboveFab("Se actualizo el producto correctamente.")
                        findNavController().navigateUp()
                    }
                }
            }
        }

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
                2 -> {
                    binding.supplierChipGroup.gone()
                    binding.clientsChipGroup.gone()
                }
            }
            viewModel.updateRecordType(i)
        }
    }

    fun openBottomSheetForClients(){
        OptionsSheet().build(requireContext()){
            title("Agregar Clientes")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Cliente 1"),
                Option("Cliente 2")
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
}