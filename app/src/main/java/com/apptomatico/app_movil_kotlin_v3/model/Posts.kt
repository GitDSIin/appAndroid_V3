package com.apptomatico.app_movil_kotlin_v3.model

class Posts : ArrayList<UserDataCollectionItem>()




data class UserDataCollectionItem(
        val token: String,
        val user: Usuario


)



data class Usuario(

        val username: String,
        val email: String,
        val id: Int

)

data class UserDataRequest (
        val email: String,
        val password: String
)


//MODELO LICENCIA VALIDACION MOVIL


data class DataValLicencia(
        val data:List<ModelLicencia>
)

data class ModelLicencia(
        val id: Int,
        val hardware_key: String,
        val ip_publica: String,
        val estatus: Int,
        val licencia_aceptacion: String,
        val fecha_desde: String,
        val fecha_hasta: String,
        val movil_id: Int,
        val negocio_id: Int,
        val nom_movil: String,
        val sistema_operativo: Int,
        val puerto: Int,
        val fec_act_apps_negocio: Int,
        val consulta_act: Int,
        val ip_local: String,
        val alias_movil: String,
        val requiere_ubicacion: String,
        val scroll_alertas: Int,
        val dias_vigencia_x_vencer: Int,
        val intervalo_vigencia_x_vencer: String,
        val svr_web_socket: String,
        val svr_puerto_web_socket: String,
        val conf_movil_alertas: Int,
        val conf_movil_alertas_recientes: Int,
        val conf_movil_monitores: Int,
        val conf_movil_estatus_equipos: Int,
        val conf_movil_info_equipos: Int,
        val omite_certificados: Int




)



data class DataValLicenciaMovil(
        val data:List<ModelLicenciaMovil>
)

data class ModelLicenciaMovil(
        val id: Int,
        val hardware_key: String,
        val ip_publica: String,
        val estatus: Int,
        val licencia_aceptacion: String,
        val fecha_desde: String,
        val fecha_hasta: String,
        val movil_id: Int,
        val negocio_id: Int,
        val nom_movil: String,
        val dominio_portal: String,
        val ftpbackup: String,
        val requiere_ubicacion: Int,
        val movil_longitud_min_nip: Int,
        val movil_longitud_max_nip: Int


)


//Neogcios





data class DataUserDispositivo(
        val data:List<UserDispositivo>
)

data class UserDispositivo(
        val id: Int,
        val tipo_async: Int,
        val id_suscriptor: Int

)

data class UserDispositivoData (
        val id: Int,
        val dispositivo: Int,
        val mac: String,
        val ip: String
)

data class UserUpdDisp(
        val pk: Int,
        val tipo_async: Int

)

//Suscripcion

//Respuesta de la validacion del suscriptor

data class SubscriptorData(

    val data:List<Suscriptor>,
    val count: Int,
    val numpages: Int,
    val nextlink: String,
    val prevlink: String

)

data class Suscriptor(

        val pk:  Int,
        val nombre: String,
        val paterno: String,
        val materno: String,
        val correo: String,
        val nombre_negocio: String,
        val razon_social: String,
        val giro: String,
        val pwd: String,
        val db_docker: String,
        val estatus: Boolean,
        val fecha_alta: String

)

data class SuscriptorAlta(
        val password: String,
        val username: String,
        val mail: String

)

data class RespuestaAltaSub(
        val token: String,
        val username: String,
        val id: Int

)


