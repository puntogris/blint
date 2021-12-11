package com.puntogris.blint.feature_store.presentation.store

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragmentOptions
import com.puntogris.blint.common.utils.UiInterface
import com.puntogris.blint.databinding.FragmentStoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : BaseFragmentOptions<FragmentStoreBinding>(R.layout.fragment_store) {

    private val args: StoreFragmentArgs by navArgs()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.business = args.store
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.businessFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteBusiness -> {
                val action =
                    StoreFragmentDirections.actionStoreFragmentToDeleteStoreFragment(args.store)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}