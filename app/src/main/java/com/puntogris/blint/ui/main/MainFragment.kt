package com.puntogris.blint.ui.main

import android.widget.Button
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.microsoft.schemas.vml.STTrueFalse
import com.puntogris.blint.R
import com.puntogris.blint.data.local.dao.ProductsDao
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.time.ExperimentalTime

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var productsDao: ProductsDao

    @ExperimentalTime
    override fun initializeViews() {
        setUpUi(showFab = false)
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupBadgeListener()
        setupCalendarRecyclerView()


        //view?.doOnPreDraw { startPostponedEnterTransition() }

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
        findNavController().navigate(menuCard.navigationId)
    }

    fun onAddEventClicked(){
        findNavController().navigate(R.id.createEventFragment)
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        binding.calendarRecyclerView.adapter = null
        super.onDestroyView()
    }
}