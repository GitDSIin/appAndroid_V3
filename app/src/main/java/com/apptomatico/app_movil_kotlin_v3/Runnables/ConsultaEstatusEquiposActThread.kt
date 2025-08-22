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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
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
import java.util.Locale
import java.util.concurrent.TimeUnit

class ConsultaEstatusEquiposActThread(
    private val databaseHelper_Data: DatabaseHelper,
    private val context: Activity,
    private val framrNegocioList: FrameLayout,
    private val allEquipos: List<ControlEquiposXMovil>,
    private var dominio_actual: String?,
    private val modelo_comunicacion: Int?,
    private val webSocketService: EchoService?,
    private val idDispositivo: String?,
    private val fragmentoOrigen: String,
    private val myConnectivityManager: MyConnectivityManager
) {

    private var ipPublica: String? = null
    private var iPuerto: String? = null
    private var idHardware: String? = null
    private var equipoid: Int? = null
    private var equipoinconoon: String? = null
    private var equipoinconooff: String? = null
    private var iplocal: String? = null
    private var nombreequipo: String? = null
    private var ivalues: List<ControlRedMovil>? = null

    private var imageLogo = ""

    private var equipoActivo: Boolean = false

    /**
     * Punto de entrada coroutine
     */
    @RequiresApi(Build.VERSION_CODES.N)
    suspend fun run() = withContext(Dispatchers.IO) {
        if (fragmentoOrigen != "InicioEquiposFragment") return@withContext
        if (accionejecutandose) return@withContext
        if (vwModalActiva) return@withContext

        val connectionState = myConnectivityManager.connectionAsStateFlow.value

        withContext(Dispatchers.Main) {
            MemoryUtilsMail.checkMemoryUsageAndSendAlert("Consulta Estatus Equipo", databaseHelper_Data)
        }

        ConnectivityUiView(connectionState)

        Log.i("ESTATUS_EQUIPO", "Finaliza consulta estatus equipo")
    }

    private suspend fun ConnectivityUiView(isOnline: Boolean) {
        if (isOnline) {
            validaConexionEquipo()
        } else {
            withContext(Dispatchers.Main) {
                encendido(framrNegocioList, false)
            }
        }
    }

    private suspend fun validaConexionEquipo() {
        val entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
        val svrWebSocket: String

        dominio_actual = if (entornoAct == 1) {
            "https://${BuildConfig.DOMINIO_PORTAL}".also { svrWebSocket = BuildConfig.DOMINIO_PORTAL }
        } else {
            "https://${BuildConfig.DOMINIO_PORTAL}".also { svrWebSocket = BuildConfig.DOMINIO_PORTAL }
        }

        val strEquiposId = allEquipos.joinToString("|") { it.equipo_id.toString() }

        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url("${dominio_actual}/api/estatus_equipos_socket_all/?id_equipos=$strEquiposId")
            .header("Authorization", "JWT ${BuildConfig.TOKEN_MAESTRO}")
            .header("Content-Type", "application/json")
            .build()

        val myTrace: Trace = FirebasePerformance.getInstance().newTrace("trace_rnb_estatus_equipo")
        myTrace.start()

        try {
            val responseStr = client.await(request)
            val mtoken = Gson().fromJson(responseStr, NewWebSocketEstatusAll::class.java)

            // Map para almacenar estado final de cada equipo
            val estadosEquipos = mutableMapOf<Int, Boolean>()

            mtoken.data.forEach { item ->
                val modeloComunicacion = databaseHelper_Data.getEquipoTipoComunicacion(item.negocio_id!!)
                ivalues = databaseHelper_Data.getConexionRed()
                val filterAllEquipos = allEquipos.filter { s -> s.equipo_id == item.negocio_id!! }

                if (filterAllEquipos.isNotEmpty()) {
                    filterAllEquipos[0].apply {
                        ipPublica = ip_publica
                        iPuerto = puerto.toString()
                        idHardware = id_hardware
                        equipoid = equipo_id
                        equipoinconoon = icono_on
                        equipoinconooff = icono_off
                        iplocal = ip_local
                        nombreequipo = nombre_equipo
                    }
                }

                val isOnline = if (item.estatus_chat == "online") {
                    databaseHelper_Data.getEstadoEquipo(equipoid!!) != "offline"
                } else {
                    if (modeloComunicacion == 0) {
                        try {
                            val url = URL("http://${ipPublica}:${iPuerto}/")
                            val huc: HttpURLConnection = url.openConnection() as HttpURLConnection
                            huc.connectTimeout = 5000
                            huc.responseCode == 200
                        } catch (ex: Exception) {
                            false
                        }
                    } else {
                        false
                    }
                }

                // Guardamos el estado final de cada equipo
                estadosEquipos[equipoid!!] = isOnline
            }

            // Actualizar UI y DB en bloque
            withContext(Dispatchers.Main) {
                estadosEquipos.forEach { (idEquipo, estado) ->
                    updatesas(estado, idEquipo)
                }
            }

        } catch (ex: Exception) {
            Log.e("ESTATUS_EQUIPO", "Error al recuperar listado de equipos activos ${ex.message}")
            databaseHelper_Data.addControlServerSocketLog("ConsultaEstatusEquiposActThread: ${ex.message}")
            withContext(Dispatchers.Main) { encendido(framrNegocioList, false) }
        } finally {
            myTrace.stop()
        }
    }


    /**
     * Helper suspend para OkHttp
     */
    private suspend fun OkHttpClient.await(request: Request): String =
        suspendCancellableCoroutine { cont ->
            newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (cont.isActive) cont.resumeWith(Result.failure(e))
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        cont.resumeWith(Result.failure(IOException("Unexpected code $response")))
                        return
                    }
                    cont.resume(response.body!!.string(), null)
                }
            })
        }

    private suspend fun updatesas(result: Boolean, equipoid: Int) {
        val cardChlEquipo = getViewsByTag(framrNegocioList, "${equipoid}9999")
        val cardEquipo = cardChlEquipo?.firstOrNull() as? CardView

        if (result) {
            val existeequipo = databaseHelper_Data.CheckExisteControlEquipo(equipoid)
            if (existeequipo) {
                databaseHelper_Data.updControlEstadoEquipos(
                    equipoid, "Activo", nombreequipo.orEmpty(),
                    idHardware.orEmpty(), ipPublica.orEmpty(), iplocal.orEmpty(), iPuerto.orEmpty()
                )
            } else {
                databaseHelper_Data.addControlEstadoEquipos(
                    equipoid, "Activo", nombreequipo.orEmpty(),
                    idHardware.orEmpty(), ipPublica.orEmpty(), iplocal.orEmpty(), iPuerto.orEmpty()
                )
            }

            val estEquipAct = databaseHelper_Data.getControlEstadoEquipos()
            val fltEqAct = estEquipAct?.filter { s -> s.equipo_id == equipoid && s.estado_equipo == "Activo" }

            if (!fltEqAct.isNullOrEmpty()) {
                if (cardEquipo != null) {
                    val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                    val currentDateAndTime: String = simpleDateFormat.format(Date())
                    val fecUltAct = currentDateAndTime.split(" ")

                    databaseHelper_Data.addInfoNegocio(1, currentDateAndTime, equipoid)
                    databaseHelper_Data.updateInfoNegocio(1, currentDateAndTime, equipoid)

                    val cardChlFecUpdEquipo = getViewsByTag(framrNegocioList, "${equipoid}842644946549")
                    val UltActEquipo = cardChlFecUpdEquipo?.firstOrNull() as? TextView

                    val cardHoraActUpdEquipo = getViewsByTag(framrNegocioList, "${equipoid}8426449465492")
                    val HoraActEquipo = cardHoraActUpdEquipo?.firstOrNull() as? TextView

                    withContext(Dispatchers.Main) {
                        UltActEquipo?.text = "(Ult.Act: ${fecUltAct[0]} "
                        HoraActEquipo?.text = "${fecUltAct[1]})"
                        encendido(cardEquipo, true)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    cardEquipo?.let { encendido(it, false) }
                }
            }
        } else {
            databaseHelper_Data.delControlEstadoEquiposXID(equipoid)
            withContext(Dispatchers.Main) {
                cardEquipo?.let { encendido(it, false) }
            }
        }
    }

    private fun getViewsByTag(root: ViewGroup, tag: String): ArrayList<View>? {
        val views = ArrayList<View>()
        val childCount = root.childCount
        for (i in 0 until childCount) {
            val child = root.getChildAt(i)
            if (child is ViewGroup) {
                views.addAll(getViewsByTag(child, tag) ?: emptyList())
            }
            val tagObj = child.tag
            if (tagObj != null && tagObj == tag) {
                views.add(child)
            }
        }
        return views
    }

    private fun encendido(viewGroup: ViewGroup?, enabled: Boolean) {
        val childCount: Int = viewGroup?.childCount ?: 0
        for (i in 0 until childCount) {
            val view: View = viewGroup!!.getChildAt(i)
            if (view is ImageView) {
                if (enabled) {
                    view.clearColorFilter()
                    view.isClickable = enabled
                } else {
                    view.setColorFilter(context.resources.getColor(R.color.dg_gray))
                    view.isClickable = enabled
                }
            }
            if (view is ViewGroup) {
                encendido(view, enabled)
            }
        }
    }
}
