package com.puntogris.blint.feature_store.presentation.main

import android.graphics.Color
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
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
        setupCalendarRecyclerView()

        val donutSet = listOf(
            80f,
            80f,
            40f
        )

        binding.donutChartView.apply {
            donutColors = intArrayOf(
                Color.parseColor("#2D8EFF"),
                Color.parseColor("#FF9E2D"),
                Color.parseColor("#FFFFFF")
            )
            animation.duration = 1000L
            animate(donutSet)
        }
    }

    private fun setupCalendarRecyclerView() {
//        MainCalendarAdapter { onCalendarEventClicked(it) }.let {
//            binding.calendarRecyclerView.adapter = it
//            subscribeEventsUi(it)
//        }
    }

    private fun subscribeEventsUi(adapter: MainCalendarAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.lastEventsFlow.collect {
                adapter.updateList(it)
            }
        }
    }

    private fun onCalendarEventClicked(event: Event) {
        val action = HomeFragmentDirections.actionMainFragmentToEventInfoBottomSheet(event)
        findNavController().navigate(action)
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(), 2)

        binding.recyclerView.apply {
            adapter = menuAdapter
            layoutManager = manager
            setHasFixedSize(true)
            addItemDecoration(GridSpanMarginDecoration(60, 60, manager))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard) {
        findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked() {
        findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        //  binding.recyclerView.adapter = null
        //  binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}