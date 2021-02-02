package com.puntogris.blint.ui.main

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.puntogris.blint.R
import com.puntogris.blint.data.local.dao.UsersDao
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.model.Product
import com.puntogris.blint.model.RoomUser
import com.puntogris.blint.ui.SharedPref
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.OPERATIONS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
import com.puntogris.blint.utils.Constants.REPORT_FIELD_FIRESTORE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import me.farahani.spaceitemdecoration.SpaceItemDecoration
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        setupRecyclerView()
        setupBusinessMenu()
        changeBusinessListener()
    }

    private fun setupBusinessMenu(){
        viewModel.getBusiness()
        val dropDownMenu = (binding.businessMenuDropDown as? AutoCompleteTextView)
        lifecycleScope.launch {
            viewModel.businesses.collect { list ->
                val businessesName = list.map { it.name }
                dropDownMenu?.setText(viewModel.getCurrentBusiness().currentBusinessName,false)
                val adapter = ArrayAdapter(requireContext(),R.layout.dropdown_item_list, businessesName)
                dropDownMenu?.setAdapter(adapter)
            }
        }
    }

    private fun changeBusinessListener(){
        binding.businessMenuDropDown.setOnItemClickListener { _, _, i, _ ->
            lifecycleScope.launch { viewModel.updateCurrentBusiness(i) }
        }
    }

    private fun setupRecyclerView(){
        mainMenuAdapter = MainMenuAdapter{ onMenuCardClicked(it) }
        binding.recyclerView.apply {
            adapter = mainMenuAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(SpaceItemDecoration(20, true))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        when(menuCard.code){
            ALL_PRODUCTS_CARD_CODE -> findNavController().navigate(R.id.manageProductsFragment)
            ALL_CLIENTS_CARD_CODE -> findNavController().navigate(R.id.manageClientsFragment)
            ALL_SUPPLIERS_CARD_CODE -> findNavController().navigate(R.id.manageSuppliersFragment)
            ACCOUNTING_CARD_CODE -> findNavController().navigate(R.id.calendarFragment)
            RECORDS_CARD_CODE -> findNavController().navigate(R.id.manageRecordsFragment)
            OPERATIONS_CARD_CODE -> findNavController().navigate(R.id.operationsFragment)
            CHARTS_CARD_CODE -> findNavController().navigate(R.id.reportsFragment)
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        (binding.businessMenuDropDown as? AutoCompleteTextView)?.setAdapter(null)
        super.onDestroyView()
    }
}