package com.puntogris.blint.feature_store.presentation.main

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.visible
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
            else -> R.id.accountPreferences
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
                if (it.isEmpty()) {
                    binding.addEventTitle.visible()
                    binding.addEventButton.visible()
                } else {
                    binding.calendarEvents.visible()
                    adapter.updateList(it)
                }
            }
        }
    }

    private fun onCalendarEventClicked(event: Event) {
        val action = MainFragmentDirections.actionMainFragmentToEventInfoBottomSheet(event)
        findNavController().navigate(action)
    }

    private fun setupMenuRecyclerView() {
        val adapter = MainMenuAdapter { onMenuCardClicked(it) }

        binding.recyclerView.apply {
            this.adapter = adapter
            setHasFixedSize(true)
            val manager = GridLayoutManager(requireContext(), 3)
                .also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (adapter.isHeader(position)) it.spanCount else 1
                        }
                    }
                }

            layoutManager = manager
            addItemDecoration(
                GridSpanMarginDecoration(
                    30, 10,
                    gridLayoutManager = manager
                )
            )
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