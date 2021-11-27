package com.puntogris.blint.feature_store.presentation.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.R
import com.puntogris.blint.databinding.MainBottomNavDrawerBinding

class MainBottomNavDrawer : BottomSheetDialogFragment() {

    private lateinit var binding: MainBottomNavDrawerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also { dialog ->
            dialog.setOnShowListener {
                val bottomSheet = dialog.findViewById<FrameLayout>(
                    com.google.android.material.R.id.design_bottom_sheet
                )
                BottomSheetBehavior.from(bottomSheet).apply {
                    skipCollapsed = true
                    state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainBottomNavDrawerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.drawerNavigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> R.id.mainFragment
                R.id.action_products -> R.id.manageProductsFragment
                R.id.action_suppliers -> R.id.manageSuppliersFragment
                R.id.action_clients -> R.id.manageClientsFragment
                R.id.action_orders -> R.id.manageOrdersFragment
                R.id.action_settings -> R.id.preferencesFragment
                else -> null
            }?.let { action -> findNavController().navigate(action) }

            dismiss()
            true
        }
    }

    companion object {
        val TAG: String = MainBottomNavDrawer::class.java.simpleName
        fun newInstance() = MainBottomNavDrawer()
    }
}