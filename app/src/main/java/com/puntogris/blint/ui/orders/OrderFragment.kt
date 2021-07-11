package com.puntogris.blint.ui.orders

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.print.PrintAttributes
import android.text.Layout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.maxkeppeler.sheets.options.DisplayMode
import com.maxkeppeler.sheets.options.Option
import com.maxkeppeler.sheets.options.OptionsSheet
import com.puntogris.blint.R
import com.puntogris.blint.databinding.FragmentOrderBinding
import com.puntogris.blint.model.FirestoreRecord
import com.puntogris.blint.model.OrdersTableItem
import com.puntogris.blint.ui.base.BaseFragment
import com.puntogris.blint.utils.setUpUi
import com.puntogris.blint.utils.showShortSnackBar
import com.rajat.pdfviewer.PdfViewerActivity
import com.wwdablu.soumya.simplypdf.DocumentInfo
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.TextComposer
import com.wwdablu.soumya.simplypdf.composers.models.TextProperties
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.OutputStream
import java.util.*


@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>(R.layout.fragment_order) {

    private val args: OrderFragmentArgs by navArgs()
    private lateinit var adapter: OrdersTableAdapter
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        setUpUi(showAppBar = false)
        binding.fragment = this
        binding.order = args.order?.order
        adapter = OrdersTableAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        if (!args.orderId.isNullOrEmpty()){
//            lifecycleScope.launch {
//                val orderItems = viewModel.fetchOrderRecords(args.orderId.toString())
//                adapter.submitList(orderItems)
//            }
        }else {
            args.order?.records?.let { order ->
                val tableItems = order.map {
                    OrdersTableItem(it.productName, it.amount, it.value)
                }
                adapter.submitList(tableItems)
            }
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){ uri->
            try {
                val output = requireActivity().contentResolver.openOutputStream(uri)
                showShortSnackBar("Se guardo la factura correctamente.")
            }catch (e:Exception){
                showShortSnackBar("Ocurrio un error al guardar la factura.")
            }
        }
    }

    fun onGenerateOrderReceiptClicked(){
        OptionsSheet().build(requireContext()){
            displayMode(DisplayMode.GRID_HORIZONTAL)
            with(
                Option(R.drawable.ic_baseline_article_24,"Ver"),
                Option(R.drawable.ic_baseline_share_24, "Compartir"),
                Option(R.drawable.ic_baseline_archive_24,"Guardar")
            )
            onPositive { index: Int, _: Option ->
                when(index){
                    0 -> onSeeReceipt()
                    1 -> onShareReceipt()
                    2 -> onSaveReceipt()
                }
            }
        }.show(parentFragmentManager, "")
    }


    private fun createPdf(file:OutputStream){
        val doc = PdfDocument()
        val paintCenter = Paint()
        paintCenter.color = Color.BLACK
        paintCenter.textSize = 15F
        paintCenter.textAlign = Paint.Align.CENTER

        val paintLeft = Paint()
        paintLeft.color = Color.BLACK
        paintLeft.textSize = 15F
        paintLeft.textAlign = Paint.Align.LEFT

        val paintRight = Paint()
        paintRight.color = Color.BLACK
        paintRight.textSize = 15F
        paintRight.textAlign = Paint.Align.RIGHT

        val linePaint = Paint()
        linePaint.color = Color.BLACK
        paintRight.strokeWidth = 1f

        val width = 595
        val height = 842
        var pageNumber = 1

        var page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()

        var pageCanvas = doc.startPage(page)


        var canvas = pageCanvas.canvas
        val horizontalMargin = 40F
        val lineHeight = 50f

        canvas.drawText("Factura orden ${args.order?.order?.number}.", horizontalMargin,40f, paintLeft)

        canvas.drawLine(horizontalMargin,60f,width - horizontalMargin, 60f, paintCenter)

        canvas.drawText("Producto", horizontalMargin,100F, paintLeft)
        canvas.drawText("Cantidad",width / 2f,100f, paintCenter)
        canvas.drawText("Valor",width - horizontalMargin,100f ,paintRight)

        var initY = 140F

        args.order?.records?.forEach { firestoreRecord ->
            if (initY >= height - 100) {
                canvas.drawLine(horizontalMargin, initY + 25,width - horizontalMargin, initY + 25, paintCenter)
                canvas.drawText(pageNumber.toString() ,width / 2f, height - 20f ,paintCenter)

                pageNumber += 1
                doc.finishPage(pageCanvas)
                page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                pageCanvas = doc.startPage(page)
                canvas = pageCanvas.canvas
                initY = 40f
                canvas.drawLine(horizontalMargin,initY,width - horizontalMargin, initY, paintCenter)
                initY += 50f
            }

            canvas.drawText(firestoreRecord.productName, horizontalMargin,initY, paintLeft)
            canvas.drawText(firestoreRecord.amount.toString(),width / 2f,initY, paintCenter)
            canvas.drawText("${firestoreRecord.value}$",width - horizontalMargin,initY ,paintRight)
            initY += 25
        }

        if (initY >=  height - 100){
            canvas.drawLine(horizontalMargin, initY + 25,width - horizontalMargin, initY + 25, paintCenter)
            canvas.drawText(pageNumber.toString() ,width / 2f, height - 20f ,paintCenter)

            pageNumber += 1
            doc.finishPage(pageCanvas)
            page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
            pageCanvas = doc.startPage(page)
            canvas = pageCanvas.canvas
            initY = 40f
            canvas.drawLine(horizontalMargin,initY,width - horizontalMargin, initY, paintCenter)
            initY += 50f
        }else{
            initY += 40f
        }

        canvas.drawText("TOTAL:" ,horizontalMargin, initY ,paintLeft)
        canvas.drawText("${args.order?.order?.value}$" ,width - horizontalMargin, initY ,paintRight)
        initY += 15
        canvas.drawLine(horizontalMargin, initY,width - horizontalMargin, initY, paintCenter)
        canvas.drawText(pageNumber.toString() ,width / 2f, height - 20f ,paintCenter)


        doc.finishPage(pageCanvas)
        doc.writeTo(file)
        doc.close()
        file.close()
    }

    private fun onSeeReceipt(){
        val file = File(requireContext().filesDir.absolutePath + "/receipt.pdf")
        createPdf(file.outputStream())
        startActivity(
            PdfViewerActivity.launchPdfFromPath(
            context,
                file.path,
            "Factura orden ${args.order?.order?.number}",
            "pdf",
            enableDownload = false
        ))
    }

    private fun onShareReceipt(){
        val file = File(requireContext().filesDir.absolutePath + "/receipt.pdf")
        createPdf(file.outputStream())
        val uri = FileProvider.getUriForFile(requireContext(), "com.puntogris.blint", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(intent)
    }


    private fun onSaveReceipt(){
        activityResultLauncher.launch("application.pdf")
    }

    fun onExternalChipClicked(){
//        if(args.order.traderId != 0){
//            when(args.order.type){
//                "IN"-> {
//                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
//                    findNavController().navigate(action)
//                }
//                "OUT" -> {
//                    val action = OrderInfoBottomSheetDirections.actionOrderInfoBottomSheetToSupplierFragment(args.order.traderId)
//                    findNavController().navigate(action)
//                }
//            }
//        }
    }
}