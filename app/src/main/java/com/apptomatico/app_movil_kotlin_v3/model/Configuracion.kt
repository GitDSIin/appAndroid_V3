package com.apptomatico.app_movil_kotlin_v3.model

data class data_extensiones_x_negocio(
        val data:List<extensiones_x_negocio>

)


data class extensiones_x_negocio(
        var id: Int,
        val extension_negocio: String,
        val programa_sistema: String


)


data class config_default_movil(
        val get_conf_movil_alertas: Int,
        val get_conf_movil_alertas_recientes: Int,
        val get_conf_movil_monitores: Int,
        val get_conf_movil_estatus_equipos: Int,
        val get_conf_movil_info_equipos: Int


)


data class perfil_app_header_filters(
        val header_id_negocio: String,
        val header_id_equipos: String,
        val filtro_alertas_estatus: Int,
        val filtro_alertas_equipo: Int,
        val filtro_alertas_fecha: Int,
        val filtro_alertas_fecha_desde_hasta: String,
        val filtro_alertas_eliminadas_estatus: Int,
        val filtro_alertas_eliminadas_fecha: Int,
        val filtro_alertas_eliminadas_fecha_desde_hasta: String


)