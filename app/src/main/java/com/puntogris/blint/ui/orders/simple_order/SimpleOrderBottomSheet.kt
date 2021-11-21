package com.puntogris.blint.ui.orders.simple_order

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puntogris.blint.R
import com.puntogris.blint.databinding.SimpleOrderBinding
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.IN
import com.puntogris.blint.utils.Constants.OUT
import com.puntogris.blint.utils.types.RepoResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimpleOrderBottomSheet : DialogFragment() {

    private val viewModel: SimpleOrderViewModel by viewModels()
    private lateinit var binding: SimpleOrderBinding

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = SimpleOrderBinding.inflate(layoutInflater)
        setupOrderTypeAdapter()

        return MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupOrderTypeAdapter() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item_list,
            resources.getStringArray(R.array.order_type)
        )

        binding.recordType.apply {
            setAdapter(adapter)
            setOnItemClickListener { _, _, i, _ ->
                viewModel.updateOrderType(if (i == 0) IN else OUT)
            }
        }
    }

    fun onSaveButtonClicked() {
        val amount = binding.productAmountText.getString().toIntOrNull() ?: 0

        lifecycleScope.launch {
            viewModel.createSimpleOrder(amount).collect {
                when (it) {
                    is RepoResult.Error -> {
                        UiInterface.showSnackBar(getString(it.error))
                        navigateBack(false)
                    }
                    is RepoResult.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_created_order_success))
                        navigateBack(true)
                    }
                    RepoResult.InProgress -> {
                        binding.simpleOrderGroup.gone()
                        binding.progressBar.visible()
                    }
                }
            }
        }
    }

    private fun navigateBack(orderSaved: Boolean) {
        setFragmentResult(
            Constants.SIMPLE_ORDER_KEY,
            bundleOf(Constants.SIMPLE_ORDER_KEY to orderSaved)
        )
        findNavController().navigateUp()
    }
}