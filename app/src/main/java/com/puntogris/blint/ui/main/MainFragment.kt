package com.puntogris.blint.ui.main

import android.content.Context
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.microsoft.schemas.vml.STTrueFalse
import com.puntogris.blint.R
import com.puntogris.blint.data.local.dao.DebtsDao
import com.puntogris.blint.data.local.dao.OrdersDao
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.*
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by viewModels()

    override fun onAttach(context: Context) {
        (context as MainFabListener).addListener(showFab = false)
        super.onAttach(context)
    }

    @ExperimentalTime
    override fun initializeViews() {
        //setUpUi(showFab = false)
      //  (requireActivity() as MainFabListener).addListener(showFab = false)
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupBadgeListener()
        setupCalendarRecyclerView()

        //view?.doOnPreDraw { startPostponedEnterTransition() }
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
            layoutManager = GridLayoutManager(requireContext(), 3)
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        if (menuCard.navigationId != R.id.accountPreferences &&
            viewModel.currentUser.value.currentBusinessStatus != "VALID"){
            createLongSnackBar("Sin permisos para acceder a esta informacion.").setAction("Leer mas"){
            }.show()
        }else findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked(){
        if (viewModel.currentUser.value.currentBusinessStatus != "VALID"){
            createLongSnackBar("Sin permisos para acceder a esta informacion.").setAction("Leer mas"){
            }.show()
        }else findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}