package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseBottomSheetFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.OrderTraderBottomSheetBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.presentation.trader.manage.ManageTradersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTraderBottomSheet :
    BaseBottomSheetFragment<OrderTraderBottomSheetBinding>(R.layout.order_trader_bottom_sheet) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) { defaultViewModelProviderFactory }

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

        setupToolbar()
        subscribeUi()
    }

    private fun subscribeUi() {
        ManageTradersAdapter { onTraderClicked(it) }.let { adapter ->
            binding.orderTraderRecyclerView.adapter = adapter
            viewModel.tradersLiveData.observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }
        }
    }

    override fun dismiss() {
        hideKeyboard()
        super.dismiss()
    }

    private fun onTraderClicked(trader: Trader) {
        viewModel.updateOrderTrader(trader.name, trader.traderId)
        UiInterface.showSnackBar(getString(R.string.snack_trader_added_order, trader.name))
        findNavController().navigateUp()
    }

    private fun setupToolbar(){
        binding.orderTraderToolbar.apply {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_close){
                    dismiss()
                }
                true
            }
        }
    }
}