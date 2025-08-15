package com.apptomatico.app_movil_kotlin_v3.Sockets.Services

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings
import android.util.Base64
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.apptomatico.app_movil_kotlin_v3.MainActivity
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.android.schedulers.AndroidSchedulers
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.toString
import com.tinder.scarlet.Message as MessageScarlet

class WebSocketServerConn(databaseHelper_Data: DatabaseHelper?, context: Activity, framrNegocioList: FrameLayout?, linearLayoutAccFav: LinearLayout?, webSocketService: EchoService?, dominio_actual: String?, idDispositivo: String?) {

    var databaseHelper_Data = databaseHelper_Data
    var iSocketService: EchoService? =  webSocketService
    var context = context
    var equipoid: Int? = null
    var imageLogo = ""
    var framrNegocioList = framrNegocioList
    var linearLayoutAccFav = linearLayoutAccFav
    var dominioactual: String? = dominio_actual
    var equipoinconoon: String? = null
    var equipoinconooff: String? = null
    var iplocal: String? = null
    var nombreequipo: String? = null
    var ipPublica: String? = null
    var iPuerto: String? = null
    var idHardware: String? = null
    var lblUltAct: TextView? = null
    var lblUltHoraAct: TextView? = null
    var imgLogo: ImageView? = null
    var imgAyuda: ImageView? = null
    var imgUnimovil: ImageView? = null
    var imgCloudEquipo: ImageView? = null
    var imgApiEquipo: ImageView? = null

    var imgFisicoEquipo:ImageView? = null
    var imgvMEquipo:ImageView? = null

