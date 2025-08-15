package com.apptomatico.app_movil_kotlin_v3.model



class Familias : ArrayList<UserDataCollectionItem>()


data class FamiliasDataCollectionItem(
        val data:List<Familias_List>
)

data class  Familias_List(
        val cfm_id: Int,
        val cfm_nombre: String,
        val cfm_eva_nombre: String
)

data class FamiliasHeaders (
        val valores: String

)


data class FamiliasAll (
        val cfm_id: Int,
        val cfm_id_dis: Int,
        val cfm_id_hijo: Int,
        val cfm_nombre: String,
        val cfm_eva_nombre: String,
        val cfm_sync: Int

)
