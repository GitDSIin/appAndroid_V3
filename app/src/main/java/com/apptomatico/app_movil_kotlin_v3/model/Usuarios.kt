package com.apptomatico.app_movil_kotlin_v3.model

data class UsuariosDataCollectionItem(
        val id: Int,
        val nombre: String,
        val descripcion: String,
        val fec_alta: String,
        val fec_ult_act: String
)

data class Usuario_List(
        val nombre: String,
        val descripcion: String

)
data class Usuario_Upd(
        val id: Int,
        val nombre: String,
        val descripcion: String

)
