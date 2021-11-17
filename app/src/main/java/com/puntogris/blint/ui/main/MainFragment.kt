package com.puntogris.blint.ui.main

import android.annotation.SuppressLint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.Constants.DISMISS_EVENT_KEY
import com.puntogris.blint.utils.UiInterface
import com.puntogris.blint.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.utils.onBackStackLiveData
import com.puntogris.blint.utils.types.EventsDashboard
import com.puntogris.blint.utils.visible
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels()

    override fun initializeViews() {
        UiInterface.apply {
            registerUi(showFab = false)
        }
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

    @SuppressLint("NotifyDataSetChanged")
    private fun setupCalendarRecyclerView() {
        MainCalendarAdapter { onCalendarEventClicked(it) }.let {
            binding.calendarRecyclerView.adapter = it

            subscribeEventsUi(it)

            onBackStackLiveData<Boolean>(DISMISS_EVENT_KEY) { dismiss ->
                if (dismiss) it.notifyDataSetChanged()
            }
        }
    }

    private fun subscribeEventsUi(adapter: MainCalendarAdapter) {
        launchAndRepeatWithViewLifecycle {
            when (val data = viewModel.getBusinessLastEvents()) {
                EventsDashboard.DataNotFound -> {
                    binding.button17.visible()
                    binding.textView151.visible()
                }
                is EventsDashboard.Error -> {
                    binding.textView151.apply {
                        text = context.getString(R.string.retrieve_information_error)
                        visible()
                    }
                }
                is EventsDashboard.Success -> {
                    binding.materialCardView2.visible()
                    adapter.submitList(data.data)
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