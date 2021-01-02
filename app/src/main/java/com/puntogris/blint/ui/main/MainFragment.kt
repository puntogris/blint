package com.puntogris.blint.ui.main

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {
    private lateinit var adapter: MainMenuAdapter

    override fun initializeViews() {
        adapter = MainMenuAdapter{onMenuCardClicked(it)}
        val manager = GridLayoutManager(requireContext(),2)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.addItemDecoration(SpaceItemDecoration(30, true))
        adapter.submitList(test)

        binding.button2.setOnClickListener {
            findNavController().navigate(R.id.action_mainFragment_to_registerBusiness)
        }
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