package com.puntogris.blint.ui.main

import android.content.Context
import android.widget.ArrayAdapter
import com.puntogris.blint.R

class BusinessAdapter (context: Context): ArrayAdapter<String>(context,android.R.layout.simple_list_item_1){

    fun setList(list:List<String>){
        addAll(list)
    }
}