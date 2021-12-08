package com.puntogris.blint.feature_store.presentation.main

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.puntogris.blint.databinding.MainBottomNavDrawerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainBottomNavDrawer : BottomSheetDialogFragment() {

    private lateinit var binding: MainBottomNavDrawerBinding
    private val viewModel: MainViewModel by activityViewModels()

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.drawerNavigationView.setupWithNavController(findNavController())

    }

    companion object {
        val TAG: String = MainBottomNavDrawer::class.java.simpleName
        fun newInstance() = MainBottomNavDrawer()
    }
}