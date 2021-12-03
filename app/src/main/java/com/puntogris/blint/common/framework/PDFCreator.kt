package com.puntogris.blint.common.framework

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.puntogris.blint.R
import com.puntogris.blint.common.utils.Constants
import com.puntogris.blint.common.utils.getDateWithTimeFormattedString
import com.puntogris.blint.feature_store.domain.model.order.OrderWithRecords
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.*
import javax.inject.Inject

class PDFCreator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val paintCenter = Paint().apply {
        color = Color.BLACK
        textSize = 15F
        textAlign = Paint.Align.CENTER
    }

    private val paintLeft = Paint().apply {
        color = Color.BLACK
        textSize = 15F
        textAlign = Paint.Align.LEFT
    }

    private val paintBarcode = Paint().apply {
        color = Color.BLACK
        textSize = 11F
        textAlign = Paint.Align.LEFT
    }

    private val paintRight = Paint().apply {
        color = Color.BLACK
        textSize = 15F
        textAlign = Paint.Align.RIGHT
    }

    private val blintPaint = Paint().apply {
        color = Color.BLACK
        textSize = 11F
        textAlign = Paint.Align.RIGHT
    }


    fun createPdf(uri: Uri?, orderWithRecords: OrderWithRecords): File {

        val localFile = File(
            context.filesDir.absolutePath + "/${
                context.getString(
                    R.string.invoice_file_name,
                    orderWithRecords.order.number
                )
            }"
        )

        if (localFile.exists()) {
            uri?.let {
                val outputStream = context.contentResolver.openOutputStream(uri)
                outputStream?.write(localFile.readBytes())
                outputStream?.close()
            }
            return localFile
        }

        val doc = PdfDocument()

        val width = 595
        val height = 842
        var pageNumber = 1

        var page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()

        var pageCanvas = doc.startPage(page)

        var canvas = pageCanvas.canvas
        val horizontalMargin = 40F
        canvas.drawText(
            context.getString(
                R.string.order_number_invoice,
                orderWithRecords.order.number
            ), horizontalMargin, 40f, paintLeft
        )
        canvas.drawText(
            "${context.getString(R.string.business)}: ${orderWithRecords.order.businessName} ",
            horizontalMargin,
            70f,
            paintLeft
        )
        canvas.drawText(
            "${context.getString(R.string.date)}: ${Date().getDateWithTimeFormattedString()}",
            horizontalMargin,
            100f,
            paintLeft
        )
        val trader =
            if (orderWithRecords.order.type == Constants.IN) context.getString(R.string.supplier_label) else context.getString(
                R.string.client_label
            )
        val traderName =
            if (orderWithRecords.order.traderName.isBlank()) context.getString(R.string.not_specified) else orderWithRecords.order.traderName
        canvas.drawText("$trader: $traderName", horizontalMargin, 130f, paintLeft)

        canvas.drawLine(horizontalMargin, 150f, width - horizontalMargin, 150f, paintCenter)

        val xRow2 = ((width - 100) / 3f) + horizontalMargin
        val xRow3 = ((width - 100) / 3f * 2) + horizontalMargin
        val xRow4 = width - horizontalMargin

        canvas.drawText(context.getString(R.string.product), horizontalMargin, 190F, paintLeft)
        canvas.drawText(context.getString(R.string.sku_caps), xRow2, 190f, paintCenter)
        canvas.drawText(context.getString(R.string.amount), xRow3, 190f, paintCenter)
        canvas.drawText(context.getString(R.string.total), xRow4, 190f, paintRight)

        var initY = 230F

        orderWithRecords.records.forEachIndexed { i, record ->
            if (initY >= 730) {
                if (i == orderWithRecords.records.size - 1) {
                    canvas.drawFooter(width, height, pageNumber, false, orderWithRecords)
                    pageNumber += 1
                    doc.finishPage(pageCanvas)
                    page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                    pageCanvas = doc.startPage(page)
                    canvas = pageCanvas.canvas
                    initY = 40f
                    canvas.drawLine(
                        horizontalMargin,
                        initY,
                        width - horizontalMargin,
                        initY,
                        paintCenter
                    )
                    initY += 50f
                    canvas.drawFooter(width, height, pageNumber, true, orderWithRecords)
                } else {
                    canvas.drawFooter(width, height, pageNumber, false, orderWithRecords)
                    pageNumber += 1
                    doc.finishPage(pageCanvas)
                    page = PdfDocument.PageInfo.Builder(width, height, pageNumber).create()
                    pageCanvas = doc.startPage(page)
                    canvas = pageCanvas.canvas
                    initY = 40f
                    canvas.drawLine(
                        horizontalMargin,
                        initY,
                        width - horizontalMargin,
                        initY,
                        paintCenter
                    )
                    initY += 50f
                }
            } else {
                if (i == orderWithRecords.records.size - 1) canvas.drawFooter(
                    width,
                    height,
                    pageNumber,
                    true,
                    orderWithRecords
                )
            }
            canvas.drawText(record.productName, horizontalMargin, initY, paintLeft)
            canvas.drawText(
                "Cod.:${record.barcode}",
                horizontalMargin,
                initY + 13,
                paintBarcode
            )
            canvas.drawText(record.sku, xRow2, initY, paintLeft)
            canvas.drawText(record.amount.toString(), xRow3, initY, paintCenter)
            canvas.drawText("${record.value}$", xRow4, initY, paintRight)
            initY += 50

        }

        val stream = localFile.outputStream()
        doc.finishPage(pageCanvas)
        doc.writeTo(stream)
        doc.close()
        stream.close()
        return localFile
    }

    private fun Canvas.drawFooter(
        width: Int,
        height: Int,
        pageNumber: Int,
        isLastPage: Boolean,
        orderWithRecords: OrderWithRecords
    ) {
        if (isLastPage) {
            drawText("${context.getString(R.string.total_caps)}:", 40f, height - 75f, paintLeft)
            drawText(
                "${orderWithRecords.order.value}$",
                width - 40f,
                height - 75f,
                paintRight
            )
        }
        drawLine(40f, height - 50f, width - 40f, height - 50f, paintCenter)
        drawText(pageNumber.toString(), width / 2f, height - 20f, paintCenter)
        drawText(
            context.getString(R.string.invoice_by_blint),
            width - 40f,
            height - 25f,
            blintPaint
        )
    }
}