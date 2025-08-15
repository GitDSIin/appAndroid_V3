package com.apptomatico.app_movil_kotlin_v3

import android.app.Activity
import androidx.appcompat.app.AlertDialog

internal class loadingDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null
    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.layout_carga_universal, null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog!!.show()
    }

    fun dismisDialog() {
        try{
            alertDialog!!.dismiss()
        }catch (ex: Exception){

        }

    }

}