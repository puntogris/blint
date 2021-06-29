package com.puntogris.blint.ui.main

import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.activityViewModels
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
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ACCOUNT_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.DEB_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
import com.puntogris.blint.utils.Constants.TOOLS_CARD_CODE
import com.puntogris.blint.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime


@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var badge: BadgeDrawable

    @ExperimentalTime
    override fun initializeViews() {
        binding.fragment = this
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupCalendarRecyclerView()
        setupBadgeListener()


      //  val appID = ApplicationID("T9JVC6GCMX")
       // val apiKey = APIKey("5a99602f5b7c591435cdcb99a696ced4")

        //val client = ClientSearch(appID, apiKey)

      //  val cli = client.initIndex(IndexName("clients"))
     //   val contact = Contact("Jimmie", "Barninger", "New York", ObjectID("myID"))
//
     //   val test = Json.encodeToJsonElement(contact).jsonObject

      //  lifecycleScope.launch {
          //  Test(objectID = ObjectID("")).se
           // cli.saveObject(test)
//           val time = measureTime {
//               val data = cli.getObject(ObjectID("myID"))
//               val obj = Json.decodeFromJsonElement<Contact>(data)
//               println(obj)
//           }
//            println(time)
//        lifecycleScope.launch {
//            val time = measureTime {
//                val deoc = Firebase.firestore.collection("test")
//                   // .whereEqualTo("firstname","firstname")
//                    .whereArrayContains("search", "cola")
//                    .get().await()
//
//                val asd = deoc.map {
//                    Contact(
//                        it.get("name").toString(),
//                        it.get("firstname").toString(),
//                        it.get("lastname").toString()
//                        )
//                }
//                println(asd)
//            }
//            println(time)
//        }
//
//        val text = "coca cola"
//        val list = mutableListOf<String>()
//
//        text.forEachIndexed { index, c ->
//            if (c != ' '){
//                for (i in 1..text.length - index){
//                    list.add(text.substring(index, index + i))
//                }
//            }
//        }
//
//        val test = list.sumBy {
//            it.length
//        }
//        println(test)


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
        lifecycleScope.launch(Dispatchers.IO) {
            val events = viewModel.getLastEvents()
            if (events.isEmpty()){
                binding.button17.visible()
                binding.textView151.visible()
            }else{
                mainCalendarAdapter.submitList(events)
                binding.materialCardView2.visible()
            }
        }

        binding.calendarRecyclerView.apply {
            adapter = mainCalendarAdapter
            layoutManager = LinearLayoutManager(requireContext())
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
         //   addItemDecoration(SpaceItemDecoration(10, true))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        when(menuCard.code){
            ALL_PRODUCTS_CARD_CODE -> R.id.manageProductsFragment
            ALL_CLIENTS_CARD_CODE -> R.id.manageClientsFragment
            ALL_SUPPLIERS_CARD_CODE -> R.id.manageSuppliersFragment
            ACCOUNTING_CARD_CODE -> R.id.calendarFragment
            RECORDS_CARD_CODE -> R.id.manageRecordsFragment
            TOOLS_CARD_CODE -> R.id.operationsFragment
            CHARTS_CARD_CODE -> R.id.reportsFragment
            DEB_CARD_CODE -> R.id.manageDebtFragment
            ACCOUNT_CARD_CODE -> R.id.accountPreferences
            else -> R.id.mainFragment
        }.apply { findNavController().navigate(this) }
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