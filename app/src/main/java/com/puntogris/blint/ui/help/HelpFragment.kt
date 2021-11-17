package com.puntogris.blint.ui.help

import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentHelpBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.UiInterface

class HelpFragment : BaseFragment<FragmentHelpBinding>(R.layout.fragment_help) {

    override fun initializeViews() {
        UiInterface.registerUi()
        binding.fragment = this
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HelpAdapter()
        }
    }

    fun onCreateTicketClicked() {
        findNavController().navigate(R.id.sendTicketFragment)
    }
}