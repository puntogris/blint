package com.puntogris.blint.feature_store.presentation.main

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.databinding.FragmentHomeBinding
import com.puntogris.blint.feature_store.domain.model.MenuCard
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupMenuRecyclerView()
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(), 2)

        binding.recyclerView.apply {
            addItemDecoration(GridSpanMarginDecoration(60, 60, manager))
            adapter = menuAdapter
            layoutManager = manager
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard) {
        findNavController().navigate(menuCard.navigationId)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}