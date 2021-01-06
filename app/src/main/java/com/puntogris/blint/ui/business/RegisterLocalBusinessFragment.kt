package com.puntogris.blint.ui.business

import androidx.core.view.setPadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentRegisterLocalBusinessBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.ui.main.MainViewModel
import com.puntogris.blint.utils.*
import kotlinx.coroutines.launch

class RegisterLocalBusinessFragment : BaseFragment<FragmentRegisterLocalBusinessBinding>(R.layout.fragment_register_local_business) {

    private val viewModel: MainViewModel by activityViewModels()

        override fun initializeViews() {
            binding.animationView.setPadding(-700)
            getParentFab().apply {
                isEnabled = true
                changeIconFromDrawable(R.drawable.ic_baseline_save_24)
                setOnClickListener {

                }
            }
        }
    }