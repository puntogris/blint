package com.puntogris.blint.ui.main

import android.content.Context
import android.widget.ArrayAdapter

class DropDownMenuAdapter(context: Context):ArrayAdapter<String>(context, android.R.layout.simple_expandable_list_item_1) {

    fun setList(list: List<String>){
        clear()
        addAll(list)
        notifyDataSetChanged()
    }
}