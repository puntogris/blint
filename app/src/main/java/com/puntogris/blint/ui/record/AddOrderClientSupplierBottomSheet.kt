package com.puntogris.blint.ui.record

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.puntogris.blint.R
import com.puntogris.blint.databinding.AddOrderClientSupplierBottomSheetBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddOrderClientSupplierBottomSheet:BaseBottomSheetFragment<AddOrderClientSupplierBottomSheetBinding>(R.layout.add_order_client_supplier_bottom_sheet) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }
    private val args:AddOrderClientSupplierBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            val bottomSheet =
                bottomSheetDialog.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
            bottomSheet?.let {
                setupFullHeight(it)
            }
            val behavior = BottomSheetBehavior.from(bottomSheet!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED

        }
        return bottomSheetDialog
    }


    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun initializeViews() {

        var clients = emptyList<Client>()
        var suppliers = emptyList<Supplier>()

        binding.button21.setOnClickListener {
            dismiss()
        }
        binding.productSearchText.apply {
            addTextChangedListener {
                lifecycleScope.launch {
                    val names = if (args.orderType == "IN"){
                        suppliers = viewModel.getSuppliersWithName("%${it.toString()}%")
                        suppliers.map { it.companyName }
                    }else{
                        clients = viewModel.getClientsWithName("%${it.toString()}%")
                        clients.map { it.name }
                    }
                    val adapter = ArrayAdapter(requireContext(),R.layout.dropdown_item_list, names)
                    binding.productSearchText.setAdapter(adapter)
                }
            }

            setOnItemClickListener { _, _, i, _ ->
                hideKeyboard()
                binding.foundProductGroup.visible()

                val name: String
                val id: String

                if (args.orderType == "IN"){
                    binding.textView165.text = "Proveedor seleccionado"
                    binding.productName.text = suppliers[i].companyName
                    name = suppliers[i].companyName
                    id = suppliers[i].supplierId.toString()

                }else {
                    binding.productName.text = clients[i].name
                    binding.textView165.text = "Cliente seleccionado"
                    name = clients[i].name
                    id = clients[i].clientId.toString()
                }

                binding.button18.setOnClickListener {
                    viewModel.updateOrderExternalInfo(name, id)
                    findNavController().navigate(R.id.action_addOrderClientSupplierBottomSheet_to_createRecordFragment)
                }
            }
        }
    }
}