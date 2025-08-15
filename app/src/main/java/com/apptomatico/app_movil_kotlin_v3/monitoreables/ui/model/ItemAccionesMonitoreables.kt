package com.apptomatico.app_movil_kotlin_v3.monitoreables.ui.model


class ItemAccionesMonitoreables{

    var id_movil: Int? = null
    var idaccion: Int?= null
    var origen: Int? = null
    var comando:String?=null
    var accion:String?=null
    var porcentaje: String? = null
    var total_usado: String? = null
    var espacio_libre: String? = null
    var paquetes_enviados: String? = null
    var paquetes_recibidos: String? = null
    var nomServidor: String? = null
    var redNegocio: List<com.apptomatico.app_movil_kotlin_v3.model.RedEstatusServer>? = null
    var esFavorito: Boolean? = null
    var fecUltAct: String? = null
    var idEquipo: Int? = null

    constructor(id_movil: Int, idaccion: Int, origen: Int, comando: String, accion: String, porcentaje: String, total_usado: String, espacio_libre: String, paquetes_enviados: String, paquetes_recibidos: String, nomServidor: String, redNegocio:List<com.apptomatico.app_movil_kotlin_v3.model.RedEstatusServer>?, esFavorito: Boolean, fecUltAct: String, idEquipo: Int){
        this.id_movil = id_movil
        this.idaccion = idaccion
        this.origen = origen
        this.comando = comando
        this.accion = accion
        this.porcentaje = porcentaje
        this.total_usado = total_usado
        this.espacio_libre = espacio_libre
        this.paquetes_enviados= paquetes_enviados
        this.paquetes_recibidos = paquetes_recibidos
        this.nomServidor = nomServidor
        this.redNegocio = redNegocio
        this.esFavorito = esFavorito
        this.fecUltAct = fecUltAct
        this.idEquipo = idEquipo
    }
}

