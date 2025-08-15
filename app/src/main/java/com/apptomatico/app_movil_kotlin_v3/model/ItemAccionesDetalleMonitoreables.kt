package com.apptomatico.app_movil_kotlin_v3.model

class ItemAccionesDetalleMonitoreables {
    var idEquipo: Int? = null
    var accion:String?=null
    var porcentaje: String? = null
    var total_usado: String? = null
    var espacio_libre: String? = null
    var paquetes_enviados: String? = null
    var paquetes_recibidos: String? = null
    var fecUltAct: String? = null


    constructor(idEquipo: Int, accion: String, porcentaje: String, total_usado: String, espacio_libre: String, paquetes_enviados: String, paquetes_recibidos: String, fecUltAct: String){
        this.idEquipo = idEquipo
        this.accion = accion
        this.porcentaje = porcentaje
        this.total_usado = total_usado
        this.espacio_libre = espacio_libre
        this.paquetes_enviados= paquetes_enviados
        this.paquetes_recibidos = paquetes_recibidos
        this.fecUltAct = fecUltAct

    }
}