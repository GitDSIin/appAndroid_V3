package com.apptomatico.app_movil_kotlin_v3.Runnables

import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.MainActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_eliminadas_estatus
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_eliminadas_fecha
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_eliminadas_fecha_desde_hasta
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_equipo
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_estatus
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_fecha
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_fecha_desde_hasta
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_equipos
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_negocio
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.model.ParametrosAccDataAll
import com.apptomatico.app_movil_kotlin_v3.negocio.ChildFragment
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.gson.Gson
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class ConsultaInfoEquiposThread(databaseHelper_Data: DatabaseHelper, context: Activity, id_movil: Int, progressbartop: ProgressBar, fragmentoOrigen: String): Runnable {
    var databaseHelper_Data = databaseHelper_Data
    var context = context
    val fragmentoOrigen = fragmentoOrigen
    var values: List<com.apptomatico.app_movil_kotlin_v3.model.Negocios_List> = ArrayList()
    var id_movil = id_movil
    var progressbartop = progressbartop
    var equipoConInternet: Boolean = false
    var dominio_actual = ""


    override fun run() {
        if (LoginActivity.accionejecutandose){
            return
        }
        if(NegocioFragment.vwModalActiva){
            return
        }
        var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }

        var ordenamiento_equipos = databaseHelper_Data.getOrdenamientoEquipos()
        context.runOnUiThread {
            databaseHelper_Data.updateEstadoInactivoEquipos()
            databaseHelper_Data.updateEstadoInactivoAccionesEquipos()

            //REGISTRA EN LA BASE LOCAL LOS ULTIMOS CAMBIOS REALIZADOS EN EL PERFIL DE ENCANEZADO Y FILTROS DEL USUARIO
            if (!databaseHelper_Data.chkPerfilFiltroEncabezado()){
                databaseHelper_Data.addPerfilFiltroEncabezado(header_id_negocio, header_id_equipos,filtro_alertas_estatus,filtro_alertas_equipo, filtro_alertas_fecha,
                    filtro_alertas_fecha_desde_hasta, filtro_alertas_eliminadas_estatus, filtro_alertas_eliminadas_fecha,filtro_alertas_eliminadas_fecha_desde_hasta )
            }else{
                databaseHelper_Data.updPerfilFiltroEncabezado(header_id_negocio, header_id_equipos,filtro_alertas_estatus,filtro_alertas_equipo, filtro_alertas_fecha,
                    filtro_alertas_fecha_desde_hasta, filtro_alertas_eliminadas_estatus, filtro_alertas_eliminadas_fecha,filtro_alertas_eliminadas_fecha_desde_hasta )

            }

            if (fragmentoOrigen == "InicioEquiposFragment" && fragmentoOrigen == "tgEquiposUnicamenteFragment"){
                progressbartop!!.visibility = View.VISIBLE
            }


        }

        val client2 = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()


        val formBody = FormBody.Builder()
            .add("email", "licencias@dsiin.com")
            .add("password", "xhceibEd")
            .build()

        val requestToken = Request.Builder()
            .url("${dominio_actual}/token-auth/")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()


        val myTrace: Trace = FirebasePerformance.getInstance().newTrace("trace_rnb_info_quipo")
        myTrace.start()


        client2.newCall(requestToken).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                myTrace.stop()
                context.runOnUiThread {
                    if (fragmentoOrigen == "InicioEquiposFragment" && fragmentoOrigen == "tgEquiposUnicamenteFragment"){
                        progressbartop!!.visibility = View.GONE
                    }


                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.code == 200) {
                    val resStr = response.body!!.string()
                    var gson = Gson()
                    var mtoken: com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem? = gson?.fromJson(resStr, com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem::class.java)

                    Log.i("TOKEN", mtoken!!.token)
                    //#CORECCION DE ERROR DATABASE
                    SQLiteDatabase.loadLibs(context)
                    var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
                    var Movil_Id = Licenciadb[0].id_movil

                    val request = Request.Builder()
                        .url("${dominio_actual}/api/list_moviles_negocios/?id_movil=$Movil_Id&id_negocio=0&ls_ordenamiento=$ordenamiento_equipos")
                        .header("Authorization", "JWT ${mtoken.token}")
                        .header("Content-Type", "application/json")
                        .build()

                    val myTraceDet: Trace = FirebasePerformance.getInstance().newTrace("trace_rnb_info_quipo_det")
                    myTraceDet.start()


                    client2.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: okhttp3.Call, e: IOException) {
                            myTraceDet.stop()
                            context.runOnUiThread {
                                if (fragmentoOrigen == "InicioEquiposFragment" && fragmentoOrigen == "tgEquiposUnicamenteFragment"){
                                    progressbartop!!.visibility = View.GONE
                                }


                            }
                        }

                        override fun onResponse(call: okhttp3.Call, response: Response) {
                            if (response.isSuccessful && response.code == 200) {
                                try{
                                    val resData = response.body!!.string()
                                    var gsonData = Gson()
                                    var mMineUserEntity: com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios? = gsonData?.fromJson(resData, com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios::class.java)
                                    values=mMineUserEntity!!.data.toList()





                                    databaseHelper_Data.delControlEquipos()
                                    for (i in 0 until values.count()){
                                        databaseHelper_Data.addControlEquipos(values[i].id_negocio_id, values[i].ip_publica, values[i].ip_local, values[i].puerto, values[i].hardware_key, values[i].tipo_eq_icono_on, values[i].tipo_eq_icono_off, values[i].nombre, values[i].id_catnegocio, values[i].id_accion_monitoreable.toString())
                                        databaseHelper_Data.updateEquipos(values[i])
                                        databaseHelper_Data.addEquipos(values[i])
                                        databaseHelper_Data.addEquiposTipoLIc(values[i].id_negocio_id, values[i].unimovil!!)
                                        databaseHelper_Data.updEquiposTipoLIc(values[i].id_negocio_id, values[i].unimovil!!)
                                        //#CORECCION DE ERROR DATABASE
                                        SQLiteDatabase.loadLibs(context)
                                        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
                                        var Movil_Id = Licenciadb[0].id_movil
                                        databaseHelper_Data.delEquiposNoAsignadosAMovil(Movil_Id)
                                        //SE ACTUALIZA TABLA DE API VM EN CASO DE EXISTIR CONFIGURACION
                                        values[i].gc_project_screenshot?.let { databaseHelper_Data.addControlEquiposAPIVM(values[i].id_negocio_id, "","","", "" ,it) }
                                        values[i].gc_project_screenshot?.let { databaseHelper_Data.updControlEquiposAPIVM(values[i].id_negocio_id, "","","", "" ,it) }
                                        Log.i("ACTUALIZA-EQUIPOS-EX","Se inserta Equipos")


                                    }


                                    val requestDet = Request.Builder()
                                        .url("${dominio_actual}/api/moviles_get_acciones_validas_x_negocio/?id_movil=$Movil_Id&id_negocio=0")
                                        .header("Authorization", "JWT ${mtoken.token}")
                                        .header("Content-Type", "application/json")
                                        .build()


                                    client2.newCall(requestDet).enqueue(object : Callback {
                                        override fun onFailure(call: Call, e: IOException) {
                                            context.runOnUiThread {
                                                if (fragmentoOrigen == "InicioEquiposFragment" && fragmentoOrigen == "tgEquiposUnicamenteFragment"){
                                                    progressbartop!!.visibility = View.GONE
                                                }

                                            }
                                        }

                                        override fun onResponse(call: Call, response: Response) {
                                            if (response.isSuccessful && response.code == 200) {
                                                val resDet = response.body!!.string()
                                                var gsonDet = Gson()
                                                var mEquiposDet: com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas?= gsonDet?.fromJson(resDet, com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas::class.java)
                                                var valAccionesDinamicas=mEquiposDet!!.data.filter { it -> it.grupo == 1 || it.grupo == 2  }.toList()

                                                for(acc in valAccionesDinamicas){
                                                    var clsAccDm: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List = acc
                                                    if (clsAccDm.id == 154){

                                                        println(clsAccDm.id)
                                                    }

                                                    var existe = databaseHelper_Data.CheckExisteAccionesDinamicas(clsAccDm.id, clsAccDm.id_negocio_id)
                                                    if(!existe){
                                                        databaseHelper_Data.addAccionesDinamicas(acc)
                                                        Log.i("ACTUALIZA-EQUIPOS","Se inserta Acciones")
                                                    }else{
                                                        databaseHelper_Data.updAccionesDinamicas(acc)
                                                        Log.i("ACTUALIZA-EQUIPOS","Se actualiza Acciones")
                                                    }


                                                }


                                                context.runOnUiThread {


                                                    if (fragmentoOrigen == "InicioEquiposFragment"){
                                                        ChildFragment.NegociosHomeAdapter!!.refreshFamiliasDB()
                                                        ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()
                                                        (context as MainActivity).getAllChildExercisesFromParentID()
                                                        progressbartop!!.visibility = View.GONE

                                                    }
                                                    if (fragmentoOrigen == "tgEquiposUnicamenteFragment"){
//                                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.refreshFamiliasDB()
//                                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.notifyDataSetChanged()
//                                                        (context as MainActivity).getAllChildExercisesFromParentID()
//                                                        progressbartop!!.visibility = View.GONE
                                                    }



                                                }

                                            }else{
                                                context.runOnUiThread {
                                                    if (fragmentoOrigen == "InicioEquiposFragment"){
                                                        ChildFragment.NegociosHomeAdapter!!.refreshFamiliasDB()
                                                        ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()
                                                        (context as MainActivity).getAllChildExercisesFromParentID()
                                                        progressbartop!!.visibility = View.GONE

                                                    }
                                                    if (fragmentoOrigen == "tgEquiposUnicamenteFragment"){
//                                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.refreshFamiliasDB()
//                                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.notifyDataSetChanged()
//                                                        (context as MainActivity).getAllChildExercisesFromParentID()
//                                                        progressbartop!!.visibility = View.GONE
                                                    }


                                                }
                                            }

                                        }

                                    })





                                }catch (ex: Exception){
                                    progressbartop!!.visibility = View.GONE
                                }



                            }else{
                                context.runOnUiThread {
                                    if (fragmentoOrigen == "InicioEquiposFragment"){
                                        ChildFragment.NegociosHomeAdapter!!.refreshFamiliasDB()
                                        ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()
                                        (context as MainActivity).getAllChildExercisesFromParentID()
                                        progressbartop!!.visibility = View.GONE

                                    }
                                    if (fragmentoOrigen == "tgEquiposUnicamenteFragment"){
//                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.refreshFamiliasDB()
//                                        NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.notifyDataSetChanged()
//                                        (context as MainActivity).getAllChildExercisesFromParentID()
//                                        progressbartop!!.visibility = View.GONE
                                    }


                                }
                            }
                            myTraceDet.stop()
                        }
                    })

                }else{
                    context.runOnUiThread {
                        if (fragmentoOrigen == "InicioEquiposFragment"){
                            ChildFragment.NegociosHomeAdapter!!.refreshFamiliasDB()
                            ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()
                            (context as MainActivity).getAllChildExercisesFromParentID()
                            progressbartop!!.visibility = View.GONE

                        }
                        if (fragmentoOrigen == "tgEquiposUnicamenteFragment"){
//                            NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.refreshFamiliasDB()
//                            NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.notifyDataSetChanged()
//                            (context as MainActivity).getAllChildExercisesFromParentID()
//                            progressbartop!!.visibility = View.GONE
                        }

                    }
                }
                myTrace.stop()
            }

        })


        //FUNCION PARA RECUPERAR Y ACTUALIZAR PARAMETROS DE ACCIONES
        ControlParametrosAcciones(client2)




    }



    private fun ControlParametrosAcciones(client:  OkHttpClient){
        val Equipos = databaseHelper_Data.getControlEquipos()
        if (Equipos.isNotEmpty()){

            var strEquiposId: String = ""
            for (i in 0 until Equipos.count()){
                strEquiposId += "${Equipos[i].equipo_id}|"
            }

            val request = Request.Builder()
                .url("${dominio_actual}/api/get_par_base_datos_all/?id_equipos=$strEquiposId")
                .header("Authorization", "JWT ${BuildConfig.TOKEN_MAESTRO}")
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.i("UPDPARAMACC", "${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.code == 200) {
                        val resStr = response.body!!.string()
                        var gson = Gson()
                        var mParams: ParametrosAccDataAll? = gson?.fromJson(resStr, ParametrosAccDataAll::class.java)
                        for (item in  mParams!!.data) {
                            if(!databaseHelper_Data.chkExistControlParamsAcc(item.id_accion!!, item.campo!!)){
                                databaseHelper_Data.addControlParamsAcc(item.id_accion!!,item.campo!!, item.valor!!, item.tipo!!, item.base_datos_query, item.consumo_api_ext_query_params)
                            }else{
                                databaseHelper_Data.updControlParamsAcc(item.id_accion!!,item.campo!!, item.valor!!, item.tipo!!, item.base_datos_query, item.consumo_api_ext_query_params)
                            }
                        }
                    }
                }

            })



        }

    }
}