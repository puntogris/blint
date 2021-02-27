package com.puntogris.blint.ui.notifications

import android.graphics.*
import android.text.TextPaint
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeToDeleteCallback: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val textPaint: TextPaint = TextPaint()
    private val text = "Eliminar"

    init {
        textPaint.isAntiAlias = true
        textPaint.color = Color.GRAY
        textPaint.textSize = 40F
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val margin = 60f
        val textWidth = textPaint.measureText(text)
        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val textLeftX = itemView.left + itemView.paddingRight.toFloat() + margin
        val textRightX = itemView.right - itemView.paddingRight - textWidth - margin
        val textY = itemView.top + itemHeight / 2f

        c.drawText(text, textRightX, textY, textPaint)
        c.drawText(text, textLeftX, textY, textPaint)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

    }


    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}