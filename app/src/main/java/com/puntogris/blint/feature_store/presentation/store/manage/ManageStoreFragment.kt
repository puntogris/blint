package com.puntogris.blint.feature_store.presentation.store.manage

import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.types.Resource
import com.puntogris.blint.databinding.FragmentManageStoreBinding
import com.puntogris.blint.feature_store.domain.model.Store
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageStoreFragment :
    BaseFragmentOptions<FragmentManageStoreBinding>(R.layout.fragment_manage_store) {

    private val viewModel: ManageStoreViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.fragment = this
        setupStoreAdapter()
    }

    private fun setupStoreAdapter() {
        ManageStoreAdapter(
            { onStoreClicked(it) },
            { onStoreSelected(it) }
        ).let {
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

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageStoreMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newStore -> {
                findNavController().navigate(R.id.registerStoreFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}