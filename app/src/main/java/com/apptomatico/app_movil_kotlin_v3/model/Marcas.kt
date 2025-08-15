package com.apptomatico.app_movil_kotlin_v3.model

class Marcas : ArrayList<UserDataCollectionItem>()


data class MarcasDataCollectionItem(
        val data:List<Marcas_List>
)

data class  Marcas_List(
        val cm_id: Int,
        val cm_nombre: String,
        val cm_eva_nombre: String,
        val cm_descripcion: String,
        val cm_contacto: String,
        val cm_pais: String






)

data class MarcasHeaders (
        val valores: String

)


data class MarcasAll (
        val cm_id: Int,
        val cm_id_dis: Int,
        val cm_id_hijo: Int,
        val cm_nombre: String,
        val cm_eva_nombre: String,
        val cm_descripcion: String,
        val cm_pais: String,
        val cm_contacto: String,
        val cm_estatus: String,
        val cm_sync: Int

)
