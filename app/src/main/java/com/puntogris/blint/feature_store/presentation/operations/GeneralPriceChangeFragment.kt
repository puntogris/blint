package com.puntogris.blint.feature_store.presentation.operations

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.maxkeppeler.sheets.info.InfoSheet
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getFloat
import com.puntogris.blint.common.utils.getString
import com.puntogris.blint.common.utils.visible
import com.puntogris.blint.databinding.FragmentGeneralPriceChangeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GeneralPriceChangeFragment :
    BaseFragment<FragmentGeneralPriceChangeBinding>(R.layout.fragment_general_price_change) {

    private val viewModel: OperationsViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24) {
            if (binding.productsAffectedText.text.isNullOrBlank())
            //  showLongSnackBarAboveFab("Necesitas especificar un proveedor o una categoria.")
            else if (!listOf(
                    binding.buyPriceCheckBox,
                    binding.sellPriceCheckBox,
                    binding.suggestedPriceCheckBox
                ).any {
                    it.isChecked
                }
            )
            //showLongSnackBarAboveFab("Selecciona al menos un precio a modificar.")
            else if (binding.changeValueText.text.isNullOrBlank() ||
                binding.changeValueText.getFloat() == 0F
            )
            //   showLongSnackBarAboveFab("Ingresa un valor distinto de 0 para la modificacion.")
            else if (binding.changeValueTypeText.getString() == "%" &&
                binding.changeValueText.getFloat() > 100
            )
            //     showLongSnackBarAboveFab("El valor no puede ser mayor a 100 %.")
            else
                onSaveChangesClicked()
        }

        val productsAffectedList = listOf("Proveedor", "Categoria")
        val productsAffectedAdapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_item_list, productsAffectedList)
        (binding.productsAffected.editText as? AutoCompleteTextView)?.setAdapter(
            productsAffectedAdapter
        )

        val items = listOf("%", "$")
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item_list, items)
        (binding.changeValueType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.productsAffectedText.setOnItemClickListener { _, _, i, _ ->
            when (i) {
                0 -> openSuppliersBottomSheet()
                1 -> openCategoryBottomSheet()
            }
        }
    }

    private fun openSuppliersBottomSheet() {
        lifecycleScope.launch {
            val suppliers = viewModel.getAllSuppliers()
            if (suppliers.isNullOrEmpty()) {
                //     showLongSnackBarAboveFab("No se encontraron proveedores registrados.")
            } else {
                val optionSuppliers = suppliers.map { Option(it.companyName) }.toMutableList()
                OptionsSheet().build(requireContext()) {
                    title("Agregar Proveedores")
                    displayMode(DisplayMode.LIST)
                    multipleChoices(false)
                    with(optionSuppliers)
                    onPositive { index: Int, _: Option ->
                        binding.productsAffectedTitle.visible()
                        binding.productsAffectedTitle.text = "Proveedor seleccionado:"
                        binding.productsAffectedData.visible()
                        binding.productsAffectedData.text = suppliers[index].companyName
                        suppliers[index].supplierId
                        viewModel.updateSupplierId(suppliers[index].supplierId)
                    }
                    onNegative("Cancelar")
                }.show(parentFragmentManager, "")
            }
        }
    }

    private fun openCategoryBottomSheet() {
        OptionsSheet().build(requireContext()) {
            title("Agregar Categorias")
            displayMode(DisplayMode.LIST)
            multipleChoices()
            with(
                Option("Categoria 1"),
                Option("Categoria 2"),
                Option("Categoria 3"),
                Option("Categoria 4"),
                Option("Categoria 5"),
                Option("Categoria 6")
            )
            onNegative("Cancelar")
        }.show(parentFragmentManager, "")
    }

    private fun onSaveChangesClicked() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title("Queres llevar a cabo esta accion?")
            content("Estas por cambiarle la informacion a varios productos. Tene en cuenta que esta accion es irreversible.")
            onNegative(R.string.action_cancel)
            onPositive(R.string.action_yes) {
                savesChanges()
            }
        }
    }

    private fun savesChanges() {
        lifecycleScope.launch {
            val result = viewModel.saveChangesToDatabase(
                valueType = binding.changeValueTypeText.getString(),
                changeAmount = binding.changeValueText.getFloat(),
                isValueUp = binding.increaseAmount.isChecked,
                affectsBuyPrice = binding.buyPriceCheckBox.isChecked,
                affectsSellPrice = binding.sellPriceCheckBox.isChecked,
                affectsSuggestedPrice = binding.suggestedPriceCheckBox.isChecked
            )

            //  showLongSnackBarAboveFab("Cambios realizados correctamente. Afecto $result productos.")
            findNavController().navigateUp()
        }

    }

}