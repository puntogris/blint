package com.puntogris.blint.ui.client

import android.view.Menu
import android.view.MenuItem
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentManageClientsBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManageClientsFragment : BaseFragmentOptions<FragmentManageClientsBinding>(R.layout.fragment_manage_clients) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {

        val manageProductsAdapter = ManageClientsAdapter { onClientClickListener(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getClientPaging().collect {
                manageProductsAdapter.submitData(it)
            }
        }

        binding.productSearchText.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getClientsWithName(it.toString()).collect {
                    manageProductsAdapter.submitData(it)
                }
            }
        }
        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.editClientFragment)
        }
    }

    private fun onClientClickListener(client: Client){
        val action = ManageClientsFragmentDirections.actionManageClientsFragmentToClientFragment(client.clientId)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.manageClientsFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.newClient) {
            findNavController().navigate(R.id.editClientFragment)
            true
        }
        else super.onOptionsItemSelected(item)
    }

}