package com.puntogris.blint.ui.about

import android.webkit.WebView
import android.webkit.WebViewClient
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentPrivacyPolicyBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.PRIVACY_POLICY_URI
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible

class PrivacyPolicyFragment: BaseFragment<FragmentPrivacyPolicyBinding>(R.layout.fragment_privacy_policy) {

    override fun initializeViews() {
        binding.apply {

            webView.webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.gone()
                    webView.visible()
                }
            }
            webView.loadUrl(PRIVACY_POLICY_URI)
        }
    }
}