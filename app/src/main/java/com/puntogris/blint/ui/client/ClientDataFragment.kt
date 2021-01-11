package com.puntogris.blint.ui.client

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientDataBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDataFragment : BaseFragment<FragmentClientDataBinding>(R.layout.fragment_client_data) {

    private val viewModel: ClientViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getInt("client_key").let {
                lifecycleScope.launchWhenStarted {
                    val client = viewModel.getClient(it)
                    viewModel.setClientData(client)
                }
            }
        }
    }
}