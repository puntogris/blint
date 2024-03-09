package com.puntogris.blint.feature_store.presentation.store

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.maxkeppeler.sheets.info.InfoSheet
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.getString
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.DeleteStore
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentDeleteStoreBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeleteStoreFragment :
    Fragment(R.layout.fragment_delete_store) {

    private val args: DeleteStoreFragmentArgs by navArgs()

    private val viewModel: StoreViewModel by viewModels()

    private val binding by viewBinding(FragmentDeleteStoreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerToolbarBackButton(binding.toolbar)
        setupListeners()
    }

    private fun setupListeners() {
        binding.buttonDeleteStore.setOnClickListener {
            if (binding.editTextStoreName.getString() == args.store.name) {
                showDeleteBusinessDialog()
            } else {
                UiInterface.showSnackBar(getString(R.string.snack_store_name_does_not_match))
            }
        }
    }

    private fun showDeleteBusinessDialog() {
        InfoSheet().show(requireParentFragment().requireContext()) {
            title(R.string.do_you_want_to_delete_store)
            content(R.string.delete_local_store_warning_dialog)
            onNegative(R.string.action_no)
            onPositive(R.string.action_delete) {
                onDeleteConfirmation()
            }
        }
    }

    private fun onDeleteConfirmation() {
        lifecycleScope.launch {
            when (viewModel.deleteBusiness(args.store.storeId)) {
                DeleteStore.Failure -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_store_error))
                }

                DeleteStore.Success.HasStores -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_store_success))
                    findNavController().navigate(R.id.homeFragment)
                }

                DeleteStore.Success.NoStores -> {
                    UiInterface.showSnackBar(getString(R.string.snack_delete_store_success))
                    findNavController().navigate(R.id.newUserFragment)
                }
            }
        }
    }
}
