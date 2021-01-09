package com.puntogris.blint.ui.about

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentAboutBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.APP_PLAY_STORE_URI
import com.puntogris.blint.utils.Constants.PLAY_STORE_PACKAGE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(R.layout.fragment_about) {

    override fun initializeViews() {

        binding.termsConditions.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_termsConditionsFragment)
        }
        binding.privacyPolicy.setOnClickListener {
            findNavController().navigate(R.id.action_aboutFragment_to_privacyPolicyFragment)
        }

        binding.rateAppButton.setOnClickListener {
               val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(APP_PLAY_STORE_URI)
                setPackage(PLAY_STORE_PACKAGE)
            }
            startActivity(intent)
        }
        binding.sendSuggestion.setOnClickListener {
            //bottom sheet
        }
    }
}