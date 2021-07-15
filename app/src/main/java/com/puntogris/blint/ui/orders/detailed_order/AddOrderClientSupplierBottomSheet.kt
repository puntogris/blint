package com.puntogris.blint.ui.orders.detailed_order

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.puntogris.blint.R
import com.puntogris.blint.databinding.AddOrderClientSupplierBottomSheetBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.ui.client.ManageClientsAdapter
import com.puntogris.blint.ui.supplier.ManageSuppliersAdapter
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.IN
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddOrderClientSupplierBottomSheet:BaseBottomSheetFragment<AddOrderClientSupplierBottomSheetBinding>(R.layout.add_order_client_supplier_bottom_sheet) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private val args:AddOrderClientSupplierBottomSheetArgs by navArgs()
    private lateinit var manageSuppliersAdapter: ManageSuppliersAdapter
    private lateinit var manageClientsAdapter: ManageClientsAdapter

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

    private suspend fun getAllWithNameAndFillAdapter(text:String){
        if (args.orderType == IN){
            viewModel.getSuppliersWithName(text).collect {
                manageSuppliersAdapter.submitData(it)
            }
        }else{
            viewModel.getClientsWithName(text).collect {
                manageClientsAdapter.submitData(it)
            }
        }
    }

    private suspend fun getAllAndFillAdapter(){
        if (args.orderType == IN){
            viewModel.getSuppliersPaging().collect {
                manageSuppliersAdapter.submitData(it)
            }
        }else{
            viewModel.getClientPaging().collect {
                manageClientsAdapter.submitData(it)
            }
        }
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun initializeViews() {
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = if (args.orderType == IN){
            manageSuppliersAdapter = ManageSuppliersAdapter{ onSupplierClicked(it) }
            manageSuppliersAdapter
        }else{
            manageClientsAdapter = ManageClientsAdapter{ onClientClicked(it)}
            manageClientsAdapter
        }
        binding.recyclerView.adapter = adapter

        launchAndRepeatWithViewLifecycle {
            getAllAndFillAdapter()
        }

        binding.supplierSearch.addTextChangedListener {
            lifecycleScope.launch {
                it.toString().let {
                    if (it.isBlank()) getAllAndFillAdapter()
                    else getAllWithNameAndFillAdapter(it)
                }
            }
        }

        binding.closeButton.setOnClickListener {
            dismiss()
        }
    }

    private fun onClientClicked(client: Client){
        viewModel.updateOrderExternalInfo(client.name, client.clientId)
        findNavController().navigate(R.id.createOrderFragment)
        showLongSnackBarAboveFab(getString(R.string.snack_client_added_order, client.name))
    }

    private fun onSupplierClicked(supplier: Supplier){
        viewModel.updateOrderExternalInfo(supplier.companyName, supplier.supplierId)
        findNavController().navigate(R.id.createOrderFragment)
        showLongSnackBarAboveFab(getString(R.string.snack_suppliers_added_order, supplier.companyName))
    }
}