package com.apptomatico.app_movil_kotlin_v3.model

data class Licencia(val id: Int = -1, val hardware_key: String, val licencia_aceptacion: String, val id_movil: Int, val nommbre_movil: String, val nombre_dominio: String, val version_software: String)

data class Seguridad(val id: Int = -1, val nip: String)


data class DataRegLicecnia(
        val data:List<RegLicenacia_List>
)

data class RegLicenacia_List(
        val hardware_key: String,
        val intentos_fallidos: Int,
        val intentos_permitidos: Int
)


data class updRegLicencia(
        val ultimo_acceso: String,
        val ult_latitud: String,
        val ult_longitud: String


)


data class RegLicencia(
        val id: Int,
        val intentos_fallidos: Int,
        val estatus: Boolean,
        val ultimo_acceso: String,
        val hardware_key: String
)




data class DataHorariosMovil(
        val data:List<RegHorarios_Movil>
)

data class RegHorarios_Movil(
        val id: Int,
        val horario_desde: String,
        val horario_hasta: String,
        val dia_semana: Int,
        val ult_latitud: String,
        val ult_longitud: String,
        val rango_metros: Double
)


data class DataCambiosVersion(
        val data:List<DetCambiosVersion>
)

data class DetCambiosVersion(
        val pragma_key_base_negocio: String
)






data class DataVersionSoftwareMoviles(
        val data:List<RegVersion_Movil>
)

data class RegVersion_Movil(
        val ultima_version: String

)

data class DataResVigenciaMovil(
        val data:String
)


data class strValidacionAcesoNip(
        var intentos_restantes: Int,
        var esValido: Boolean

)