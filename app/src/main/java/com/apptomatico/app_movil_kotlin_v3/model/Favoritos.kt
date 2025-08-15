package com.apptomatico.app_movil_kotlin_v3.model


data class DataListFavoritos(
        val data:List<Favoritos_List>
)

data class Favoritos_List(
        var id: Int,
        var icono:String,
        var alias_aplicacion:String,
        var id_negocio_id:Int,
        var aplicacion_ruta_aplicacion: String,
        var aplicacion_nombre_aplicacion: String,
        var aplicacion_parametros_aplicacion: String,
        var exploracion_ruta: String,
        var eliminacion_tipo: String,
        var eliminacion_nombre_archivo: String,
        var eliminacion_ruta_archivo: String,
        var tipo_id: Int,
        var comando: String,
        var fecha_alta: String,
        var origen: Int,
        var titulo_aplicacion: String,
        var estado_programa_nombre: String,
        var ip_publica: String,
        var puerto: Int,
        var hardware_key: String,
        var nombre_negocio: String,
        var boveda_software: String,
        var ip_local: String,
        var puerto_local: Int,
        var aplicacion_usuario: String,
        var aplicacion_pwd: String


)


data class DataListString_tmp(
        val data:List<Str_tmp_List>
)

data class Str_tmp_List(
        var resultado: String
)




