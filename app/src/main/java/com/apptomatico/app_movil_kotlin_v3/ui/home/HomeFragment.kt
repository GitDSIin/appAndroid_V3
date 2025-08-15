package com.apptomatico.app_movil_kotlin_v3.ui.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.ConsultaDinamica.ConsultaDinamicaActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosEjecucion
import com.apptomatico.app_movil_kotlin_v3.model.ItemParametrosAccDB
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.negocio.ParametrosAdapter
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXFade
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import kotlin.text.count
import kotlin.text.substring

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var databaseHelper_Data: DatabaseHelper
    private var webSocketService: EchoService? = null
    private var ECHO_URL_WEB_SOCKET: String = ""
    var root: View? = null

    var adapter: CustomAdapterDahsboard? = null
    val client by lazy { APIService.create() }
    var arrUsuariosPermitidos: MutableList<String> = java.util.ArrayList()

    var idnegocio = ""
    var ipPublica = ""
    var idHardware = ""
    var iPuerto = ""
    var bovedaSoftware = ""
    var iNombrenegocio = ""
    var iEstiloDasboard = ""
    var ipLocal = ""

    init {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val reg = SharedApp.prefs.idNegocioF.toString().split("|")
        idnegocio = reg[0]
        ipPublica = reg[1]
        idHardware = reg[2]
        iPuerto = reg[3]
        bovedaSoftware = reg[4]
        iNombrenegocio = reg[5]
        iEstiloDasboard = reg[6]
        ipLocal = reg[7]



        webSocketService = LoginActivity.Companion.webSocketService


        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        root = inflater.inflate(R.layout.fragment_home, container, false)
        databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        val idMovil = Licenciadb[0].id_movil
        //val textView: TextView = root.findViewById(R.id.text_home)


        val titulo: TextView = root!!.findViewById(R.id.titulo_equipo_das_actual)
        titulo.text = "$iNombrenegocio"

        val estDasC: ImageView = root!!.findViewById(R.id.idEstDasC)
        estDasC.setOnClickListener {
            cambiaLayoutToGrid()

        }

        val estDasL: ImageView = root!!.findViewById(R.id.idEstDasL)
        estDasL.setOnClickListener {
            cambiaLayoutToList()
        }


        var accionList = ArrayList<ItemAcciones>()
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)
        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {

                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()

                    val negociosService: APIService =
                        RestEngine.getRestEngine(databaseHelper_Data, requireContext())
                            .create(APIService::class.java)
                    val resultAcciones: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas> =
                        negociosService.getMovilAccionesValidasXNegocio(
                            "$idMovil",
                            0,
                            "JWT $token",
                            "application/json"
                        )
                    resultAcciones.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas> {
                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Error al recuperar acciones del Movil",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>
                        ) {
                            val resultado = response.body()
                            if (resultado != null) {

                                //Consulta Favoritos

                                val resultFavorito: Call<String> =
                                    negociosService.getFavoritosMovil(
                                        "$idDispositivo",
                                        "JWT $token",
                                        "application/json"
                                    )

                                resultFavorito.enqueue(object : Callback<String> {
                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                        Toast.makeText(
                                            requireContext(),
                                            "error al recuperar acciones favoritas ",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                    override fun onResponse(
                                        call: Call<String>,
                                        response: Response<String>
                                    ) {
                                        val resultado_addFav = response.body()
                                        var favoritos: Array<String>? = null
                                        if (resultado_addFav != "") {
                                            favoritos = resultado_addFav!!.split(",").toTypedArray()

                                        }

                                        var valAccionesDinamicas: List<com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List> =
                                            ArrayList()
                                        valAccionesDinamicas =
                                            resultado.data.filter { it -> it.grupo == 1 }.toList()
                                        var AccXNegocio =
                                            valAccionesDinamicas.filter { s -> s.id_negocio_id == idnegocio.toInt() && s.screenshot_tipo != "sc_inicio_sesion" && s.screenshot_tipo != "sc_intervalo" && s.screenshot_tipo != "sc_ejecutar_programa" }
                                        for (i in AccXNegocio) {


                                            var NomAcc = ""
                                            if(i.titulo_aplicacion != "" && i.titulo_aplicacion.isNotEmpty()){
                                                NomAcc = i.titulo_aplicacion
                                            }else{
                                                NomAcc = i.alias_aplicacion
                                            }

                                            var alias =  NomAcc
                                            var tipo = i.tipo_id
                                            var esFavorita = false
                                            if (!favoritos.isNullOrEmpty()) {
                                                if (isPresent(favoritos!!, "${i.id}")) {
                                                    esFavorita = true
                                                }
                                            }


                                            accionList.add(
                                                ItemAcciones(
                                                    i.id,
                                                    "$alias",
                                                    "",
                                                    "$tipo",
                                                    "${i.comando}",
                                                    i.id_negocio_id.toString(),
                                                    iNombrenegocio,
                                                    esFavorita
                                                )
                                            )
                                        }

                                        adapter = CustomAdapterDahsboard(
                                            accionList,
                                            requireContext(),
                                            databaseHelper_Data
                                        )


                                        val grDh: GridView = root!!.findViewById(R.id.gridDashboard)
                                        //  val lyParent: LinearLayout = root.findViewById(R.id.MenuDashView)
                                        //  val grDh: GridView = root.findViewById(R.id.gridDashboard)

                                        //  if (iEstiloDasboard == "cuadricula"){
                                        //     val scale = context!!.resources.displayMetrics.density
                                        //     val pixels = (150 * scale + 0.5f)
                                        //     grDh.columnWidth =  pixels.toInt()
                                        // }else{
                                        //      grDh.columnWidth = lyParent.width
                                        //  }

                                        grDh.adapter = adapter

                                        grDh.setOnItemClickListener { parent, view, position, id ->
                                            view.findViewById<ImageView>(R.id.imageDynamicDasboard)
                                                .animationXFade(Fade.FADE_IN_DOWN)

                                            getAndExecuteAccion(
                                                AccXNegocio[position],
                                                iNombrenegocio,
                                                idnegocio.toInt(),
                                                ipPublica,
                                                ipLocal,
                                                idHardware,
                                                iPuerto.toInt(),
                                                bovedaSoftware
                                            )

                                        }


                                    }

                                })


                            }


                        }

                    })


                }
            }

        })







        return root
    }


    fun <T> isPresent(arr: Array<T>, target: T): Boolean {
        return arr.contains(target)
    }

    override fun onResume() {
        super.onResume()


        // activity!!.title = "Negocio: $iNombrenegocio"
    }

    public fun cambiaLayoutToGrid() {

        val grDh: GridView = root!!.findViewById(R.id.gridDashboard)
        val scale = requireContext().resources.displayMetrics.density
        val pixels = (150 * scale + 0.5f)
        grDh.columnWidth = pixels.toInt()
        // adapter!!.notifyDataSetChanged()

    }

    fun cambiaLayoutToList() {

        val lyParent: LinearLayout = root!!.findViewById(R.id.MenuDashView)
        val grDh: GridView = root!!.findViewById(R.id.gridDashboard)
        grDh.columnWidth = lyParent.width
        // adapter!!.notifyDataSetChanged()
    }


    fun getAndExecuteAccion(
        info_app: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List,
        NomNegocio: String,
        idNegocio: Int,
        ip_publica: String,
        ip_local: String,
        hardware_key: String,
        puerto: Int,
        boveda: String
    ) {

        arrUsuariosPermitidos.clear()
        val tipoComunicacion = databaseHelper_Data.getEquipoTipoComunicacion(info_app.id_negocio_id)

        if (info_app.tipo_id == 1) { // Ejecuta cualquier aplicacion

            if (info_app.comando == "calc") {


                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage("Desea ejecutar  ${info_app.aplicacion_nombre_aplicacion}")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (

                                getGetUsuariosPermitidosXEquipo(
                                    info_app,
                                    NomNegocio,
                                    idNegocio,
                                    ip_publica,
                                    ip_local,
                                    hardware_key,
                                    puerto,
                                    boveda,
                                    tipoComunicacion
                                )

                                )
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()


            } else if (info_app.comando == "termina_proceso_en_equipo") {


                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage("Desea finalizar el proceso  ${info_app.aplicacion_nombre_aplicacion}")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (
                                sendTerminaProcesoEquipo(
                                    "${info_app.alias_aplicacion}",
                                    "${info_app.id_negocio_id}",
                                    ip_publica,
                                    ip_local,
                                    hardware_key,
                                    "$puerto",
                                    "${info_app.aplicacion_ruta_aplicacion}",
                                    "${info_app.aplicacion_nombre_aplicacion}",
                                    "${info_app.aplicacion_parametros_aplicacion}",
                                    tipoComunicacion
                                )

                                )
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()

            }


        }

        if (info_app.tipo_id == 2) { // Ejecuta acciobnes sobre el servidor
            var desc = ""
            if (info_app.comando == "reboot_svr") { //Reinicia Servidor

                desc = "Confirma que desea reiniciar el equipo"
                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (
                                RebootNegocio(
                                    NomNegocio,
                                    "$hardware_key",
                                    "$ip_publica",
                                    "$puerto",
                                    "$ipLocal",
                                    tipoComunicacion
                                )
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()


            }
            if (info_app.comando == "log_off_svr") { //Cerrar Sesion del Servidor
                desc = "Confirma que desea cerrar sesion en el equipo"
                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                        view.dismiss()
                    })
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (
                                LogOffNegocio(
                                    NomNegocio,
                                    "$hardware_key",
                                    "$ip_publica",
                                    "$puerto",
                                    "$ipLocal",
                                    tipoComunicacion
                                )
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()

            }
            if (info_app.comando == "shutdown_svr") { //Cerrar Sesion del Servidor
                desc = "Confirma que desea apagar el equipo"
                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (
                                ShutdownNegocio(
                                    NomNegocio,
                                    "$hardware_key",
                                    "$idNegocio",
                                    "$ip_publica",
                                    "$puerto",
                                    "$ipLocal",
                                    tipoComunicacion
                                )
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()

            }



            if (info_app.comando == "start_svr") { //Cerrar Sesion del Servidor
                desc = "Confirma que desea encender el equipo"
                var dialogBuilder = AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (

                                WakeOnLanEquipo(
                                    NomNegocio,
                                    "$hardware_key",
                                    "$idNegocio",
                                    "$ip_publica",
                                    "$puerto",
                                    "$ipLocal"
                                )
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()


            }


        }


        if (info_app.tipo_id == 4) {//Base de datos

            ConsultaDinamicaBdNegocio(
                info_app,
                "$hardware_key",
                "$ip_publica",
                puerto,
                NomNegocio,
                ipLocal,
                tipoComunicacion
            )

        }

        if (info_app.tipo_id == 5) {//Consulta estado del servidor

            estatusActNegocio(
                info_app.id,
                info_app.origen,
                NomNegocio,
                "$hardware_key",
                tipoComunicacion
            )
        }


        if (info_app.tipo_id == 6) {//Esplorador de Archivos
            ExplorarDirectorio(
                info_app.exploracion_ruta,
                hardware_key,
                idNegocio,
                NomNegocio,
                ip_publica,
                puerto,
                ipLocal,
                tipoComunicacion
            )
        }


        if (info_app.tipo_id == 7) {//Screenshot bajo demanda del servidor
            ScreenshotServidor(
                hardware_key,
                idNegocio,
                NomNegocio,
                ip_publica,
                puerto,
                boveda,
                ipLocal,
                tipoComunicacion
            )
        }

        if (info_app.tipo_id == 8) {//Screenshot bajo demanda del servidor

            var desc = ""
            if (info_app.eliminacion_tipo == "eliminar_archivo") {
                desc = "Desea eliminar el archivo ${info_app.eliminacion_nombre_archivo}"
            } else {
                desc = "Desea eliminar el directorio ${info_app.eliminacion_ruta_archivo}"
            }
            var dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage(desc)
                .setCancelable(true)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    (

                            chkDelArcDirServidor(
                                hardware_key,
                                idNegocio,
                                NomNegocio,
                                ip_publica,
                                puerto,
                                "${info_app.eliminacion_tipo}",
                                "${info_app.eliminacion_ruta_archivo}",
                                "${info_app.eliminacion_nombre_archivo}",
                                ipLocal,
                                tipoComunicacion
                            )
                            )
                })
                .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("${info_app.nombre_equipo}")
            alert.show()


        }

        if (info_app.tipo_id == 9) {//Informa si un programa esta en ejecucion
            ConsultaInfoProgramaExec(
                info_app,
                "$hardware_key",
                "$ip_publica",
                puerto,
                NomNegocio,
                ipLocal,
                tipoComunicacion
            )
        }

        if (info_app.tipo_id == 16) {//CONSULTA API

            ConsultaApiExtNegocio(
                info_app,
                "$hardware_key",
                "$ip_publica",
                puerto,
                NomNegocio,
                ipLocal,
                tipoComunicacion
            )

        }


    }


    fun getGetUsuariosPermitidosXEquipo(
        info_app: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List,
        NomNegocio: String,
        id_equipo: Int,
        ip_publica: String,
        ip_local: String,
        hardware_key: String,
        puerto: Int,
        boveda: String,
        tipoComunicacion: Int
    ) {
        if (!info_app.aplicacion_usuario.isNullOrEmpty() && info_app.aplicacion_usuario != "SN") {

            showExecuteProgramServer(
                info_app.alias_aplicacion,
                "$id_equipo",
                ip_publica,
                ip_local,
                hardware_key,
                "$puerto",
                info_app.aplicacion_ruta_aplicacion,
                info_app.aplicacion_nombre_aplicacion,
                info_app.aplicacion_parametros_aplicacion,
                tipoComunicacion,
                "SN",
                info_app.aplicacion_usuario,
                info_app.aplicacion_pwd
            )

        } else {


            val getVigenciaLic = RestEngine.getRestEngineInicial(databaseHelper_Data, requireContext()).create(APIService::class.java)
            val result_usuarios_permitidos = getVigenciaLic.getUsuarioPermitidosEjecEquipo(
                id_equipo,
                "JWT ${BuildConfig.TOKEN_MAESTRO}",
                "application/json"
            )

            result_usuarios_permitidos.enqueue(object : Callback<DataUsuariosPermitidosEjecucion> {
                override fun onResponse(
                    call: Call<DataUsuariosPermitidosEjecucion>,
                    response: Response<DataUsuariosPermitidosEjecucion>
                ) {

                    if (response.isSuccessful) {
                        val usuariosEquipo = response.body()?.data.orEmpty()

                        val activos = usuariosEquipo.filter { it.estatus_act == "Activo" }
                        val inactivos = usuariosEquipo.filter { it.estatus_act != "Activo" }

                        if (activos.isEmpty() && inactivos.isEmpty()) {
                            showNoUsuariosDialog(NomNegocio)
                            return
                        }

                        val layoutInflater = LayoutInflater.from(context)
                        val view = layoutInflater.inflate(R.layout.dialog_usuarios, null)

                        val listViewActivos = view.findViewById<ListView>(R.id.listActivos)
                        val listViewInactivos = view.findViewById<ListView>(R.id.listInactivos)
                        val tvMostrarInactivos = view.findViewById<TextView>(R.id.tvMostrarInactivos)

                        val arrActivos = activos.map { it.id_usuario_activo_id }
                        val arrInactivos = inactivos.map { it.id_usuario_activo_id }

                        val listaActivos = arrActivos.map { it.replace("_", " ") }
                        val listaInactivos = arrInactivos.map { it.replace("_", " ") }

                        var usuarioSeleccionado: String? = null

                        listViewActivos.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_single_choice, listaActivos)
                        listViewActivos.choiceMode = ListView.CHOICE_MODE_SINGLE
                        listViewActivos.setOnItemClickListener { _, _, pos, _ ->
                            usuarioSeleccionado = arrActivos[pos]

                            // Limpiar selección de la lista de inactivos
                            listViewInactivos.clearChoices()
                            (listViewInactivos.adapter as ArrayAdapter<*>).notifyDataSetChanged()

                            listViewActivos.setItemChecked(pos, true) // marcar el actual
                        }

                        listViewInactivos.adapter = object : ArrayAdapter<String>(
                            requireContext(),
                            android.R.layout.simple_list_item_single_choice,
                            listaInactivos
                        ) {
                            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                val view = super.getView(position, convertView, parent)
                                val textView = view.findViewById<TextView>(android.R.id.text1)
                                textView.setTextColor(ContextCompat.getColor(context, R.color.colorUsuInactivos)) // Aplica color gris al texto
                                return view
                            }
                        }
                        listViewInactivos.choiceMode = ListView.CHOICE_MODE_SINGLE
                        listViewInactivos.setOnItemClickListener { _, _, pos, _ ->
                            usuarioSeleccionado = arrInactivos[pos]

                            // Limpiar selección de la lista de activos
                            listViewActivos.clearChoices()
                            (listViewActivos.adapter as ArrayAdapter<*>).notifyDataSetChanged()

                            listViewInactivos.setItemChecked(pos, true) // marcar el actual
                        }

                        tvMostrarInactivos.setOnClickListener {
                            val isVisible = listViewInactivos.visibility == View.VISIBLE
                            listViewInactivos.visibility = if (isVisible) View.GONE else View.VISIBLE
                            tvMostrarInactivos.text = if (isVisible) "Mostrar inactivos" else "Ocultar inactivos"
                        }

                        val dialog = AlertDialog.Builder(context)
                            .setView(view)
                            .setPositiveButton("Aceptar") { dialogInterface, _ ->
                                usuarioSeleccionado?.let { idUsuario ->
                                    showExecuteProgramServer(
                                        info_app.alias_aplicacion,
                                        "$id_equipo",
                                        ip_publica,
                                        ip_local,
                                        hardware_key,
                                        "$puerto",
                                        info_app.aplicacion_ruta_aplicacion,
                                        info_app.aplicacion_nombre_aplicacion,
                                        info_app.aplicacion_parametros_aplicacion,
                                        tipoComunicacion,
                                        idUsuario,
                                        info_app.aplicacion_usuario,
                                        info_app.aplicacion_pwd
                                    )
                                }
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create()

                        dialog.show()

                    } else {
                        showNoUsuariosDialog(NomNegocio)
                    }
                }

                override fun onFailure(call: Call<DataUsuariosPermitidosEjecucion>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Error al recuperar listado de usuarios permitidos del equipo: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun showNoUsuariosDialog(titulo: String) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(titulo)
            .setMessage("No existen usuarios activos en el equipo")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    fun sendTerminaProcesoEquipo(
        accion: String,
        idnegocio: String,
        ipPublica: String,
        ipLocal: String,
        idHardware: String,
        iPuerto: String,
        ruta_app: String,
        nombre_app: String,
        parametros_app: String,
        tipoComunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if (tipoComunicacion == 0) {

            val getTokenNegocio: APIService =
                RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data)
                    .create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //   Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(
                            ipPublica,
                            iPuerto,
                            ipLocal,
                            databaseHelper_Data
                        ).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.endAPPServidor(
                            "$Movil_Id",
                            "$nombre_Movil",
                            "$ruta_app",
                            "$nombre_app",
                            "$parametros_app",
                            "$resultado",
                            "application/json"
                        )

                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                if (t.message == "timeout") {
                                    Toast.makeText(
                                        requireContext(),
                                        "Se finalizo en el equipo",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                Toast.makeText(
                                    requireContext(),
                                    "Se finalizo en el equipo",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        })


                    } else {

                        Toast.makeText(
                            requireContext(),
                            "No esta autorizado a realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            })


        } else if (tipoComunicacion == 1) {


            var parametros = "$nombre_app"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setTerminaProcesoEquipo\",\"parametros\":\"$parametros\"}")


        }


    }


    fun chkDelArcDirServidor(
        idHardware: String,
        idNegocio: Int,
        nomNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        tipoEliminacio: String,
        rutaEliminacion: String,
        archivoEliminacion: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {


        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)
        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                Toast.makeText(requireContext(), "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val getHorario: APIService =
                        RestEngine.getRestEngine(databaseHelper_Data, requireContext())
                            .create(APIService::class.java)
                    val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> =
                        getHorario.getHorarioMovil(
                            Movil_Id,
                            idNegocio,
                            "$idDispositivo",
                            "JWT $token",
                            "application/json"
                        )
                    resultHorario.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                requireContext(),
                                "Error al recuperar horarios movil",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>
                        ) {
                            val resHorariosmv = response.body()
                            if (resHorariosmv != null) {
                                val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> =
                                    resHorariosmv.data.toList()
                                val smDateFormat = SimpleDateFormat("HH:mm:ss")
                                val currenTime: String = smDateFormat.format(Date())
                                val horaInicial = horario[0].horario_desde
                                var horaFinal = horario[0].horario_hasta
                                val dia_semana = horario[0].dia_semana
                                if (horaFinal == "00:00:00") {
                                    horaFinal = "23:59:59"
                                }
                                if (horaInicial != "SN" && horaFinal != "SN") {
                                    if (currenTime.compareTo(horaInicial) > 0 && currenTime.compareTo(
                                            horaFinal
                                        ) < 0 && dia_semana == 1
                                    ) {

                                        DelArcDirServidor(
                                            idHardware,
                                            idNegocio,
                                            nomNegocio,
                                            ipNegocio,
                                            iPuertoNegocio,
                                            tipoEliminacio,
                                            rutaEliminacion,
                                            archivoEliminacion,
                                            ipLocal,
                                            tipoComunicacion
                                        )
                                    } else {

                                        Toast.makeText(
                                            requireContext(),
                                            "El dispositivo se encuentra fuera del horario laboral para este negocio",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } else {
                                    DelArcDirServidor(
                                        idHardware,
                                        idNegocio,
                                        nomNegocio,
                                        ipNegocio,
                                        iPuertoNegocio,
                                        tipoEliminacio,
                                        rutaEliminacion,
                                        archivoEliminacion,
                                        ipLocal,
                                        tipoComunicacion
                                    )
                                }


                            } else {
                                DelArcDirServidor(
                                    idHardware,
                                    idNegocio,
                                    nomNegocio,
                                    ipNegocio,
                                    iPuertoNegocio,
                                    tipoEliminacio,
                                    rutaEliminacion,
                                    archivoEliminacion,
                                    ipLocal,
                                    tipoComunicacion
                                )
                            }
                            ///////////////
                        }

                    })


                } else {
                    Toast.makeText(
                        requireContext(),
                        "No tiene permisos para realizar esta accion",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        })


    }


    fun DelArcDirServidor(
        idHardware: String,
        idNegocio: Int,
        nomNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        tipoEliminacio: String,
        rutaEliminacion: String,
        archivoEliminacion: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {

        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                ipNegocio,
                iPuertoNegocio.toString(),
                ipLocal,
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(
                            ipNegocio,
                            iPuertoNegocio.toString(),
                            ipLocal,
                            databaseHelper_Data
                        ).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.delArchDirNegocio(
                            "$rutaEliminacion",
                            "$archivoEliminacion",
                            "$tipoEliminacio",
                            false,
                            "$resultado",
                            "application/json"
                        )
                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(
                                    requireContext(),
                                    "Error al eliminar elemento del Servidor",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                if (resultado != null) {
                                    Toast.makeText(requireContext(), "$resultado", Toast.LENGTH_LONG).show()
                                }

                            }

                        })

                    }
                }

            })
        } else if (tipoComunicacion == 1) {

            webSocketService = LoginActivity.webSocketService

            var ruta = "$rutaEliminacion|$archivoEliminacion|$tipoEliminacio|false"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setDelArchivosEquipo\",\"parametros\":\"$ruta\"}")


        }


    }


    fun showExecuteProgramServer(
        accion: String,
        idnegocio: String,
        ipPublica: String,
        idHardware: String,
        iPuerto: String,
        ruta_app: String,
        nombre_app: String,
        parametros_app: String,
        ipLocal: String,
        tipoComunicacion: Int,
        usuarioejecucion: String,
        aplicacionusr: String,
        aplicacionpwd: String
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService =
                RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data)
                    .create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            return@OnClickListener
                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //  Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(
                            ipPublica,
                            iPuerto,
                            ipLocal,
                            databaseHelper_Data
                        ).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.startAPPServidor(
                            "$Movil_Id",
                            "$nombre_Movil",
                            "$ruta_app",
                            "$nombre_app",
                            "$parametros_app",
                            "$resultado",
                            "application/json"
                        )

                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                if (t.message == "timeout") {
                                    Toast.makeText(
                                        requireContext(),
                                        "La apliacion se esta ejecutando",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                Toast.makeText(
                                    requireContext(),
                                    "La apliacion se esta ejecutando",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                        })


                    } else {

                        Toast.makeText(
                            requireContext(),
                            "No esta autorizado a realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            })
        } else if (tipoComunicacion == 1) {

            webSocketService = LoginActivity.webSocketService

            var parametros = "$ruta_app|$nombre_app|$parametros_app|$usuarioejecucion|$aplicacionusr|$aplicacionpwd"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"exeAplicacionEquipo\",\"parametros\":\"$parametros\"}")


        }


    }


    fun LogOffNegocio(
        nomNegocio: String,
        idHardware: String,
        ipNegocio: String,
        iPuertoNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil


        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                "$ipNegocio",
                "$iPuertoNegocio",
                "$ipLocal",
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            return@OnClickListener

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio(
                            "$ipNegocio",
                            "$iPuertoNegocio",
                            "$ipLocal",
                            databaseHelper_Data
                        ).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer(
                            "LogOff",
                            "1000003",
                            "$Movil_Id",
                            "$Movil_Nom",
                            "$nomNegocio",
                            "$resultado",
                            "application/json"
                        )

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(
                                        requireContext(),
                                        "Se ejecuto la accion",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "No tiene permisos para realizar esta accion",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No tiene permisos para realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            })
        } else if (tipoComunicacion == 1) {
            webSocketService = LoginActivity.webSocketService
            var parametros = "LogOff"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }


    }


    fun RebootNegocio(
        nomNegocio: String,
        idHardware: String,
        ipNegocio: String,
        iPuertoNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if (tipoComunicacion == 0) {

            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                "$ipNegocio",
                "$iPuertoNegocio",
                "$ipLocal",
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            return@OnClickListener

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio(
                            "$ipNegocio",
                            "$iPuertoNegocio",
                            "$ipLocal",
                            databaseHelper_Data
                        ).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer(
                            "Reboot",
                            "1000001",
                            "$Movil_Id",
                            "$Movil_Nom",
                            "$nomNegocio",
                            "$resultado",
                            "application/json"
                        )

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(
                                        requireContext(),
                                        "Se ejecuto la accion",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        "No tiene permisos para realizar esta accion",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(
                            requireContext(),
                            "No tiene permisos para realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            })
        } else if (tipoComunicacion == 1) {

            webSocketService = LoginActivity.webSocketService

            var parametros = "Reboot"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }


    }

    fun WakeOnLanEquipo(
        nomNegocio: String,
        idHardware: String,
        idNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: String,
        iPLocal: String
    ) {

        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)

        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService =
                        RestEngine.getRestEngine(databaseHelper_Data, context!!)
                            .create(APIService::class.java)
                    val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> =
                        negociosService.getEnciendeEquipo(
                            "$idHardware",
                            "JWT $token",
                            "application/json"
                        )
                    resultEnciendeEquipo.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>
                        ) {
                            val resultadoAccion = response.body()
                            if (resultadoAccion != null) {
                                var respuesta = resultadoAccion.data.toString()
                                Toast.makeText(context, "$respuesta", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                            t: Throwable
                        ) {
                            var desc =
                                "No fue posible encender el equipo, por favor contacte a su administrador"
                            var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                            dialogBuilder.setMessage(desc)
                                .setCancelable(false)
                                .setNegativeButton(
                                    "Aceptar",
                                    DialogInterface.OnClickListener { view, _ ->

                                        view.dismiss()
                                    })
                            var alert = dialogBuilder.create()
                            alert.setTitle("")
                            alert.show()
                        }

                    })

                }

            }

            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                var desc =
                    "No fue posible recuperar token de acceso en la accion de encendido de equipo"
                var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("")
                alert.show()
            }

        })


    }


    fun ShutdownNegocio(
        nomNegocio: String,
        idHardware: String,
        idNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                "$ipNegocio",
                "$iPuertoNegocio",
                "$ipLocal",
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            return@OnClickListener

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    // Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio(
                            "$ipNegocio",
                            "$iPuertoNegocio",
                            "$ipLocal",
                            databaseHelper_Data
                        ).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer(
                            "Shutdown",
                            "1000002",
                            "$Movil_Id",
                            "$Movil_Nom",
                            "$nomNegocio",
                            "$resultado",
                            "application/json"
                        )

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<String>,
                                response: Response<String>
                            ) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(
                                        context,
                                        "El servidor se esta apagado",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "No tiene permisos para realizar esta accion",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(
                            context,
                            "No tiene permisos para realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }

            })

            updBanderaNegocio(idNegocio)
        } else if (tipoComunicacion == 1) {
            webSocketService = LoginActivity.webSocketService

            var parametros = "Shutdown"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }


    }


    fun updBanderaNegocio(idNegocio: String) {
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)

        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()

                    client.getEstatusNegocioSvr("$idNegocio", "JWT $token", "application/json")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Toast.makeText(
                                context,
                                "Se actualizo estatus en Servidor de Licencias",
                                Toast.LENGTH_LONG
                            ).show()
                        }, { throwable ->
                            Toast.makeText(
                                context,
                                "Error: al actualizar estatus del Servidor",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        )


                }
            }

        })
    }


    fun estatusActNegocio(
        id_accion: Int,
        id_origen: Int,
        NomNegocio: String,
        hardware_key: String,
        tipocomunicacion: Int
    ) {

        LoginActivity.isReloadViewMonitores = 0
        if (tipocomunicacion == 0) {
            /*
            val accountsIntent = Intent(context, MonitorActivity::class.java)
            accountsIntent.putExtra("id_accion", id_accion.toString())
            accountsIntent.putExtra("id_origen", id_origen.toString())
            accountsIntent.putExtra("nombre_equipo", "$NomNegocio")
            accountsIntent.putExtra("hardware_key", "$hardware_key")
            accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
            requireContext().startActivity(accountsIntent)

             */
        } else if (tipocomunicacion == 1) {
            var idDispositivo: String =
                Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                    .toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo = removeTrailingZeroes(idDispositivo)


            LoginActivity.estatusequipo_id_accion = id_accion
            LoginActivity.estatusequipo_id_origen = id_origen
            webSocketService = LoginActivity.webSocketService
            webSocketService!!.sendMessage("{\"id_hardware\":\"$hardware_key\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getMonitoreoEquipo\",\"parametros\":\"ALL\"}")


        }

    }


    fun ExplorarDirectorio(
        ruta: String,
        idHardware: String,
        idNegocio: Int,
        nomNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        ipLocal: String,
        tipocomunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        if (tipocomunicacion == 0) {

            val UserData =
                com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                    "licencias@dsiin.com",
                    "xhceibEd"
                )

            val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, requireContext())
                .create(APIService::class.java)
            val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
                userService.submit(UserData)
            result.enqueue(object :
                Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                override fun onFailure(
                    call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                    t: Throwable
                ) {
                    Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG)
                        .show()
                }

                override fun onResponse(
                    call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                    response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
                ) {
                    val resultado = response.body()
                    if (resultado != null) {
                        val token = resultado.token.toString()

                        val getHorario: APIService =
                            RestEngine.getRestEngine(databaseHelper_Data, context!!)
                                .create(APIService::class.java)
                        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> =
                            getHorario.getHorarioMovil(
                                Movil_Id,
                                idNegocio,
                                "$idDispositivo",
                                "JWT $token",
                                "application/json"
                            )
                        resultHorario.enqueue(object :
                            Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                            override fun onFailure(
                                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    context,
                                    "Error al recuperar horarios movil",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                                response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>
                            ) {
                                val resHorariosmv = response.body()
                                if (resHorariosmv != null) {
                                    val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> =
                                        resHorariosmv.data.toList()
                                    val smDateFormat = SimpleDateFormat("HH:mm:ss")
                                    val currenTime: String = smDateFormat.format(Date())
                                    val horaInicial = horario[0].horario_desde
                                    var horaFinal = horario[0].horario_hasta
                                    val dia_semana = horario[0].dia_semana
                                    if (horaFinal == "00:00:00") {
                                        horaFinal = "23:59:59"
                                    }
                                    if (horaInicial != "SN" && horaFinal != "SN") {
                                        if (currenTime.compareTo(horaInicial) > 0 && currenTime.compareTo(
                                                horaFinal
                                            ) < 0 && dia_semana == 1
                                        ) {
                                            /*
                                            val accountsIntent = Intent(
                                                context,
                                                activity_directorio_negocios::class.java
                                            )
                                            accountsIntent.putExtra("nom_negocio", nomNegocio)
                                            accountsIntent.putExtra("ip_negocio", ipNegocio)
                                            accountsIntent.putExtra("ip_local", ipLocal)
                                            accountsIntent.putExtra(
                                                "ip_puerto",
                                                iPuertoNegocio.toString()
                                            )
                                            accountsIntent.putExtra("id_hardware", idHardware)
                                            accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                            accountsIntent.putExtra("phoneMovil", phoneMovil)
                                            accountsIntent.putExtra("rutaArchivosDir", ruta)
                                            accountsIntent.putExtra(
                                                "phoneSubscriberId",
                                                phoneSubscriberId
                                            )
                                            accountsIntent.putExtra(
                                                "tipo_comunicacion",
                                                tipocomunicacion.toString()
                                            )
                                            context!!.startActivity(accountsIntent)

                                             */
                                        } else {

                                            Toast.makeText(
                                                context,
                                                "El dispositivo se encuentra fuera del horario laboral para este negocio",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    } else {

                                        /*
                                        val accountsIntent = Intent(
                                            context,
                                            activity_directorio_negocios::class.java
                                        )
                                        accountsIntent.putExtra("nom_negocio", nomNegocio)
                                        accountsIntent.putExtra("ip_negocio", ipNegocio)
                                        accountsIntent.putExtra("ip_local", ipLocal)
                                        accountsIntent.putExtra(
                                            "ip_puerto",
                                            iPuertoNegocio.toString()
                                        )
                                        accountsIntent.putExtra("id_hardware", idHardware)
                                        accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                        accountsIntent.putExtra("phoneMovil", phoneMovil)
                                        accountsIntent.putExtra("rutaArchivosDir", ruta)
                                        accountsIntent.putExtra(
                                            "phoneSubscriberId",
                                            phoneSubscriberId
                                        )
                                        accountsIntent.putExtra(
                                            "tipo_comunicacion",
                                            tipocomunicacion.toString()
                                        )
                                        requireContext().startActivity(accountsIntent)

                                         */



                                    }


                                } else {
                                    /*
                                    val accountsIntent =
                                        Intent(context, activity_directorio_negocios::class.java)
                                    accountsIntent.putExtra("nom_negocio", nomNegocio)
                                    accountsIntent.putExtra("ip_negocio", ipNegocio)
                                    accountsIntent.putExtra("ip_local", ipLocal)
                                    accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                    accountsIntent.putExtra("id_hardware", idHardware)
                                    accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                    accountsIntent.putExtra("phoneMovil", phoneMovil)
                                    accountsIntent.putExtra("rutaArchivosDir", ruta)
                                    accountsIntent.putExtra("phoneSubscriberId", phoneSubscriberId)
                                    accountsIntent.putExtra(
                                        "tipo_comunicacion",
                                        tipocomunicacion.toString()
                                    )
                                    context!!.startActivity(accountsIntent)

                                     */
                                }
                                ///////////////
                            }

                        })


                    }
                }

            })


        } else if (tipocomunicacion == 1) {

            var rutaArchivosDir = ruta
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace("\\\\", "\\")
                rutaArchivosDir = rutaArchivosDir!!.replace("\\", "\\\\")
                rutaArchivosDir = rutaArchivosDir!!.replace("\\", "%2F")
            }
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace(":", "%3A")
            }

            LoginActivity.idHardwareExpArc = idHardware
            LoginActivity.nomEquipoExpArc = nomNegocio
            webSocketService = LoginActivity.webSocketService
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getArchivosEquipo\",\"parametros\":\"$rutaArchivosDir\"}")


        }


        /*



         */


    }

    fun ScreenshotServidor(
        idHardware: String,
        idNegocio: Int,
        nomNegocio: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        iBovedaNegocios: String,
        ipLocal: String,
        tipocomunicacion: Int
    ) {

        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)

        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var Licenciadb: List<Licencia> = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        var configApiVMEquipo = databaseHelper_Data.getControlEquiposAPIVM(idNegocio)


        val token = "${BuildConfig.TOKEN_MAESTRO}"


        val getHorario: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> =
            getHorario.getHorarioMovil(
                Movil_Id,
                idNegocio,
                "$idDispositivo",
                "JWT $token",
                "application/json"
            )
        resultHorario.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                t: Throwable
            ) {
                Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>
            ) {
                val resHorariosmv = response.body()
                if (resHorariosmv != null) {
                    val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> =
                        resHorariosmv.data.toList()
                    val smDateFormat = SimpleDateFormat("HH:mm:ss")
                    val currenTime: String = smDateFormat.format(Date())
                    val horaInicial = horario[0].horario_desde
                    var horaFinal = horario[0].horario_hasta
                    val dia_semana = horario[0].dia_semana
                    if (horaFinal == "00:00:00") {
                        horaFinal = "23:59:59"
                    }
                    if (horaInicial != "SN" && horaFinal != "SN") {
                        if (currenTime.compareTo(horaInicial) > 0 && currenTime.compareTo(horaFinal) < 0 && dia_semana == 1) {

                            if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0) {

                                // (context as DashboardActivity).EnableProgressBarDasboard()
                                ScreenshotServidorXAPI(
                                    idHardware,
                                    idNegocio,
                                    nomNegocio,
                                    tipocomunicacion,
                                    token
                                )
                                Handler().postDelayed({
                                    //(context as DashboardActivity).DisableProgressBarDasboard()
                                }, 10000)


                            } else {
                                if (tipocomunicacion == 0) {
                                    /*
                                    val accountsIntent =
                                        Intent(context, ImageViewActivity::class.java)
                                    accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                    accountsIntent.putExtra("id_hardware", idHardware)
                                    accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                    accountsIntent.putExtra("ip_negocio", ipNegocio)
                                    accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                    accountsIntent.putExtra("ip_local", ipLocal)
                                    accountsIntent.putExtra(
                                        "tipo_comunicacion",
                                        tipocomunicacion.toString()
                                    )
                                    accountsIntent.putExtra("info", "")
                                    context!!.startActivity(accountsIntent)

                                     */

                                } else {
                                    //(context as DashboardActivity).EnableProgressBarDasboard()
                                    webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                                    Handler().postDelayed({
                                        // (context as DashboardActivity).DisableProgressBarDasboard()
                                    }, 10000)

                                }
                            }


                        } else {

                            Toast.makeText(
                                context,
                                "El dispositivo se encuentra fuera del horario laboral para este negocio",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {

                        if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0) {

                            //(context as DashboardActivity).EnableProgressBarDasboard()
                            ScreenshotServidorXAPI(
                                idHardware,
                                idNegocio,
                                nomNegocio,
                                tipocomunicacion,
                                token
                            )

                            Handler().postDelayed({
                                //  (context as DashboardActivity).DisableProgressBarDasboard()
                            }, 10000)

                        } else {
                            if (tipocomunicacion == 0) {
                                /*
                                val accountsIntent = Intent(context, ImageViewActivity::class.java)
                                accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                accountsIntent.putExtra("id_hardware", idHardware)
                                accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                accountsIntent.putExtra("ip_negocio", ipNegocio)
                                accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                accountsIntent.putExtra("ip_local", ipLocal)
                                accountsIntent.putExtra(
                                    "tipo_comunicacion",
                                    tipocomunicacion.toString()
                                )
                                accountsIntent.putExtra("info", "")
                                context!!.startActivity(accountsIntent)

                                 */
                            } else {
                                // (context as DashboardActivity).EnableProgressBarDasboard()
                                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                                Handler().postDelayed({
                                    // (context as DashboardActivity).DisableProgressBarDasboard()
                                }, 10000)
                            }
                        }


                    }


                } else {

                    if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0) {

                        // (context as DashboardActivity).EnableProgressBarDasboard()
                        ScreenshotServidorXAPI(
                            idHardware,
                            idNegocio,
                            nomNegocio,
                            tipocomunicacion,
                            token
                        )
                        Handler().postDelayed({
                            //(context as DashboardActivity).DisableProgressBarDasboard()
                        }, 10000)

                    } else {
                        if (tipocomunicacion == 0) {
                            /*
                            val accountsIntent = Intent(context, ImageViewActivity::class.java)
                            accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                            accountsIntent.putExtra("id_hardware", idHardware)
                            accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                            accountsIntent.putExtra("ip_negocio", ipNegocio)
                            accountsIntent.putExtra("nombre_negocio", nomNegocio)
                            accountsIntent.putExtra("ip_local", ipLocal)
                            accountsIntent.putExtra(
                                "tipo_comunicacion",
                                tipocomunicacion.toString()
                            )
                            accountsIntent.putExtra("info", "")
                            context!!.startActivity(accountsIntent)

                             */
                        } else {
                            //(context as DashboardActivity).EnableProgressBarDasboard()
                            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                            Handler().postDelayed({
                                //  (context as DashboardActivity).DisableProgressBarDasboard()
                            }, 10000)

                        }
                    }


                }
                ///////////////
            }

        })


    }


    fun ScreenshotServidorXAPI(
        idHardware: String,
        idNegocio: Int,
        nomNegocio: String,
        tipocomunicacion: Int,
        token: String
    ) {


        val negociosService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> =
            negociosService.getScreenshotEquipoGcVm("$idHardware", "JWT $token", "application/json")
        resultEnciendeEquipo.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>
            ) {
                val resultadoAccion = response.body()
                if (resultadoAccion != null) {
                    var respuesta = resultadoAccion.data.toString()
                    //(context as DashboardActivity).DisableProgressBarDasboard()
                    /*
                    val accountsIntent = Intent(context, ImageViewActivity::class.java)
                    accountsIntent.putExtra("bovedaSoftware", "")
                    accountsIntent.putExtra("id_hardware", idHardware)
                    accountsIntent.putExtra("ip_puerto", "")
                    accountsIntent.putExtra("ip_negocio", "")
                    accountsIntent.putExtra("ip_local", "")
                    accountsIntent.putExtra("nombre_negocio", nomNegocio)
                    accountsIntent.putExtra("tipo_comunicacion", "1")
                    accountsIntent.putExtra("url_imagen", "$respuesta")
                    accountsIntent.putExtra("info", "")
                    context!!.startActivity(accountsIntent)

                     */


                }
            }

            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                t: Throwable
            ) {
                var desc =
                    "No fue posible generar screenshot del equipo, por favor contacte a su administrador"
                var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                dialogBuilder.setMessage(desc)
                    .setCancelable(false)
                    .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("")
                alert.show()
            }

        })


    }


    fun ConsultaInfoProgramaExec(
        accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List,
        idHardware: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        iNomNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {
        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                "$ipNegocio",
                "$iPuertoNegocio",
                "$ipLocal",
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(
                        context,
                        "Error al recuperar token del negocio",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {
                        val resultAccion: Call<ResponseBody> = getTokenNegocio.getInfoProgramSvr(
                            "${accion.estado_programa_nombre}",
                            "$resultado",
                            "application/json"
                        )
                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(
                                    context,
                                    "Error al recuperar informacion del porgrama",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val resultJson = response.body()!!.string()

                                var dialogBuilder = AlertDialog.Builder(requireContext())
                                dialogBuilder.setMessage("$resultJson")
                                    .setCancelable(false)
                                    .setPositiveButton(
                                        "Aceptar",
                                        DialogInterface.OnClickListener { dialog, id ->
                                            dialog.dismiss()
                                        })
                                var alert = dialogBuilder.create()
                                alert.setTitle("$iNomNegocio")
                                alert.show()


                            }

                        })

                    }
                }

            })
        } else if (tipoComunicacion == 1) {
            webSocketService = LoginActivity.webSocketService
            var parametros = "${accion.estado_programa_nombre}"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaProgramaEquipo\",\"parametros\":\"$parametros\"}")

        }


    }


    fun ConsultaDinamicaBdNegocio(
        accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List,
        idHardware: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        iNomNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {

        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        if (tipoComunicacion == 0) {
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(
                "$ipNegocio",
                "$iPuertoNegocio",
                "$ipLocal",
                databaseHelper_Data
            ).create(APIService::class.java)
            val result_token: Call<String> =
                getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            databaseHelper_Data.deleteinfoAccion()
            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc =
                        "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            return@OnClickListener
                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    // Toast.makeText(context, "Error al recuperar token del negocio", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val resultAccion: Call<ResponseBody> =
                            getTokenNegocio.getConsultaDinamicaBdSvr(
                                "${accion.origen}",
                                "${accion.id}",
                                "${accion.id_negocio_id}",
                                "${accion.tipo_id}",
                                "$resultado",
                                "application/json"
                            )

                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val resultJson = response.body()!!.string()
                                if (resultJson.contains("INSERT_OK")) {

                                    Toast.makeText(
                                        context,
                                        "Se inserto registro(s) correctamente",
                                        Toast.LENGTH_LONG
                                    ).show()

                                } else if (resultJson.contains("DELETE_OK")) {
                                    Toast.makeText(
                                        context,
                                        "Se eliminaron registro(s) correctamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else if (resultJson.contains("UPDATE_OK")) {
                                    Toast.makeText(
                                        context,
                                        "Se actualizan registro(s) correctamente",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {

                                    var newInfoAccion =
                                        com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
                                            result = resultJson
                                        )
                                    databaseHelper_Data.addInfoAccion(newInfoAccion)
                                    val accountsIntent =
                                        Intent(context, ConsultaDinamicaActivity::class.java)
                                    accountsIntent.putExtra(
                                        "titulo_consulta",
                                        "${accion.titulo_aplicacion}"
                                    )
                                    accountsIntent.putExtra("nombre_negocio", "$iNomNegocio")
                                    //accountsIntent.putExtra("result_query", result)
                                    context!!.startActivity(accountsIntent)
                                }


                            }


                        })

                    } else {
                        Toast.makeText(
                            context,
                            "No tiene permisos para realizar esta accion",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            })
        } else if (tipoComunicacion == 1) {


            //VALIDAMOS SI EXISTEN PARAMETROS DE LA CONSULTA
            var resParams = "SN"
            if(databaseHelper_Data.chkExistControlParamsAcc(accion.id)){
                val consulta = databaseHelper_Data.getConsultaControlParamsAccDB(accion.id)
                val allparams =  databaseHelper_Data.getAllParamsAccDB(accion.id)

                mostrarDialogoTablaDeParametros(allparams, consulta) { listaActualizada ->
                    if (listaActualizada != null) {
                        // Ejemplo: concatenar en una cadena
                        val resultado = listaActualizada.joinToString("-") {
                            "${it.campo}=${it.valor}(${it.tipo})"
                        }
                        resParams = resultado

                        webSocketService = LoginActivity.webSocketService
                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo = "$idHardware"
                        LoginActivity.bd_parametros_accion =
                            "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                        var parametros =
                            "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL"
                        webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")

                    }else{
                        Toast.makeText(context, "Accion cancelada", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{

                webSocketService = LoginActivity.webSocketService
                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo = "$idHardware"
                LoginActivity.bd_parametros_accion =
                    "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                var parametros =
                    "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL"
                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")

            }


        }


    }



    //**************************

    fun mostrarDialogoTablaDeParametros(
        lista: java.util.ArrayList<ItemParametrosAccDB>,
        consulta: String,
        onComplete: (java.util.ArrayList<ItemParametrosAccDB>?) -> Unit
    ) {
        val recyclerView = RecyclerView(requireContext()).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ParametrosAdapter(lista, consulta) { cadena, valor ->
                obtenerNombreDeCampoPorValor(cadena, valor) ?: valor
            }

            val divider = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
            ContextCompat.getDrawable(requireContext(), R.drawable.divider_line)?.let {
                divider.setDrawable(it)
            }
            addItemDecoration(divider)

        }

        val container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * context.resources.displayMetrics.density).toInt() // 16dp
            setPadding(padding, 0, padding, 0)
        }

        val headerView = LayoutInflater.from(requireContext()).inflate(R.layout.item_parametro_header, container, false)
        container.addView(headerView)
        container.addView(recyclerView)

        val scrollView = ScrollView(requireContext())
        scrollView.addView(container)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("$consulta")
            .setView(scrollView)
            .setCancelable(false)
            .setPositiveButton("Aceptar", null)
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
                onComplete(null)
            }
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                for (item in lista) {
                    val valor = item.valor?.trim()
                    if (valor.isNullOrBlank()) {
                        Toast.makeText(requireContext(), "Falta valor para ${item.campo}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    when (item.tipo?.uppercase()) {
                        "INTEGER" -> if (valor.toIntOrNull() == null) {
                            Toast.makeText(requireContext(), "${item.campo} debe ser número entero", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "DECIMAL" -> if (valor.toDoubleOrNull() == null) {
                            Toast.makeText(requireContext(), "${item.campo} debe ser número decimal", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "TEXT" -> {} // Nada extra
                        else -> {
                            Toast.makeText(requireContext(), "Tipo no reconocido: ${item.tipo}", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                    }
                }

                dialog.dismiss()
                onComplete(lista)
            }
        }

        dialog.show()
    }


    fun obtenerNombreDeCampoPorValor(cadena: String, valorConInterrogacion: String): String? {
        val escapedValor = Regex.escape(valorConInterrogacion)

        // Patrón para JSON/diccionario: 'campo':'1?' o "campo":"1?"
        val regexJson = Regex("""['"](\w+)['"]\s*:\s*['"]$escapedValor['"]""")
        regexJson.find(cadena)?.let { return it.groupValues[1] }

        // Patrón para SQL-like: campo = valor?
        val regexSql = Regex("""(\w+)\s*=\s*$escapedValor""")
        regexSql.find(cadena)?.let { return it.groupValues[1] }

        return null
    }

    //**************************



    fun ConsultaApiExtNegocio(
        accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List,
        idHardware: String,
        ipNegocio: String,
        iPuertoNegocio: Int,
        iNomNegocio: String,
        ipLocal: String,
        tipoComunicacion: Int
    ) {

        var idDispositivo: String =
            Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID)
                .toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val phoneMovil: String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        if (tipoComunicacion == 1) {

            //VALIDAMOS SI EXISTEN PARAMETROS DE LA CONSULTA
            var resParams = "SN"
            if(databaseHelper_Data.chkExistControlParamsAccAPI(accion.id)){
                val consulta = databaseHelper_Data.getConsultaControlParamsAccAPI(accion.id)
                val allparams =  databaseHelper_Data.getAllParamsAccDB(accion.id)

                mostrarDialogoTablaDeParametros(allparams, consulta) { listaActualizada ->
                    if (listaActualizada != null) {
                        // Ejemplo: concatenar en una cadena
                        val resultado = listaActualizada.joinToString("-") {
                            "${it.campo}=${it.valor}(${it.tipo})"
                        }
                        resParams = resultado

                        webSocketService = LoginActivity.webSocketService
                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo = "$idHardware"
                        LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                        var parametros = "${accion.id}|$resParams"
                        webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")

                    }else{
                        Toast.makeText(context, "Accion cancelada", Toast.LENGTH_SHORT).show()
                    }

                }
            }else{

                webSocketService = LoginActivity.webSocketService
                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo = "$idHardware"
                LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                var parametros = "${accion.id}|$resParams"
                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")


            }
        }


    }


    private fun magicPacket(mac: String, ip: String, PORT: Int = 7) {

        val packet = buildPacket(mac)
        val datagramSocket = DatagramSocket()
        try {
            datagramSocket.send(
                DatagramPacket(
                    packet,
                    packet.size,
                    InetAddress.getByName(ip),
                    PORT
                )
            )
            datagramSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun buildPacket(mac: String): ByteArray {
        // prepare packet   (6 bytes header) (16 * 6 bytes MAC)
        val temp = ByteArray(6 + 6 * 16)
        val macBytes = ByteArray(6)
        for (i in 0..5) {
            temp[i] = 0xFF.toByte()
            macBytes[i] = mac.split(":", "-")[i].toInt(16).toByte()
        }
        for (i in 6 until temp.size step macBytes.size) System.arraycopy(
            macBytes,
            0,
            temp,
            i,
            macBytes.size
        )
        return temp
    }


    fun getMovilData(): String {
        val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return "S/N"
        }

        var telNumber = tm.line1Number
        if (telNumber != null) {
            if (telNumber != "+52" && telNumber.count() > 10) {
                telNumber = telNumber.substring(telNumber.length - 10, telNumber.length)
            }
            return telNumber
        } else {

            return "S/N"

        }


    }

    private fun removeLeadingZeroes(s: String): String {
        var index: Int = 0
        while (index < s.length - 1) {
            if (s[index] != '0') {
                break
            }
            index++
        }
        return s.substring(index)
    }

    private fun removeTrailingZeroes(s: String): String {
        var index: Int = s.length - 1
        while (index > 0) {
            if (s[index] != '0') {
                break
            }
            index--
        }
        return s.substring(0, index + 1)
    }

    fun getMovilIdSuscriber(): String {
        val tm = requireContext().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return "S/N"
        }

        val telNumber = "S/N"
        if (telNumber != null) {
            return telNumber
        } else {

            return "S/N"

        }


    }


}