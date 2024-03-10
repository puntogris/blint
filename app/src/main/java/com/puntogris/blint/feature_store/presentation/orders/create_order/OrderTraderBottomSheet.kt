package com.puntogris.blint.feature_store.presentation.orders.create_order

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.OrderTraderBottomSheetBinding
import com.puntogris.blint.feature_store.domain.model.Trader
import com.puntogris.blint.feature_store.presentation.trader.manage.ManageTradersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderTraderBottomSheet : BottomSheetDialogFragment() {

    private val binding by viewBinding(OrderTraderBottomSheetBinding::bind)

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.createOrderGraphNav) {
        defaultViewModelProviderFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.order_trader_bottom_sheet, container, false)
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        subscribeUi()
        setupListeners()
    }

    private fun setupListeners() {
        binding.editTextTraderSearch.doAfterTextChanged { editable ->
            if (editable != null) {
                viewModel.setQuery(editable)
            }
        }
    }

    private fun subscribeUi() {
        val adapter = ManageTradersAdapter { onTraderClicked(it) }
        binding.recyclerViewTraders.adapter = adapter
        viewModel.tradersLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
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

    private fun setupToolbar() {
        with(binding.toolbar) {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_menu_item_close) {
                    dismiss()
                }
                true
            }
        }
    }
}