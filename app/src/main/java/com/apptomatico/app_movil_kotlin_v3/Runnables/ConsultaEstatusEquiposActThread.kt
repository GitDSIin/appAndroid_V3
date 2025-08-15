package com.apptomatico.app_movil_kotlin_v3.Runnables

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.MemoryUtilsMail
import com.apptomatico.app_movil_kotlin_v3.MyConnectivityManager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.accionejecutandose
import com.apptomatico.app_movil_kotlin_v3.model.ControlEquiposXMovil
import com.apptomatico.app_movil_kotlin_v3.model.ControlRedMovil
import com.apptomatico.app_movil_kotlin_v3.model.NewWebSocketEstatusAll
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment.Companion.vwModalActiva
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.concurrent.TimeUnit

class ConsultaEstatusEquiposActThread(databaseHelper_Data: DatabaseHelper, context: Activity, framrNegocioList: FrameLayout, allEquipos: List<ControlEquiposXMovil>, dominio_actual: String?, modelo_comunicacion: Int?, webSocketService: EchoService?, idDispositivo: String?, fragmentoOrigen: String, imyConnectivityManager: MyConnectivityManager): Runnable {
    var databaseHelper_Data = databaseHelper_Data
    var context = context
    var framrNegocioList = framrNegocioList
    var allEquipos: List<ControlEquiposXMovil> = allEquipos
    var dominioactual: String? = dominio_actual
    var fragmentoOrigen = fragmentoOrigen
    var ipPublica: String? = null
    var iPuerto: String? = null
    var idHardware: String? = null
    var equipoid: Int? = null
    var equipoinconoon: String? = null
    var equipoinconooff: String? = null
    var iplocal: String? = null
    var nombreequipo: String? = null
    var myConnectivityManager = imyConnectivityManager
    var ivalues: List<ControlRedMovil>? = null

    var idDispositivo: String? = idDispositivo
    var modelo_comunicacion: Int? = modelo_comunicacion
    var webSocketService: EchoService? = webSocketService
    var imageLogo = ""
    var lblUltAct: TextView? = null
    var lblUltHoraAct: TextView? = null
    var imgLogo: ImageView? = null
    var imgAyuda: ImageView? = null
    var imgUnimovil: ImageView? = null
    var imgCloudEquipo:ImageView? = null
    var imgApiEquipo:ImageView? = null

    var imgFisicoEquipo:ImageView? = null
    var imgvMEquipo:ImageView? = null

    var equipoActivo: Boolean = false
    var dominio_actual = ""

    @RequiresApi(Build.VERSION_CODES.N)
    override fun run() {

        if (fragmentoOrigen != "InicioEquiposFragment"){
            return
        }

        if (accionejecutandose){
            return
        }

        if(vwModalActiva){
            return
        }

        if (fragmentoOrigen != "InicioEquiposFragment"){
            return
        }

        val connectionState =  myConnectivityManager.connectionAsStateFlow.value
        ConnectivityUiView(connectionState)

        context.runOnUiThread {
            MemoryUtilsMail.checkMemoryUsageAndSendAlert("Consulta Etatus Equipo", databaseHelper_Data)
        }

        Log.i("ESTATUS_EQUIPO", "FInaliza consulta estatus equipo")

    }





    fun ConnectivityUiView(isOnline: Boolean) {
        if(isOnline){
            validaConexionEquipo()
        }else{
            encendido(framrNegocioList, false)
        }
    }



    fun validaConexionEquipo(){
        var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
        var  svrWebSocket = ""

        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            svrWebSocket =  BuildConfig.DOMINIO_PORTAL
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            svrWebSocket =  BuildConfig.DOMINIO_PORTAL
        }

        var strEquiposId: String = ""
        for (i in 0 until allEquipos.count()){
            strEquiposId += "${allEquipos[i].equipo_id}|"
        }

        val client2 = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()


