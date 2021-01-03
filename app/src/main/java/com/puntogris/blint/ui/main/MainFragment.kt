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
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()

    private val testList  = mutableListOf(
        MenuCard(1,"Productos", R.drawable.ic_baseline_library_books_24,"1 / 40 stock","Ver todos", R.color.card1),
        MenuCard(2,"Proveedores", R.drawable.ic_baseline_store_24," 1 proveedor","Ver todos", R.color.card2),
        MenuCard(3,"Clientes", R.drawable.ic_baseline_people_alt_24,"4 clientes","Ver todos",R.color.card3),
    )

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