    var idDispositivo: String? = idDispositivo


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CheckResult")
    fun observeConnection() {
        iSocketService!!.observeConnection()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                Log.d("observeConnection", response.toString())

                onReceiveResponseConnection(response)
            }, { error ->
                Log.e("observeConnection", error.message.orEmpty())
                // Snackbar.make(binding.root, error.message.orEmpty(), Snackbar.LENGTH_SHORT).show()
            })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun onReceiveResponseConnection(response: WebSocket.Event) {
        when (response) {
            is WebSocket.Event.OnConnectionOpened<*> -> changeToolbarTitle("Conexión abierta")
            is WebSocket.Event.OnConnectionClosed -> changeToolbarTitle("Conexión cerrada")
            is WebSocket.Event.OnConnectionClosing -> changeToolbarTitle("Cerrando conexión..")
            is WebSocket.Event.OnConnectionFailed -> changeToolbarTitle("Conexión fallida")
            is WebSocket.Event.OnMessageReceived -> handleOnMessageReceived(response.message)

        }
    }




    private fun changeToolbarTitle(title: String) {
        Log.i("ESTADO_EQUIPO", title)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleOnMessageReceived(message: MessageScarlet) {
        //adapter.addItem(Message(message.toValue(), false))
        //Message(message.toValue(), false)
        var msg = message.toValue()

        if (msg.contains("Not Authorized")){
            return
        }


        var ctxNavFragment: NavHostFragment? =  (context as MainActivity).supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
        var iNegocioActivity: NegocioFragment? = null
//        var NegociosUnicamenteActivity: NegociosUnicamenteFragment? = null
//        var DashboardActivity: DashboardActivity? = null
//        var MonitoreablesActivity: MonitoreablesFragment? = null
//        var AlertasActivity: AlertasFragment? = null




        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
        when ( ctxFragment?.view?.tag) {
            "InicioEquiposFragment" -> iNegocioActivity = ctxFragment as NegocioFragment
//            "tgAlertasFragment" -> AlertasActivity = ctxFragment as AlertasFragment
//            "tgEquiposUnicamenteFragment" -> NegociosUnicamenteActivity = ctxFragment as NegociosUnicamenteFragment
//            "tgDashboardFragment" -> DashboardActivity = ctxFragment as DashboardActivity
//            "tgMonitoresFragment" -> MonitoreablesActivity = ctxFragment as MonitoreablesFragment
            else -> { // Note the block
                print("null")
            }
        }







//
//        if (msg.contains("REQ-$idDispositivo")){
//            val simpleDateFormat = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
//            val currentDateAndTime: String  =  simpleDateFormat.format(Date())
//
//            var respuesta = msg.split("|")
//            var msj = respuesta[1].replace("message", "")
//            msj = msj.replace("|", "")
//            msj = msj.replace("{", "")
//            msj = msj.replace("}", "")
//            msj = msj.replace(":", "")
//            msj = msj.replace("\"", "")
//            msj = msj.replace(" ", "")
//            Log.i("MESSAGE-RECEIVER", "$currentDateAndTime Mensaje recibido del equipo: $msj")
//            val Equipos = databaseHelper_Data!!.getControlEquiposXHardware(msj)
//            equipoid = Equipos[0].equipo_id
//            equipoinconoon = Equipos[0].icono_on
//            equipoinconooff = Equipos[0].icono_off
//            nombreequipo = Equipos[0].nombre_equipo
//            iplocal = Equipos[0].ip_local
//            ipPublica = Equipos[0].ip_publica
//            iPuerto = Equipos[0].puerto.toString()
//            idHardware = Equipos[0].id_hardware
//            databaseHelper_Data!!.addControlEquiposFecUltAct(equipoid!!,idHardware!!)
//            databaseHelper_Data!!.updControlEquiposFecUltAct(equipoid!!,idHardware!!)
//            // updateEstatusEquipos(true)
//        }
//
//
//
//
//        else if(msg.contains("movil") && msg.contains("setControlEquipo") && msg.contains("respuesta")){
//
//
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "controlequipo")
//                    if(mRespuesta!!.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "screenshot")
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "screenshot")
//                        var nomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        var respuesta = mRespuesta.message!!.respuesta!!.toString()
//                        Log.i("WS_CONTROL_EQUIPO", mRespuesta.message!!.respuesta!!)
//
//                        Toast.makeText(context,"$nomEquipo $respuesta", Toast.LENGTH_LONG).show()
//
//                    }
//                }
//            }
//
//
//
//
//
//        }else if(msg.contains("movil") && msg.contains("screenshot") && msg.contains("respuesta") && !msg.contains("chkUsuActivosWindows")){
//            LoginActivity.accionejecutandose = false
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "screenshot")
//                    if(mRespuesta!!.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "screenshot")
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "screenshot")
//                        var nomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        Log.i("WS_SCREENSHOT_EQUIPO", mRespuesta.message!!.respuesta!!)
//
//
//                        //if(NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                        //    NegociosUnicamenteActivity.stopProgressBarAccFav(mRespuesta.message!!.idequipo!!.toInt())
//                        //}
//
//                        //if(DashboardActivity!= null && DashboardActivity.isVisible){
//                        //    DashboardActivity.DisableProgressBarDasboard()
//                        //}
//
//
//
//                        val accountsIntent = Intent(context, ImageViewActivity::class.java)
//                        accountsIntent.putExtra("bovedaSoftware", "")
//                        accountsIntent.putExtra("id_hardware", idHardware)
//                        accountsIntent.putExtra("ip_puerto", "")
//                        accountsIntent.putExtra("ip_negocio", "")
//                        accountsIntent.putExtra("ip_local", "")
//                        accountsIntent.putExtra("nombre_negocio", nomEquipo)
//                        accountsIntent.putExtra("tipo_comunicacion", "1")
//                        accountsIntent.putExtra("url_imagen", "${mRespuesta.message!!.respuesta!!}")
//                        accountsIntent.putExtra("info", "${mRespuesta.message!!.info!!}")
//                        context.startActivity(accountsIntent)
//
//
//
//
//
//                    }
//                }
//            }
//
//        }else if(msg.contains("movil") && msg.contains("setImgScreenShotEquipo") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("\"[", "[")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("{\"message\":\"{", "{\"message\":{")
//                msg = msg.replace("\"}\"}", "\"}}")
//
//                var gson = Gson()
//
//                var mRespuesta: RespuestaWebSocketHistoricoSCImg? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocketHistoricoSCImg::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.idmovil!! == idDispositivo) {
//
//                    if(iNegocioActivity != null && iNegocioActivity.isVisible){
//                        NegocioFragment.iAlertDialogSc!!.dismiss()
//                    }
//
//                    if(NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                        iAlertDialogScUnic!!.dismiss()
//                    }
//
//                    if(DashboardActivity!= null && DashboardActivity.isVisible){
//                        iAlertDialogScDash!!.dismiss()
//                    }
//
//
//                    if (mRespuesta.message!!.respuesta.toString().contains("ERROR")){
//
//                        if(iNegocioActivity != null && iNegocioActivity.isVisible){
//                            iNegocioActivity.msgErrorMonitores(mRespuesta.message!!.respuesta.toString())
//                        }
//
//                        if(NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                            NegociosUnicamenteActivity.msgErrorMonitores(mRespuesta.message!!.respuesta.toString())
//                        }
//
//                        if(DashboardActivity!= null && DashboardActivity.isVisible){
//                            DashboardActivity.msgErrorMonitores(mRespuesta.message!!.respuesta.toString())
//                        }
//
//                    }else{
//                        val decodedString: ByteArray = Base64.decode(mRespuesta!!.message!!.respuesta!!.toString(), Base64.DEFAULT)
//                        val decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//                        databaseHelper_Data!!.deleteAuxScreenshot()
//                        databaseHelper_Data!!.addAuxScreenshot(mRespuesta!!.message!!.idequipo.toString(),decodedImage)
//                        var intent = Intent(context, PngJpgViewActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP
//                        intent.putExtra("Nombre", "")
//                        intent.putExtra("Negocio", "")
//                        intent.putExtra("Path", "")
//                        intent.putExtra("ViewType","screenshot")
//                        intent.putExtra("tipocomunicacion","1")
//                        intent.putExtra("idequipo",mRespuesta!!.message!!.idequipo.toString())
//                        intent.putExtra("nomimagen", mRespuesta!!.message!!.nomimagen.toString())
//                        context.startActivity(intent)
//                    }
//
//
//
//
//                }
//            }
//        }else if(msg.contains("movil") && msg.contains("chkUsuActivosWindows") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("\"[", "[")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "chkUsuActivosWindows")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "chkUsuActivosWindows")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "chkUsuActivosWindows")
//
//                        var nomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//
//                        if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                            iNegocioActivity.chkUsuariosActivosXEquipo(nomEquipo, mRespuesta.message!!.respuesta!!, mRespuesta.message!!.idequipo!!.toInt())
//
//                        }
//                        if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                            NegociosUnicamenteActivity.chkUsuariosActivosXEquipo(nomEquipo, mRespuesta.message!!.respuesta!!, mRespuesta.message!!.idequipo!!.toInt())
//
//                        }
//                        if (DashboardActivity!= null && DashboardActivity.isVisible){
//                            DashboardActivity.chkUsuariosActivosXEquipo(nomEquipo, mRespuesta.message!!.respuesta!!, mRespuesta.message!!.idequipo!!.toInt())
//
//                        }
//
//
//                    }
//
//                }
//            }
//
//
//
//        }else if(msg.contains("movil") && msg.contains("getMonitoreoEquipo") && !msg.contains("getMonitoreoEquipoGr") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("\"[", "[")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCION? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCION::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getMonitoreoEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipo")
//                        var nomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//
//                        if( LoginActivity.isReloadViewMonitores == 0){
//
//
//                            val accountsIntent = Intent(context, MonitorActivity::class.java)
//                            accountsIntent.putExtra("id_accion", "${LoginActivity.estatusequipo_id_accion}")
//                            accountsIntent.putExtra("id_origen", "${LoginActivity.estatusequipo_id_origen }")
//                            accountsIntent.putExtra("nombre_equipo", "$nomEquipo")
//                            accountsIntent.putExtra("hardware_key", "${LoginActivity.idHardwareExpArc}")
//                            accountsIntent.putExtra("tipo_comunicacion", "1")
//                            accountsIntent.putExtra("respuesta",  msg)
//                            context.startActivity(accountsIntent)
//
//                        }else{
//
//                            var subContext = LoginActivity.contextMonitores as MonitorActivity
//                            subContext.loadResulSocketResponse(mRespuesta.message!!.respuesta.toList())
//
//                        }
//
//
//
//
//                        //(context as MonitorActivity).loadResulSocketResponse(mRespuesta.message!!.respuesta.toList())
//                    }
//
//                }
//            }
//
//
//
//        }else if(msg.contains("movil") && msg.contains("exeAplicacionEquipo") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "exeAplicacionEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAplicacionEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAplicacionEquipo")
//                        Log.i("WS_SCREENSHOT_EQUIPO", mRespuesta.message!!.respuesta!!)
//                        if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                            iNegocioActivity.getSKRespuetaExeProgma(mRespuesta.message!!.respuesta!!, NomEquipo)
//                        }
//
//
//                    }
//                }
//
//            }
//
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "exeAplicacionEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAplicacionEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAplicacionEquipo")
//                        Log.i("WS_SCREENSHOT_EQUIPO", mRespuesta.message!!.respuesta!!)
//                        if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                            NegociosUnicamenteActivity.getSKRespuetaExeProgma(mRespuesta.message!!.respuesta!!, NomEquipo)
//                        }
//
//                    }
//                }
//
//            }
//
//        }else if(msg.contains("movil") && msg.contains("setTerminaProcesoEquipo") && msg.contains("respuesta")){
//
//            if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "setTerminaProcesoEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setTerminaProcesoEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setTerminaProcesoEquipo")
//                        Log.i("WS_SCREENSHOT_EQUIPO", mRespuesta.message!!.respuesta!!)
//                        iNegocioActivity.getSKRespuetaExeProgma(mRespuesta.message!!.respuesta!!, NomEquipo)
//                    }
//                }
//
//            }
//
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "setTerminaProcesoEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setTerminaProcesoEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setTerminaProcesoEquipo")
//                        Log.i("WS_SCREENSHOT_EQUIPO", mRespuesta.message!!.respuesta!!)
//                        NegociosUnicamenteActivity.getSKRespuetaExeProgma(mRespuesta.message!!.respuesta!!, NomEquipo)
//                    }
//
//                }
//
//            }
//
//        }else if(msg.contains("movil") && msg.contains("getArchivosEquipo") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if ((iNegocioActivity != null && iNegocioActivity.isVisible) || (DashboardActivity!= null && DashboardActivity.isVisible) || (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible)) {
//                //msg = msg.replace("\\", "")
//                msg = msg.replace("\\\"", "\"")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("\"[", "[")
//                var gson = Gson()
//                try{
//                    var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                    idDispositivo = removeLeadingZeroes(idDispositivo)
//                    idDispositivo =  removeTrailingZeroes(idDispositivo)
//
//
//                    if(msg.contains("SKERROR")){
//                        var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.WSEXPLORACIONEQUIPOERROR? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.WSEXPLORACIONEQUIPOERROR::class.java)
//                        if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                            var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta!!.message!!.idequipo!!.toInt(), "getArchivosEquipo")
//                            if(mRespuesta!!.message!!.id!!.toInt() > idact){
//                                databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getArchivosEquipo")
//                                databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getArchivosEquipo")
//                                if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                                    iNegocioActivity.getSKRespuetaExploradorARchivos(mRespuesta.message!!.respuesta!!.replace("SKERROR", ""))
//                                }
//                                if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                                    NegociosUnicamenteActivity.getSKRespuetaExploradorARchivos(mRespuesta.message!!.respuesta!!.replace("SKERROR", ""))
//                                }
//                                if (DashboardActivity != null && DashboardActivity.isVisible){
//                                    DashboardActivity.getSKRespuetaExploradorARchivos(mRespuesta.message!!.respuesta!!.replace("SKERROR", ""))
//                                }
//                            }
//                        }
//
//                    }else{
//                        var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.WSEXPLORACIONEQUIPO? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.WSEXPLORACIONEQUIPO::class.java)
//                        if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                            var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta!!.message!!.idequipo!!.toInt(), "getArchivosEquipo")
//                            if(mRespuesta!!.message!!.id!!.toInt() > idact){
//                                databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getArchivosEquipo")
//                                databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getArchivosEquipo")
//                                // LoginActivity.Companion.accionesRealizadas_Archivos = mRespuesta.message!!.id!!.toInt()
//
//
//
//                                var id_hardware = LoginActivity.idHardwareExpArc
//                                var nomEquipo = LoginActivity.nomEquipoExpArc
//                                val accountsIntent = Intent(context, activity_directorio_negocios::class.java)
//                                accountsIntent.putExtra("nom_negocio", "$nomEquipo")
//                                accountsIntent.putExtra("ip_negocio", "")
//                                accountsIntent.putExtra("ip_local", "")
//                                accountsIntent.putExtra("ip_puerto", "")
//                                accountsIntent.putExtra("id_hardware", id_hardware)
//                                accountsIntent.putExtra("id_dispositivo", idDispositivo)
//                                accountsIntent.putExtra("phoneMovil", "")
//                                accountsIntent.putExtra("rutaArchivosDir", "")
//                                accountsIntent.putExtra("phoneSubscriberId", "")
//                                accountsIntent.putExtra("tipo_comunicacion", "1")
//                                accountsIntent.putExtra("respuesta", msg)
//                                context.startActivity(accountsIntent)
//
//                            }
//
//
//                        }
//
//                    }
//
//
//
//                    Log.i("WS_SCREENSHOT_EQUIPO", msg)
//                }catch (ex: Exception){
//
//                }
//
//            }
//
//
//
//
//
//
//        }else if(msg.contains("movil") && msg.contains("setDelArchivosEquipo") && msg.contains("respuesta")){
//            if (iNegocioActivity != null && iNegocioActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocketDel? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocketDel::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "setDelArchivosEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact ){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        iNegocioActivity.getSKGetRespuestaDelArchivo(mRespuesta.message!!.respuesta!!)
//                    }
//                }
//                Log.i("WS_DELARCHIVO_EQUIPO", msg)
//            }
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "setDelArchivosEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        NegociosUnicamenteActivity.getSKGetRespuestaDelArchivo(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//                Log.i("WS_DELARCHIVO_EQUIPO", msg)
//            }
//            if ( context is activity_directorio_negocios){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "setDelArchivosEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "setDelArchivosEquipo")
//                        (context as activity_directorio_negocios).getSKGetRespuestaDelArchivo(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//                Log.i("WS_DELARCHIVO_EQUIPO", msg)
//            }
//        } else if(msg.contains("movil") && msg.contains("getMonitoreoEquipoGr") && msg.contains("respuesta")){
//
//            if (MonitoreablesActivity != null && MonitoreablesActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("\"[", "[")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCIONGR? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCIONGR::class.java)
//                var iRespuesta= mRespuesta!!.message!!.respuesta.toList()
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getMonitoreoEquipoGr")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipoGr")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipoGr")
//                        MonitoreablesActivity.loadResulSocketMonitorGrResponse(mRespuesta.message!!.respuesta.toList(), mRespuesta.message!!.dataequipo!!)
//                    }
//                }
//            }
//
//            //SI LA ACTIVIDAD ACTUAL ES EQUIPOS SE REALIZA CARGA INICIAL DE GRAFICOS
//            if (iNegocioActivity != null && iNegocioActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("\"[", "[")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCIONGR? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.WSRESMONITORACCIONGR::class.java)
//                var iRespuesta= mRespuesta!!.message!!.respuesta.toList()
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getMonitoreoEquipoGr")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipoGr")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getMonitoreoEquipoGr")
//                        iNegocioActivity.loadResulSocketMonitorGrResponse(mRespuesta.message!!.respuesta.toList(), mRespuesta.message!!.dataequipo!!)
//                    }
//
//                }
//            }
//
//
//
//
//
//        }else if(msg.contains("movil") && msg.contains("getSkFilePdf") && msg.contains("respuesta")){
//
//            if (context is PdfViewActivity) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getSkFilePdf")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFilePdf")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFilePdf")
//                        (context as PdfViewActivity).getPdfFileSocket(mRespuesta.message!!.respuesta!!)
//
//                    }
//
//
//
//                }
//
//            }
//
//        }else if(msg.contains("movil") && msg.contains("getSkFileTxt") && msg.contains("respuesta")){
//
//            if (context is TxtViewActivity) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getSkFileTxt")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileTxt")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileTxt")
//                        (context as TxtViewActivity).getTxtFileSocket(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//
//            }
//        }else if(msg.contains("movil") && msg.contains("getSkFileImagen") && msg.contains("respuesta")){
//
//            if (context is MainActivity) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                msg = msg.replace("\"[", "[")
//                msg = msg.replace("]\"", "]")
//                msg = msg.replace("{\"message\":\"{", "{\"message\":{")
//                msg = msg.replace("\"}\"}", "\"}}")
//
//                var gson = Gson()
//                var mRespuesta: RespuestaEncodeImageWebSocket? = gson?.fromJson(msg, RespuestaEncodeImageWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getSkFileImagen")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileImagen")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileImagen")
//
//                        val decodedString: ByteArray = Base64.decode(mRespuesta!!.message!!.respuesta!!.toString(), Base64.DEFAULT)
//                        val decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//                        // databaseHelper_Data!!.deleteAuxScreenshot()
//                        // databaseHelper_Data!!.addAuxScreenshot(mRespuesta!!.message!!.idequipo.toString(),decodedImage)
//
//
//                        databaseHelper_Data!!.deleteTmpImgExplorador()
//                        databaseHelper_Data!!.addTmpImgExplorador(decodedImage)
//
//                        var intent = Intent(activity_directorio_negocios.iactivity_dicractorio_negocio!!, PngJpgViewActivity::class.java)
//                        intent.putExtra("Nombre", "")
//                        intent.putExtra("Negocio", "")
//                        intent.putExtra("Path", "")
//                        intent.putExtra("ViewType","explorador")
//                        intent.putExtra("tipocomunicacion","1")
//                        intent.putExtra("idequipo",mRespuesta!!.message!!.idequipo.toString())
//                        intent.putExtra("nomimagen", mRespuesta!!.message!!.nomimagen.toString())
//                        // intent.putExtra("binaryimg", mRespuesta!!.message!!.respuesta!!.toString())
//                        intent.putExtra("binaryimg", "Prueba")
//                        activity_directorio_negocios.iactivity_dicractorio_negocio!!.startActivity(intent)
//
//
//
//
//                        //(context as PngJpgViewActivity).getImageFileSocket(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//
//            }
//        }else if(msg.contains("movil") && msg.contains("getImgScreenShotHistorico") && msg.contains("respuesta")){
//
//            if (context is PngJpgViewActivity) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getSkFileImagen")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileImagen")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getSkFileImagen")
//                        (context as PngJpgViewActivity).getImageFileSocket(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//
//            }
//        }else if(msg.contains("movil") && msg.contains("getConsultaProgramaEquipo") && msg.contains("respuesta")){
//
//            if (iNegocioActivity != null && iNegocioActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getConsultaProgramaEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaProgramaEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaProgramaEquipo")
//                        iNegocioActivity.getSKRespuestaConsultaProgramaEquipo(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//
//            }
//
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getConsultaProgramaEquipo")
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaProgramaEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaProgramaEquipo")
//                        NegociosUnicamenteActivity.getSKRespuestaConsultaProgramaEquipo(mRespuesta.message!!.respuesta!!)
//                    }
//                }
//
//            }
//        }else if(msg.contains("movil") && msg.contains("getConsultaBDEquipo") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if (iNegocioActivity != null && iNegocioActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    if (mRespuesta.message!!.respuesta == "" || mRespuesta.message!!.respuesta == null){
//                        Toast.makeText(context, "No fue posible recuperar informacion de la base de datos, revise la consulta y/o datos de conexion a la base", Toast.LENGTH_LONG).show()
//                        return
//                    }else{
//                        var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getConsultaBDEquipo")
//
//                        if(mRespuesta.message!!.id!!.toInt() >  idact){
//                            databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//                            databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//                            if (mRespuesta.message!!.respuesta!!.contains("INSERT_OK")) {
//
//                                Toast.makeText(context, "Se inserto registro(s) correctamente", Toast.LENGTH_LONG).show()
//
//                            } else if (mRespuesta.message!!.respuesta!!.contains("DELETE_OK")) {
//                                Toast.makeText(context, "Se eliminaron registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else if (mRespuesta.message!!.respuesta!!.contains("UPDATE_OK")) {
//                                Toast.makeText(context, "Se actualizan registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else {
//
//                                var newInfoAccion =
//                                    com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
//                                        result = mRespuesta.message!!.respuesta!!
//                                    )
//                                databaseHelper_Data!!.addInfoAccion(newInfoAccion)
//
//                                Log.i("WS_PDFFILE_EQUIPO", mRespuesta.message!!.respuesta!!)
//
//
//                                val accountsIntent = Intent(context!!, ConsultaDinamicaActivity::class.java)
//                                accountsIntent.putExtra("titulo_consulta", "${LoginActivity.bd_titulo_consulta}")
//                                accountsIntent.putExtra("nombre_negocio", "${LoginActivity.bd_nombre_equipo}")
//                                accountsIntent.putExtra("info_conexion", "${mRespuesta!!.message!!.infoconexion}")
//                                context!!.startActivity(accountsIntent)
//                                // (context as NegocioActivity).getSKRespuestaConsultaDBEquipo()
//
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    if (mRespuesta.message!!.respuesta == "" || mRespuesta.message!!.respuesta == null){
//                        Toast.makeText(context, "No fue posible recuperar informacion de la base de datos, revise la consulta y/o datos de conexion a la base", Toast.LENGTH_LONG).show()
//                        return
//                    }else{
//                        var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getConsultaBDEquipo")
//                        if(mRespuesta.message!!.id!!.toInt() > idact){
//                            databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//                            databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//
//                            if (mRespuesta.message!!.respuesta!!.contains("INSERT_OK")) {
//
//                                Toast.makeText(context, "Se inserto registro(s) correctamente", Toast.LENGTH_LONG).show()
//
//                            } else if (mRespuesta.message!!.respuesta!!.contains("DELETE_OK")) {
//                                Toast.makeText(context, "Se eliminaron registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else if (mRespuesta.message!!.respuesta!!.contains("UPDATE_OK")) {
//                                Toast.makeText(context, "Se actualizan registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else {
//
//                                var newInfoAccion =
//                                    com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
//                                        result = mRespuesta.message!!.respuesta!!
//                                    )
//                                databaseHelper_Data!!.addInfoAccion(newInfoAccion)
//
//                                Log.i("WS_PDFFILE_EQUIPO", mRespuesta.message!!.respuesta!!)
//                                val accountsIntent = Intent(context!!, ConsultaDinamicaActivity::class.java)
//                                accountsIntent.putExtra("titulo_consulta", "${LoginActivity.bd_titulo_consulta}")
//                                accountsIntent.putExtra("nombre_negocio", "${LoginActivity.bd_nombre_equipo}")
//                                context!!.startActivity(accountsIntent)
//                                //(context as NegociosUnicamenteActivity).getSKRespuestaConsultaDBEquipo()
//
//
//                            }
//                        }
//                    }
//                }
//            }
//
//            if(AlertasActivity != null){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaDBWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    if (mRespuesta.message!!.respuesta == "" || mRespuesta.message!!.respuesta == null){
//                        Toast.makeText(context, "No fue posible recuperar informacion de la base de datos, revise la consulta y/o datos de conexion a la base", Toast.LENGTH_LONG).show()
//                        return
//                    }else{
//                        var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getConsultaBDEquipo")
//
//                        if(mRespuesta.message!!.id!!.toInt() >  idact){
//                            databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//                            databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getConsultaBDEquipo")
//                            if (mRespuesta.message!!.respuesta!!.contains("INSERT_OK")) {
//
//                                Toast.makeText(context, "Se inserto registro(s) correctamente", Toast.LENGTH_LONG).show()
//
//                            } else if (mRespuesta.message!!.respuesta!!.contains("DELETE_OK")) {
//                                Toast.makeText(context, "Se eliminaron registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else if (mRespuesta.message!!.respuesta!!.contains("UPDATE_OK")) {
//                                Toast.makeText(context, "Se actualizan registro(s) correctamente", Toast.LENGTH_LONG).show()
//                            } else {
//
//                                var newInfoAccion =
//                                    com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
//                                        result = mRespuesta.message!!.respuesta!!
//                                    )
//                                databaseHelper_Data!!.addInfoAccion(newInfoAccion)
//
//                                Log.i("WS_PDFFILE_EQUIPO", mRespuesta.message!!.respuesta!!)
//
//
//                                if (context is ConsultaDinamicaActivity) {
//                                    Log.i("ESTAMOSAQUI", "sdddddddddsdd")
//                                }
//                                val respConsulta = mRespuesta!!.message!!.infoconexion!!.split("|")
//                                LoginActivity.bd_parametros_accion = "0|${respConsulta[5]}|${respConsulta[6]}|4|get|NULL"
//                                LoginActivity.bd_hardware_equipo = databaseHelper_Data!!.getEquipoIDHardwareXID(mRespuesta.message!!.idequipo!!)
//                                val accountsIntent = Intent(context!!, ConsultaDinamicaActivity::class.java)
//                                //accountsIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                                accountsIntent.putExtra("titulo_consulta", "${respConsulta[3]}")
//                                accountsIntent.putExtra("nombre_negocio", "${respConsulta[2]}")
//                                accountsIntent.putExtra("fecha_consulta", "${respConsulta[4]}")
//                                accountsIntent.putExtra("info_conexion", "${mRespuesta!!.message!!.infoconexion}")
//                                context!!.startActivity(accountsIntent)
//                                // (context as NegocioActivity).getSKRespuestaConsultaDBEquipo()
//
//
//                            }
//                        }
//                    }
//
//
//                }
//            }
//
//        }else if(msg.contains("movil") && msg.contains("getApiExtEquipo") && msg.contains("respuesta")){
//            LoginActivity.accionejecutandose = false
//            if (iNegocioActivity != null && iNegocioActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//
//                var gson = Gson()
//                var mRespuesta: RespuestaApiExtSocket? = gson?.fromJson(msg, RespuestaApiExtSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    if (mRespuesta.message!!.respuesta == "" || mRespuesta.message!!.respuesta == null){
//                        Toast.makeText(context, "No fue posible recuperar informacion, revise la configuracion de la API", Toast.LENGTH_LONG).show()
//                        return
//                    }else{
//                        var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getApiExtEquipo")
//
//                        if(mRespuesta.message!!.id!!.toInt() >  idact){
//                            val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                            databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getApiExtEquipo")
//                            databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getApiExtEquipo")
//                            if (mRespuesta.message!!.inforespuesta!! != "200") {
//                                iNegocioActivity.getSKRespuetaApiExt(mRespuesta.message!!.respuesta!!, NomEquipo)
//                            }  else {
//
//                                var newInfoAccion =
//                                    com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
//                                        result = mRespuesta.message!!.respuesta!!
//                                    )
//                                databaseHelper_Data!!.addInfoAccion(newInfoAccion)
//
//                                val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//                                val fecConsulta = LocalDate.now().format(formato)
//
//                                val accountsIntent = Intent(context!!, ConsultaApiExternaActivity::class.java)
//                                accountsIntent.putExtra("titulo_consulta", "${LoginActivity.bd_titulo_consulta}")
//                                accountsIntent.putExtra("nombre_negocio", "${LoginActivity.bd_nombre_equipo}")
//                                accountsIntent.putExtra("fecha_consulta", "$fecConsulta")
//                                accountsIntent.putExtra("id_accion", "${mRespuesta.message!!.idaccion}")
//
//                                context!!.startActivity(accountsIntent)
//                                // (context as NegocioActivity).getSKRespuestaConsultaDBEquipo()
//
//                            }
//                        }
//                    }
//                }
//
//            }
//
//            if (NegociosUnicamenteActivity != null && NegociosUnicamenteActivity.isVisible) {
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: RespuestaApiExtSocket? = gson?.fromJson(msg, RespuestaApiExtSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    if (mRespuesta.message!!.respuesta == "" || mRespuesta.message!!.respuesta == null){
//                        Toast.makeText(context, "No fue posible recuperar informacion, revise la configuracion de la API", Toast.LENGTH_LONG).show()
//                        return
//                    }else{
//                        var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getApiExtEquipo")
//                        if(mRespuesta.message!!.id!!.toInt() > idact){
//                            val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                            databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getApiExtEquipo")
//                            databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getApiExtEquipo")
//                            if (mRespuesta.message!!.inforespuesta!! != "200") {
//                                NegociosUnicamenteActivity.getSKRespuetaApiExt(mRespuesta.message!!.respuesta!!, NomEquipo)
//                            } else {
//
//                                var newInfoAccion =
//                                    com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
//                                        result = mRespuesta.message!!.respuesta!!
//                                    )
//                                databaseHelper_Data!!.addInfoAccion(newInfoAccion)
//
//                                val formato = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//                                val fecConsulta = LocalDate.now().format(formato)
//
//                                Log.i("WS_PDFFILE_EQUIPO", mRespuesta.message!!.respuesta!!)
//                                val accountsIntent = Intent(context!!, ConsultaApiExternaActivity::class.java)
//                                accountsIntent.putExtra("titulo_consulta", "${LoginActivity.bd_titulo_consulta}")
//                                accountsIntent.putExtra("nombre_negocio", "${LoginActivity.bd_nombre_equipo}")
//                                accountsIntent.putExtra("fecha_consulta", "$fecConsulta")
//                                accountsIntent.putExtra("id_accion", "${mRespuesta.message!!.idaccion}")
//
//                                context!!.startActivity(accountsIntent)
//
//
//                            }
//                        }
//                    }
//                }
//            }
//        } else if(msg.contains("movil") && msg.contains("getClonaArchivoEquipo") && msg.contains("respuesta")){
//            if (context is activity_directorio_negocios){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "getClonaArchivoEquipo")
//
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getClonaArchivoEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "getClonaArchivoEquipo")
//                        (context as activity_directorio_negocios).resultadoSkClon(mRespuesta.message!!.respuesta!!)
//                    }
//
//                }
//                Log.i("WS_CLONACION_EQUIPO", msg)
//            }
//        }else if(msg.contains("movil") && msg.contains("exeAppFileExplEquipo") && msg.contains("respuesta")){
//
//            if (context is activity_directorio_negocios){
//                msg = msg.replace("\\", "")
//                msg = msg.replace(": \"", ":")
//                msg = msg.replace("}\"", "}")
//                var gson = Gson()
//                var mRespuesta: com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket? = gson?.fromJson(msg, com.apptomatico.app_movil_kotlin_v3.model.RespuestaWebSocket::class.java)
//                var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
//                idDispositivo = removeLeadingZeroes(idDispositivo)
//                idDispositivo =  removeTrailingZeroes(idDispositivo)
//                if (mRespuesta!!.message!!.movil!! == idDispositivo) {
//                    var idact = databaseHelper_Data!!.getControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), "exeAppFileExplEquipo")
//
//
//                    if(mRespuesta.message!!.id!!.toInt() > idact){
//                        val NomEquipo = databaseHelper_Data!!.getEquipoNombre(mRespuesta.message!!.idequipo!!.toInt())
//                        databaseHelper_Data!!.addControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAppFileExplEquipo")
//                        databaseHelper_Data!!.updControlAccSocketXEquipo(mRespuesta.message!!.idequipo!!.toInt(), mRespuesta.message!!.id!!.toInt(), "exeAppFileExplEquipo")
//
//                        (context as activity_directorio_negocios).getSKRespuetaExeProgma(mRespuesta.message!!.respuesta!!, NomEquipo)
//                    }
//
//                }
//            }
//        }
//
//



    }

    private fun MessageScarlet.toValue(): String {
        return when (this) {
            is com.tinder.scarlet.Message.Text -> value
            is com.tinder.scarlet.Message.Bytes -> value.toString()
        }
    }



    fun removeLeadingZeroes(s: String): String {
        var index: Int = 0
        while (index < s.length - 1) {
            if (s[index] != '0') {
                break
            }
            index++
        }
        return s.substring(index)
    }

    fun removeTrailingZeroes(s: String): String {
        var index: Int = s.length - 1
        while (index > 0) {
            if (s[index] != '0') {
                break
            }
            index--
        }
        return s.substring(0, index + 1)
    }




}