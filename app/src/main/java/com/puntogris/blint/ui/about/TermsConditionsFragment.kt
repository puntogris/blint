package com.puntogris.blint.ui.about

import android.webkit.WebView
import android.webkit.WebViewClient
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentTermsConditionsBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants.TERMS_AND_CONDITIONS_URI
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible

class TermsConditionsFragment : BaseFragment<FragmentTermsConditionsBinding>(R.layout.fragment_terms_conditions) {

    override fun initializeViews() {
        binding.apply {

            webView.webViewClient = object : WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.gone()
                    webView.visible()
                }
            }
            webView.loadUrl(TERMS_AND_CONDITIONS_URI)
        }
    }
}