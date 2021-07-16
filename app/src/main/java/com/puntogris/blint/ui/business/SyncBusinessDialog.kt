package com.puntogris.blint.ui.business

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.puntogris.blint.R
import com.puntogris.blint.utils.SyncAccount
import com.puntogris.blint.utils.showSnackBarVisibilityAppBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SyncBusinessDialog: DialogFragment() {

    private val viewModel:BusinessViewModel by viewModels()
    private val job = Job()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        lifecycleScope.launch(job) {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                when(viewModel.syncAccount()){
                    is SyncAccount.Error -> {
                        dismiss()
                        showSnackBarVisibilityAppBar(getString(R.string.snack_sync_account_error))
                    }
                    SyncAccount.Success.BusinessNotFound -> {
                        findNavController().navigate(R.id.newUserFragment)
                        showSnackBarVisibilityAppBar(getString(R.string.snack_no_businesses_found))
                    }
                    SyncAccount.Success.HasBusiness -> {
                        findNavController().navigate(R.id.mainFragment)
                        showSnackBarVisibilityAppBar(getString(R.string.snack_sync_account_success))
                    }
                }
            }
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            builder.setView(inflater.inflate(R.layout.sync_business_dialog, null))
                .setTitle(getString(R.string.syncing_businesses))
                .setMessage(getString(R.string.sync_cancel_warning))
                .setNegativeButton(getString(R.string.action_close)) { dialog, id ->
                    job.cancel()
                    getDialog()?.cancel()
                    showSnackBarVisibilityAppBar(getString(R.string.snack_sync_account_cancelled))
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        job.cancel()
        super.onDismiss(dialog)
    }
}
