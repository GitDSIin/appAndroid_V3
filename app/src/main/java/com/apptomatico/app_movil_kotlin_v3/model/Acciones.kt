package com.apptomatico.app_movil_kotlin_v3.model


data class InfoAccion(val id: Int = -1, val result: String)

data class AccionesFavData(val idAccion: Int, val titulo: String, val comando: String, val id_equipo: String, val nom_negocio: String, val tipo_id: Int, val path_icono: String)


data class AccionesFavDataCarrousel(val idAccion: Int, val titulo: String, val comando: String, val id_equipo: String, val nom_negocio: String, val tipo_id: Int, val path_icono: String, val titulo_carrousel: String)


data class DataListAcionesDinamicas(
        val data:List<AcionesDinamicas_List>
)

data class AcionesDinamicas_List(
        var id: Int,
        var id_negocio_id:Int,
        var alias_aplicacion:String,
        var icono: String,
        var aplicacion_ruta_aplicacion: String,
        var aplicacion_nombre_aplicacion: String,
        var aplicacion_parametros_aplicacion: String,
        var exploracion_ruta: String,
        var eliminacion_tipo: String,
        var eliminacion_nombre_archivo: String,
        var eliminacion_ruta_archivo: String,
        var tipo_id: Int,
        var grupo: Int,
        var comando: String,
        var fecha_alta: String,
        var origen: Int,
        var titulo_aplicacion: String,
        var estado_programa_nombre: String,
        var screenshot_tipo: String,
        var descripcion: String,
        var nombre_equipo: String,
        var aplicacion_usuario: String,
        var aplicacion_pwd: String
)
