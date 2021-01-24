package com.puntogris.blint.ui.main

import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import javax.inject.Inject
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var dropDownMenuAdapter: DropDownMenuAdapter
    @Inject
    lateinit var sharedPref: SharedPref

    override fun initializeViews() {
        setupRecyclerView()
        setupBusinessMenu()
        changeBusinessListener()
    }

    private fun setupBusinessMenu(){
        val dropDownMenu = (binding.businessMenuDropDown as? AutoCompleteTextView)
        dropDownMenuAdapter = DropDownMenuAdapter(requireContext())
        dropDownMenu?.setAdapter(dropDownMenuAdapter)
        lifecycleScope.launch(Dispatchers.IO) {
            val businesses = viewModel.getBusiness()
            val businessesNames = businesses.map { it.name }
            dropDownMenu?.setText(businesses.first().name, false)
            dropDownMenuAdapter.setList(businessesNames)
        }
    }

    private fun changeBusinessListener(){
        binding.businessMenuDropDown.setOnItemClickListener { adapterView, view, i, l ->

        }
    }

    private fun setupRecyclerView(){
        mainMenuAdapter = MainMenuAdapter{ onMenuCardClicked(it) }
        binding.recyclerView.apply {
            adapter = mainMenuAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItemDecoration(20, true))
        }

        lifecycleScope.launch(Dispatchers.IO) {
            mainMenuAdapter.currentList[0].description = "${viewModel.getProductsCount()} productos"
            mainMenuAdapter.currentList[1].description = "${viewModel.getClientsCount()} clientes"
            mainMenuAdapter.currentList[2].description = "${viewModel.getSuppliersCount()} proveedores"
            requireActivity().runOnUiThread {
                mainMenuAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        when(menuCard.code){
            ALL_PRODUCTS_CARD_CODE -> findNavController().navigate(R.id.manageProductsFragment)
            ALL_CLIENTS_CARD_CODE -> findNavController().navigate(R.id.manageClientsFragment)
            ALL_SUPPLIERS_CARD_CODE -> findNavController().navigate(R.id.manageSuppliersFragment)
            ACCOUNTING_CARD_CODE -> findNavController().navigate(R.id.calendarFragment)
            RECORDS_CARD_CODE -> findNavController().navigate(R.id.manageRecordsFragment)
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        (binding.businessMenuDropDown as? AutoCompleteTextView)?.setAdapter(null)
        super.onDestroyView()
    }
}