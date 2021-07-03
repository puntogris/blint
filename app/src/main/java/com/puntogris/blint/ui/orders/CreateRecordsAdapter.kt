package com.puntogris.blint.ui.orders

import android.content.Context
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.diffcallback.ProductWithRecordItemDiffCallBack
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.ui.notifications.SwipeToDeleteCallback
import com.puntogris.blint.utils.bindDimen
import com.puntogris.blint.utils.dp
import com.puntogris.blint.utils.getValueAnimator
import com.puntogris.blint.utils.screenWidth

class CreateRecordsAdapter(private val context: Context,
                           private val amountListener: () -> (Unit),
                           private val deleteListener: (ProductWithRecord) -> Unit):
    ListAdapter<ProductWithRecord, OrderItemViewHolder>(ProductWithRecordItemDiffCallBack()) {

    var recordsList = mutableListOf<ProductWithRecord>()
    private var expandedHeight = -1 // will be calculated dynamically
    private var originalHeight = -1 // will be calculated dynamically
    private lateinit var recyclerView: RecyclerView
    var animationPlaybackSpeed: Double = 0.8

    private val listItemHorizontalPadding: Float by bindDimen(context, R.dimen.list_item_horizontal_padding)
    private val listItemVerticalPadding: Float by bindDimen(context, R.dimen.list_item_vertical_padding)

    private var isScaledDown = false

    init {
        submitList(recordsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder.from(parent, context)
    }
    private var expandedModel: ProductWithRecord? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                viewHolder.bindingAdapterPosition.apply {
                    deleteListener.invoke(getItem(this))
                    recordsList.removeAt(this)
                    notifyItemRemoved(this)
                    amountListener.invoke()
                }
            }
        }.apply { ItemTouchHelper(this).attachToRecyclerView(recyclerView) }
        this.recyclerView = recyclerView
    }

    fun getRecordTotalPrice() = recordsList.sumByDouble { it.record.value.toDouble() }.toFloat()

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(recordsList[position], amountListener)

        expandItem(holder, getItem(position) == expandedModel, animate = false)
  //      scaleDownItem(holder, position, isScaledDown)

        holder.binding.cardContainer.setOnClickListener {
            when (expandedModel) {
                null -> {
                    // expand clicked view
                    expandItem(holder, expand = true, animate = true)
                    expandedModel = getItem(position)
                }
                getItem(position) -> {
                    // collapse clicked view
                    expandItem(holder, expand = false, animate = true)
                    expandedModel = null
                }
                else -> {
                    // collapse previously expanded view
                    val expandedModelPosition = recordsList.indexOf(expandedModel!!)
                    val oldViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? OrderItemViewHolder
                    if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                    // expand clicked view
                    expandItem(holder, expand = true, animate = true)
                    expandedModel = getItem(position)
                }
            }
        }
    }

//    fun getScaleDownAnimator(isScaledDown: Boolean): ValueAnimator {
//        val lm = recyclerView.layoutManager as LinearLayoutManager
//
//        val animator = getValueAnimator(isScaledDown,
//            duration = 300L, interpolator = AccelerateDecelerateInterpolator()
//        ) { progress ->
//
//            // Get viewHolder for all visible items and animate attributes
//            for (i in lm.visibleItemsRange) {
//                val holder = recyclerView.findViewHolderForLayoutPosition(i) as RecordItemViewHolder
//                setScaleDownProgress(holder, i, progress)
//            }
//        }
//
//        // Set adapter variable when animation starts so that newly binded views in
//        // onBindViewHolder will respect the new size when they come into the screen
//        animator.doOnStart { this.isScaledDown = isScaledDown }
//
//        // For all the non visible items in the layout manager, notify them to adjust the
//        // view to the new size
//        animator.doOnEnd {
//            repeat(lm.itemCount) { if (it !in lm.visibleItemsRange) notifyItemChanged(it) }
//        }
//        return animator
//    }

    private fun setScaleDownProgress(holder: OrderItemViewHolder, position: Int, progress: Float) {
        val itemExpanded = position >= 0 && getItem(position) == expandedModel
        holder.binding.cardContainer.layoutParams.apply {
            width = ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height = ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
        }
        holder.binding.cardContainer.requestLayout()

        holder.binding.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.binding.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.binding.scaleContainer.setPadding(
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
            (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

        //holder.listItemFg.alpha = progress
    }

    override fun onViewAttachedToWindow(holder: OrderItemViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.binding.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.binding.expandView.isVisible = true
                view.doOnPreDraw {
                    expandedHeight = view.height
                    holder.binding.expandView.isVisible = false
                }
            }
        }
    }

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()

    private fun expandItem(holder: OrderItemViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            val animator = getValueAnimator(
                expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
            ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.binding.expandView.isVisible = true }
            else animator.doOnEnd { holder.binding.expandView.isVisible = false }

            animator.start()
        } else {
             //show expandView only if we have expandedHeight (onViewAttached)
            holder.binding.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    /** Convenience method for calling from onBindViewHolder */
    private fun scaleDownItem(holder: OrderItemViewHolder, position: Int, isScaleDown: Boolean) {
        setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
    }

    private val originalWidth = context.screenWidth - 16.dp
    private val expandedWidth = context.screenWidth - 0.dp

    private fun setExpandProgress(holder: OrderItemViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.binding.cardContainer.layoutParams.height =
                (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.binding.cardContainer.layoutParams.width =
            (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        //holder.binding.cardView.setBackgroundColor(blendColors(originalBg, expandedBg, progress))
        holder.binding.cardContainer.requestLayout()
        holder.binding.chevron.rotation = 90 * progress
    }

    override fun getItemCount() = recordsList.size
}

