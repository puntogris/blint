package com.puntogris.blint.ui.reports

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.net.toFile
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentGenerateReportBinding
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.ReportType
import com.puntogris.blint.utils.Util.copyFile
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class GenerateReportFragment : BaseFragment<FragmentGenerateReportBinding>(R.layout.fragment_generate_report) {

    private val viewModel: ReportsViewModel by viewModels()
    private val args: GenerateReportFragmentArgs by navArgs()

    override fun initializeViews() {
        lifecycleScope.launchWhenStarted {
            val report = viewModel.getReportData(
                args.reportCode,
                args.timeCode,
                args.startTime,
                args.endTime
            )
            when(report){
                ReportType.SuppliersList -> createIntent(101, "suppliers")
                ReportType.ClientsList -> createIntent(102, "clients")
                ReportType.ProductList -> createIntent(103, "products")
                is ReportType.ProductRecords -> {

                }
                is ReportType.SuppliersRecords -> {
                }
                is ReportType.ClientRecords -> {

                }
            }
        }
    }

    private fun createIntent(code:Int, name:String){
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/vnd.ms-excel"

        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val fileName: String = sdf.format(Date()).toString() + "_$name.xls"
        intent.putExtra(Intent.EXTRA_TITLE, fileName)
        startActivityForResult(intent, code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        val downloadFileUri: Uri = data!!.data!!
        requireActivity().contentResolver.openOutputStream(downloadFileUri)

        when(requestCode){
            101-> exportSupplierList(downloadFileUri)
            102 -> exportClientList(downloadFileUri)
            103 -> exportProductList(downloadFileUri)
        }
    }

    private fun exportSupplierList(downloadFileUri:Uri){
        val sqliteToExcel = SQLiteToExcel(
            requireContext(),
            "blint_database",
            requireContext().filesDir.path
        )
        val prettyNameMapping = hashMapOf<String, String>()
        sqliteToExcel.setExcludeColumns(listOf("supplierId","businessId"))
        sqliteToExcel.exportSingleTable("Supplier", "supplier.xls", exportTableListener(downloadFileUri))
    }

    private fun exportClientList(downloadFileUri: Uri){
        val sqliteToExcel = SQLiteToExcel(
            requireContext(),
            "blint_database",
            requireContext().filesDir.path
        )
        val prettyNameMapping = hashMapOf<String, String>()
        sqliteToExcel.setExcludeColumns(listOf("clientId","businessId"))
        sqliteToExcel.exportSingleTable("Client", "clients.xls", exportTableListener(downloadFileUri))
    }


    private fun exportProductList(downloadFileUri: Uri){
        val sqliteToExcel = SQLiteToExcel(
            requireContext(),
            "blint_database",
            requireContext().filesDir.path
        )
        val prettyNameMapping = hashMapOf<String, String>()
        val columnsToExclude = listOf("productId","image","lastRecordTimestamp","suppliers","businessId","totalInStock","totalOutStock")
        sqliteToExcel.setExcludeColumns(columnsToExclude)
        sqliteToExcel.exportSingleTable("Product","/productos.xls",exportTableListener(downloadFileUri))
    }

    private fun exportTableListener(downloadFileUri: Uri):ExportListener {
        return object : ExportListener {
            override fun onStart() {}

            override fun onCompleted(filePath: String) {
                requireActivity().contentResolver.openInputStream(Uri.fromFile(File(filePath)))
                    ?.use { inputStream ->
                        requireActivity().contentResolver.openOutputStream(downloadFileUri)?.let {
                            inputStream.copyTo(it)
                            it.close()
                        }
                        inputStream.close()
                    }
            }

            override fun onError(e: Exception) {}
        }
    }

}