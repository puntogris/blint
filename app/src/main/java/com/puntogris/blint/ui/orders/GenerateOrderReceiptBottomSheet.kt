package com.puntogris.blint.ui.orders

import android.graphics.Typeface
import android.print.PrintAttributes
import android.text.Layout
import com.puntogris.blint.R
import com.puntogris.blint.databinding.GenerateOrderReceiptBottomSheetBinding
import com.puntogris.blint.ui.base.BaseBottomSheetFragment
import com.rajat.pdfviewer.PdfViewerActivity
import com.wwdablu.soumya.simplypdf.DocumentInfo
import com.wwdablu.soumya.simplypdf.SimplyPdf
import com.wwdablu.soumya.simplypdf.composers.TableComposer
import com.wwdablu.soumya.simplypdf.composers.TextComposer
import com.wwdablu.soumya.simplypdf.composers.models.TableProperties
import com.wwdablu.soumya.simplypdf.composers.models.TextProperties
import com.wwdablu.soumya.simplypdf.composers.models.cell.TextCell
import java.io.File

class GenerateOrderReceiptBottomSheet :BaseBottomSheetFragment<GenerateOrderReceiptBottomSheetBinding>(R.layout.generate_order_receipt_bottom_sheet){


    override fun initializeViews() {

        val file = File(requireContext().filesDir.absolutePath + "/test.pdf")
        val simplyPdfDocument = SimplyPdf.with(requireContext(), file)
            .colorMode(DocumentInfo.ColorMode.COLOR)
            .paperSize(PrintAttributes.MediaSize.ISO_A4)
            .margin(DocumentInfo.Margins.DEFAULT)
            .paperOrientation(DocumentInfo.Orientation.PORTRAIT)
            .build()

        val textProperties = TextProperties()
        textProperties.textSize = 24
        textProperties.textColor = "#000000"
        textProperties.alignment = Layout.Alignment.ALIGN_CENTER
        textProperties.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val textComposer = TextComposer(simplyPdfDocument)
        textComposer.write("Demonstrate the usage of TextComposer.", textProperties)

        val table = TableProperties()
        table.borderColor = "#000000"
        table.borderWidth = 2

        val paparer = PrintAttributes.MediaSize.ISO_A4
        val tableText = TableComposer(simplyPdfDocument)
        println(paparer)
        tableText.draw(listOf(listOf(TextCell("sdfsdfsdf", textProperties, 340))))
        tableText.draw(listOf(listOf(TextCell("sdfsdfsdf", textProperties, 340))))
        simplyPdfDocument.finish()




        startActivity(PdfViewerActivity.launchPdfFromPath(           //PdfViewerActivity.Companion.launchPdfFromUrl(..   :: incase of JAVA
            context,
            file.path,                                // PDF URL in String format
            "Pdf title/name ",                        // PDF Name/Title in String format
            "pdf directory to save",                  // If nothing specific, Put "" it will save to Downloads
            enableDownload = true                    // This param is true by defualt.
        ))
    }

}