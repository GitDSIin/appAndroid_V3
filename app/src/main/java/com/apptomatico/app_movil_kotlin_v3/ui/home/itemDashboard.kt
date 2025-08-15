package com.apptomatico.app_movil_kotlin_v3.ui.home

class ItemAcciones{
    var id_accion: Int? = null
    var accion:String?=null
    var imagen: String? = null
    var tipo: String? = null
    var comando: String? = null
    var id_Equipo: String? = null
    var nomServidor: String? = null
    var favorita: Boolean? = null

    constructor(id_accion: Int,accion: String, imagen: String, tipo: String, comando: String, id_Equipo: String, nomServidor: String, favorita: Boolean){
        this.id_accion = id_accion
        this.accion = accion
        this.imagen = imagen
        this.tipo = tipo
        this.comando = comando
        this.id_Equipo = id_Equipo
        this.nomServidor = nomServidor
        this.favorita = favorita
    }
}