package com.apptomatico.app_movil_kotlin_v3.model


data class DataControlMonitoreablesFav(val id_accion: Int, val tipo_hardware: String ,val tipo_grafico: String, val porcentaje: String, val nom_svr: String, val espacio_libre: String, val espacio_total: String, val tot_enviados: String, val tot_recibidos: String, val id_equipo: Int)

data class DataControlMonitoreablesTag(val id_accion: Int, val tag_grafico: Int, val hardware: String, val grafico: String)

data class DataControlVistasMonitoreablesTag(val id_accion: Int, val tag_vista_grafico: Int, val hardware: String)

data class DataListFavMonitoreables(
        val data:List<Monitoreables_Fav_List>
)

data class Monitoreables_Fav_List(
        val id_accion_negocio: String,
        val origen: Int,
        val accion_grafico:String,
        val comando:String,
        val tipo_grafico:String,
        val nombre_negocio:String
)

data class FecMetricas(val fecha: String, val metrica: String )