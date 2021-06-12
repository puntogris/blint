package com.puntogris.blint.ui.record

import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentPublishOrderBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.getParentFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PublishOrderFragment : BaseFragment<FragmentPublishOrderBinding>(R.layout.fragment_publish_order) {

    private val viewModel: NewOrderViewModel by navGraphViewModels(R.id.newOrderGraphNav) { defaultViewModelProviderFactory }

    override fun initializeViews() {
        getParentFab().hide()

        //guardar items en base de datos con un id que los una
        // y guardar el padre en otra base de datos asi a parece en las ordenes generales
        lifecycleScope.launchWhenStarted {

            viewModel.saveOrderIntoDatabase()
        }
    }
}