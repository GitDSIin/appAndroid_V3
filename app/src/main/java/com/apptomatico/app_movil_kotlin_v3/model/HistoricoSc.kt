package com.apptomatico.app_movil_kotlin_v3.model

import android.graphics.Bitmap

data class Historico_Screenshot(
    val id: Int,
    val imagen: String,
    val ruta_imagen: String,
    val descripcion: String,
    val fecha_creacion: String,
    val hardware_equipo: String,
    val nombre_equipo: String



)


data class ControlHistoricoScreenshotData(val idImagen: Int, val idEquipo: Int, val nombreImagen: String, val  fechaCreacion: String, val urlImagen: String, val estatusImagen: Int){
    private var idImagen_: Int? = null
    private var isSelectedA = false

    fun ControlAlertasData(idImagen: Int) {
        idImagen_ = idImagen
    }

    fun getIdAlerta(): Int? {
        return idImagen_
    }

    fun setSelectedA(selected: Boolean) {
        isSelectedA = selected
    }


    fun isSelectedA(): Boolean {
        return isSelectedA
    }
}