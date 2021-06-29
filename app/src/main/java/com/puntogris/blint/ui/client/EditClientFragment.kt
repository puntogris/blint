package com.puntogris.blint.ui.client

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentEditClientBinding
import com.puntogris.blint.model.Client
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class EditClientFragment : BaseFragment<FragmentEditClientBinding>(R.layout.fragment_edit_client) {

    private val viewModel:ClientViewModel by viewModels()
    private val args:EditClientFragmentArgs by navArgs()
    lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupContactPermissions()

        lifecycleScope.launch {
            args.client?.let {
                viewModel.setClientData(it)
            }
        }

        getParentFab().let { fab ->
            fab.changeIconFromDrawable(R.drawable.ic_baseline_save_24)
            fab.setOnClickListener {
                viewModel.updateClientData(getClientFromViews())
                when(val validator = StringValidator.from(viewModel.currentClient.value!!.name, allowSpecialChars = true)){
                    is StringValidator.Valid -> {
                        lifecycleScope.launch {
                            when(viewModel.saveClientDatabase()){
                                SimpleResult.Failure ->
                                    createShortSnackBar("Ocurrio un error al guardar el cliente.").setAnchorView(fab).show()
                                SimpleResult.Success -> {
                                    createShortSnackBar("Se guardo el cliente satisfactoriamente.").setAnchorView(fab).show()
                                    findNavController().navigateUp()
                                }
                            }
                        }
                    }
                    is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(fab).show()
                }
            }
        }
    }

    private fun setupContactPermissions(){
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = ContactsContract.Contacts.CONTENT_TYPE
                    }
                    intent.type = ContactsContract.Contacts.CONTENT_TYPE

                    if (intent.resolveActivity(requireActivity().packageManager) != null) {
                        startActivityForResult(intent, 1)
                    }
                }
                else showLongSnackBarAboveFab("Necesitamos acceso a tu agenda para ver tus contactos.")
            }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.let {
                val contactUri = it.data!!
                val resolver = requireActivity().contentResolver
                val cursorLookUpKey = resolver.query(contactUri, arrayOf(ContactsContract.Data.LOOKUP_KEY), null, null, null)

                cursorLookUpKey?.let { cursor ->
                    if (cursor.moveToFirst()){
                        val lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))
                        if (lookUpKey != null){
                            getEmailWithLookUpKey(lookUpKey)
                            getPhoneAndNameWithLookUpKey(lookUpKey)
                            loadAdressWithLookUpKey(lookUpKey)
                        }
                    }
                }


                cursorLookUpKey?.close()
            }
        }
    }

    private fun getEmailWithLookUpKey(key: String){
        val emailWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val emailWhereParams = arrayOf(
            key,
            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
        )
        val emailCursor = requireActivity().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            emailWhere,
            emailWhereParams,
            null
        )
        if (emailCursor!!.moveToNext()) {
            val emailId = emailCursor.getString(
                emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)
            )

            if (!emailId.isNullOrEmpty()) binding.clientInformationLayout.clientEmailText.setText(emailId)
        }
        emailCursor.close()

    }

    private fun loadAdressWithLookUpKey(key: String){
        val addrWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val addrWhereParams =
            arrayOf(key, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
        val addrCursor = requireActivity().contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            addrWhere,
            addrWhereParams,
            null
        )
        if (addrCursor!!.moveToNext()) {
            val formattedAddress: String =
                addrCursor.getString(addrCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS))
            if (formattedAddress.isNotEmpty()) binding.clientInformationLayout.clientAddressText.setText(formattedAddress)

        }
        addrCursor.close()
    }

    private fun getPhoneAndNameWithLookUpKey(key: String){
        val contentResolver: ContentResolver = requireActivity().contentResolver
        val contactWhere =
            ContactsContract.Data.LOOKUP_KEY + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"
        val contactWhereParams =
            arrayOf(key, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        val cursorPhone = contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            null,
            contactWhere,
            contactWhereParams,
            null
        )
        if (cursorPhone != null && cursorPhone.count > 0) {
            if (cursorPhone.moveToNext()) {
                if (cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        .toInt() > 0
                ) {
                    val givenName =
                        cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNo =
                        cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    if (!givenName.isNullOrEmpty()) binding.clientInformationLayout.clientNameText.setText(givenName)
                    if (!phoneNo.isNullOrEmpty()) binding.clientInformationLayout.clientPhoneNumberText.setText(phoneNo)

                }
            }
        }

        cursorPhone?.close()

    }

    fun onAddClientFromContactsClicked(){
        requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
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

}