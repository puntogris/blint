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
import kotlinx.coroutines.flow.collect
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

    private fun setupToolbar() {
        binding.toolbar.apply {
            registerToolbarBackButton(this)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_add_store) {
                    findNavController().navigate(R.id.registerStoreFragment)
                }
                true
            }
        }
    }

    private fun setupStoreAdapter() {
        ManageStoreAdapter(
            clickListener = { onStoreClicked(it) },
            selectListener = { onStoreSelected(it) }
        ).also {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageStoreAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.storesFlow.collect { data ->
                adapter.submitList(data)
                binding.storesEmptyUi.isVisible = data.isEmpty()
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

    fun onCreateNewStoreClicked() {
        findNavController().navigate(R.id.registerStoreFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}