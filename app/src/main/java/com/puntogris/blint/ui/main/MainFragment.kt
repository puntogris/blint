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
import com.puntogris.blint.model.MenuCard.Companion.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.model.MenuCard.Companion.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.model.MenuCard.Companion.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()


    //esta lista viene de la base de datos del usuario o shared pref donde guardo las tarjetas que habilito
    private val cardList = listOf(ALL_PRODUCTS_CARD_CODE, ALL_CLIENTS_CARD_CODE, ALL_SUPPLIERS_CARD_CODE)

    val testList = cardList.map {
        MenuCard.fromCardCode(it)
    }

    //ahora con estos codigos busco la informacion que necesito y armo la lista de menu para el adapter

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
        mainMenuAdapter.submitList(testList)
        binding.recyclerView.apply {
            adapter = mainMenuAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
            addItemDecoration(SpaceItemDecoration(30, true))
            visible()
        }
    }


    private fun onMenuCardClicked(menuCard: MenuCard){
        //
    }
}