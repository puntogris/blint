package com.puntogris.blint.feature_store.presentation.orders.detailed_order

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.Constants.IN
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.OrderTraderBottomSheetBinding
import com.puntogris.blint.feature_store.domain.model.Client
import com.puntogris.blint.feature_store.domain.model.Supplier
import com.puntogris.blint.feature_store.presentation.client.manage.ManageClientsAdapter
import com.puntogris.blint.feature_store.presentation.supplier.manage.ManageSuppliersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTraderBottomSheet :
    BaseBottomSheetFragment<OrderTraderBottomSheetBinding>(R.layout.order_trader_bottom_sheet) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.detailedOrderGraphNav) { defaultViewModelProviderFactory }
    private val args: OrderTraderBottomSheetArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog

        bottomSheetDialog.setOnShowListener {
            bottomSheetDialog.findViewById<FrameLayout>(
                com.google.android.material.R.id.design_bottom_sheet
            )?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
                it.layoutParams = layoutParams
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return bottomSheetDialog
    }

    override fun initializeViews() {
        binding.viewModel = viewModel
        binding.fragment = this

        registerToolbarBackButton(binding.searchToolbar)

        if (args.orderType == IN) subscribeSuppliersUi() else subscribeClientsUi()
    }

    private fun subscribeClientsUi() {
        ManageClientsAdapter { onTraderClicked(it) }.let { adapter ->
            binding.recyclerView.adapter = adapter
            viewModel.clientsLiveData.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    private fun subscribeSuppliersUi() {
        ManageSuppliersAdapter { onTraderClicked(it) }.let { adapter ->
            binding.recyclerView.adapter = adapter
            viewModel.suppliersLiveData.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }

    private fun onTraderClicked(client: Client) {
        viewModel.updateOrderTrader(client.name, client.clientId)
        UiInterface.showSnackBar(getString(R.string.snack_client_added_order, client.name))
        findNavController().navigate(R.id.createOrderFragment)
    }

    private fun onTraderClicked(supplier: Supplier) {
        viewModel.updateOrderTrader(supplier.companyName, supplier.supplierId)
        UiInterface.showSnackBar(
            getString(
                R.string.snack_suppliers_added_order,
                supplier.companyName
            )
        )
        findNavController().navigate(R.id.createOrderFragment)
    }
}