package com.puntogris.blint.feature_store.presentation.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentHomeBinding
import com.puntogris.blint.feature_store.domain.model.MenuCard
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupMenuRecyclerView()
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(), 2)

        with(binding.homeFragmentMenuRecyclerView) {
            addItemDecoration(GridSpanMarginDecoration(60, 60, manager))
            adapter = menuAdapter
            layoutManager = manager
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard) {
        findNavController().navigate(menuCard.navigationId)
    }

    override fun onDestroyView() {
        binding.homeFragmentMenuRecyclerView.adapter = null
        super.onDestroyView()
    }
}
