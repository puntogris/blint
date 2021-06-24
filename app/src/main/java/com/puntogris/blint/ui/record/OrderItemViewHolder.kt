package com.puntogris.blint.ui.record

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.databinding.CreateRecordItemVhBinding
import com.puntogris.blint.model.OrderItem
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.model.Record

class OrderItemViewHolder private constructor(val binding: CreateRecordItemVhBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root){

    fun bind(productWithRecord: ProductWithRecord, clickListener: (ProductWithRecord)-> Unit, amountListener: () -> Unit) {
        binding.item = productWithRecord
        binding.root.setOnClickListener { clickListener(productWithRecord) }

        val items = listOf("%", "$")
        val adapter = ArrayAdapter(context, R.layout.dropdown_item_list, items)
        (binding.discountType.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.amountText.addTextChangedListener {
            if (it.toString().isNotEmpty()){
                productWithRecord.record.amount = it.toString().toInt()
                binding.recordAmount.text = it.toString()
            }else{
                productWithRecord.record.amount = 0
                binding.recordAmount.text = ""
            }
            updateTotalProductValue(productWithRecord.record)
            amountListener.invoke()
        }



        binding.recordPriceText.addTextChangedListener {
            if (it.toString().isNotEmpty()){
                productWithRecord.record.productUnitPrice = it.toString().toFloat()
            }else{
                productWithRecord.record.productUnitPrice = 0F
            }
            updateTotalProductValue(productWithRecord.record)
            amountListener.invoke()
        }
        binding.executePendingBindings()
    }

    private fun updateTotalProductValue(record:Record){
        binding.textView13516.text = (record.amount * record.productUnitPrice).toString()
    }

    companion object{
        fun from(parent: ViewGroup, context: Context) :OrderItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CreateRecordItemVhBinding.inflate(layoutInflater,parent, false)
            return OrderItemViewHolder(binding, context)
        }
    }
}