package com.puntogris.blint.feature_store.presentation.main

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.feature_store.domain.model.Event
import com.puntogris.blint.feature_store.domain.model.MenuCard
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by activityViewModels()

    override fun initializeViews() {
        UiInterface.registerUi(showFab = false)

        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        setupMenuRecyclerView()
        setupCalendarRecyclerView()
    }

    fun onSideMenuClicked(item: Int) {
        val nav = when (item) {
            0 -> R.id.calendarFragment
            1 -> R.id.manageCategoriesFragment
            else -> R.id.manageBusinessFragment
        }
        findNavController().navigate(nav)
    }

    private fun setupCalendarRecyclerView() {
        MainCalendarAdapter { onCalendarEventClicked(it) }.let {
            binding.calendarRecyclerView.adapter = it
            subscribeEventsUi(it)
        }
    }

    private fun subscribeEventsUi(adapter: MainCalendarAdapter) {
        launchAndRepeatWithViewLifecycle {
            viewModel.lastEventsFlow.collect {
                binding.emptyEventsGroupUi.isVisible = it.isEmpty()
                binding.calendarEvents.isVisible = it.isNotEmpty()
                adapter.updateList(it)
            }
        }
    }

    private fun onCalendarEventClicked(event: Event) {
        val action = MainFragmentDirections.actionMainFragmentToEventInfoBottomSheet(event)
        findNavController().navigate(action)
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(), 3).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (menuAdapter.isHeader(position)) spanCount else 1
                }
            }
        }

        binding.recyclerView.apply {
            adapter = menuAdapter
            layoutManager = manager
            setHasFixedSize(true)
            addItemDecoration(GridSpanMarginDecoration(30, 10, manager))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard) {
        findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked() {
        findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}