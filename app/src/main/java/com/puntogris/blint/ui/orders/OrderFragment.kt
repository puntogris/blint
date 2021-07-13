package com.puntogris.blint.ui.orders

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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
import com.puntogris.blint.utils.*
import com.rajat.pdfviewer.PdfViewerActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream
import java.util.*

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>(R.layout.fragment_order) {

    private val args: OrderFragmentArgs by navArgs()
    private lateinit var ordersAdapter: OrdersTableAdapter
    private val viewModel: OrdersViewModel by viewModels()
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>

    override fun initializeViews() {
        setUpUi(showAppBar = false)
        binding.fragment = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        ordersAdapter = OrdersTableAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ordersAdapter
        }

        if (!args.orderId.isNullOrEmpty()){
            lifecycleScope.launch {
                val order = viewModel.fetchOrderRecords(args.orderId.toString())
                if (!order.records.isNullOrEmpty()){
                    ordersAdapter.submitList(order.records.map { OrdersTableItem(it.productName, it.amount, it.value) })
                    viewModel.updateOrder(order)
                    viewModel.order.value?.records?.let { orderItems ->
                        val tableItems = orderItems.map {
                            OrdersTableItem(it.productName, it.amount, it.value)
                        }
                        ordersAdapter.submitList(tableItems)
                    }
                }
            }
        }else {
            args.order?.let {
                viewModel.updateOrder(it)
            }
            viewModel.order.value?.records?.let { order ->
                val tableItems = order.map {
                    OrdersTableItem(it.productName, it.amount, it.value)
                }
                ordersAdapter.submitList(tableItems)
            }
        }

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument()){ uri->
            try {
                requireActivity().contentResolver.openOutputStream(uri)?.let {
                    createPdf(it)
                }
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

    val paintCenter = Paint().also {
        it.color = Color.BLACK
        it.textSize = 15F
        it.textAlign = Paint.Align.CENTER
    }
    val paintLeft = Paint().also {
        it.color = Color.BLACK
        it.textSize = 15F
        it.textAlign = Paint.Align.LEFT
    }

    val paintBarcode = Paint().also {
        it.color = Color.BLACK
        it.textSize = 11F
        it.textAlign = Paint.Align.LEFT
    }

    val paintRight = Paint().also {
        it.color = Color.BLACK
        it.textSize = 15F
        it.textAlign = Paint.Align.RIGHT
    }

    val blintPaint = Paint().also {
        it.color = Color.BLACK
        it.textSize = 11F
        it.textAlign = Paint.Align.RIGHT
    }

    val linePaint = Paint().also {
        it.color = Color.BLACK
        it.strokeWidth = 1f
    }

    val width = 595

    private fun createPdf(file:OutputStream){
        val doc = PdfDocument()

        val height = 842
        var pageNumber = 1

        var page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()

        var pageCanvas = doc.startPage(page)

        var canvas = pageCanvas.canvas
        val horizontalMargin = 40F

        canvas.drawText("Factura orden ${viewModel.order.value?.order?.number}.", horizontalMargin,40f, paintLeft)
        canvas.drawText("Negocio: ${viewModel.order.value?.order?.businessName} ", horizontalMargin,70f, paintLeft)
        canvas.drawText("Fecha: ${Date().getDateWithTimeFormattedString()}", horizontalMargin,100f, paintLeft)
        val trader = if (viewModel.order.value?.order?.type == "IN") "Proveedor" else "Cliente"
        val traderName = if(viewModel.order.value?.order?.traderName.isNullOrBlank()) "No especificado" else viewModel.order.value?.order?.traderName.toString()
        canvas.drawText("$trader: $traderName", horizontalMargin,130f, paintLeft)

        val bitmap:Bitmap = generateQRImage("order:${viewModel.order.value?.order?.orderId.toString()}", 75, 75)
        canvas.drawBitmap(bitmap, width.toFloat() - 100, 25f, paintRight)

        canvas.drawLine(horizontalMargin,150f,width - horizontalMargin, 150f, paintCenter)

        val xRow2 = ((width - 100) / 3f) + horizontalMargin
        val xRow3 = ((width - 100) / 3f *2) + horizontalMargin
        val xRow4 = width - horizontalMargin

        canvas.drawText("Producto", horizontalMargin, 190F, paintLeft)
        canvas.drawText("SKU", xRow2,190f, paintCenter)
        canvas.drawText("Cantidad", xRow3 ,190f, paintCenter)
        canvas.drawText("Valor", xRow4, 190f , paintRight)

        var initY = 230F

       viewModel.order.value?.records?.forEachIndexed { i, firestoreRecord ->
           if (initY >= 730) {
                if (i == viewModel.order.value?.records!!.size - 1){
                    canvas.drawFooter(width, height, pageNumber, false)
                    pageNumber += 1
                    doc.finishPage(pageCanvas)
                    page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                    pageCanvas = doc.startPage(page)
                    canvas = pageCanvas.canvas
                    initY = 40f
                    canvas.drawLine(horizontalMargin,initY,width - horizontalMargin, initY, paintCenter)
                    initY += 50f
                    canvas.drawFooter(width, height, pageNumber, true)
                }else{
                    canvas.drawFooter(width, height, pageNumber, false)
                    pageNumber += 1
                    doc.finishPage(pageCanvas)
                    page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                    pageCanvas = doc.startPage(page)
                    canvas = pageCanvas.canvas
                    initY = 40f
                    canvas.drawLine(horizontalMargin,initY,width - horizontalMargin, initY, paintCenter)
                    initY += 50f
                }
            }else{
                if (i == viewModel.order.value?.records?.size!! - 1) canvas.drawFooter(width, height, pageNumber, true)
            }
            canvas.drawText(firestoreRecord.productName, horizontalMargin, initY, paintLeft)
            canvas.drawText("Cod.:${firestoreRecord.barcode}", horizontalMargin, initY + 13, paintBarcode)
            canvas.drawText(firestoreRecord.sku, xRow2,initY, paintLeft)
            canvas.drawText(firestoreRecord.amount.toString() ,xRow3,initY, paintCenter)
            canvas.drawText("${firestoreRecord.value}$", xRow4,initY ,paintRight)
           initY += 50

        }
        doc.finishPage(pageCanvas)
        doc.writeTo(file)
        doc.close()
        file.close()
    }


    private fun Canvas.drawFooter(width: Int, height: Int, pageNumber: Int, isLastPage: Boolean){
        if (isLastPage){
            drawText("TOTAL:" ,40f, height - 75f, paintLeft)
            drawText("${viewModel.order.value?.order?.value}$" ,width - 40f, height - 75f ,paintRight)
        }
        drawLine(40f, height - 50f,width - 40f, height - 50f, paintCenter)
        drawText(pageNumber.toString() ,width / 2f, height - 20f ,paintCenter)
        drawText("Generado por blint.app" ,width - 40f, height - 25f , blintPaint)
    }

    private fun onSeeReceipt(){
        val file = File(requireContext().filesDir.absolutePath + "/receipt.pdf")
        createPdf(file.outputStream())
        startActivity(
            PdfViewerActivity.launchPdfFromPath(
            context,
                file.path,
            "Factura orden ${viewModel.order.value?.order?.number}",
            "pdf",
            enableDownload = false
        ))
    }

    private fun onShareReceipt(){
        val file = File(requireContext().filesDir.absolutePath + "/order${viewModel.order.value?.order?.number}_receipt.pdf")
        createPdf(file.outputStream())
        val uri = FileProvider.getUriForFile(requireContext(), "com.puntogris.blint", file)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(intent)
    }

    private fun onSaveReceipt(){
        activityResultLauncher.launch("order${viewModel.order.value?.order?.number}_receipt.pdf")
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