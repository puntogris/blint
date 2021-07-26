package com.puntogris.blint.ui.main

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE
import com.puntogris.blint.utils.Constants.ENABLED
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by viewModels()

    @ExperimentalTime
    override fun initializeViews() {
        registerUiInterface.register(showFab = false)
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupBadgeListener()
        setupCalendarRecyclerView()

        //view?.doOnPreDraw { startPostponedEnterTransition() }

        binding.imageView11.setOnClickListener { findNavController().navigate(R.id.manageCategoriesFragment) }
        binding.imageView17.setOnClickListener { findNavController().navigate(R.id.manageDebtFragment) }
    }
    private fun setupBadgeListener(){
        launchAndRepeatWithViewLifecycle {
            viewModel.getUnreadNotificationsCount().collect {
                getParentBadge().setNumber(it)
            }
        }
    }

    private fun setupCalendarRecyclerView(){
        mainCalendarAdapter = MainCalendarAdapter { onCalendarEventClicked(it) }

        launchAndRepeatWithViewLifecycle {
            when(val data = viewModel.getBusinessLastEvents()){
                EventsDashboard.DataNotFound -> {
                    view?.findViewById<Button>(R.id.button17)?.visible()
                    view?.findViewById<TextView>(R.id.textView151)?.visible()
                }
                is EventsDashboard.Error -> {
                    view?.findViewById<Button>(R.id.textView151)?.apply {
                        text = context.getString(R.string.retrieve_information_error)
                        visible()
                    }
                }
                is EventsDashboard.Success -> {
                    view?.findViewById<CardView>(R.id.materialCardView2)?.visible()
                    mainCalendarAdapter.submitList(data.data)
                }
            }
        }

        binding.calendarRecyclerView.apply {
            adapter = mainCalendarAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("dismiss_key")?.observe(
            viewLifecycleOwner) {
            if (it) mainCalendarAdapter.notifyDataSetChanged()
        }
    }

    private fun onCalendarEventClicked(event: Event){
        val action = MainFragmentDirections.actionMainFragmentToEventInfoBottomSheet(event)
        findNavController().navigate(action)
    }


    private fun setupMenuRecyclerView(){
        mainMenuAdapter = MainMenuAdapter{ onMenuCardClicked(it) }
        binding.recyclerView.apply {
            adapter = mainMenuAdapter
            val manager = GridLayoutManager(requireContext(), 3).also {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (mainMenuAdapter.isHeader(position)) it.spanCount else 1
                    }
                }
            }
            layoutManager = manager

        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        if (menuCard.navigationId != R.id.accountPreferences &&
            viewModel.currentUser.value.currentBusinessStatus != ENABLED){
            createLongSnackBar(getString(R.string.action_require_permissions)).setAction(getString(R.string.read_more)){
                launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
            }.show()
        }else findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked(){
        if (viewModel.currentUser.value.currentBusinessStatus != ENABLED){
            createLongSnackBar(getString(R.string.action_require_permissions)).setAction(getString(R.string.read_more)){
                launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
            }.show()
        }else findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}