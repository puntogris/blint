package com.puntogris.blint.feature_store.presentation.main

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentHomeBinding
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.model.MenuCard
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : BaseFragmentOptions<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        UiInterface.registerUi(showToolbar = false)

        setupMenuRecyclerView()

        val donutSet = listOf(
            80f,
            80f,
            40f
        )

        binding.donutChartView.apply {
            donutColors = intArrayOf(
                Color.parseColor("#2D8EFF"),
                Color.parseColor("#FBB449"),
                Color.parseColor("#FFFFFF")
            )
            animation.duration = 1000L
            animate(donutSet)
        }
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(),2)

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