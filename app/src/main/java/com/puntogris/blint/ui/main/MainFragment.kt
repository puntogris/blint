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
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var adapter: MainMenuAdapter
    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        lifecycleScope.launchWhenStarted {
            when(viewModel.getBusinessCount()){
                0 -> createNewBusiness()
                else -> showCurrentBusiness()
            }
        }
    }

    private fun createNewBusiness(){
        binding.button2.visible()
        binding.textView4.visible()
        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_registerBusiness)
        }
    }

    private fun showCurrentBusiness(){
        lifecycleScope.launch {
            val businesses = viewModel.getBusiness()
            val businessesNames = businesses.map { it.name }
            (binding.textField as? AutoCompleteTextView)?.apply {
                setAdapter(ArrayAdapter(requireContext(), R.layout.dropdown_item_list, businessesNames))
                setText(businesses.first().name, false)
            }
            binding.menu.visible()
        }
        adapter = MainMenuAdapter{ onMenuCardClicked(it) }
        binding.recyclerView.apply {
            adapter = this@MainFragment.adapter
            layoutManager = GridLayoutManager(requireContext(),2)
            addItemDecoration(SpaceItemDecoration(30, true))
            visible()
        }
        adapter.submitList(test)
    }

    val test  = mutableListOf(
            MenuCard(1,"Productos", R.drawable.ic_baseline_library_books_24,"1 / 40 stock","Ver todos", R.color.card1),
           // MenuCard(2,"Agregar",R.drawable.ic_baseline_library_add_24,"","Producto", R.color.card2),
            MenuCard(2,"Proveedores", R.drawable.ic_baseline_store_24," 1 proveedor","Ver todos", R.color.card2),
            MenuCard(3,"Clientes", R.drawable.ic_baseline_people_alt_24,"4 clientes","Ver todos",R.color.card3),
    )

    private fun onMenuCardClicked(menuCard: MenuCard){
        // test.removeAt(menuCard.id - 1)
     //   adapter.notifyItemRemoved(menuCard.id - 1)
        if (menuCard.id == 6){
            test.add(3, MenuCard(4,"Ver todos", R.drawable.ic_baseline_people_alt_24,"","Clientes", R.color.card5))
            adapter.notifyItemInserted(3)

        }
    }
}