package com.apptomatico.app_movil_kotlin_v3.model

data class DataListMonitor(
        val data:List<Monitor_List>
)

data class Monitor_List(
        val id_proceso: String,
        val descripcion: String,
        val fec_consulta: String


)


data class RendimientoDetList(
        val data:List<Rendimiento_Deatlle_Grefico_List>
)

data class Rendimiento_Deatlle_Grefico_List(
        val negocio_id: String,
        val nombre: String,
        val rendimiento_actual: String
)

data class MonitoresDetList(
        val data:List<Monitor_Deatlle_Grefico_List>
)


data class Monitor_Deatlle_Grefico_List(
    val id_proceso: String,
    val descripcion: String,
    val porcentaje_uso: String,
    val espacio_total: String,
    val espacio_libre: String,
    val paquetes_enviados: String,
    val paquetes_recibidos: String,
    val Red: List<RedEstatusServer>,
    val fec_consulta: String
)


data class DataListMonitoreables(
        val data:List<Monitoreables_List>
)

data class Monitoreables_List(
        val id: Int,
        val id_negocio_id: Int,
        val ip_publica: String,
        val hardware_key: String,
        val puerto: Int,
        val comando: String,
        val monitoreo_cpu: Int,
        val monitoreo_disco: Int,
        val monitoreo_memoria: Int,
        val monitoreo_red: Int,
        val origen: Int,
        val nombre: String,
        val monitoreo_unidad_disco: String,
        val monitoreo_unidad_memoria: String,
        val ip_local: String,
        val puerto_local: Int


)


data class MonitoreablesCarrousel_List(
        val id: Int,
        val id_negocio_id: Int,
        val ip_publica: String,
        val hardware_key: String,
        val puerto: Int,
        val comando: String,
        val monitoreo_cpu: Int,
        val monitoreo_disco: Int,
        val monitoreo_memoria: Int,
        val monitoreo_red: Int,
        val origen: Int,
        val nombre: String,
        val monitoreo_unidad_disco: String,
        val monitoreo_unidad_memoria: String,
        val tipo_grafico: String,
        val nombre_negocio: String,
        val hardware: String,
        val porcentaje: String,
        val metrica_libre: String,
        val metrica_total: String,
        val usoRed: List<RedEstatusServer>?


)

data class RedEstatusServer(
        val nombre: String,
        val enviados: String,
        val recibidos: String

)