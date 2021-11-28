package com.puntogris.blint.feature_store.presentation.client.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.hideKeyboard
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentManageClientsBinding
import com.puntogris.blint.feature_store.domain.model.Client
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ManageClientsFragment :
    BaseFragmentOptions<FragmentManageClientsBinding>(R.layout.fragment_manage_clients) {

    private val viewModel: ManageClientsViewModel by viewModels()

    override fun initializeViews() {
        binding.viewModel = viewModel
        registerToolbarBackButton(binding.searchToolbar)

        UiInterface.registerUi(showToolbar = false, showAppBar = true, showFab = true) {
            findNavController().navigate(R.id.editClientFragment)
        }
        setupClientsAdapter()
    }

    private fun setupClientsAdapter() {
        ManageClientsAdapter { onClientClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageClientsAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.clientsFlow.collect {
                adapter.submitData(it)
            }
        }
    }

    private fun onClientClickListener(client: Client) {
        hideKeyboard()
        val action =
            ManageClientsFragmentDirections.actionManageClientsFragmentToClientFragment(client = client)
        findNavController().navigate(action)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageClientsFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.newClient) {
            findNavController().navigate(R.id.editClientFragment)
            true
        } else super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}