package com.puntogris.blint.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentWebPageBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.Constants
import com.puntogris.blint.utils.gone
import com.puntogris.blint.utils.visible

class WebPageFragment: BaseFragment<FragmentWebPageBinding>(R.layout.fragment_web_page) {

    private val args: WebPageFragmentArgs by navArgs()

    override fun initializeViews() {
        binding.apply {

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.gone()
                    webView.visible()
                }
            }
            webView.loadUrl(args.uri)
        }
    }
}

