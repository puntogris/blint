package com.puntogris.blint.ui.sync

import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOutOfSyncBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.SYNC_INFO_WEBSITE_LEARN_MORE
import com.puntogris.blint.utils.launchWebBrowserIntent
import com.puntogris.blint.utils.registerUiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OutOfSyncFragment : BaseFragment<FragmentOutOfSyncBinding>(R.layout.fragment_out_of_sync) {

    override fun initializeViews() {
        binding.fragment = this
        registerUiInterface.register(showFab = true, fabIcon = R.drawable.ic_baseline_sync_24, showAppBar = false){
            findNavController().navigate(R.id.syncAccountFragment)
        }
    }
    fun onReadMoreAboutSyncClicked(){
        launchWebBrowserIntent(SYNC_INFO_WEBSITE_LEARN_MORE)
    }
}