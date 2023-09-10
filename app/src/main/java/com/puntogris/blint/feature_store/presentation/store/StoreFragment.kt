package com.puntogris.blint.feature_store.presentation.store

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.presentation.base.BaseFragment
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.databinding.FragmentStoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : BaseFragment<FragmentStoreBinding>(R.layout.fragment_store) {

    private val args: StoreFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.business = args.store
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.storeToolbar.apply {
            registerToolbarBackButton(this)

            setOnMenuItemClickListener {
                if (it.itemId == R.id.action_delete_store) {
                    val action =
                        StoreFragmentDirections.actionStoreFragmentToDeleteStoreFragment(args.store)
                    findNavController().navigate(action)
                }
                true
            }
        }
    }
}
