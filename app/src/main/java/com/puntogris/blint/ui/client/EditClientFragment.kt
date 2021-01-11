package com.puntogris.blint.ui.client

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditClientBinding
import com.puntogris.blint.databinding.FragmentEditProductBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditClientFragment : BaseFragment<FragmentEditClientBinding>(R.layout.fragment_edit_client) {
    private val viewModel:ClientViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getParcelable<Client>("client_key")?.let {
                viewModel.setCurrentClientData(it)
            }
        }
        getParentFab().apply {
            changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            setOnClickListener {
                viewModel.updateCurrentClientData(getClientFromViews())
                when(val validator = StringValidator.from(viewModel.currentClient.value!!.name, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        viewModel.saveClientToDatabase()
                        createShortSnackBar("Se guardo el cliente satisfactoriamente.").setAnchorView(this).show()
                        findNavController().navigateUp()
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(this).show()
                }
            }
        }
    }

    private fun getClientFromViews(): Client {
        return Client(
            name = binding.clientInformationLayout.clientNameText.getString(),
            address = binding.clientInformationLayout.clientAddressText.getString(),
            email = binding.clientInformationLayout.clientEmailText.getString(),
            phone = binding.clientInformationLayout.clientPhoneNumberText.getString(),
            paymentInfo = binding.clientExtrasLayout.clientPaymentInfoText.getString(),
            discount = binding.clientExtrasLayout.clientDiscountText.getFloat()
        )
    }

    fun onClientInformationClicked(){
        binding.clientInformationLayout.apply {
            expandableLayout.toggle()
            clientInformationButton.toggleIcon()
        }
        hideKeyboard()
    }

    fun onClientExtraClicked(){
        binding.clientExtrasLayout.apply {
            expandableLayout.toggle()
            clientExtraButton.toggleIcon()
        }
        hideKeyboard()
    }

}