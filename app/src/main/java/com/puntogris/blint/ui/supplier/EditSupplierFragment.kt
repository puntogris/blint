package com.puntogris.blint.ui.supplier

import android.Manifest
import android.app.Activity
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
import com.puntogris.blint.databinding.FragmentEditSupplierBinding
import com.puntogris.blint.model.Supplier
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditSupplierFragment : BaseFragment<FragmentEditSupplierBinding>(R.layout.fragment_edit_supplier) {

    private val viewModel: SupplierViewModel by viewModels()
    private val args: EditSupplierFragmentArgs by navArgs()
    lateinit var requestPermissionCompanyContact: ActivityResultLauncher<String>
    lateinit var requestPermissionSellerContact: ActivityResultLauncher<String>

    override fun initializeViews() {
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpUi(showFab = true, fabIcon = R.drawable.ic_baseline_save_24){
            viewModel.updateSupplierData(getSupplierFromViews())
            when(val validator = StringValidator.from(viewModel.currentSupplier.value!!.companyName, allowSpecialChars = true)){
                is StringValidator.Valid -> {
                    lifecycleScope.launch {
                        when(viewModel.saveSupplierDatabase()){
                            SimpleResult.Failure ->
                                createShortSnackBar(getString(R.string.snack_save_supplier_error)).setAnchorView(it).show()
                            SimpleResult.Success -> {
                                createShortSnackBar(getString(R.string.snack_save_supplier_succes)).setAnchorView(it).show()
                                findNavController().navigateUp()
                            }
                        }
                    }
                }
                is StringValidator.NotValid -> createShortSnackBar(validator.error).setAnchorView(it).show()
            }
        }

        lifecycleScope.launchWhenStarted {
            args.supplier?.let {
                viewModel.setSupplierData(it)
            }
        }

        requestPermissionCompanyContact = getPermissionLauncher(1)
        requestPermissionSellerContact = getPermissionLauncher(2)

    }

    fun onCompanyAddContactInfoClicked(){
        requestPermissionCompanyContact.launch(Manifest.permission.READ_CONTACTS)
    }

    fun onSellerAddContactInfoClicked(){
        requestPermissionSellerContact.launch(Manifest.permission.READ_CONTACTS)
    }

    private fun getPermissionLauncher(requestCode: Int) =
        registerForActivityResult(ActivityResultContracts.RequestPermission())
        { isGranted: Boolean ->
            if (isGranted) {
                val intent = Intent(Intent.ACTION_PICK).apply {
                    type = ContactsContract.Contacts.CONTENT_TYPE
                }
                intent.type = ContactsContract.Contacts.CONTENT_TYPE

                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(intent, requestCode)
                }
            }
            else showLongSnackBarAboveFab(getString(R.string.snack_require_contact_permission))
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 || requestCode == 2 && resultCode == Activity.RESULT_OK) {
            data?.let {
                val contactUri = it.data!!
                val resolver = requireActivity().contentResolver
                val cursorLookUpKey = resolver.query(contactUri, arrayOf(ContactsContract.Data.LOOKUP_KEY), null, null, null)

                cursorLookUpKey?.let { cursor ->
                    if (cursor.moveToFirst()){
                        val lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.LOOKUP_KEY))
                        if (lookUpKey != null){
                            getEmailWithLookUpKey(lookUpKey, requestCode)
                            getPhoneAndNameWithLookUpKey(lookUpKey, requestCode)
                            if (requestCode == 1) loadAdressWithLookUpKey(lookUpKey)
                        }
                    }
                }
                cursorLookUpKey?.close()
            }
        }
    }

    private fun getEmailWithLookUpKey(key: String, requestCode: Int){
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
            if (requestCode == 1){
                if (!emailId.isNullOrEmpty()) binding.companyLayout.supplierCompanyEmailText.setText(emailId)
            }else{
                if (!emailId.isNullOrEmpty()) binding.sellerLayout.supplierSellerEmailText.setText(emailId)
            }
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
            if (formattedAddress.isNotEmpty()) binding.companyLayout.supplierCompanyAddressText.setText(formattedAddress)

        }
        addrCursor.close()
    }

    private fun getPhoneAndNameWithLookUpKey(key: String, requestCode: Int){
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


                    if (requestCode == 1){
                        if (!givenName.isNullOrEmpty()) binding.companyLayout.supplierCompanyNameText.setText(givenName)
                        if (!phoneNo.isNullOrEmpty()) binding.companyLayout.supplierCompanyPhoneText.setText(phoneNo)
                    }else{
                        if (!givenName.isNullOrEmpty()) binding.sellerLayout.supplierSellerNameText.setText(givenName)
                        if (!phoneNo.isNullOrEmpty()) binding.sellerLayout.supplierSellerPhoneText.setText(phoneNo)
                    }
                }
            }
        }

        cursorPhone?.close()
    }

    private fun getSupplierFromViews(): Supplier {
        return Supplier(
            companyName = binding.companyLayout.supplierCompanyNameText.getString(),
            companyPhone = binding.companyLayout.supplierCompanyPhoneText.getString(),
            address = binding.companyLayout.supplierCompanyAddressText.getString(),
            companyEmail = binding.companyLayout.supplierCompanyEmailText.getString(),
            companyPaymentInfo = binding.supplierExtrasLayout.supplierPaymentInfoText.getString(),
            sellerEmail = binding.sellerLayout.supplierSellerEmailText.getString(),
            sellerName = binding.sellerLayout.supplierSellerNameText.getString(),
            sellerPhone = binding.sellerLayout.supplierSellerPhoneText.getString(),
            notes = binding.supplierExtrasLayout.supplierNotesText.getString()
        )
    }
}