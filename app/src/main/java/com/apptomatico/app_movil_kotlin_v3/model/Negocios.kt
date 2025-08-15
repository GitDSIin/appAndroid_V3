package com.apptomatico.app_movil_kotlin_v3.model

class Negocios : ArrayList<UserDataCollectionItem>()

data class ControlEquiposXMovil(val equipo_id: Int, val ip_publica: String, val ip_local: String, val puerto: Int, val id_hardware: String, val icono_on: String, val icono_off: String, val nombre_equipo: String, val id_accion_monitoreable: String)

data class ControlEquiposXNegocio(val equipo_id: Int, val nombre_equipo: String)


data class ControlEquiposApiMv(val equipo_id: Int, val url: String, val nom_proyecto: String, val zona: String, val nom_instancia: String, val screenshot_vm: Int)


data class ControlEstadoEquipos(val equipo_id: Int, val estado_equipo: String, val nombre_equipo: String, val hardware_key: String)

data class ControlDeatalleEquipos(val equipo_ip_publica: String, val equipo_ip_local: String, val equipo_puerto: String)



data class DataListNegociosAll(
        val data:List<Negocios_List_All>
)

data class Negocios_List_All(
        val id_negocio_id: Int,
        val nombre: String,
        val id_accion_monitoreable: Int,
        val hardware_key: String)

data class DataListNegocios(
        val data:List<Negocios_List>
)

data class Negocios_List(
        val id_movil_id: Int,
        val id_negocio_id: Int,
        val nombre: String,
        val ip_publica: String,
        val ip_local: String,
        val puerto_local: Int,
        val puerto: Int,
        val boveda_software: String,
        val ruta_archivos_locales: String,
        val hardware_key: String,
        val estado_servidor: Int,
        val estado_ult_act: String,
        val icono_negocio_on: String,
        val icono_negocio_off: String,
        val fec_act_status: Int,
        val catnombreneg: String,
        val id_catnegocio: Int,
        val tipo_eq_icono_on: String,
        val tipo_eq_icono_off: String,
        val tipo_eq_clave: String,
        val id_accion_monitoreable: Int?,
        val modelo_comunicacion: Int?,
        val wake_on: Int?,
        val unimovil: Int?,
        val gc_project_vm: String?,
        val gc_project_screenshot: Int?,
        val estatus_equipo: String?,
        val ordenamiento_equipos: String?,
        val equipo_virtual: Int?
)


data class Negocios_Token(
        val token: String

)


data class DataListAccionesNegocios(
        val data:List<AccionesNegocios_List>
)

data class AccionesNegocios_List(
        val accion: String,
        val descripcion: String,
        val tipo: String,
        val comando: String,
        val id_negocio_id: Int


)


data class DataBovedaNegocio(
        val data:List<Negocios_Boveda_List>
)

data class Negocios_Boveda_List(
        val boveda_software: String

)


data class Negocio_Directorio(
        val id_menu: Int,
        val nombre: String,
        val Path: String,
        val parent: Int,
        val tipo: Int,
        val peso: String,
        val peso_real:  Long,
        val mascara: String,
        val tipo_archivo: String,
        val ult_mod: String
)



data class DataListAccionesMovil(
        val data:List<Acciones_List>
)

data class Acciones_List(
        val id_accion_id: Int,
        val comando: String

)


data class Negocios_Monitoreables_list(
        val nombre: String,
        val hardware_key: String,
        val id_accion: Int

) {

    override fun toString(): String {
        return nombre
    }
}


data class DataStatusEncendidoEquipo(
        val data:String
)

data class DataCatNegocio_List(
        val data:List<CatNegocio_List>
)




data class CatNegocio_List(
        val nombre: String,
        val id: Int



)


data class Cat_Negocios_list(
        val nombre: String,
        val id: String

) {

    override fun toString(): String {
        return nombre
    }
}



data class DataDirMacEquipo(
        val data:List<RegDirMac_Equipo>
)

data class RegDirMac_Equipo(
        val adaptador: String,
        val dir_mac: String

)


data class Unidades_Backup_DB_list(
        val unidad: String,
        val id: Int

) {

    override fun toString(): String {
        return unidad
    }
}



data class Equipos_Monitoreables_list(
        val id_equipo: Int,
        val nombre: String,
        val hardware_key: String

) {

    override fun toString(): String {
        return nombre
    }
}


data class DataListEntornos(
        val ientorno: String
)



data class DataUsuariosPermitidosEjecucion(
        val data:List<uariosPermitidosEjecucion_list>
)


data class uariosPermitidosEjecucion_list(
        var id_usuario_activo_id: String,
        var estatus_act: String

)


data class DataApiExtResult(
        val data:List<apiExtParametros_list>
)


data class apiExtParametros_list(
        val id: Int = 0,
        val titulo_aplicacion: String,
        val consumo_api_ext_base_url: String?,
        val consumo_api_ext_request_method: String?,
        val consumo_api_ext_auth_type: String?,
        val consumo_api_ext_api_key: String?,
        val consumo_api_ext_access_token: String?,
        val consumo_api_ext_refresh_token: String?,
        val consumo_api_ext_headers: String?,
        val consumo_api_ext_query_params: String?,
        val consumo_api_ext_client_id: String?,
        val consumo_api_ext_client_secret: String?,
        val consumo_api_ext_timeout: Int?

)




data class Negocios_parconexion_List(
        val nombre: String,
        val ip_publica: String,
        val ip_local: String,
        val puerto_local: Int,
        val puerto: Int
)




data class ParametrosAccDataAll(
        var data: ArrayList<ParametrosAccAll> = arrayListOf()
)

data class ParametrosAccAll(
        var id_accion: Int? = null,
        var campo: String? = null,
        var valor: String? = null,
        var tipo: String? = null,
        var base_datos_query: String? = null,
        var consumo_api_ext_query_params: String? = null
)




data class DataUsuariosPermitidosRecAlertasMovil(
        val data:List<uariosPermitidosRecAlertasMovil_list>
)


data class uariosPermitidosRecAlertasMovil_list(
        var id: Int? = null,
        var usuario: String? = null,
        var equipo: Int? = null,
        var nombre: String? =  null,
        var estatus_act: String? = null,
        var alerta: Int? = null
)






data class MovilUsuPermitidosRecAlertasMovil_list(
        var id: Int? = null,
        var usuario: String? = null,
        var equipo: Int? = null,
        var nombre: String? =  null,
        var estatus_act: String? = null,
        var alerta: Int? = null,
        var alerta_movil: Int? = null
)