package com.puntogris.blint.ui.main

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentMainBinding
import com.puntogris.blint.model.Event
import com.puntogris.blint.model.MenuCard
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.Constants.ACCOUNTING_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_CLIENTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_PRODUCTS_CARD_CODE
import com.puntogris.blint.utils.Constants.ALL_SUPPLIERS_CARD_CODE
import com.puntogris.blint.utils.Constants.CHARTS_CARD_CODE
import com.puntogris.blint.utils.Constants.OPERATIONS_CARD_CODE
import com.puntogris.blint.utils.Constants.RECORDS_CARD_CODE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import me.farahani.spaceitemdecoration.SpaceItemDecoration

@AndroidEntryPoint
class MainFragment : BaseFragmentOptions<FragmentMainBinding>(R.layout.fragment_main) {

    private lateinit var mainMenuAdapter: MainMenuAdapter
    private lateinit var mainCalendarAdapter: MainCalendarAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var badge: BadgeDrawable

    private lateinit var billingClient: BillingClient

    override fun initializeViews() {
        binding.lifecycleOwner= this
        binding.viewModel = viewModel
        setupMenuRecyclerView()
        setupCalendarRecyclerView()

//        val purchasesUpdateListener =
//            PurchasesUpdatedListener { billingResult, purchases ->
//                // To be implemented in a later section.
//            }
//
//        billingClient = BillingClient.newBuilder(requireActivity())
//            .setListener(purchasesUpdateListener)
//            .enablePendingPurchases()
//            .build()
//
//        billingClient.startConnection(object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
//                // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
//                    val params = SkuDetailsParams.newBuilder()
//                    params.setSkusList(listOf("online_business_tier_1"))
//                    params.setType(BillingClient.SkuType.SUBS)
//                    val test = params.build()
//                    billingClient.querySkuDetailsAsync(test, object :SkuDetailsResponseListener{
//                        override fun onSkuDetailsResponse(
//                            p0: BillingResult,
//                            p1: MutableList<SkuDetails>?
//                        ) {
//
//                            val flowParams = p1?.get(0)?.let {
//                                BillingFlowParams.newBuilder()
//                                    .setSkuDetails(it)
//                                    .build()
//                            }
//                            val responseCode = flowParams?.let {
//                                billingClient.launchBillingFlow(requireActivity(),
//                                    it
//                                ).responseCode
//                            }
//                        }
//                    })
//
//                }
//            }
//            override fun onBillingServiceDisconnected() {
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//            }
//        })
    }

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onPrepareOptionsMenu(menu: Menu) {
        badge = BadgeDrawable.create(requireContext())
        badge.number = 4
        BadgeUtils.attachBadgeDrawable(badge, requireActivity().findViewById(R.id.toolbar), R.id.notificationsFragment)
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setupCalendarRecyclerView(){
        mainCalendarAdapter = MainCalendarAdapter { onCalendarEventClicked(it) }
        lifecycleScope.launch(Dispatchers.IO) {
            val events = viewModel.getLastEvents()
            mainCalendarAdapter.submitList(events)
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
            addItemDecoration(SpaceItemDecoration(10, true))
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard){
        when(menuCard.code){
            ALL_PRODUCTS_CARD_CODE -> findNavController().navigate(R.id.manageProductsFragment)
            ALL_CLIENTS_CARD_CODE -> findNavController().navigate(R.id.manageClientsFragment)
            ALL_SUPPLIERS_CARD_CODE -> findNavController().navigate(R.id.manageSuppliersFragment)
            ACCOUNTING_CARD_CODE -> findNavController().navigate(R.id.calendarFragment)
            RECORDS_CARD_CODE -> findNavController().navigate(R.id.manageRecordsFragment)
            OPERATIONS_CARD_CODE -> findNavController().navigate(R.id.operationsFragment)
            CHARTS_CARD_CODE -> findNavController().navigate(R.id.reportsFragment)
        }
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

    @com.google.android.material.badge.ExperimentalBadgeUtils
    override fun onDestroyView() {
        BadgeUtils.detachBadgeDrawable(badge, requireActivity().findViewById(R.id.toolbar), R.id.notificationsFragment)
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}