package com.puntogris.blint.ui.client

import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentClientDataBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.showLongSnackBarAboveFab
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClientDataFragment : BaseFragment<FragmentClientDataBinding>(R.layout.fragment_client_data) {

    private val viewModel: ClientViewModel by viewModels()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        arguments?.takeIf { it.containsKey("client_key") }?.apply {
            getParcelable<Client>("client_key")?.let {
                lifecycleScope.launchWhenStarted {
                    viewModel.setClientData(it)
                }
            }
        }
        setupContactPermissions()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){}
    }

    private fun setupContactPermissions(){
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_INSERT).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                        putExtra(ContactsContract.Intents.Insert.NAME, viewModel.currentClient.value!!.name)
                        putExtra(ContactsContract.Intents.Insert.PHONE, viewModel.currentClient.value!!.phone)
                        putExtra(ContactsContract.Intents.Insert.EMAIL, viewModel.currentClient.value!!.email)
                        putExtra(ContactsContract.Intents.Insert.POSTAL, viewModel.currentClient.value!!.address)
                    }
                    activityResultLauncher.launch(intent)
                }
                else showLongSnackBarAboveFab(getString(R.string.snack_require_contact_permission))
            }
    }

    fun onAddContactButtonClicked(){
        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_CONTACTS)
    }

    fun onPhoneButtonClicked(){
        OptionsSheet().build(requireContext()) {
            displayMode(DisplayMode.LIST)
            with(
                Option(R.drawable.ic_baseline_call_24, this@ClientDataFragment.getString(R.string.action_call)),
                Option(R.drawable.ic_baseline_message_24, this@ClientDataFragment.getString(R.string.action_message)),
                Option(R.drawable.ic_whatsapp, this@ClientDataFragment.getString(R.string.action_whats_app))
            )
            onPositive { index: Int, _: Option ->
                val phone = viewModel.currentClient.value!!.phone
                when(index){
                    0 -> {
                        val uri = Uri.fromParts("tel", phone, null)
                        val intent = Intent(Intent.ACTION_DIAL, uri)
                        activityResultLauncher.launch(intent)
                    }
                    1 -> {
                        val uri = Uri.parse("smsto:$phone")
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        activityResultLauncher.launch(intent)
                    }
                    2 -> {
                        val uri = Uri.parse("smsto:$phone")
                        val intent = Intent(Intent.ACTION_SENDTO, uri)
                        intent.setPackage("com.whatsapp")
                        activityResultLauncher.launch(intent)
                    }
                }
            }
        }.show(parentFragmentManager, "")
    }

    fun onEmailButtonClicked(){
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:${viewModel.currentClient.value!!.email}")
        }
        activityResultLauncher.launch(intent)
    }
}