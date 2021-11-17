package com.puntogris.blint.ui.orders.simple_order

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.SimpleOrderBinding
import com.puntogris.blint.model.FirestoreRecord
import com.puntogris.blint.model.OrderWithRecords
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.puntogris.blint.ui.orders.OrdersViewModel
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import com.puntogris.blint.utils.Constants.SIMPLE_ORDER_KEY
import com.puntogris.blint.utils.types.SimpleResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimpleOrderBottomSheet : BaseBottomSheetFragment<SimpleOrderBinding>(R.layout.simple_order) {

    private val args: SimpleOrderBottomSheetArgs by navArgs()
    private val viewModel: OrdersViewModel by viewModels()
    private var orderType = IN

    override fun initializeViews() {
        binding.fragment = this

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item_list,
            resources.getStringArray(R.array.order_type)
        )

        binding.apply {
            (recordType.editText as? AutoCompleteTextView)?.setAdapter(adapter)
            recordTypeText.setOnItemClickListener { _, _, i, _ ->
                orderType = if (i == 0) IN else OUT
            }
        }
    }

    fun onSaveButtonClicked() {
        val amount = binding.productAmountText.getString().toIntOrNull()
        if (amount != null && amount > 0 &&
            if (orderType == IN) true else amount <= args.product.amount
        ) {
            binding.simpleOrderGroup.gone()
            binding.progressBar.visible()
            val order = OrderWithRecords()
            order.order.type = orderType
            order.records = listOf(
                FirestoreRecord(
                    productId = args.product.productId,
                    productName = args.product.name,
                    amount = amount,
                    totalOutStock = args.product.totalOutStock,
                    totalInStock = args.product.totalInStock
                )
            )

            lifecycleScope.launch {
                when (viewModel.createSimpleOrder(order)) {
                    SimpleResult.Failure -> {
                        dismiss()
                        UiInterface.showSnackBar(getString(R.string.snack_order_created_error))
                    }
                    SimpleResult.Success -> {
                        navigateBack()
                        dismiss()
                        UiInterface.showSnackBar(getString(R.string.snack_created_order_success))
                    }
                }
            }
        } else {
            showSackBarAboveBottomSheet(getString(R.string.product_amount_empty))
        }
    }

    private fun navigateBack() {
        findNavController().apply {
            previousBackStackEntry!!.savedStateHandle.set(SIMPLE_ORDER_KEY, true)
            popBackStack()
        }
    }
}