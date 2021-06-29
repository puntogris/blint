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
    private lateinit var manageProductsAdapter: ManageClientsAdapter

    override fun initializeViews() {
        manageProductsAdapter = ManageClientsAdapter { onClientClickListener(it) }
        binding.recyclerView.adapter = manageProductsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenStarted {
            getAllClientsAndFillAdapter()
        }

        binding.productSearchText.addTextChangedListener {
            lifecycleScope.launch {
                it.toString().let {
                    if (it.isBlank()) getAllClientsAndFillAdapter()
                    else getAllClientsWithNameAndFillAdapter(it)
                }
            }
        }
        getParentFab().setOnClickListener {
            findNavController().navigate(R.id.editClientFragment)
        }
    }

    private suspend fun getAllClientsWithNameAndFillAdapter(text:String){
        viewModel.getClientsWithName(text).collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private suspend fun getAllClientsAndFillAdapter(){
        viewModel.getClientPaging().collect {
            manageProductsAdapter.submitData(it)
        }
    }

    private fun onClientClickListener(client: Client){
        val action = ManageClientsFragmentDirections.actionManageClientsFragmentToClientFragment(client)
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