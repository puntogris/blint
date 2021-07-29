package com.puntogris.blint.ui.login

import android.content.Intent
import android.net.Uri
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentLoginProblemsBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.FIX_GOOGLE_PLAY_SERVICES_URL
import com.puntogris.blint.utils.Constants.SUPPORT_EMAIL
import com.puntogris.blint.utils.launchWebBrowserIntent

class LoginProblemsFragment : BaseFragment<FragmentLoginProblemsBinding>(R.layout.fragment_login_problems) {

    override fun initializeViews() {
        binding.fragment = this
    }

    fun onGoBackButtonClicked(){
        findNavController().navigateUp()
    }

    fun onSendEmailClicked(){
        val email = Intent(Intent.ACTION_SENDTO)
        email.data = Uri.parse("mailto:$SUPPORT_EMAIL")
        email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.problem_with_blint_account))
        startActivity(email)
    }

    fun onEnableAccountClicked(){
        launchWebBrowserIntent(FIX_GOOGLE_PLAY_SERVICES_URL)
    }
}