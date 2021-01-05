package com.puntogris.blint.ui.main

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        lifecycleScope.launchWhenStarted {
            when(viewModel.getBusinessCount()){
                0 -> createNewBusiness()
                else -> showBusinessUI()
            }
        }
    }

    private fun createNewBusiness(){
        binding.registerBusinessSummary.visible()
        binding.registerBusinessButton.apply {
            visible()
            setOnClickListener {
                findNavController().navigate(R.id.action_mainFragment_to_registerBusiness)
            }
        }
    }

    private fun showBusinessUI(){
        setupBusinessMenu()
        setupRecyclerView()
    }

    private fun setupBusinessMenu(){
        lifecycleScope.launch {
            val businesses = viewModel.getBusiness()
            val businessesNames = businesses.map { it.name }
            (binding.businessMenuDropDown as? AutoCompleteTextView)?.apply {
                setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item_list, businessesNames))
                setText(businesses.first().name, false)
            }
            binding.businessMenu.visible()
            binding.syncSwitch.visible()
        }
    }

    private fun changeBusinessListener(){
        binding.businessMenuDropDown.setOnItemClickListener { adapterView, view, i, l ->
            //get the info from database and update ui
        }
    }

    private fun setupRecyclerView(){
        mainMenuAdapter = MainMenuAdapter{ onMenuCardClicked(it) }
        val cardList = listOf(ALL_PRODUCTS_CARD_CODE, ALL_CLIENTS_CARD_CODE, ALL_SUPPLIERS_CARD_CODE)
        lifecycleScope.launch {
            mainMenuAdapter.submitList(getMenuCardList(cardList))

        }
        binding.recyclerView.apply {
            adapter = mainMenuAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
            addItemDecoration(SpaceItemDecoration(30, true))
            visible()
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        when(menuCard.code){
            ALL_PRODUCTS_CARD_CODE -> findNavController().navigate(R.id.manageProductsFragment)
            ALL_CLIENTS_CARD_CODE -> findNavController().navigate(R.id.manageClientsFragment)
            ALL_SUPPLIERS_CARD_CODE -> findNavController().navigate(R.id.manageSuppliersFragment)
        }
    }

    private suspend fun getMenuCardList(codeList: List<Int>): List<MenuCard?>{
        return codeList.map {
            when(it){
                ALL_PRODUCTS_CARD_CODE ->{
                    val count = viewModel.getProductsCount()
                    MenuCard(it,"Productos", R.drawable.ic_baseline_library_books_24,"$count productos","Ver todos", R.color.card1)
                }
                ALL_SUPPLIERS_CARD_CODE -> {
                    val count = viewModel.getClientsCount()
                    MenuCard(
                        it,
                        "Proveedores",
                        R.drawable.ic_baseline_store_24,
                        "$count proveedores",
                        "Ver todos",
                        R.color.card2
                    )
                }
                ALL_CLIENTS_CARD_CODE -> {
                    val count = viewModel.getSuppliersCount()
                    MenuCard(it,"Clientes", R.drawable.ic_baseline_people_alt_24,"$count clientes","Ver todos", R.color.card3)
                }
                else -> null
            }
        }
    }


    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}