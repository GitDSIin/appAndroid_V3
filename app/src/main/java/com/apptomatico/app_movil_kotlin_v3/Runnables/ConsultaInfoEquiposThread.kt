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
import com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List
import com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas
import com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.model.Negocios_List
import com.apptomatico.app_movil_kotlin_v3.model.ParametrosAccDataAll
import com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem
import com.apptomatico.app_movil_kotlin_v3.negocio.ChildFragment
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.google.gson.Gson
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class ConsultaInfoEquiposThread(
    private val databaseHelper: DatabaseHelper,
    private val context: Activity,
    private val idMovil: Int,
    private val progressBar: ProgressBar,
    private val fragmentoOrigen: String
) : Runnable {

    private var equipoConInternet: Boolean = false
    private var dominioActual = ""

    override fun run() {
        if (LoginActivity.accionejecutandose || NegocioFragment.vwModalActiva) {
            return
        }

        inicializarDominio()
        actualizarUIInicial()

        val client = crearHttpClient()
        val token = obtenerTokenAutenticacion(client)

        token?.let {
            obtenerInformacionEquipos(client, it)
            obtenerAccionesEquipos(client, it)
            controlParametrosAcciones(client)
        } ?: run {
            ocultarProgressBar()
        }
    }

    private fun inicializarDominio() {
        val entornoAct = databaseHelper.getControlEntornoConexion(context)
        dominioActual = "https://${BuildConfig.DOMINIO_PORTAL}"
    }

    private fun actualizarUIInicial() {
        context.runOnUiThread {
            databaseHelper.updateEstadoInactivoEquipos()
            databaseHelper.updateEstadoInactivoAccionesEquipos()
            actualizarPerfilFiltros()

            if (debeMostrarProgressBar()) {
                progressBar.visibility = View.VISIBLE
            }
        }
    }

    private fun actualizarPerfilFiltros() {
        if (!databaseHelper.chkPerfilFiltroEncabezado()) {
            databaseHelper.addPerfilFiltroEncabezado(
                header_id_negocio,
                header_id_equipos,
                filtro_alertas_estatus,
                filtro_alertas_equipo,
                filtro_alertas_fecha,
                filtro_alertas_fecha_desde_hasta,
                filtro_alertas_eliminadas_estatus,
                filtro_alertas_eliminadas_fecha,
                filtro_alertas_eliminadas_fecha_desde_hasta
            )
        } else {
            databaseHelper.updPerfilFiltroEncabezado(
                header_id_negocio,
                header_id_equipos,
                filtro_alertas_estatus,
                filtro_alertas_equipo,
                filtro_alertas_fecha,
                filtro_alertas_fecha_desde_hasta,
                filtro_alertas_eliminadas_estatus,
                filtro_alertas_eliminadas_fecha,
                filtro_alertas_eliminadas_fecha_desde_hasta
            )
        }
    }

    private fun crearHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()
    }

    private fun obtenerTokenAutenticacion(client: OkHttpClient): String? {
        val formBody = FormBody.Builder()
            .add("email", obtenerEmail())
            .add("password", obtenerPassword())
            .build()

        val request = Request.Builder()
            .url("${dominioActual}/token-auth/")
            .header("Content-Type", "application/json")
            .post(formBody)
            .build()
        var response: Response? = null
        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                parsearToken(response.body?.string())
            } else {
                null
            }
        } catch (e: IOException) {
            null
        } finally {
            response?.close()
        }
    }

    private fun obtenerInformacionEquipos(client: OkHttpClient, token: String) {
        val trace = FirebasePerformance.getInstance().newTrace("trace_rnb_info_equipo")
        trace.start()

        try {
            val movilId = obtenerIdMovil()
            val ordenamiento = databaseHelper.getOrdenamientoEquipos()

            val request = Request.Builder()
                .url("${dominioActual}/api/list_moviles_negocios/?id_movil=$movilId&id_negocio=0&ls_ordenamiento=$ordenamiento")
                .header("Authorization", "JWT $token")
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                procesarInformacionEquipos(response.body?.string())
            }
        } catch (e: Exception) {
            Log.e("ConsultaInfoEquipos", "Error obteniendo información equipos: ${e.message}")
        } finally {
            trace.stop()
        }
    }

    private fun procesarInformacionEquipos(jsonData: String?) {
        jsonData?.let {
            try {
                val gson = Gson()
                val data = gson.fromJson(it, DataListNegocios::class.java)

                data.data.forEach { equipo ->
                    actualizarEquipoEnBaseDatos(equipo)
                }
            } catch (e: Exception) {
                Log.e("ConsultaInfoEquipos", "Error procesando equipos: ${e.message}")
            }
        }
    }

    private fun actualizarEquipoEnBaseDatos(equipo: Negocios_List) {
        databaseHelper.addControlEquipos(
            equipo.id_negocio_id,
            equipo.ip_publica,
            equipo.ip_local,
            equipo.puerto,
            equipo.hardware_key,
            equipo.tipo_eq_icono_on,
            equipo.tipo_eq_icono_off,
            equipo.nombre,
            equipo.id_catnegocio,
            equipo.id_accion_monitoreable.toString()
        )

        databaseHelper.updateEquipos(equipo)
        databaseHelper.addEquipos(equipo)
        databaseHelper.addEquiposTipoLIc(equipo.id_negocio_id, equipo.unimovil!!)
        databaseHelper.updEquiposTipoLIc(equipo.id_negocio_id, equipo.unimovil!!)

        val movilId = obtenerIdMovil()
        databaseHelper.delEquiposNoAsignadosAMovil(movilId)

        equipo.gc_project_screenshot?.let { screenshot ->
            databaseHelper.addControlEquiposAPIVM(equipo.id_negocio_id, "", "", "", "", screenshot)
            databaseHelper.updControlEquiposAPIVM(equipo.id_negocio_id, "", "", "", "", screenshot)
        }
    }

    private fun obtenerAccionesEquipos(client: OkHttpClient, token: String) {
        val movilId = obtenerIdMovil()

        val request = Request.Builder()
            .url("${dominioActual}/api/moviles_get_acciones_validas_x_negocio/?id_movil=$movilId&id_negocio=0")
            .header("Authorization", "JWT $token")
            .header("Content-Type", "application/json")
            .build()

        var response: Response? = null
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                procesarAccionesEquipos(response.body?.string())
            } else {
                actualizarUIFinal()
            }
        } catch (e: IOException) {
            Log.e("ConsultaInfoEquipos", "Error obteniendo acciones: ${e.message}")
            actualizarUIFinal()
        } finally {
            response?.body?.close()
        }
    }

    private fun procesarAccionesEquipos(jsonData: String?) {
        jsonData?.let {
            try {
                val gson = Gson()
                val accionesData = gson.fromJson(it, DataListAcionesDinamicas::class.java)

                val accionesFiltradas = accionesData.data
                    .filter { accion -> accion.grupo == 1 || accion.grupo == 2 }
                    .toList()

                accionesFiltradas.forEach { accion ->
                    actualizarAccionEnBaseDatos(accion)
                }

            } catch (e: Exception) {
                Log.e("ConsultaInfoEquipos", "Error procesando acciones: ${e.message}")
            } finally {
                actualizarUIFinal()
            }
        } ?: run {
            actualizarUIFinal()
        }
    }

    private fun actualizarAccionEnBaseDatos(accion: AcionesDinamicas_List) {
        val existe = databaseHelper.CheckExisteAccionesDinamicas(accion.id, accion.id_negocio_id)

        if (!existe) {
            databaseHelper.addAccionesDinamicas(accion)
            Log.i("ACTUALIZA-EQUIPOS", "Se inserta Acción ID: ${accion.id}")
        } else {
            databaseHelper.updAccionesDinamicas(accion)
            Log.i("ACTUALIZA-EQUIPOS", "Se actualiza Acción ID: ${accion.id}")
        }
    }

    private fun controlParametrosAcciones(client: OkHttpClient) {
        val equipos = databaseHelper.getControlEquipos()
        if (equipos.isNotEmpty()) {
            val strEquiposId = equipos.joinToString("|") { it.equipo_id.toString() }

            val request = Request.Builder()
                .url("${dominioActual}/api/get_par_base_datos_all/?id_equipos=$strEquiposId")
                .header("Authorization", "JWT ${BuildConfig.TOKEN_MAESTRO}")
                .header("Content-Type", "application/json")
                .build()
            var response: Response? = null
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    procesarParametrosAcciones(response.body?.string())
                }
            } catch (e: IOException) {
                Log.e("ConsultaInfoEquipos", "Error parámetros acciones: ${e.message}")
            } finally {
                response?.body?.close()
            }
        }
    }

    private fun procesarParametrosAcciones(jsonData: String?) {
        jsonData?.let {
            try {
                val gson = Gson()
                val parametros = gson.fromJson(it, ParametrosAccDataAll::class.java)

                parametros.data.forEach { param ->
                    if (!databaseHelper.chkExistControlParamsAcc(param.id_accion!!, param.campo!!)) {
                        databaseHelper.addControlParamsAcc(
                            param.id_accion!!,
                            param.campo!!,
                            param.valor!!,
                            param.tipo!!,
                            param.base_datos_query,
                            param.consumo_api_ext_query_params
                        )
                    } else {
                        databaseHelper.updControlParamsAcc(
                            param.id_accion!!,
                            param.campo!!,
                            param.valor!!,
                            param.tipo!!,
                            param.base_datos_query,
                            param.consumo_api_ext_query_params
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ConsultaInfoEquipos", "Error procesando parámetros: ${e.message}")
            }
        }
    }

    private fun actualizarUIFinal() {
        context.runOnUiThread {
            when (fragmentoOrigen) {
                "InicioEquiposFragment" -> {
                    ChildFragment.NegociosHomeAdapter?.refreshFamiliasDB()
                    ChildFragment.NegociosHomeAdapter?.notifyDataSetChanged()
                    (context as? MainActivity)?.getAllChildExercisesFromParentID()
                }
                "tgEquiposUnicamenteFragment" -> {
                    // Implementar cuando esté disponible
                }
            }
            ocultarProgressBar()
        }
    }

    private fun ocultarProgressBar() {
        context.runOnUiThread {
            if (debeMostrarProgressBar()) {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun debeMostrarProgressBar(): Boolean {
        return fragmentoOrigen == "InicioEquiposFragment" ||
                fragmentoOrigen == "tgEquiposUnicamenteFragment"
    }

    private fun obtenerEmail(): String {
        // TODO: Implementar recuperación segura de credenciales
        return "licencias@dsiin.com"
    }

    private fun obtenerPassword(): String {
        // TODO: Implementar recuperación segura de credenciales
        return "xhceibEd"
    }

    private fun obtenerIdMovil(): Int {
        SQLiteDatabase.loadLibs(context)
        val licencias = databaseHelper.getAllLicencia()
        return licencias.firstOrNull()?.id_movil ?: 0
    }

    private fun parsearToken(json: String?): String? {
        return json?.let {
            try {
                Gson().fromJson(it, UserDataCollectionItem::class.java)?.token
            } catch (e: Exception) {
                null
            }
        }
    }
}