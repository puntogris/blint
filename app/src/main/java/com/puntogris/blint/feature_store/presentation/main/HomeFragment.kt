package com.puntogris.blint.feature_store.presentation.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.db.williamchart.view.DonutChartView
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.capitalizeFirstChar
import com.puntogris.blint.common.utils.launchAndRepeatWithViewLifecycle
import com.puntogris.blint.common.utils.setCompareTrafficRevenue
import com.puntogris.blint.common.utils.setProfileImage
import com.puntogris.blint.common.utils.setTrafficDonutChart
import com.puntogris.blint.common.utils.setTrafficRevenue
import com.puntogris.blint.common.utils.setTrafficRevenuePercentage
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentHomeBinding
import com.puntogris.blint.feature_store.domain.model.MenuCard
import com.rubensousa.decorator.GridSpanMarginDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: MainViewModel by activityViewModels()

    private val binding by viewBinding(FragmentHomeBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            it?.findViewById<DonutChartView>(R.id.donutChart_store_revenue)?.setTrafficDonutChart(
                emptyList()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupMenuRecyclerView()
    }

    private fun setupObservers() {
        launchAndRepeatWithViewLifecycle {
            viewModel.user.collectLatest {
                binding.textViewUserName.text = it.name.capitalizeFirstChar()
                binding.imageViewUserPhoto.setProfileImage(it.photoUrl)
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.lastTraffic.collectLatest {
                binding.textViewStoreDailyRevenue.setTrafficRevenue(it)
                binding.textViewStoreRevenueDifference.setCompareTrafficRevenue(it)
                binding.donutChartStoreRevenue.setTrafficDonutChart(it)
                binding.textViewStoreRevenuePercentage.setTrafficRevenuePercentage(it)
            }
        }
        launchAndRepeatWithViewLifecycle {
            viewModel.currentBusiness.collectLatest {
                binding.textViewStoreName.text = it.name
            }
        }
    }

    private fun setupMenuRecyclerView() {
        val menuAdapter = MainMenuAdapter { onMenuCardClicked(it) }
        val manager = GridLayoutManager(requireContext(), 2)

        with(binding.homeFragmentMenuRecyclerView) {
            addItemDecoration(GridSpanMarginDecoration(60, 60, manager))
            adapter = menuAdapter
            layoutManager = manager
        }
    }

    private fun onMenuCardClicked(menuCard: MenuCard) {
        findNavController().navigate(menuCard.navigationId)
    }

    override fun onDestroyView() {
        binding.homeFragmentMenuRecyclerView.adapter = null
        super.onDestroyView()
    }
}
