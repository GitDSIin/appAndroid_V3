package com.apptomatico.app_movil_kotlin_v3.model

class ItemParametrosAccDB {
    var campo: String? = null
    var valor:String?=null
    var tipo: String? = null

    constructor(campo: String?, valor: String?, tipo: String?){
        this.campo = campo
        this.valor = valor
        this.tipo = tipo
    }
}