        try{

            val request = Request.Builder()
                .url("${dominio_actual}/api/estatus_equipos_socket_all/?id_equipos=$strEquiposId")
                .header("Authorization", "JWT ${BuildConfig.TOKEN_MAESTRO}")
                .header("Content-Type", "application/json")
                .build()


            val myTrace: Trace = FirebasePerformance.getInstance().newTrace("trace_rnb_estatus_equipo")
            myTrace.start()
            client2.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("ESTATUS_EQUIPO", "Error al recuperar listado de equipos activos")
                    myTrace.stop()
                    databaseHelper_Data.addControlServerSocketLog("ConsultaEstatusEquiposActThead: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful && response.code == 200) {
                        val resStr = response.body!!.string()
                        var gson = Gson()
                        var mtoken: NewWebSocketEstatusAll? = gson?.fromJson(resStr, NewWebSocketEstatusAll::class.java)

                        for (item in mtoken!!.data) {
                            val modeloComunicacion = databaseHelper_Data.getEquipoTipoComunicacion(item.negocio_id!!)
                            ivalues =  databaseHelper_Data.getConexionRed()
                            var filterAllEquipos = allEquipos.filter { s -> s.equipo_id ==  item.negocio_id!! }

                            ipPublica = filterAllEquipos[0].ip_publica
                            iPuerto = filterAllEquipos[0].puerto.toString()
                            idHardware = filterAllEquipos[0].id_hardware
                            equipoid = filterAllEquipos[0].equipo_id
                            equipoinconoon = filterAllEquipos[0].icono_on
                            equipoinconooff = filterAllEquipos[0].icono_off
                            iplocal = filterAllEquipos[0].ip_local
                            nombreequipo = filterAllEquipos[0].nombre_equipo

                            if (item.estatus_chat == "online"){
                                if(databaseHelper_Data.getEstadoEquipo(equipoid!!) == "offline"){
                                    updatesas(false, equipoid!!)
                                }else{
                                    updatesas(true, equipoid!!)
                                }
                            }else{
                                if(modeloComunicacion == 0){
                                    try{
                                        val url = URL("http://${ipPublica}:${iPuerto}/")
                                        val huc: HttpURLConnection = url.openConnection() as HttpURLConnection
                                        huc.connectTimeout = 5000
                                        val responseCode = huc.responseCode
                                        if(responseCode  == 200){
                                            updatesas(true, equipoid!!)
                                        }else{
                                            updatesas(false, equipoid!!)
                                        }
                                    }catch (ex: Exception){
                                        updatesas(false, equipoid!!)
                                    }
                                }else{
                                    Log.i("PPSDFG", item.negocio_id.toString())
                                    updatesas(false, equipoid!!)
                                }
                            }
                        }



                    }else{
                        Log.e("ESTATUS_EQUIPO", "Error al recuperar listado de equipos activos")
                        encendido(framrNegocioList, false)

                    }
                    myTrace.stop()
                }

            })












        }catch (ex: SocketTimeoutException){
            Log.e("ESTATUS_EQUIPO", "Error al conectar con socket durante  recuperar listado de equipos activos ${ex.message}")
            databaseHelper_Data.addControlServerSocketLog("ConsultaEstatusEquiposActThead-2: ${ex.message}")
            encendido(framrNegocioList, false)


        } catch (e: IOException) {
            Log.e("ESTATUS_EQUIPO", "Error al recuperar listado de equipos activos ${e.message}")
            databaseHelper_Data.addControlServerSocketLog("ConsultaEstatusEquiposActThead-3: ${e.message}")
            encendido(framrNegocioList, false)


        }catch (e: Exception) {
            Log.e("ESTATUS_EQUIPO", "Error al recuperar listado de equipos activos ${e.message}")
            databaseHelper_Data.addControlServerSocketLog("ConsultaEstatusEquiposActThead-3: ${e.message}")
            encendido(framrNegocioList, false)


        }

    }








    private fun updatesas(result: Boolean, equipoid: Int){


        val cardChlEquipo = getViewsByTag(framrNegocioList, "${equipoid}9999")
        var cardEquipo: CardView? = null
        if (cardChlEquipo  != null) {
            if (cardChlEquipo .isNotEmpty()){
                cardEquipo = cardChlEquipo[0] as CardView
            }
        }
        if (result!!) {

            val existeequipo: Boolean = databaseHelper_Data.CheckExisteControlEquipo(equipoid!!)
            if (existeequipo) {
                databaseHelper_Data.updControlEstadoEquipos(equipoid!!, "Activo", nombreequipo!!, idHardware!!, ipPublica!!, iplocal!!, iPuerto!!)
            } else {
                databaseHelper_Data.addControlEstadoEquipos(equipoid!!, "Activo", nombreequipo!!, idHardware!!, ipPublica!!, iplocal!!, iPuerto!!)
            }


            var estEquipAct = databaseHelper_Data.getControlEstadoEquipos()
            if (estEquipAct != null) {
                var fltEqAct =
                    estEquipAct.filter { s -> s.equipo_id == equipoid!! && s.estado_equipo == "Activo" }
                if (fltEqAct.isNotEmpty()) {

                    if (cardEquipo != null){

                        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                        val currentDateAndTime: String  =  simpleDateFormat.format(Date())
                        var fecUltAct = currentDateAndTime.split(" ")
                        databaseHelper_Data.addInfoNegocio(1, currentDateAndTime, equipoid!!)
                        databaseHelper_Data.updateInfoNegocio(1,currentDateAndTime, equipoid!!)

                        val cardChlFecUpdEquipo = getViewsByTag(framrNegocioList, "${equipoid}842644946549")
                        var UltActEquipo: TextView? = null
                        if (cardChlFecUpdEquipo  != null){
                            if (cardChlFecUpdEquipo.isNotEmpty()){
                                UltActEquipo = cardChlFecUpdEquipo[0] as TextView
                                context.runOnUiThread {
                                    UltActEquipo.text = "(Ult.Act: " + fecUltAct[0] + " "
                                }
                            }
                        }

                        val cardHoraActUpdEquipo = getViewsByTag(framrNegocioList, "${equipoid}8426449465492")
                        var HoraActEquipo: TextView? = null
                        if (cardHoraActUpdEquipo  != null){
                            if (cardHoraActUpdEquipo.isNotEmpty()){
                                HoraActEquipo = cardHoraActUpdEquipo[0] as TextView
                                context.runOnUiThread {
                                    HoraActEquipo.text = fecUltAct[1] + ")"
                                }
                            }
                        }


                        encendido(cardEquipo, true)
                    }


                }else{
                    if (cardEquipo != null) {
                        encendido(cardEquipo, false)
                    }

                }
            }
        }else{
            databaseHelper_Data.delControlEstadoEquiposXID(equipoid!!)
            if (cardEquipo != null) {
                encendido(cardEquipo, false)
            }

        }
    }

    private fun getViewsByTag(root: ViewGroup, tag: String): java.util.ArrayList<View>? {
        val views = ArrayList<View>()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            val child = root.getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(getViewsByTag(child, tag)!!)
            }
            if(child != null) {
                val tagObj = child.tag
                if (tagObj != null && tagObj == tag) {
                    views.add(child)
                }
            }

        }
        return views
    }

    fun encendido(viewGroup:ViewGroup?, enabled: Boolean){

        val childCount: Int = viewGroup?.childCount!!
        for (i in 0 until childCount) {
            val view: View = viewGroup.getChildAt(i)
            context.runOnUiThread {

                if(view is ImageView){
                    if(enabled){
                        view.clearColorFilter()
                        view.isClickable = enabled
                    }else{
                        view.setColorFilter(context.resources.getColor(R.color.dg_gray))
                        view.isClickable = enabled
                    }
                }
            }
            if (view is ViewGroup) {
                encendido(view, enabled)
            }
        }
    }

}