package com.puntogris.blint.ui.login

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLoginProblemsBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.FIX_GOOGLE_PLAY_SERVICES_URL
import com.puntogris.blint.utils.Constants.SUPPORT_EMAIL

class LoginProblemsFragment : BaseFragment<FragmentLoginProblemsBinding>(R.layout.fragment_login_problems) {

    override fun initializeViews() {
        binding.goBackButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginProblemsFragment_to_loginFragment)
        }
        binding.enableAccountLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(FIX_GOOGLE_PLAY_SERVICES_URL)
            startActivity(intent)
        }
        binding.sendEmail.setOnClickListener {
            val email = Intent(Intent.ACTION_SENDTO)
            email.data = Uri.parse("mailto:$SUPPORT_EMAIL")
            email.putExtra(Intent.EXTRA_SUBJECT, "Problema con mi cuenta de PetMate")
            startActivity(email)
        }
    }
}