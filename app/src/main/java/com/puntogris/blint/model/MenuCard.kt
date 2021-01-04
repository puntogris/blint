package com.puntogris.blint.model

import com.puntogris.blint.R

data class MenuCard(
    val id:Int,
    val title:String,
    val icon:Int,
    val description:String,
    val subtitle:String,
    val color: Int
   ) {

    companion object{

        fun fromCardCode(code: Int): MenuCard?{
            return when(code){
                ALL_PRODUCTS_CARD_CODE ->
                    MenuCard(1,"Productos", R.drawable.ic_baseline_library_books_24,"1 / 41 stock","Ver todos", R.color.card1)
                ALL_CLIENTS_CARD_CODE ->
                    MenuCard(2,"Proveedores", R.drawable.ic_baseline_store_24," 1 proveedor","Ver todos", R.color.card2)
                ALL_SUPPLIERS_CARD_CODE ->
                    MenuCard(3,"Clientes", R.drawable.ic_baseline_people_alt_24,"4 clientes","Ver todos",R.color.card3)
                else -> null
            }
        }

        const val ALL_PRODUCTS_CARD_CODE = 101
        const val ALL_CLIENTS_CARD_CODE = 102
        const val ALL_SUPPLIERS_CARD_CODE = 103
    }


}