package com.puntogris.blint.feature_store.presentation.store

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.registerToolbarBackButton
import com.puntogris.blint.common.utils.viewBinding
import com.puntogris.blint.databinding.FragmentStoreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment(R.layout.fragment_store) {

    private val args: StoreFragmentArgs by navArgs()

    private val binding by viewBinding(FragmentStoreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.business = args.store
        setupToolbar()
    }

    private fun setupToolbar() {
        with(binding.storeToolbar) {
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
