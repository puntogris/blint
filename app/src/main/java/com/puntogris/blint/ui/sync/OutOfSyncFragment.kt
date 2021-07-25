package com.puntogris.blint.ui.sync

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOutOfSyncBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.registerUiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutOfSyncFragment : BaseFragment<FragmentOutOfSyncBinding>(R.layout.fragment_out_of_sync) {

    override fun initializeViews() {
        registerUiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_sync_24, showAppBar = false){
            findNavController().navigate(R.id.syncAccountFragment)
        }
    }
}