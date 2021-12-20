package com.puntogris.blint.feature_store.presentation.orders.simple_order

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.*
import com.puntogris.blint.common.utils.Constants.IN
import com.puntogris.blint.common.utils.Constants.OUT
import com.puntogris.blint.common.utils.types.ProgressResource
import com.puntogris.blint.databinding.DialogSimpleOrderBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SimpleOrderDialog : DialogFragment() {

    private val viewModel: SimpleOrderViewModel by viewModels()
    private lateinit var binding: DialogSimpleOrderBinding

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        binding = DialogSimpleOrderBinding.inflate(layoutInflater)
        binding.dialog = this

        //TODO for some reason declaring this on the xml doesn't work as intended
        binding.productAmountText.inputType = InputType.TYPE_CLASS_NUMBER

        setupOrderTypeAdapter()

        return MaterialAlertDialogBuilder(requireContext(), R.style.MaterialAlertDialog_rounded)
            .setView(binding.root)
            .setBackgroundInsetStart(20)
            .setBackgroundInsetEnd(20)
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
                    is ProgressResource.Error -> {
                        UiInterface.showSnackBar(getString(it.error))
                        dismiss()
                    }
                    is ProgressResource.Success -> {
                        UiInterface.showSnackBar(getString(R.string.snack_created_order_success))
                        dismiss()
                    }
                    ProgressResource.InProgress -> {
                        hideKeyboard()
                        binding.simpleOrderGroup.gone()
                        binding.progressBar.visible()
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        setFragmentResult(
            Keys.SCANNER_FRAGMENT_KEY,
            bundleOf(Keys.RESUME_CAMERA_KEY to true)
        )
        super.onDismiss(dialog)
    }
}