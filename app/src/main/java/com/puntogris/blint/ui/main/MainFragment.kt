package com.puntogris.blint.ui.main

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import com.puntogris.blint.utils.Constants.BLINT_WEBSITE_LEARN_MORE
import com.puntogris.blint.utils.Constants.DISMISS_EVENT_KEY
import com.puntogris.blint.utils.Constants.ENABLED
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by viewModels()

    @Inject lateinit var productsDao: ProductsDao

    @ExperimentalTime
    override fun initializeViews() {
        UiInterface.register(showFab = false)
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupBadgeListener()
        setupCalendarRecyclerView()
    }

    fun onSideMenuClicked(item: Int){
        val nav = when(item){
            0 -> R.id.calendarFragment
            1 -> R.id.manageCategoriesFragment
            else -> R.id.accountPreferences
        }
        findNavController().navigate(nav)
    }
    private fun setupBadgeListener(){
        launchAndRepeatWithViewLifecycle {
            viewModel.getUnreadNotificationsCount().collect {
                UiInterface.updateBadge(it)
            }
        }
    }

    private fun setupCalendarRecyclerView(){
        mainCalendarAdapter = MainCalendarAdapter { onCalendarEventClicked(it) }

        launchAndRepeatWithViewLifecycle {
            when(val data = viewModel.getBusinessLastEvents()){
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
                    mainCalendarAdapter.submitList(data.data)
                }
            }
        }

        binding.calendarRecyclerView.apply {
            adapter = mainCalendarAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        onBackStackLiveData<Boolean>(DISMISS_EVENT_KEY){
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
            setHasFixedSize(true)
            val manager = GridLayoutManager(requireContext(), 3)
                .also {
                it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (mainMenuAdapter.isHeader(position)) it.spanCount else 1
                    }
                }
            }

            layoutManager = manager
            addItemDecoration(
                GridSpanMarginDecoration(
                    30,10,
                    gridLayoutManager = manager
                )
            )
        }
    }

    private fun showRequirePermissionsSnackBar(){
        UiInterface.showSnackBar(getString(R.string.action_require_permissions)){
            launchWebBrowserIntent(BLINT_WEBSITE_LEARN_MORE)
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        if (menuCard.navigationId != R.id.accountPreferences &&
            viewModel.currentUser.value.businessStatus != ENABLED){
            showRequirePermissionsSnackBar()
        }else findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked(){
        if (viewModel.currentUser.value.businessStatus != ENABLED){
            showRequirePermissionsSnackBar()
        }else findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}