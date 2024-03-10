package com.puntogris.blint.feature_store.presentation.store.manage

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentManageStoreBinding
import com.puntogris.blint.feature_store.domain.model.Store
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageStoreFragment : Fragment(R.layout.fragment_manage_store) {

    private val viewModel: ManageStoreViewModel by viewModels()

    private val binding by viewBinding(FragmentManageStoreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupStoreAdapter()
    }

    private fun setupStoreAdapter() {
        val adapter = ManageStoreAdapter(
            clickListener = { onStoreClicked(it) },
            selectListener = { onStoreSelected(it) }
        )
        binding.recyclerViewStores.adapter = adapter
        subscribeUi(adapter)
    }

    private fun subscribeUi(adapter: ManageStoreAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.storesFlow.collect { data ->
                adapter.submitList(data)
                binding.manageStoresEmptyUi.root.isVisible = data.isEmpty()
            }
        }
    }

    private fun onStoreClicked(store: Store) {
        val action =
            ManageStoreFragmentDirections.actionManageStoreFragmentToStoreFragment(store)
        findNavController().navigate(action)
    }

    private fun onStoreSelected(store: Store) {
        lifecycleScope.launch {
            when (viewModel.updateCurrentStore(store)) {
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                }

                is Resource.Success -> {
                    findNavController().navigate(R.id.homeFragment)
                    UiInterface.showSnackBar(getString(R.string.store_selected))
                }
            }
        }
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_add_store) {
                    findNavController().navigate(R.id.registerStoreFragment)
                }
                true
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerViewStores.adapter = null
        super.onDestroyView()
    }
}
