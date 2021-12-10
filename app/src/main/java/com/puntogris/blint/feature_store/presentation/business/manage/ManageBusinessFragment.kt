package com.puntogris.blint.feature_store.presentation.business.manage

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
import com.puntogris.blint.databinding.FragmentManageBusinessBinding
import com.puntogris.blint.feature_store.domain.model.Business
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageBusinessFragment :
    BaseFragmentOptions<FragmentManageBusinessBinding>(R.layout.fragment_manage_business) {

    private val viewModel: ManageBusinessViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.fragment = this
        setupBusinessAdapter()
    }

    private fun setupBusinessAdapter() {
        ManageBusinessAdapter(
            { onBusinessClicked(it) },
            { onBusinessSelected(it) }
        ).let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageBusinessAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.businesses.collect { data ->
                adapter.submitList(data)
                binding.businessEmptyUi.isVisible = data.isEmpty()
            }
        }
    }

    private fun onBusinessClicked(business: Business) {
        val action =
            ManageBusinessFragmentDirections.actionManageBusinessFragmentToBusinessFragment(business)
        findNavController().navigate(action)
    }

    private fun onBusinessSelected(business: Business) {
        lifecycleScope.launch {
            when (viewModel.updateCurrentBusiness(business)) {
                is Resource.Error -> {
                    UiInterface.showSnackBar(getString(R.string.snack_an_error_occurred))
                }
                is Resource.Success -> {
                    findNavController().navigate(R.id.homeFragment)
                    UiInterface.showSnackBar(getString(R.string.business_selected))
                }
            }
        }
    }

    fun onCreateNewBusinessClicked() {
        findNavController().navigate(R.id.registerBusinessFragment)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageBusinessMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.newBusiness -> {
                findNavController().navigate(R.id.registerBusinessFragment)
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