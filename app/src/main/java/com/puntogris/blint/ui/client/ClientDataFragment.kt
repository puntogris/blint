package com.puntogris.blint.ui.client

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientDataBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDataFragment : BaseFragment<FragmentClientDataBinding>(R.layout.fragment_client_data) {

    private val viewModel:ClientViewModel by viewModels()

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        println("asdasd")

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getParcelable<Client>("client_key")?.let {
                println(it)
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