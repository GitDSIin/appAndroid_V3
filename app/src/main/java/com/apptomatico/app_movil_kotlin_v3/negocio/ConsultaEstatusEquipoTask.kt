package com.apptomatico.app_movil_kotlin_v3.negocio


import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.model.*
import com.apptomatico.app_movil_kotlin_v3.servicios.ResAsyncEngine
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine.Companion.getRestEngine

import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ConsultaEstatusEquipoTask(databaseHelper_Data: DatabaseHelper, context: Context, id_movil: Int, progressbartop: ProgressBar) {
    var databaseHelper_Data = databaseHelper_Data
    var context = context
    var values: List<com.apptomatico.app_movil_kotlin_v3.model.Negocios_List> = ArrayList()
    var id_movil = id_movil
    var progressbartop = progressbartop



    fun getAllEquipos(){

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        var Movil_Id = id_movil

        var ordenamiento_equipos = databaseHelper_Data.getOrdenamientoEquipos()


        val userTknService: APIService = ResAsyncEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val resultTkn: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userTknService.submit(UserData)
        resultTkn.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Log.i("ACTUALIZA-EQUIPOS","Error de conexion Portal de Administracion")
                progressbartop!!.visibility = View.GONE
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, responsetk: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultadotk= responsetk.body()

                if (resultadotk != null) {
                    val token = resultadotk.token.toString()

                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                    val result: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios> = negociosService.getListNegocios("$Movil_Id", "0", ordenamiento_equipos,"JWT $token","application/json")


                    result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios>, t: Throwable) {
                            Log.i("ACTUALIZA-EQUIPOS","No existen negocios relacionados a este dispositivo")
                            progressbartop.visibility = View.GONE
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios>) {
                            val resultadoequipos= response.body()
                            if (resultadoequipos != null) {
                                values=resultadoequipos.data.toList()



                                databaseHelper_Data.delControlEquipos()
                                for (i in 0 until values.count()){

                                    databaseHelper_Data.addControlEquipos(values[i].id_negocio_id, values[i].ip_publica, values[i].ip_local, values[i].puerto, values[i].hardware_key, values[i].tipo_eq_icono_on, values[i].tipo_eq_icono_off, values[i].nombre, values[i].id_catnegocio, values[i].id_accion_monitoreable.toString())

                                    Log.i("ACTUALIZA-EQUIPOS","Se inserta Equipos")

                                    databaseHelper_Data.updateEquipos(values[i])
                                    databaseHelper_Data.addEquipos(values[i])
                                    databaseHelper_Data.addEquiposTipoLIc(values[i].id_negocio_id, values[i].unimovil!!)
                                    databaseHelper_Data.updEquiposTipoLIc(values[i].id_negocio_id, values[i].unimovil!!)
                                }





                                val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                                val resultAcciones: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas> = negociosService.getMovilAccionesValidasXNegocio("$Movil_Id", 0,"JWT $token","application/json")
                                resultAcciones.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas> {
                                    override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>, t: Throwable) {
                                        Log.i("ACTUALIZA-EQUIPOS","No fue posible recuperar acciones del Movil")
                                        progressbartop!!.visibility = View.GONE

                                    }

                                    override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>) {
                                        val resultado= response.body()
                                        if (resultado != null) {

                                            var valAccionesDinamicas=resultado.data.filter { it -> it.grupo == 1 || it.grupo == 2 }.toList()


                                            for(acc in valAccionesDinamicas){
                                                var clsAccDm: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List = acc
                                                var existe = databaseHelper_Data.CheckExisteAccionesDinamicas(clsAccDm.id, clsAccDm.id_negocio_id)
                                                if(!existe){
                                                    databaseHelper_Data.addAccionesDinamicas(acc)
                                                    Log.i("ACTUALIZA-EQUIPOS","Se inserta Acciones")
                                                }else{
                                                    databaseHelper_Data.updAccionesDinamicas(acc)
                                                    Log.i("ACTUALIZA-EQUIPOS","Se actualiza Acciones")
                                                }

                                            }

                                            progressbartop!!.visibility = View.GONE




                                        }else{
                                            progressbartop.visibility = View.GONE
                                        }



                                    }

                                })




                            }else{
                                progressbartop.visibility = View.GONE
                            }


                        }

                    })


                }else{
                    progressbartop!!.visibility = View.GONE
                }

            }

        })




    }




}