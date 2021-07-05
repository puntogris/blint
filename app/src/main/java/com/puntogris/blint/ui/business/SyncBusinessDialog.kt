package com.puntogris.blint.ui.business

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.puntogris.blint.R
import com.puntogris.blint.utils.showSnackBarVisibilityAppBar

class SyncBusinessDialog: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)

            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.sync_business_dialog, null))
                // Add action buttons
                .setTitle("Sincronizando negocios")
                .setMessage("Cerrar esta ventana cancelara la sincronizacion.")
//                .setPositiveButton(R.string.signin,
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // sign in the user ...
//                    })
                .setNegativeButton("Cerrar") { dialog, id ->
                    getDialog()?.cancel()
                    showSnackBarVisibilityAppBar("Sincronizacion cancelada")
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) {
        showSnackBarVisibilityAppBar("Sincronizacion cancelada")
        super.onDismiss(dialog)
    }
}
