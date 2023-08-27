package com.puntogris.blint.feature_store.presentation.store.manage

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentManageStoreBinding
import com.puntogris.blint.feature_store.domain.model.Store
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageStoreFragment :
    BaseFragment<FragmentManageStoreBinding>(R.layout.fragment_manage_store) {

    private val viewModel: ManageStoreViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        setupToolbar()
        setupStoreAdapter()
    }

    private fun setupStoreAdapter() {
        ManageStoreAdapter(
            clickListener = { onStoreClicked(it) },
            selectListener = { onStoreSelected(it) }
        ).also {
            binding.manageStoresRecyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageStoreAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.storesFlow.collect { data ->
                adapter.submitList(data)
                binding.manageStoresEmptyUi.isVisible = data.isEmpty()
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
        binding.manageStoresToolbar.apply {
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
        binding.manageStoresRecyclerView.adapter = null
        super.onDestroyView()
    }
}