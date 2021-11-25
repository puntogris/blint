package com.puntogris.blint.ui.business

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentBusinessBinding
import com.puntogris.blint.ui.base.BaseFragmentOptions
import com.puntogris.blint.utils.UiInterface
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BusinessFragment : BaseFragmentOptions<FragmentBusinessBinding>(R.layout.fragment_business) {

    private val args: BusinessFragmentArgs by navArgs()

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.business = args.business
    }

    override fun setUpMenuOptions(menu: Menu) {
        menu.findItem(R.id.businessFragmentMenu).isVisible = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.deleteBusiness -> {
                val action =
                    BusinessFragmentDirections.actionBusinessFragmentToDeleteBusinessFragment(args.business)
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}