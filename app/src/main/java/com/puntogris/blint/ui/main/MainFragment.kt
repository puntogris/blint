package com.puntogris.blint.ui.main

import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.EventsDashboard
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by viewModels()
    private lateinit var badge: BadgeDrawable

    @ExperimentalTime
    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupBadgeListener()
        setupCalendarRecyclerView()

//        val file = File(requireContext().filesDir.absolutePath + "/test.pdf")
//        val simplyPdfDocument = SimplyPdf.with(requireContext(), file)
//            .colorMode(DocumentInfo.ColorMode.COLOR)
//            .paperSize(PrintAttributes.MediaSize.ISO_A4)
//            .margin(DocumentInfo.Margins.DEFAULT)
//            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
//            .build()
//
//        val textProperties = TextProperties()
//        textProperties.textSize = 24
//        textProperties.textColor = "#000000"
//        textProperties.alignment = Layout.Alignment.ALIGN_CENTER
//        textProperties.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
//
//        val textComposer = TextComposer(simplyPdfDocument)
//        textComposer.write("Demonstrate the usage of TextComposer.", textProperties)
//
//        val table = TableProperties()
//        table.borderColor = "#000000"
//        table.borderWidth = 2
//
//        val paparer = PrintAttributes.MediaSize.ISO_A4
//        val tableText = TableComposer(simplyPdfDocument)
//        println(paparer)
//        tableText.draw(listOf(listOf(TextCell("sdfsdfsdf", textProperties, 340))))
//        tableText.draw(listOf(listOf(TextCell("sdfsdfsdf", textProperties, 340))))
//        simplyPdfDocument.finish()

    }

    private fun setupBadgeListener(){
        badge = BadgeDrawable.create(requireContext())
        lifecycleScope.launchWhenStarted {
            viewModel.getUnreadNotificationsCount().collect {
                badge.apply {
                    number = it
                    isVisible = it != 0
                }
            }
        }
    }

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onPrepareOptionsMenu(menu: Menu) {
        BadgeUtils.attachBadgeDrawable(
            badge,
            requireActivity().findViewById(R.id.toolbar),
            R.id.notificationsFragment
        )
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupCalendarRecyclerView(){
        mainCalendarAdapter = MainCalendarAdapter { onCalendarEventClicked(it) }

        lifecycleScope.launch {
            when(val data = viewModel.getBusinessLastEvents()){
                EventsDashboard.DataNotFound -> {
                    view?.findViewById<Button>(R.id.button17)?.visible()
                    view?.findViewById<TextView>(R.id.textView151)?.visible()
                }
                is EventsDashboard.Error -> {
                    view?.findViewById<Button>(R.id.textView151)?.apply {
                        text = "Error al retirar la informacion."
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
           // addItemDecoration(I(requireContext(), DividerItemDecoration.))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        findNavController().navigate(menuCard.navigationId)
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.notificationsFragment).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.notificationsFragment){
            findNavController().navigate(R.id.notificationsFragment)
            true
        }
        else super.onOptionsItemSelected(item)
    }

    fun onAddEventClicked(){
        findNavController().navigate(R.id.createEventFragment)
    }

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onDestroyView() {
        BadgeUtils.detachBadgeDrawable(
            badge,
            requireActivity().findViewById(R.id.toolbar),
            R.id.notificationsFragment
        )
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}