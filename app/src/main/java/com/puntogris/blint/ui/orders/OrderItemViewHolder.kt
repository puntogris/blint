package com.puntogris.blint.ui.orders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.puntogris.blint.R
import com.puntogris.blint.databinding.CreateRecordItemVhBinding
import com.puntogris.blint.model.ProductWithRecord
import com.puntogris.blint.model.Record
import com.puntogris.blint.utils.toMoneyFormatted

class OrderItemViewHolder private constructor(val binding: CreateRecordItemVhBinding) : RecyclerView.ViewHolder(binding.root){

    fun bind(productWithRecord: ProductWithRecord, amountListener: () -> Unit) {
        binding.item = productWithRecord

        binding.amountText.addTextChangedListener {
            if (it.toString().isNotEmpty()){
                productWithRecord.record.amount = it.toString().toInt()
                binding.recordAmount.text = it.toString()
            }else{
                productWithRecord.record.amount = 0
                binding.recordAmount.text = ""
            }
            productWithRecord.record.value = productWithRecord.record.productUnitPrice * productWithRecord.record.amount
            updateTotalProductValue(productWithRecord.record.value)
            amountListener.invoke()
        }

        binding.recordPriceText.addTextChangedListener {
            if (it.toString().isNotEmpty()){
                productWithRecord.record.productUnitPrice = it.toString().toFloat()
            }else{
                productWithRecord.record.productUnitPrice = 0F
            }
            productWithRecord.record.value = productWithRecord.record.productUnitPrice * productWithRecord.record.amount
            updateTotalProductValue(productWithRecord.record.value)
            amountListener.invoke()
        }
        binding.executePendingBindings()
    }

    private fun updateTotalProductValue(value: Float){
        binding.textView13516.text = (value).toMoneyFormatted()
    }

    companion object{
        fun from(parent: ViewGroup) :OrderItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = CreateRecordItemVhBinding.inflate(layoutInflater,parent, false)
            return OrderItemViewHolder(binding)
        }
    }

}
