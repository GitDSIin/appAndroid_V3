package com.apptomatico.app_movil_kotlin_v3.model


class Articulos : ArrayList<UserDataCollectionItem>()


data class Articulos_List(
        val id: Int,
        val sku: String,
        val ean: String,
        val nombre: String,
        val descripcion: String,
        val estatus: String,
        val fec_alta: String,
        val fec_ult_act: String

)

data class add_Articulos(

        val sku: String,
        val ean: String,
        val nombre: String,
        val descripcion: String,
        val estatus: Int


)

data class upd_Articulos(

        val id: Int,
        val sku: String,
        val ean: String,
        val nombre: String,
        val descripcion: String,
        val estatus: Int


)