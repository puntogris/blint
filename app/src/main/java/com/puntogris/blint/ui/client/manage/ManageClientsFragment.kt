package com.puntogris.blint.ui.client.manage

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageClientsBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.hideKeyboard
import com.puntogris.blint.utils.registerToolbarBackButton
import dagger.hilt.android.AndroidEntryPoint

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

        ManageClientsAdapter { onClientClickListener(it) }.let {
            binding.recyclerView.adapter = it
            subscribeUi(it)
        }
    }

    private fun subscribeUi(adapter: ManageClientsAdapter) {
        viewModel.clientsLiveData.observe(viewLifecycleOwner) {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }
    }

    private fun onClientClickListener(client: Client) {
        hideKeyboard()
        val action =
            ManageClientsFragmentDirections.actionManageClientsFragmentToClientFragment(client)
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