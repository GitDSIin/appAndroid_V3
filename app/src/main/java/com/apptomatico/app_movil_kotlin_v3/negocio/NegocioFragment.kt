package com.apptomatico.app_movil_kotlin_v3.negocio

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.MainActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.WebSocketServerConn
import com.apptomatico.app_movil_kotlin_v3.UnexpectedCrashSaver
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_negocio
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.webSocketService
import com.apptomatico.app_movil_kotlin_v3.model.AccionesFavDataCarrousel
import com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List
import com.apptomatico.app_movil_kotlin_v3.model.ControlAlertasData
import com.apptomatico.app_movil_kotlin_v3.model.ControlEstadoEquipos
import com.apptomatico.app_movil_kotlin_v3.model.DataControlMonitoreablesFav
import com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosEjecucion
import com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List
import com.apptomatico.app_movil_kotlin_v3.monitoreables.ui.model.ItemAccionesMonitoreables
import com.apptomatico.app_movil_kotlin_v3.negocio.ChildFragment.Companion.NegociosHomeAdapter
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.Rotate
import com.rommansabbir.animationx.Zoom
import com.rommansabbir.animationx.animationXFade
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt
import kotlin.text.count
import kotlin.text.substring

class NegocioFragment: Fragment() {
    private var TAG_ACT:  String = "NegocioFragment"
    //private val executorServiceMonitores: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    //private val executorServiceEstatusEquipos: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()


    lateinit var rootView: View


    var swipeRefreshLayoutP: SwipeRefreshLayout? = null

    private var progressIconAltFav: ProgressBar? = null
    private var progress_icon_mnt_fav: ProgressBar? = null
    private var progress_icon_acc_fav: ProgressBar? = null
    private var progressEquiposAll: ProgressBar? = null
    private var linearLayoutAltFav: LinearLayout? = null
    private var ibtnReloadFavMonitor: ImageView? = null
    private var linearLayoutAccFav: LinearLayout? = null
    private var btnUpdActFavorita: ImageView? = null


    private var vwpopupModalALertas: View? = null
    private var imgModalAlertasReturn: ImageView? = null
    private var mgDefaultMntFav: ImageView? = null
    private var txtTituloAlertaModal: TextView? = null
    private var txtDescAlertaModal: TextView? = null
    private var txtFecAlertaModal: TextView? = null
    private var txtHoraAlertaModal: TextView? = null


    private var vwpopupModalMonitoreables: View? = null
    private var cardModalViwMonitoreable: ExpandableListView? = null
    private var btnModalMonitoreablesReturn: ImageView? = null


    private lateinit var donutGaugeUpdateMonitores: ProgressBar

    private lateinit var databaseHelper_Data: DatabaseHelper
    private var arrUsuariosPermitidos: MutableList<String>  = ArrayList()


    private var carrouselMonitorItems = ArrayList<com.apptomatico.app_movil_kotlin_v3.model.MonitoreablesCarrousel_List>()
    private var dataListaAlertas: List<com.apptomatico.app_movil_kotlin_v3.model.Alertas_List> = ArrayList()
    private var possibleItems: List<com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List> = ArrayList()
    private var  dialogPopop: androidx.appcompat.app.AlertDialog? = null
    private var banderaSeCargaronGraficosFavoritos: Boolean = false
    private var adapter: ExpandableListAdapter? = null
    var alertas_fav_data: List<com.apptomatico.app_movil_kotlin_v3.model.ControlAlertasFavData> = ArrayList()
    var alertas_list_fav_data: List<com.apptomatico.app_movil_kotlin_v3.model.ControlAlertasData> = ArrayList()
    private var titleList: List<String>? = null

    val client by lazy { APIService.create() }

    private var ECHO_URL_WEB_SOCKET:  String = ""
    lateinit var clientsw: OkHttpClient
    lateinit var  requestsw: Request

    private var vwpopupModalAlertas: View? = null
    private var btnModalAlertasReturn_2: Chip? = null
    private var imgAltFavEstSvr: Switch? = null
    private var imgAltIniSesion: Switch? = null
    private var imgAltCierreSesion: Switch? = null
    private var imgAltEnvSistema: Switch? = null
    private var imgAltErrorSistema: Switch? = null
    private var imgFavAltEstadoEquipo: Switch? = null

    private var itextInputEncenderEquipo: TextInputEditText? = null
    private var itextInputApagadoEquipo: TextInputEditText? = null
    private var itextInputInicioSesion: TextInputEditText? = null
    private var itextInputCierreSesion: TextInputEditText? = null
    private var itextInputErrorEquipo: TextInputEditText? = null
    private var itextInputEstadoEquipo: TextInputEditText? = null
    private var iclosebtnAlertas: LinearLayout? = null
    private var lblsubTituloAlertasFav: TextView? = null



    var mNameInstanceFragment: String? = null

    companion object{
        var NegociosRecyclerView: RecyclerView? = null
        var vwModalActiva: Boolean = false
        var accionListMonitoreables = ArrayList<ItemAccionesMonitoreables>()
        var modal_id_accion: Int = 0
        var modal_tipoGrafico: String = ""
        var modal_hardware: String = ""
        var modal_monitores_activo: Boolean = false
        var linearLayoutMntFav: LinearLayout? = null
        var framrNegocioList: FrameLayout? = null
        var iprogressBarGeneralInicio: ProgressBar? = null
        var iAlertDialogSc: androidx.appcompat.app.AlertDialog? = null
        var valNegAccionesDinamicas : List<AcionesDinamicas_List> = ArrayList()

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try{

            // Inflate the layout for this fragment
            rootView =  inflater.inflate(R.layout.negocio_activity, container, false)
            Thread.setDefaultUncaughtExceptionHandler(UnexpectedCrashSaver(requireContext()))
            databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
            swipeRefreshLayoutP = rootView.findViewById(R.id.swipeRefreshPrincipal)
            iprogressBarGeneralInicio = rootView.findViewById(R.id.progressBarGeneral)
            progressIconAltFav = rootView.findViewById(R.id.progressIconAltFav)
            progress_icon_mnt_fav = rootView.findViewById(R.id.progressIconMntfav)
            progress_icon_acc_fav = rootView.findViewById(R.id.progressIconAccfavx)
            progressEquiposAll = rootView.findViewById<ProgressBar>(R.id.progressEquiposAll)
            linearLayoutAccFav = rootView.findViewById<View>(R.id.linearAccFav) as LinearLayout
            linearLayoutAltFav = rootView.findViewById<View>(R.id.linearAltFav) as LinearLayout
            linearLayoutMntFav = rootView.findViewById<View>(R.id.linearMntFav) as LinearLayout
            donutGaugeUpdateMonitores = rootView.findViewById<View>(R.id.progressbarmonitores) as ProgressBar
            ibtnReloadFavMonitor = rootView.findViewById(R.id.btnUpdFavMonitoreables)
            btnUpdActFavorita = rootView.findViewById(R.id.btnUpdActFavorita)
            mgDefaultMntFav = rootView.findViewById<View>(R.id.imgDefaultMntFav) as ImageView

            framrNegocioList = rootView.findViewById(R.id.container)

            var btnAddMonitoreablesFav: ImageView = rootView.findViewById(R.id.btnSetAddMonitoreables)
            var btnActualizaEquipos: ImageView = rootView.findViewById(R.id.btnActualizaEquipos)
            var btnReloadFavAlertas: ImageView = rootView.findViewById(R.id.btnUpdFavAlertas)
            var btnAlertasFavoritas: ImageView = rootView.findViewById(R.id.btnSetAlertasFavInicio)
            var btnSetAddAltFavoritas: ImageView = rootView.findViewById(R.id.btnSetAddAltFavoritas)


            val screenWidth = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(screenWidth)

            val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar)



            with(rootView) {
                navBar.visibility = View.VISIBLE
                btnSetAddAltFavoritas.setOnClickListener {
                    btnSetAddAltFavoritas.animationXFade(Zoom.ZOOM_IN)


                    //findNavController().navigate(R.id.five_fragment)

                }

                btnAddMonitoreablesFav.setOnClickListener {
                    btnAddMonitoreablesFav.animationXFade(Zoom.ZOOM_IN)


                    (requireActivity() as MainActivity).setSelectedItem(2)

                }

                btnUpdActFavorita!!.setOnClickListener {
                    btnUpdActFavorita!!.animationXFade(Rotate.ROTATE_IN)
                    possibleItems = ArrayList()
                    linearLayoutAccFav?.removeAllViews()
                    UpdateAccionesFav(requireContext(), rootView,linearLayoutAccFav!!,databaseHelper_Data, progress_icon_acc_fav!!, possibleItems, arrUsuariosPermitidos).execute()

                }

                ibtnReloadFavMonitor!!.setOnClickListener {
                    ibtnReloadFavMonitor!!.animationXFade(Rotate.ROTATE_IN)
                    accionListMonitoreables.clear()
                    carrouselMonitorItems.clear()
                    linearLayoutMntFav?.removeAllViews()
                    UpdateMonitoreablesFav(requireContext(), rootView,databaseHelper_Data,  progress_icon_mnt_fav, mgDefaultMntFav,linearLayoutMntFav, this@NegocioFragment).execute()

                }

                btnActualizaEquipos.setOnClickListener {
                    btnActualizaEquipos.animationXFade(Rotate.ROTATE_IN)
                    if (!checkForInternet(requireContext())) {
                        var desc = "Conéctese a Internet, revise la configuracion de su red"
                        var dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setMessage(desc)
                            .setCancelable(false)
                            .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                                view.dismiss()
                            })
                        var alert = dialogBuilder.create()
                        alert.setTitle("")
                        alert.show()
                        return@setOnClickListener
                    }
                    progressEquiposAll!!.visibility = View.VISIBLE
                    NegociosHomeAdapter?.refreshFamiliasDB()
                    NegociosHomeAdapter?.notifyDataSetChanged()
                    Handler().postDelayed({
                        progressEquiposAll!!.visibility = View.GONE
                    }, 5000)

                }

                btnAlertasFavoritas.setOnClickListener {
                    btnAlertasFavoritas.animationXFade(Zoom.ZOOM_IN)
                    showModalConfiguracionAlertas()
                }

                btnReloadFavAlertas.setOnClickListener {
                    btnReloadFavAlertas.animationXFade(Rotate.ROTATE_IN)
                    dataListaAlertas = ArrayList()
                    linearLayoutAltFav?.removeAllViews()
                    UpdateAlertasFav(requireContext(), rootView,linearLayoutAltFav, databaseHelper_Data, progressIconAltFav).execute()

                }

                swipeRefreshLayoutP?.setOnRefreshListener {
                    possibleItems = ArrayList()
                    linearLayoutAccFav?.removeAllViews()
                    dataListaAlertas = ArrayList()
                    linearLayoutAltFav?.removeAllViews()
                    UpdateAccionesFav(requireContext(), rootView,linearLayoutAccFav!!,databaseHelper_Data, progress_icon_acc_fav!!, possibleItems, arrUsuariosPermitidos).execute()


                    dataListaAlertas = ArrayList()
                    linearLayoutAltFav?.removeAllViews()
                    UpdateAlertasFav(requireContext(), rootView,linearLayoutAltFav, databaseHelper_Data, progressIconAltFav).execute()



                    //**MONITORES**
                    accionListMonitoreables.clear()
                    carrouselMonitorItems.clear()
                    linearLayoutMntFav?.removeAllViews()
                    progress_icon_mnt_fav?.visibility = View.VISIBLE
                    UpdateMonitoreablesFav(requireContext(), rootView,databaseHelper_Data,  progress_icon_mnt_fav, mgDefaultMntFav,linearLayoutMntFav, this@NegocioFragment).execute()

                    swipeRefreshLayoutP?.isRefreshing = false
                }

                Handler().postDelayed({
                    iprogressBarGeneralInicio!!.visibility = View.GONE
                }, 5000)

                rootView?.viewTreeObserver?.addOnWindowFocusChangeListener { hasFocus ->
                    if (hasFocus) {


                    } else {




                    }
                }




                setupWebSocketService()
                //actualizaInicio()



                clientsw = OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .build()


                var entornoAct = databaseHelper_Data.getControlEntornoConexion(requireActivity())

                if(entornoAct == 1){
                    var dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
                    var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
                    idDispositivo = removeLeadingZeroes(idDispositivo)
                    idDispositivo =  removeTrailingZeroes(idDispositivo)
                    requestsw = Request.Builder()
                        .url("${dominio_actual}/api/get_acciones_tipo_monitoreables_list/?id_hardware=Todos&id_hardware_movil=$idDispositivo&id_cat_negocio=0")
                        .header("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwiZXhwIjoxNjc5MDg4Mjc2LCJlbWFpbCI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwib3JpZ19pYXQiOjE2NzkwODEwNzZ9.s-hvTxqpjfRIRkydS9NASkTecKNCgMzhYvHbNep-GD0")
                        .header("Content-Type", "application/json")
                        .build()
                }else{
                    var dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
                    var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
                    idDispositivo = removeLeadingZeroes(idDispositivo)
                    idDispositivo =  removeTrailingZeroes(idDispositivo)
                    requestsw = Request.Builder()
                        .url("${dominio_actual}/api/get_acciones_tipo_monitoreables_list/?id_hardware=Todos&id_hardware_movil=$idDispositivo&id_cat_negocio=0")
                        .header("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwiZXhwIjoxNjc5MDg4Mjc2LCJlbWFpbCI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwib3JpZ19pYXQiOjE2NzkwODEwNzZ9.s-hvTxqpjfRIRkydS9NASkTecKNCgMzhYvHbNep-GD0")
                        .header("Content-Type", "application/json")
                        .build()
                }








            }
            return rootView

        }catch (e: java.lang.Exception) {
            Log.e("ERRORFRAGMENT", "onCreateView", e)
            throw e
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val childFragment: Fragment = ChildFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, childFragment).commit()
    }




    fun updateBadgeAlertas() {
        LoginActivity.gtotal_alerta_pendientes = databaseHelper_Data.getTotalAlertasSinLeer()
        if (LoginActivity.gtotal_alerta_pendientes > 0) {
            (requireActivity() as MainActivity).setBadge(1, LoginActivity.gtotal_alerta_pendientes)
        } else {
            (requireActivity() as MainActivity).removeBadge(1)
        }

    }

    override fun onResume() {
        super.onResume()
        //actualizaInicio()
        updateBadgeAlertas()

    }



    fun actualizaInicio(){

        possibleItems = ArrayList()
        //linearLayoutAccFav?.removeAllViews()
        UpdateAccionesFav(requireContext(), rootView,linearLayoutAccFav!!,databaseHelper_Data, progress_icon_acc_fav!!, possibleItems, arrUsuariosPermitidos).execute()


        dataListaAlertas = ArrayList()
        //linearLayoutAltFav?.removeAllViews()
        UpdateAlertasFav(requireContext(), rootView,linearLayoutAltFav, databaseHelper_Data, progressIconAltFav).execute()



        //**MONITORES**
        accionListMonitoreables.clear()
        carrouselMonitorItems.clear()
        //linearLayoutMntFav?.removeAllViews()
        progress_icon_mnt_fav?.visibility = View.VISIBLE
        UpdateMonitoreablesFav(requireContext(), rootView,databaseHelper_Data,  progress_icon_mnt_fav, mgDefaultMntFav,linearLayoutMntFav, this@NegocioFragment).execute()

    }





    fun getGetUsuariosPermitidosXEquipo(idHardware: String, idDispositivo: String, idEquipo: Int, accion: String) {
        arrUsuariosPermitidos.clear()
        val getVigenciaLic = RestEngine.getRestEngineInicial(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result_usuarios_permitidos = getVigenciaLic.getUsuarioPermitidosEjecEquipo(
            idEquipo, "JWT ${BuildConfig.TOKEN_MAESTRO}", "application/json"
        )

        result_usuarios_permitidos.enqueue(object : Callback<DataUsuariosPermitidosEjecucion> {
            override fun onResponse(
                call: Call<DataUsuariosPermitidosEjecucion>,
                response: Response<DataUsuariosPermitidosEjecucion>
            ) {
                if (response.code() == 200) {
                    val usuariosEquipo = response.body()?.data.orEmpty()

                    val activos = usuariosEquipo.filter { it.estatus_act == "Activo" }
                    val inactivos = usuariosEquipo.filter { it.estatus_act != "Activo" }

                    if (activos.isEmpty() && inactivos.isEmpty()) {
                        showDialog("No existen usuarios en el equipo")
                        return
                    }

                    val inflater = LayoutInflater.from(requireContext())
                    val view = inflater.inflate(R.layout.dialog_usuarios, null)

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
                        val visible = listViewInactivos.visibility == View.VISIBLE
                        listViewInactivos.visibility = if (visible) View.GONE else View.VISIBLE
                        tvMostrarInactivos.text = if (visible) "Mostrar inactivos" else "Ocultar inactivos"
                    }

                    val dialog = AlertDialog.Builder(requireContext())
                        .setView(view)
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", null)
                        .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
                        .create()

                    dialog.show()

                    val btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    btnPositivo.setOnClickListener {
                        if (usuarioSeleccionado == null) {
                            showDialog("Seleccione un usuario")
                            return@setOnClickListener
                        }

                        val tipoComunicacion = databaseHelper_Data.getEquipoTipoComunicacion(idEquipo)
                        if (tipoComunicacion == 1) LoginActivity.accionejecutandose = true

                        when (accion) {
                            "screenshot" -> {
                                val iBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                    .setTitle("Recuperando Screenshot")
                                    .setMessage("Espere un momento...")
                                    .setCancelable(false)
                                    .setNeutralButton("Cancelar") { d, _ -> d.dismiss() }

                                iAlertDialogSc = iBuilder.create()
                                iAlertDialogSc!!.show()
                                iAlertDialogSc!!.getButton(AlertDialog.BUTTON_NEUTRAL).visibility = View.INVISIBLE

                                Handler().postDelayed({ dialog.dismiss() }, 15000)

                                val parametros = "ALL|$usuarioSeleccionado"
                                webSocketService?.sendMessage(
                                    "{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getScreenShot\",\"parametros\":\"$parametros\"}"
                                )
                            }

                            "LogOff" -> {
                                val parametros = "$accion|$usuarioSeleccionado"
                                webSocketService?.sendMessage(
                                    "{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setControlEquipo\",\"parametros\":\"$parametros\"}"
                                )
                            }
                        }

                        dialog.dismiss()
                    }

                } else {
                    showDialog("No existen usuarios activos en el equipo")
                }
            }

            override fun onFailure(call: Call<DataUsuariosPermitidosEjecucion>, t: Throwable) {
                Toast.makeText(requireContext(), "Error al recuperar usuarios: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun showDialog(mensaje: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(mensaje)
            .setCancelable(false)
            .setPositiveButton("Aceptar") { d, _ -> d.dismiss() }
            .create()
            .show()
    }


    fun getMovilData():String{
        val tm = requireContext().getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }

        var telNumber =   tm.line1Number
        if (telNumber != null){
            if(telNumber != "+52" && telNumber.count() > 10){
                telNumber = telNumber.substring(telNumber.length - 10, telNumber.length)
            }
            return telNumber
        }else{

            return "S/N"

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

    fun getMovilIdSuscriber():String{
        val tm = requireContext().getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }

        val telNumber =   "S/N"
        if (telNumber != null){
            return telNumber
        }else{

            return "S/N"

        }


    }

    private fun checkForInternet(context: Context): Boolean {


        val connectivityManager = context.getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {

                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true


                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true


                else -> false
            }
        } else {

            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWebSocketService() {
        var dominio_actual = ""
        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        /*
        if(existeLic.count() > 0){
            for (i in 0 until existeLic.count()) {
                dominio_actual = existeLic[i].nombre_dominio
            }
        }
         */

        var entornoAct = databaseHelper_Data.getControlEntornoConexion(requireContext())
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }


        WebSocketServerConn(
            databaseHelper_Data, requireActivity(), framrNegocioList!!, linearLayoutAccFav!!,
            webSocketService, dominio_actual, idDispositivo
        ).observeConnection()



    }


    fun chkUsuariosActivosXEquipo(NomNegocio: String, mensaje: String, idEquipo: Int){

        var splParametros = mensaje.split("|")
        var idHardware: String = ""
        var idDispositivo: String = ""
        var parametros: String = ""
        var msgUsuario: String = ""
        if(splParametros.size == 4){
            msgUsuario = splParametros[0]
            idHardware = splParametros[1]
            idDispositivo = splParametros[2]
            parametros = splParametros[3]

            if(msgUsuario.contains("No existen usuarios conectados")){

                if(parametros == "screenshot"){


                    var dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage("$msgUsuario al equipo, si toma una captura de pantalla en sesiones desconectadas es probable que visualice la imagen con un fondo negro. ¿Desea continuar?")
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->


                            //iprogressBarGeneralInicio!!.visibility = View.VISIBLE
                            var ibluider = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            ibluider.setTitle("Recuperando Screenshot")
                            ibluider.setMessage("Espere un momento...").setCancelable(false)

                            iAlertDialogSc = ibluider.create()
                            iAlertDialogSc!!.show()

                            var  ButtonNt = iAlertDialogSc!!.getButton(AlertDialog.BUTTON_NEUTRAL);
                            ButtonNt.visibility = View.INVISIBLE

                            Handler().postDelayed(
                                Runnable { ButtonNt.visibility = View.VISIBLE },
                                15000
                            )
                            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getScreenShot\",\"parametros\":\"ALL|Todos\"}")

                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("$NomNegocio")
                    if (!alert.isShowing){
                        alert.show()
                    }




                }else{
                    if(parametros == "LogOff"){
                        Toast.makeText(requireContext(), "No existen usuarios conectados", Toast.LENGTH_LONG).show()

                    }else{
                        webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setControlEquipo\",\"parametros\":\"$parametros|Todos\"}")

                    }

                }

            }else{
                if(parametros == "screenshot"){
                    getGetUsuariosPermitidosXEquipo(idHardware, idDispositivo, idEquipo, parametros)
                }else if(parametros == "LogOff") {
                    getGetUsuariosPermitidosXEquipo(idHardware, idDispositivo, idEquipo, parametros)
                }else{
                    var dialogBuilder = AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage(msgUsuario)
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setControlEquipo\",\"parametros\":\"$parametros|Todos\"}")
                                    )})
                    var alert = dialogBuilder.create()
                    alert.setTitle("$NomNegocio")
                    if (!alert.isShowing){
                        alert.show()
                    }
                }


            }
        }else{
            var dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("No fue posible recuperar informacion del equipo, intentelo nuevamente")
                .setCancelable(false)
                .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                    view.dismiss()
                })
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    (
                            dialog.dismiss()        )})
            var alert = dialogBuilder.create()
            alert.setTitle("$NomNegocio")
            if (!alert.isShowing){
                alert.show()
            }
        }
    }


    fun getSKRespuetaExeProgma(respuesta: String, NombreEquipo: String){
        var mensaje = if(respuesta == "OK"){
            "La apliacion se esta ejecutando"
        }else{
            respuesta
        }

        var dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(mensaje)
            .setCancelable(false)
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                (
                        dialog.dismiss()
                        )
            })
        var alert = dialogBuilder.create()
        alert.setTitle("$NombreEquipo")
        alert.show()

    }


    fun getSKRespuetaApiExt(respuesta: String, NombreEquipo: String){
        var dialogBuilder = AlertDialog.Builder(requireContext())

        var respfinal = ""
        when (respuesta) {
            "" -> respfinal = "No fue posible recuperar información de la solicitud, por favor revise la configuración de la acción"
            "[]" -> respfinal = "No fue posible recuperar información de la solicitud, por favor revise la configuración de la acción"
            else -> {
                respfinal = respuesta
            }
        }


        dialogBuilder.setMessage(respfinal)
            .setCancelable(false)
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                (
                        dialog.dismiss()
                        )
            })
        var alert = dialogBuilder.create()
        alert.setTitle("$NombreEquipo")
        alert.show()
    }


    fun getSKRespuetaExploradorARchivos(respuesta: String){
        Toast.makeText(requireContext(), "$respuesta", Toast.LENGTH_LONG).show()
    }


    fun getSKGetRespuestaDelArchivo(respuesta: String){

        Toast.makeText(requireContext(), respuesta, Toast.LENGTH_LONG).show()

    }

    fun loadResulSocketMonitorGrResponse(respuesta : List<com.apptomatico.app_movil_kotlin_v3.model.Monitor_Deatlle_Grefico_List>, data_equipo: String){

        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var accionList = java.util.ArrayList<ItemAccionesMonitoreables>()
        var info = data_equipo.split("|")

        for (i in respuesta) {


            val Equipos = databaseHelper_Data.getControlEstadoEquipos()

            var dataRedNombre = "Ethernet"
            var dataRedEnviados = ""
            var dataRedRecibidos = ""
            if (i.Red != null) {
                for (r in i.Red) {
                    if (r.enviados.toLong() > 0 && r.recibidos.toLong() > 0) {
                        dataRedEnviados = r.enviados
                        dataRedRecibidos = r.recibidos
                    }

                }
                dataRedNombre = "$dataRedNombre|$dataRedEnviados|$dataRedRecibidos"
            }

            val existe = databaseHelper_Data.CheckExisteTableMonitores(Movil_Id, info[0].toInt(), "${i.id_proceso}", info[3].toInt())

            if (existe) {
                Log.i("ACTUALIZA-MONITORES", "Se Actualiza monitores ${i.id_proceso}")
                databaseHelper_Data.updateTableMonitores(Movil_Id, info[0].toInt(), info[1].toInt(), "${info[2]}", "${i.id_proceso}", "${i.porcentaje_uso}",
                    "${i.espacio_total}", "${i.espacio_libre}", "${i.paquetes_enviados}",
                    "${i.paquetes_recibidos}", "${info[4]}", dataRedNombre, false, i.fec_consulta, info[3].toInt())

                databaseHelper_Data.updateTableMonitoresFav(Movil_Id, info[0].toInt(), info[1].toInt(), "${info[2]}", "${i.id_proceso}", "${i.porcentaje_uso}",
                    "${i.espacio_total}", "${i.espacio_libre}", "${i.paquetes_enviados}",
                    "${i.paquetes_recibidos}", "${info[4]}", dataRedNombre, false, i.fec_consulta, info[3].toInt())


            } else {
                Log.i("ACTUALIZA-MONITORES", "Se incerta monitores ${i.id_proceso}")
                databaseHelper_Data.addTableMonitores(Movil_Id, info[0].toInt(), info[1].toInt(), "${info[2]}", "${i.id_proceso}", "${i.porcentaje_uso}",
                    "${i.espacio_total}", "${i.espacio_libre}", "${i.paquetes_enviados}",
                    "${i.paquetes_recibidos}", "${info[4]}", dataRedNombre, true, i.fec_consulta,  info[3].toInt())

            }



        }


    }

    fun getSKRespuestaConsultaProgramaEquipo(respuesta: String){

        var dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("$respuesta")
            .setCancelable(false)
            .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
        var alert = dialogBuilder.create()
        alert.setTitle("")
        alert.show()


    }




    private var btnModalAlertasReturn: Chip? = null
    @SuppressLint("SetTextI18n")
    fun showModalConfiguracionAlertas(){
        vwpopupModalAlertas  = layoutInflater.inflate(R.layout.modal_alertas_favoritas, null)
        btnModalAlertasReturn = vwpopupModalAlertas?.findViewById(R.id.appCompatButtonAceptarAltFav) as Chip
        btnModalAlertasReturn_2 = vwpopupModalAlertas?.findViewById(R.id.appCompatButtonCancelarAltFav_2) as Chip
        imgAltFavEstSvr =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltEstSvr) as Switch
        imgAltIniSesion =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltIniSesion) as Switch
        imgAltCierreSesion =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltCierreSesion) as Switch
        imgAltEnvSistema =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltEnvSistema) as Switch
        imgAltErrorSistema =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltErrorSistema) as Switch
        imgFavAltEstadoEquipo =  vwpopupModalAlertas?.findViewById(R.id.swimgFavAltEstadoEquipo) as Switch
        iclosebtnAlertas = vwpopupModalAlertas?.findViewById(R.id.ly_visor_alertas_fav) as LinearLayout
        lblsubTituloAlertasFav = vwpopupModalAlertas?.findViewById(R.id.lblsubTituloAlertasFav) as TextView


        // val values = arrayOf("0", "5", "10", "15", "20", "25", "30", "35", "40", "45", "50")
        val pkEncenderEquipo = vwpopupModalAlertas?.findViewById(R.id.pkEncenderEquipo) as ScrollableNumberPicker
        val pkInputApagadoEquipo = vwpopupModalAlertas?.findViewById(R.id.pkInputApagadoEquipo) as ScrollableNumberPicker
        val pkInputInicioSesion = vwpopupModalAlertas?.findViewById(R.id.pkInputInicioSesion) as ScrollableNumberPicker
        val pkInputCierreSesion = vwpopupModalAlertas?.findViewById(R.id.pkInputCierreSesion) as ScrollableNumberPicker
        val pkInputErrorEquipo = vwpopupModalAlertas?.findViewById(R.id.pkInputErrorEquipo) as ScrollableNumberPicker
        val pkInputEstadoEquipo = vwpopupModalAlertas?.findViewById(R.id.pkInputEstadoEquipo) as ScrollableNumberPicker

        //Campos Texto Tops

        itextInputEncenderEquipo = vwpopupModalAlertas?.findViewById(R.id.textInputEncenderEquipo) as TextInputEditText
        itextInputApagadoEquipo = vwpopupModalAlertas?.findViewById(R.id.textInputApagadoEquipo) as TextInputEditText
        itextInputInicioSesion = vwpopupModalAlertas?.findViewById(R.id.textInputInicioSesion) as TextInputEditText
        itextInputCierreSesion = vwpopupModalAlertas?.findViewById(R.id.textInputCierreSesion) as TextInputEditText
        itextInputErrorEquipo = vwpopupModalAlertas?.findViewById(R.id.textInputErrorEquipo) as TextInputEditText
        itextInputEstadoEquipo = vwpopupModalAlertas?.findViewById(R.id.textInputEstadoEquipo) as TextInputEditText


        var topAlertas = databaseHelper_Data.getConfiguracionAlertas()
        if (topAlertas.isNotEmpty()) {
            lblsubTituloAlertasFav!!.text = "Seleccione el Top ${topAlertas[0].top_alertas} de alertas que desea agregar a favoritos"
        } else {
            databaseHelper_Data.addConfiguracionAlertas(5)
            lblsubTituloAlertasFav!!.text = "Seleccione el Top 5 de alertas que desea agregar a favoritos"

        }


        iclosebtnAlertas!!.setOnClickListener{
            dialogPopop?.cancel()
            dataListaAlertas = ArrayList()
            linearLayoutAltFav?.removeAllViews()
            UpdateAlertasFav(requireContext(),rootView, linearLayoutAltFav, databaseHelper_Data, progressIconAltFav).execute()
        }




        var listAlertasFav = databaseHelper_Data.getAlertasFavLisData()

        imgAltFavEstSvr!!.tag = 0
        imgAltIniSesion!!.tag = 0
        imgAltCierreSesion !!.tag = 0
        imgAltEnvSistema!!.tag = 0
        imgAltErrorSistema!!.tag = 0
        imgFavAltEstadoEquipo!!.tag = 0

        for (i in 0 until listAlertasFav.count()) {

            if (listAlertasFav[i].alerta_tipo  == 1){
                imgAltFavEstSvr!!.isChecked = true
                imgAltFavEstSvr!!.tag = 1
                pkEncenderEquipo.value = listAlertasFav[i].ultimas_entradas
            }

            if (listAlertasFav[i].alerta_tipo  == 2){
                imgAltIniSesion!!.isChecked = true
                imgAltIniSesion!!.tag = 1
                pkInputApagadoEquipo.value = listAlertasFav[i].ultimas_entradas

            }

            if (listAlertasFav[i].alerta_tipo  == 3){
                imgAltCierreSesion!!.isChecked = true
                imgAltCierreSesion !!.tag = 1
                pkInputInicioSesion.value = listAlertasFav[i].ultimas_entradas

            }

            if (listAlertasFav[i].alerta_tipo  == 4){
                imgAltEnvSistema!!.isChecked = true
                imgAltEnvSistema!!.tag = 1
                pkInputCierreSesion.value = listAlertasFav[i].ultimas_entradas
            }

            if (listAlertasFav[i].alerta_tipo  == 5){
                imgAltEnvSistema!!.isChecked = true
                imgAltErrorSistema!!.tag = 1
                pkInputErrorEquipo.value = listAlertasFav[i].ultimas_entradas
            }

            if (listAlertasFav[i].alerta_tipo  == 6) {
                imgAltEnvSistema!!.isChecked = true
                imgFavAltEstadoEquipo!!.tag = 1
                pkInputEstadoEquipo.value = listAlertasFav[i].ultimas_entradas
            }


        }




        if (imgAltFavEstSvr!!.tag == 1){

            // imgAltFavEstSvr!!.setImageResource(R.drawable.estrellafavoritoon)
            imgAltFavEstSvr!!.tag = 1
            //databaseHelper_Data.delAlertasFavoritas(1)
            //databaseHelper_Data.addAlertaFavoritasDB(1, 1)
        }

        if (imgAltIniSesion!!.tag == 1){
            //imgAltIniSesion!!.setImageResource(R.drawable.estrellafavoritoon)
            imgAltIniSesion!!.tag = 1
            // databaseHelper_Data.delAlertasFavoritas(2)
            // databaseHelper_Data.addAlertaFavoritasDB(2, 1)

        }

        if (imgAltCierreSesion !!.tag == 1){
            //imgAltCierreSesion!!.setImageResource(R.drawable.estrellafavoritoon)
            imgAltCierreSesion!!.tag = 1
            //databaseHelper_Data.delAlertasFavoritas(3)
            // databaseHelper_Data.addAlertaFavoritasDB(3, 1)

        }

        if ( imgAltEnvSistema!!.tag == 1){
            //imgAltEnvSistema!!.setImageResource(R.drawable.estrellafavoritoon)
            imgAltEnvSistema!!.tag = 1
            //databaseHelper_Data.delAlertasFavoritas(4)
            // databaseHelper_Data.addAlertaFavoritasDB(4, 1)

        }

        if (imgAltErrorSistema!!.tag == 1){

            //imgAltErrorSistema!!.setImageResource(R.drawable.estrellafavoritoon)
            imgAltErrorSistema!!.tag = 1
            //databaseHelper_Data.delAlertasFavoritas(5)
            // databaseHelper_Data.addAlertaFavoritasDB(5, 1)
        }

        if (imgFavAltEstadoEquipo!!.tag == 1){
            //imgFavAltEstadoEquipo!!.setImageResource(R.drawable.estrellafavoritoon)
            imgFavAltEstadoEquipo!!.tag = 1
            // databaseHelper_Data.delAlertasFavoritas(6)
            // databaseHelper_Data.addAlertaFavoritasDB(6, 1)
        }



        //////DIALO




        //////

        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        dialogBuilder.setView(vwpopupModalAlertas)
        dialogBuilder.setTitle("")

        imgAltFavEstSvr!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgAltFavEstSvr!!.tag = 1
                databaseHelper_Data.delAlertasFavoritas(1)
                var iTopVal =  pkEncenderEquipo.value.toString()
                databaseHelper_Data.addAlertaFavoritasDB(1, iTopVal.toInt())
            } else {
                imgAltFavEstSvr!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(1)
            }
        }


        imgAltIniSesion!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgAltIniSesion!!.tag = 1
                databaseHelper_Data.delAlertasFavoritas(2)
                //var iTopVal = itextInputApagadoEquipo!!.text.toString()
                var iTopVal =  pkInputApagadoEquipo.value.toString()
                databaseHelper_Data.addAlertaFavoritasDB(2, iTopVal.toInt())
            } else {
                imgAltIniSesion!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(2)
            }
        }


        imgAltCierreSesion!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgAltCierreSesion!!.tag = 1
                var iTopVal =  pkInputInicioSesion.value.toString()
                databaseHelper_Data.delAlertasFavoritas(3)
                databaseHelper_Data.addAlertaFavoritasDB(3, iTopVal.toInt())
            } else {
                imgAltCierreSesion!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(3)
            }
        }


        imgAltEnvSistema!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgAltEnvSistema!!.tag = 1
                var iTopVal =  pkInputCierreSesion.value.toString()
                databaseHelper_Data.delAlertasFavoritas(4)
                databaseHelper_Data.addAlertaFavoritasDB(4, iTopVal.toInt())
            } else {
                imgAltEnvSistema!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(4)
            }
        }


        imgAltEnvSistema!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgAltErrorSistema!!.tag = 1
                var iTopVal =  pkInputErrorEquipo.value.toString()
                databaseHelper_Data.delAlertasFavoritas(5)
                databaseHelper_Data.addAlertaFavoritasDB(5, iTopVal.toInt())
            } else {
                imgAltErrorSistema!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(5)
            }
        }


        imgAltEnvSistema!!.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                imgFavAltEstadoEquipo!!.tag = 1
                var iTopVal =  pkInputEstadoEquipo.value.toString()
                databaseHelper_Data.delAlertasFavoritas(6)
                databaseHelper_Data.addAlertaFavoritasDB(6, iTopVal.toInt())
            } else {
                imgFavAltEstadoEquipo!!.tag = 0
                databaseHelper_Data.delAlertasFavoritas(6)
            }
        }


        btnModalAlertasReturn_2!!.setOnClickListener{
            databaseHelper_Data.delCOnfiguracionAlertas()
            databaseHelper_Data.addConfiguracionAlertas(5)

            dialogPopop?.cancel()
        }

        btnModalAlertasReturn!!.setOnClickListener{
            /*
           if ( imgAltFavEstSvr!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(1)
               var iTopVal = pkEncenderEquipo.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(1, iTopVal.toInt())
           }

           if(imgAltIniSesion!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(2)
               var iTopVal =  pkInputApagadoEquipo.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(2, iTopVal.toInt())
           }

           if(imgAltCierreSesion!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(3)
               var iTopVal =  pkInputInicioSesion.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(3, iTopVal.toInt())
           }

           if(imgAltEnvSistema!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(4)
               var iTopVal =  pkInputCierreSesion.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(4, iTopVal.toInt())
           }

           if(imgAltErrorSistema!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(5)
               var iTopVal =  pkInputErrorEquipo.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(5, iTopVal.toInt())
           }


           if(imgFavAltEstadoEquipo!!.tag == 1){
               databaseHelper_Data.delAlertasFavoritas(6)
               var iTopVal =  pkInputEstadoEquipo.value.toString()
               databaseHelper_Data.addAlertaFavoritasDB(6, iTopVal.toInt())
           }
           */

            Toast.makeText(requireContext(), "Cambios realizados correctamente", Toast.LENGTH_LONG).show()
            dialogPopop?.cancel()
            dataListaAlertas = ArrayList()
            linearLayoutAltFav?.removeAllViews()
            UpdateAlertasFav(requireContext(), rootView, linearLayoutAltFav, databaseHelper_Data, progressIconAltFav).execute()

        }



        if (dialogPopop != null){
            dialogPopop?.cancel()
            dialogPopop == null
        }


        dialogPopop = dialogBuilder.create()
        dialogPopop?.show()

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val displayWidth = displayMetrics.densityDpi
        val displayHeight = displayMetrics.densityDpi
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(dialogPopop!!.window!!.attributes)
        val dialogWindowWidth = (displayWidth * 0.5f).toInt()
        val dialogWindowHeight = (displayHeight * 0.5f).toInt()


        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT

        layoutParams.width = width
        layoutParams.height = height
        dialogPopop!!.window!!.attributes = layoutParams
    }




    //**********************************TASK ALERTAS***************************

    class UpdateAlertasFav(context: Context, rootView: View, linearLayoutAltFav: LinearLayout?, databaseHelper_Data: DatabaseHelper, progressIconAltFav: ProgressBar?) : AsyncTask<String?, String?, List<ControlAlertasData>>() {
        val context = context
        val databaseHelper_Data = databaseHelper_Data
        val progressIconAltFav = progressIconAltFav
        var linearLayoutAltFav = linearLayoutAltFav
        var btnModalAlertasReturn: Chip? = null
        var dialogPopop: androidx.appcompat.app.AlertDialog? = null
        val rootView = rootView
        override fun doInBackground(vararg params: String?): List<ControlAlertasData>? {
            val alertas_fav_data = databaseHelper_Data.getAlertasFavLisData()

            var tipos =  ArrayList<String>()
            var topAlertas = databaseHelper_Data.getConfiguracionAlertas()
            var valTopAlertas = 0
            for(i in 0 until topAlertas.count()){
                valTopAlertas = topAlertas[i].top_alertas
            }


            for (i in 0 until alertas_fav_data.count()) {

                if(alertas_fav_data[i].alerta_tipo == 1){
                    tipos.add("1")

                }

                if(alertas_fav_data[i].alerta_tipo == 2){
                    tipos.add("2")

                }
                if(alertas_fav_data[i].alerta_tipo == 3){
                    tipos.add("3")

                }
                if(alertas_fav_data[i].alerta_tipo == 4){

                    tipos.add("4")

                }
                if(alertas_fav_data[i].alerta_tipo == 5){
                    tipos.add("5")

                }
                if(alertas_fav_data[i].alerta_tipo == 6){
                    tipos.add("6")

                }

            }


            var alertas_list_fav_data = databaseHelper_Data.getUltimasAlertas(tipos, valTopAlertas)


            if (header_id_negocio != "Todos"){
                var id_cat_negocio = header_id_negocio
                var listEquipos = databaseHelper_Data.GetListaEquiposXNegocio(id_cat_negocio.toInt())
                alertas_list_fav_data = alertas_list_fav_data.filter { s -> listEquipos.any { it.nombre_equipo == s.nom_negocio }}


            }


            return  alertas_list_fav_data

        }

        override fun onPostExecute(dataListFav: List<ControlAlertasData>) {
            super.onPostExecute(dataListFav)
            progressIconAltFav?.visibility = View.VISIBLE
            var tm = 2


            val typeface = ResourcesCompat.getFont(context, R.font.helvetica)

            val tmpLinearLayout: LinearLayout? =  rootView.findViewById<View>(R.id.linearAltFav) as LinearLayout as LinearLayout
            tmpLinearLayout?.removeAllViews()

            for (i in 0 until dataListFav.count()) {


                val tituloView = TextView(context)
                tituloView.gravity = Gravity.CENTER_HORIZONTAL
                tituloView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                tituloView.typeface = typeface
                tituloView.setPadding(tm, 2, 2, 2)

                val DescView = TextView(context)
                DescView.gravity = Gravity.CENTER_HORIZONTAL
                DescView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                DescView.typeface = typeface
                DescView.setPadding(tm, 2, 2, 2)



                val FecView = TextView(context)
                FecView.gravity = Gravity.CENTER_HORIZONTAL
                FecView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10F)
                FecView.typeface = typeface
                FecView.setPadding(tm, 2, 2, 2)

                val imageView = ImageView(context)
                imageView.id = i
                imageView.setPadding(tm, 2, 2, 2)




                if(dataListFav[i].titulo.length > 20){
                    tituloView.text = dataListFav[i].titulo.take(20) + "..."
                }else{
                    tituloView.text = dataListFav[i].titulo
                }


                var fec =  ""
                var time =  ""

                if (dataListFav[i].fecha.contains("T")){
                    val stDate = dataListFav[i].fecha.split("T").toTypedArray()
                    fec =  stDate[0]
                    time =  stDate[1]

                }else{
                    val stDate = dataListFav[i].fecha.split(" ").toTypedArray()
                    fec =  stDate[0]
                    time =  stDate[1]
                }

                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentDateAndTime: Date =  simpleDateFormat.parse("$fec $time")
                val newDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(currentDateAndTime)





                when (dataListFav[i].tipo_id) {
                    0 -> {
                        imageView.setImageResource(R.drawable.estrelladefault24)

                    }
                    1 -> {
                        imageView.setImageResource(R.drawable.alertaencendidoequipo)
                        DescView.text = "Se inicio Servidor"
                        // FecView.text = "$fec $time"
                        FecView.text = newDateTime

                    }
                    2 -> {
                        imageView.setImageResource(R.drawable.alertaapagadoequipo)
                        DescView.text = "Se apago Servidor"
                        //FecView.text = "$fec $time"
                        FecView.text = newDateTime
                    }
                    3 -> {
                        imageView.setImageResource(R.drawable.alertainiciosesion)
                        DescView.text = "Inicio de Sesión"
                        //FecView.text = "$fec $time"
                        FecView.text = newDateTime

                    }
                    4 -> {
                        imageView.setImageResource(R.drawable.alertacierresesion)
                        DescView.text = "Cierre de Sesión"
                        //FecView.text = "$fec $time"
                        FecView.text = newDateTime
                    }
                    5 -> {
                        imageView.setImageResource(R.drawable.alertaerror)
                        DescView.text = "Error"
                        // FecView.text = "$fec $time"
                        FecView.text = newDateTime

                    }
                    6 -> {
                        imageView.setImageResource(R.drawable.alertaaviso)
                        DescView.text = "Aviso"
                        //FecView.text = "$fec $time"
                        FecView.text = newDateTime
                    }

                    else -> {
                        imageView.setImageResource(R.drawable.alertashome)

                    }
                }



                imageView.setOnClickListener {
                    getModalFavAlertas(dataListFav[i])
                }


                val layoutDetalle = LinearLayout(context)
                layoutDetalle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutDetalle.orientation = LinearLayout.VERTICAL


                layoutDetalle?.addView(tituloView)
                layoutDetalle?.addView(imageView)
                layoutDetalle?.addView(DescView)
                layoutDetalle?.addView(FecView)


                tmpLinearLayout?.addView(layoutDetalle)

                tm = 90
            }

            if (tmpLinearLayout != null){
                linearLayoutAltFav = tmpLinearLayout
            }

            progressIconAltFav?.visibility = View.GONE
        }


        fun getModalFavAlertas(info_app: com.apptomatico.app_movil_kotlin_v3.model.ControlAlertasData){

            showModalAlertas(info_app.titulo, info_app.descripcion, info_app.fecha, info_app.tipo_id)

        }

        fun showModalAlertas(titulo_alerta: String, desc_alerta: String, fec_alerta: String, tipo_alerta: Int){
            val vwpopupModalALertas = (context as MainActivity).layoutInflater.inflate(R.layout.modal_alertasfav, null)
            btnModalAlertasReturn = vwpopupModalALertas?.findViewById(R.id.btnModalAlertasReturn) as Chip
            val imgModalAlertasReturn = vwpopupModalALertas?.findViewById(R.id.iconModalAlertaFav) as ImageView
            val txtTituloAlertaModal = vwpopupModalALertas?.findViewById(R.id.modal_titulo_alerta) as TextView
            val txtDescAlertaModal =  vwpopupModalALertas?.findViewById(R.id.modal_desc_alerta) as TextView
            val txtFecAlertaModal =  vwpopupModalALertas?.findViewById(R.id.modal_fecha_alerta) as TextView
            val txtHoraAlertaModal =  vwpopupModalALertas?.findViewById(R.id.modal_hora_alerta) as TextView


            when (tipo_alerta) {
                0 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.estrellafavoritos64)

                }
                1 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertaencendidoequipo)
                }
                2 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertaapagadoequipo)
                }
                3 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertainiciosesion)

                }
                4 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertacierresesion)

                }
                5 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertaerror)

                }
                6 -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertaaviso)

                }

                else -> {
                    imgModalAlertasReturn!!.setImageResource(R.drawable.alertashome)

                }
            }


            txtTituloAlertaModal!!.text = titulo_alerta
            txtDescAlertaModal!!.text = desc_alerta
            if (fec_alerta.contains("T")){
                val dvdFecha =  fec_alerta.split("T").toTypedArray()
                txtFecAlertaModal!!.text = dvdFecha[0]
                txtHoraAlertaModal!!.text = dvdFecha[1]
            }else{
                val dvdFecha = fec_alerta.split(" ").toTypedArray()
                txtFecAlertaModal!!.text = dvdFecha[0]
                txtHoraAlertaModal!!.text = dvdFecha[1]
            }


            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context)
            dialogBuilder.setView(vwpopupModalALertas)
            dialogBuilder.setTitle("")
            btnModalAlertasReturn!!.setOnClickListener{
                dialogPopop?.cancel()
            }


            dialogPopop = dialogBuilder.create()
            dialogPopop?.show()
        }


    }



    //*************************************************************************

    public fun sssa(){

    }

    class UpdateAccionesFav(context: Context, rootView: View ,linearLayoutAccFav: LinearLayout, databaseHelper_Data: DatabaseHelper, progress_icon_acc_fav: ProgressBar, possibleItems: List<Favoritos_List>, arrUsuariosPermitidos: MutableList<String>) : AsyncTask<String?, String?, List<AccionesFavDataCarrousel>?>() {
        val context = context
        val progress_icon_acc_fav = progress_icon_acc_fav
        val databaseHelper_Data = databaseHelper_Data
        var linearLayoutAccFav= linearLayoutAccFav
        var possibleItems = possibleItems
        var arrUsuariosPermitidos = arrUsuariosPermitidos
        val rootView = rootView
        override fun doInBackground(vararg strings: String?): List<AccionesFavDataCarrousel> {


            var listAccFav = databaseHelper_Data.getAccionesFavoritasListDataV2()


            if (header_id_negocio != "Todos"){
                var id_cat_negocio = header_id_negocio
                var listEquipos = databaseHelper_Data.GetListaEquiposXNegocio(id_cat_negocio.toInt())
                listAccFav = listAccFav.filter { s -> listEquipos.any { it.equipo_id == s.id_equipo.toInt() }}
            }

            return listAccFav
        }

        @SuppressLint("SuspiciousIndentation")
        override  fun onPostExecute(listAccFav: List<AccionesFavDataCarrousel>?) {
            super.onPostExecute(listAccFav)



            progress_icon_acc_fav.visibility = View.VISIBLE
            var dominio_actual = ""
            var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
            if(entornoAct == 1){
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }else{
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }



            var tm = 2

            val typeface = ResourcesCompat.getFont(context, R.font.helvetica)

            val tmpLinearLayout: LinearLayout? =  rootView.findViewById<View>(R.id.linearAccFav) as LinearLayout
            tmpLinearLayout?.removeAllViews()
            for (i in 0 until listAccFav!!.count()) {


                val tituloView = TextView(context)
                tituloView.text = listAccFav[i].nom_negocio
                tituloView.gravity = Gravity.CENTER_HORIZONTAL
                tituloView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                tituloView.typeface = typeface
                tituloView.setPadding(tm, 2, 2, 2)


                val DescView = TextView(context)
                DescView.gravity = Gravity.CENTER_HORIZONTAL
                DescView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                DescView.typeface = typeface
                DescView.setPadding(tm, 2, 2, 2)


                when (listAccFav[i].tipo_id) {
                    1 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Ejecuta aplicacion"
                        }
                    }
                    2 -> {
                        if (listAccFav[i].comando == "reboot_svr") { //Reinicia Servidor
                            if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                                DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                            }else{
                                DescView.text = "Reiniciar"
                            }

                        }
                        if (listAccFav[i].comando == "log_off_svr") { //Cerrar Sesion del Servidor
                            if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                                DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                            }else{
                                DescView.text = "Cerrar Sesion"
                            }

                        }
                        if (listAccFav[i].comando == "shutdown_svr") {
                            if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                                DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                            }else{
                                DescView.text = "Apagar"
                            }

                        }

                        if (listAccFav[i].comando == "start_svr") {
                            if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                                DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                            }else{
                                DescView.text = "Encender"
                            }

                        }

                    }
                    4 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Consulta BD"
                        }

                    }
                    5 -> {

                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Estado del Servidor"
                        }


                    }
                    6 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Explorador de Archivos"
                        }



                    }
                    7 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Screenshot"
                        }


                    }
                    8 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Elimina un Archivo"
                        }


                    }
                    9 -> {
                        if(listAccFav[i].titulo_carrousel != "" && listAccFav[i].titulo_carrousel.isNotEmpty()){
                            DescView.text = listAccFav[i].titulo_carrousel.replace("-${listAccFav[i].nom_negocio}", "")
                        }else{
                            DescView.text = "Programa en Ejecucion"
                        }

                    }
                    else -> {
                        DescView.text = ""
                    }
                }



                val imageView = ImageView(context)
                imageView.id = i
                imageView.setPadding(tm, 2, 2, 2)

                var imageUrl = ""


                var imgbd : Bitmap? = databaseHelper_Data.getAccionesFavoritasImg(listAccFav[i].idAccion)
                if(imgbd != null){

                    val uri = listAccFav[i].path_icono

                    if(uri != ""){
                        val imageResource = context.resources.getIdentifier(uri, null, context.packageName)
                        val res: Drawable = context.resources.getDrawable(imageResource)
                        val bitmap: Bitmap = (res as BitmapDrawable).bitmap
                        val d: Drawable = BitmapDrawable(context.resources, Bitmap.createScaledBitmap(bitmap, 40, 40, true))
                        imageView.setImageDrawable(d)
                    }


                }else{
                    imageUrl = "$dominio_actual/media/iconos/app_default.png"
                    Picasso.get().load(imageUrl).resize(80, 80).into(imageView)
                }


                if(listAccFav[i].comando == "start_svr"){
                    imageView.tag = "0000111${listAccFav[i].id_equipo}"
                }else{
                    imageView.tag = "0000001${listAccFav[i].id_equipo}"
                }

                imageView.setOnClickListener {
                    imageView.animationXFade(Fade.FADE_IN_DOWN)
                    getDataFavoritos(listAccFav[i].idAccion)
                }


                val layoutDetalle = LinearLayout(context)
                layoutDetalle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutDetalle.orientation = LinearLayout.VERTICAL

                layoutDetalle?.addView(tituloView)
                layoutDetalle?.addView(imageView)
                layoutDetalle?.addView(DescView)

                tmpLinearLayout?.addView(layoutDetalle)

                tm = 90
            }

            if (tmpLinearLayout != null){
                linearLayoutAccFav = tmpLinearLayout
            }

            progress_icon_acc_fav!!.visibility = View.GONE
            return

        }


        fun getDataFavoritos(id_accion: Int){
            val listAcciones = databaseHelper_Data.getAccionesFavCarrouselFavInicio(id_accion)

            if (listAcciones != null && listAcciones.isNotEmpty()){
                possibleItems = listAcciones
            }else{
                possibleItems = listOf(
                    com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List(
                        0,
                        "/iconos/estrelladefault24.png",
                        "Favoritos",
                        99999999,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        0,
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        ""
                    )
                )
            }

            getAndExecuteAccion(possibleItems[0])


        }

        fun getAndExecuteAccion(info_app: Favoritos_List){

            // var IdAccion = view.id
            arrUsuariosPermitidos.clear()

            val tipoComunicacion =  databaseHelper_Data.getEquipoTipoComunicacion(info_app.id_negocio_id)

            if (info_app.tipo_id == 1){ // Ejecuta cualquier aplicacion

                if(info_app.comando == "calc"){



                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage("Desea ejecutar  ${info_app.aplicacion_nombre_aplicacion}")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).getGetUsuariosPermitidosXEquipo(info_app, tipoComunicacion )
                                    )
                        })
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }




                }else if(info_app.comando == "termina_proceso_en_equipo"){


                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage("Desea finalizar el proceso  ${info_app.aplicacion_nombre_aplicacion}")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).sendTerminaProcesoEquipo("${info_app.alias_aplicacion}", "${info_app.id_negocio_id}", "${info_app.ip_publica}", "${info_app.ip_local}","${info_app.hardware_key}", "${info_app.puerto}", "${info_app.aplicacion_ruta_aplicacion}",
                                        "${info_app.aplicacion_nombre_aplicacion}", "${info_app.aplicacion_parametros_aplicacion}", tipoComunicacion)

                                    )
                        })
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }

                }


            }
            if (info_app.tipo_id == 2){ // Ejecuta acciobnes sobre el servidor

                var desc = ""
                if (info_app.comando == "reboot_svr"){ //Reinicia Servidor

                    desc = "Confirma que desea reiniciar el equipo"
                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).RebootNegocio("${info_app.nombre_negocio}", "${info_app.hardware_key}", "${info_app.ip_publica}", "${info_app.puerto}", "${info_app.ip_local}",tipoComunicacion)

                                    )
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }


                }
                if (info_app.comando == "log_off_svr"){ //Cerrar Sesion del Servidor
                    desc = "Confirma que desea cerrar sesion en el equipo"
                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).LogOffNegocio("${info_app.nombre_negocio}", "${info_app.hardware_key}", "${info_app.ip_publica}", "${info_app.puerto}", "${info_app.ip_local}",tipoComunicacion)
                                    )
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }

                }
                if (info_app.comando == "shutdown_svr"){ //Cerrar Sesion del Servidor
                    desc = "Confirma que desea apagar el equipo"
                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).ShutdownNegocio("${info_app.nombre_negocio}", "${info_app.hardware_key}", "${info_app.id_negocio_id}", "${info_app.ip_publica}", "${info_app.puerto}", "${info_app.ip_local}",tipoComunicacion)

                                    )
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }

                }


                if (info_app.comando == "start_svr"){
                    desc = "Confirma que desea encender el equipo"
                    var dialogBuilder = AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                            (
                                    (context as MainActivity).StartNegocio("${info_app.nombre_negocio}", "${info_app.hardware_key}", "${info_app.id_negocio_id}", "${info_app.ip_publica}", "${info_app.puerto}", "${info_app.ip_local}",tipoComunicacion)
                                    )
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("${info_app.nombre_negocio}")
                    if (!alert.isShowing){
                        alert.show()
                    }

                }





            }


            if (info_app.tipo_id == 4){//Base de datos
                // val accountsIntent = Intent(context, TableLayoutTest::class.java)



                // context.startActivity(accountsIntent)
                (context as MainActivity).ConsultaDinamicaBdNegocio(info_app, "${info_app.hardware_key}", "${info_app.ip_publica}", info_app.puerto, "${info_app.nombre_negocio}", "${info_app.ip_local}", tipoComunicacion)


            }

            if (info_app.tipo_id == 5){//Consulta estado del servidor
                SharedApp.prefs.idNegocioF = info_app.id_negocio_id.toString() + "|" + "${info_app.ip_publica}" + "|" + "${info_app.hardware_key}"+ "|" + "${info_app.puerto}" + "|" + "${info_app.ip_local}"

                (context as MainActivity).estatusActNegocio(info_app.id, info_app.origen, "${info_app.nombre_negocio}", "${info_app.hardware_key}",tipoComunicacion)
            }

            if (info_app.tipo_id == 6){//Esplorador de Archivos
                (context as MainActivity).ExplorarDirectorio(info_app.exploracion_ruta, "${info_app.hardware_key}", info_app.id_negocio_id, "${info_app.nombre_negocio}", "${info_app.ip_publica}", info_app.puerto, info_app.ip_local, tipoComunicacion)
            }


            if (info_app.tipo_id == 7){//Screenshot bajo demanda del servidor

                (context as MainActivity).ScreenshotServidor("${info_app.hardware_key}", info_app.id_negocio_id, "${info_app.nombre_negocio}", "${info_app.ip_publica}", info_app.puerto, "${info_app.boveda_software}", info_app.ip_local, tipoComunicacion)
            }

            if (info_app.tipo_id == 8){//Screenshot bajo demanda del servidor
                var desc = ""
                if (info_app.eliminacion_tipo == "eliminar_archivo"){
                    desc = "Desea eliminar el archivo ${info_app.eliminacion_nombre_archivo}"
                }else{
                    desc = "Desea eliminar el directorio ${info_app.eliminacion_ruta_archivo}"
                }
                var dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage(desc)
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (

                                (context as MainActivity).chkDelArcDirServidor("${info_app.hardware_key}", info_app.id_negocio_id, "${info_app.nombre_negocio}", "${info_app.ip_publica}", info_app.puerto,
                                    "${info_app.eliminacion_tipo}", "${info_app.eliminacion_ruta_archivo}",
                                    "${info_app.eliminacion_nombre_archivo}", "${info_app.ip_local}", tipoComunicacion)

                                )
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("${info_app.nombre_negocio}")
                alert.show()



            }


            if (info_app.tipo_id == 9){//Informa si un programa esta en ejecucion



                (context as MainActivity).ConsultaInfoProgramaExec(info_app, "${info_app.hardware_key}", "${info_app.ip_publica}", info_app.puerto, "${info_app.nombre_negocio}", "${info_app.ip_local}",tipoComunicacion)
            }


            if (info_app.tipo_id == 16){//CONSULTA API
                // val accountsIntent = Intent(context, TableLayoutTest::class.java)



                // context.startActivity(accountsIntent)
                (context as MainActivity).ConsultaApiExtNegocio(info_app, "${info_app.hardware_key}", "${info_app.ip_publica}", info_app.puerto, "${info_app.nombre_negocio}", "${info_app.ip_local}", tipoComunicacion)


            }



        }


    }



    //****************************************************

    class UpdateMonitoreablesFav(context: Context, rootView: View, databaseHelper_Data: DatabaseHelper, progress_icon_mnt_fav: ProgressBar?, mgDefaultMntFav: ImageView?,linearLayoutMntFav: LinearLayout?, view: NegocioFragment) : AsyncTask<String?, String?, String?>() {
        val context = context
        val view =  view
        val rootView = rootView
        val databaseHelper_Data = databaseHelper_Data
        var estEquipAct: List<ControlEstadoEquipos> = ArrayList()
        var dataFavBD: List<DataControlMonitoreablesFav>? = ArrayList()
        var progress_icon_mnt_fav = progress_icon_mnt_fav
        var mgDefaultMntFav = mgDefaultMntFav
        var linearLayoutMntFav = linearLayoutMntFav
        var carrouselMonitorItems = ArrayList<com.apptomatico.app_movil_kotlin_v3.model.MonitoreablesCarrousel_List>()

        override fun doInBackground(vararg params: String?): String? {

            var idNegocio = 0
            if (header_id_negocio != "Todos"){
                idNegocio = header_id_negocio.toInt()
            }
            estEquipAct = databaseHelper_Data.getControlEstadoEquipos()
            dataFavBD = databaseHelper_Data?.getControlMonitoreableFav()
            accionListMonitoreables = databaseHelper_Data.getListTableMonitoreables(idNegocio,0)

            return  ""

        }

        override  fun onPostExecute(resultado: String?) {
            super.onPostExecute(resultado)
            progress_icon_mnt_fav?.visibility = View.GONE
            if (estEquipAct != null) {
                if(estEquipAct.isEmpty()) {
                    progress_icon_mnt_fav?.visibility = View.GONE
                    carrouselMonitorItems.add(
                        com.apptomatico.app_movil_kotlin_v3.model.MonitoreablesCarrousel_List(
                            0,
                            0,
                            "0",
                            "",
                            0,
                            "",
                            0,
                            0,
                            0,
                            0,
                            0,
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            null
                        )
                    )

                    ActLinearLayoutMntFav()

                    return
                }


            }





            if (dataFavBD!!.isEmpty()) {
                progress_icon_mnt_fav?.visibility = View.GONE
                carrouselMonitorItems.add(
                    com.apptomatico.app_movil_kotlin_v3.model.MonitoreablesCarrousel_List(
                        0,
                        0,
                        "0",
                        "",
                        0,
                        "",
                        0,
                        0,
                        0,
                        0,
                        0,
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        null
                    )
                )

                ActLinearLayoutMntFav()

                return
            }


            ActListLayoutMntFav_BD()
            progress_icon_mnt_fav?.visibility = View.GONE






        }

        private fun ActLinearLayoutMntFav(){
            var tm = 2

            mgDefaultMntFav!!.visibility = View.GONE
            if (carrouselMonitorItems.isEmpty()){
                mgDefaultMntFav!!.visibility = View.VISIBLE
            }else if (carrouselMonitorItems.count() ==1){
                if (carrouselMonitorItems[0].comando == "" && carrouselMonitorItems[0].id == 0){
                    if(linearLayoutMntFav!!.childCount <= 0){
                        mgDefaultMntFav!!.visibility = View.VISIBLE
                    }

                }
            }

            for (i in 0 until carrouselMonitorItems.count()) {
                val dataFavBD = databaseHelper_Data?.getControlMonitoreableFav()

                val existeFavMnt = dataFavBD!!.filter { s -> s.tipo_hardware == carrouselMonitorItems[i].hardware  && s.id_accion ==  carrouselMonitorItems[i].id}
                if(existeFavMnt.isEmpty()){
                    continue
                }



                if(carrouselMonitorItems[i].hardware == "RED"){
                    if (carrouselMonitorItems[i].usoRed != null){
                        var enviados = 0.0
                        var recibidos = 0.0
                        for(i in carrouselMonitorItems[i].usoRed!!){
                            enviados += i.enviados.toDouble()
                            recibidos += i.recibidos.toDouble()
                        }

                        enviados *= 0.000125

                        try{

                            val descImg =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}50004") as TextView

                            descImg.text = carrouselMonitorItems[i].hardware + ":" + String.format("%.2f", enviados)+" Kbps"
                        }catch (e: Exception){

                        }
                    }else{
                        try {


                            val descImg =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}50004") as TextView
                            descImg.text = carrouselMonitorItems[i].hardware + ":" + carrouselMonitorItems[i].porcentaje +  "%"
                        }catch (e: Exception){

                        }
                    }
                }else if(carrouselMonitorItems[i].hardware == "MEMORIA"){
                    try{


                        val descImg =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}50002") as TextView
                        descImg.text = carrouselMonitorItems[i].hardware + ":" + carrouselMonitorItems[i].porcentaje +  "%"

                        val descImgMetrica =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}66662") as TextView

                        val metricaLibre = Math.round(carrouselMonitorItems[i].metrica_libre.toDouble() * 100) / 100.0
                        val metricaTotal = Math.round(carrouselMonitorItems[i].metrica_total.toDouble() * 100) / 100.0
                        descImgMetrica.text = "${metricaLibre}/${metricaTotal} GB (Libre/Usado)"
                    }catch (e: Exception){

                    }
                }else if(carrouselMonitorItems[i].hardware == "DISCO" || carrouselMonitorItems[i].hardware == "C:" || carrouselMonitorItems[i].hardware == "E:" || carrouselMonitorItems[i].hardware == "F:" || carrouselMonitorItems[i].hardware == "D:" || carrouselMonitorItems[i].hardware == "G:"){
                    try {
                        val descImg =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}50003") as TextView
                        descImg.text = carrouselMonitorItems[i].hardware + ":" + carrouselMonitorItems[i].porcentaje +  "%"


                        val descImgMetrica =  linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}66663") as TextView
                        var metricaLibre = carrouselMonitorItems[i].metrica_libre.toDouble() / 1024 / 1024 / 1024
                        var metricaTotal = carrouselMonitorItems[i].metrica_total.toDouble() / 1024 / 1024 / 1024
                        metricaLibre = (metricaLibre * 100).roundToInt() / 100.0
                        metricaTotal = (metricaTotal * 100).roundToInt() / 100.0
                        descImgMetrica.text = "${metricaLibre}/${metricaTotal} GB (Libre/Usado)"
                    }catch (e: Exception){

                    }
                }else{

                    try {
                        val descImg = linearLayoutMntFav?.findViewWithTag("${carrouselMonitorItems[i].id}50001") as TextView

                        descImg.text = carrouselMonitorItems[i].hardware + ":" + carrouselMonitorItems[i].porcentaje +  "%"
                    }catch (e: Exception){

                    }


                }


            }
        }


        private fun ActListLayoutMntFav_BD(){
            var tm = 2
            mgDefaultMntFav!!.visibility = View.GONE
            var disableModal = false
            val typeface = ResourcesCompat.getFont(context, R.font.helvetica)
            var dataFavBD = databaseHelper_Data?.getControlMonitoreableFav()
            if (dataFavBD == null){
                mgDefaultMntFav!!.visibility = View.GONE
            }
            if (dataFavBD!!.isEmpty()){
                mgDefaultMntFav!!.visibility =  View.GONE
            }
            if (dataFavBD!!.isNotEmpty()){
                progress_icon_mnt_fav?.visibility = View.VISIBLE
            }

            var estEquipAct = databaseHelper_Data.getControlEstadoEquipos()
            if (estEquipAct.isEmpty()){
                mgDefaultMntFav!!.visibility =  View.GONE
            }
            if (header_id_negocio != "Todos"){
                var id_cat_negocio = header_id_negocio
                var listEquipos = databaseHelper_Data.GetListaEquiposXNegocio(id_cat_negocio.toInt())

                dataFavBD = dataFavBD!!.filter { s -> listEquipos.any { it.equipo_id == s.id_equipo }}


            }


            var tmlListViewMonitores: LinearLayout = rootView.findViewById<View>(R.id.linearMntFav) as LinearLayout

            for (i in 0 until dataFavBD!!.count()) {
                if (Companion.linearLayoutMntFav!!.childCount > 0 && tm == 2){
                    tm = 90
                }

                var hard = ""

                hard = dataFavBD[i].tipo_hardware
                val existeFavMnt = dataFavBD!!.filter { s -> s.tipo_hardware == hard && s.id_accion ==  dataFavBD[i].id_accion}
                if(existeFavMnt.isEmpty()){
                    continue
                }




                var fecAct = databaseHelper_Data.getFecUltActTableMonitores( dataFavBD[i].id_accion, dataFavBD[i].tipo_hardware,  dataFavBD[i].id_equipo)

                if(fecAct == ""){
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    fecAct  = sdf.format(Date())
                }

                var fecUltAct = ""
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                // val bitacorahasta = sdf.format(Date())
                val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fecAct)
                val calender2 = Calendar.getInstance()
                calender2.time = convertedTodayDateHasta

                val year_Hasta = calender2.get(Calendar.YEAR)
                var month_Hasta = ""
                when (calender2.get(Calendar.MONTH)) {
                    0 -> month_Hasta = "01"
                    1 -> month_Hasta = "02"
                    2 -> month_Hasta = "03"
                    3 -> month_Hasta = "04"
                    4 -> month_Hasta = "05"
                    5 -> month_Hasta = "06"
                    6 -> month_Hasta = "07"
                    7 -> month_Hasta = "08"
                    8 -> month_Hasta = "09"
                    9 -> month_Hasta = "10"
                    10 -> month_Hasta = "11"
                    else -> { // Note the block
                        month_Hasta = "12"
                    }
                }

                var day_Hasta = ""
                day_Hasta = when (calender2.get(Calendar.DAY_OF_MONTH)){
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> { // Note the block
                        calender2.get(Calendar.DAY_OF_MONTH).toString()
                    }
                }

                var Horas_Hasta = ""
                Horas_Hasta = when (calender2.get(Calendar.HOUR_OF_DAY)){
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> { // Note the block
                        calender2.get(Calendar.HOUR_OF_DAY).toString()
                    }
                }



                var Minutos_Hasta = ""
                Minutos_Hasta = when (calender2.get(Calendar.MINUTE)){
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> { // Note the block
                        calender2.get(Calendar.MINUTE).toString()
                    }
                }

                var Segundos_Hasta = ""
                Segundos_Hasta = when (calender2.get(Calendar.SECOND)){
                    0 -> "00"
                    1 -> "01"
                    2 -> "02"
                    3 -> "03"
                    4 -> "04"
                    5 -> "05"
                    6 -> "06"
                    7 -> "07"
                    8 -> "08"
                    9 -> "09"
                    else -> { // Note the block
                        calender2.get(Calendar.SECOND).toString()
                    }
                }



                fecUltAct  = "$day_Hasta-$month_Hasta-$year_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta"



                val tituloView = TextView(context)
                tituloView.gravity = Gravity.CENTER_HORIZONTAL
                tituloView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15F)
                tituloView.typeface = typeface
                tituloView.setPadding(tm, 2, 2, 2)

                val DescView = TextView(context)
                DescView.gravity = Gravity.CENTER_HORIZONTAL
                DescView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                DescView.typeface = typeface
                var tipHard = "0"
                if(dataFavBD[i].tipo_hardware == "CPU"){
                    tipHard = "1"
                }
                if(dataFavBD[i].tipo_hardware == "MEMORIA"){
                    tipHard = "2"
                }

                when (dataFavBD[i].tipo_hardware) {
                    "DISCO" -> tipHard = "3"
                    "C:" -> tipHard = "31"
                    "D:" -> tipHard = "32"
                    "E:" -> tipHard = "33"
                    "F:" -> tipHard = "34"
                    "G:" -> tipHard = "35"
                }



                if(dataFavBD[i].tipo_hardware == "RED"){
                    tipHard = "4"
                }


                try {

                    var existeTag = Companion.linearLayoutMntFav?.findViewWithTag("${dataFavBD[i].id_accion}5000${tipHard}") as TextView
                    if ( existeTag != null){
                        continue
                    }



                }catch (e: Exception){

                }



                DescView.tag = "${dataFavBD[i].id_accion}5000$tipHard"
                DescView.setPadding(tm, 2, 2, 2)
                DescView.textSize = 12f
                val imageView = ImageView(context)
                imageView.id = i
                imageView.setPadding(tm, 2, 2, 2)

                val MetricasView = TextView(context)
                MetricasView.gravity = Gravity.CENTER_HORIZONTAL
                MetricasView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
                MetricasView.typeface = typeface
                var tipHardMetricas = "0"
                if(dataFavBD[i].tipo_hardware == "CPU"){
                    tipHardMetricas = "1"
                }
                if(dataFavBD[i].tipo_hardware == "MEMORIA"){
                    tipHardMetricas = "2"
                }


                when (dataFavBD[i].tipo_hardware) {
                    "DISCO" ->  tipHardMetricas = "3"
                    "C:" ->  tipHardMetricas = "31"
                    "D:" ->  tipHardMetricas = "32"
                    "E:" ->  tipHardMetricas = "33"
                    "F:" ->  tipHardMetricas = "34"
                    "G:" ->  tipHardMetricas = "35"
                }


                if(dataFavBD[i].tipo_hardware == "RED"){
                    tipHardMetricas= "4"
                }
                MetricasView.tag = "${dataFavBD[i].id_accion}6666$tipHardMetricas"
                MetricasView.setPadding(tm, 2, 2, 2)
                MetricasView.textSize = 11f


                if(dataFavBD[i].tipo_hardware == "RED"){

                    if (dataFavBD[i].tot_enviados != null){
                        var enviados = 0.0
                        var recibidos = 0.0
                        if(dataFavBD[i].tot_enviados != ""){
                            enviados = dataFavBD[i].tot_enviados.toDouble() * 0.000125
                            recibidos = dataFavBD[i].tot_recibidos.toDouble()
                        }

                        DescView.text = dataFavBD[i].tipo_hardware + ":" + String.format("%.2f", enviados)+" Kbps"
                        MetricasView.text = fecUltAct
                    }else{
                        var porcentajeHard = (dataFavBD[i].porcentaje).toDouble().roundToInt()
                        DescView.text = dataFavBD[i].tipo_hardware + ":" + porcentajeHard +  "%"
                        MetricasView.text = fecUltAct
                    }
                }else if(dataFavBD[i].tipo_hardware == "MEMORIA"){
                    var porcentajeHard = (dataFavBD[i].porcentaje).toDouble().roundToInt()
                    val metricaLibre = ((dataFavBD[i].espacio_libre.toDouble() * 100).roundToInt() / 100.0).roundToInt()
                    val metricaTotal = ((dataFavBD[i].espacio_total.toDouble() * 100).roundToInt() / 100.0).roundToInt()

                    DescView.text = "RAM " + porcentajeHard +  "% (" + "${metricaLibre}/${metricaTotal} GB)"
                    MetricasView.text = fecUltAct
                }else if(dataFavBD[i].tipo_hardware == "DISCO" || dataFavBD[i].tipo_hardware == "C:" || dataFavBD[i].tipo_hardware == "E:"  || dataFavBD[i].tipo_hardware == "D:" || dataFavBD[i].tipo_hardware == "F:" || dataFavBD[i].tipo_hardware == "G:"){
                    var porcentajeHard = (dataFavBD[i].porcentaje).toDouble().roundToInt()
                    var metricaLibre = dataFavBD[i].espacio_libre.toDouble() / 1024 / 1024 / 1024 // Bites 0.000000000125
                    var metricaTotal = dataFavBD[i].espacio_total.toDouble() / 1024 / 1024 / 1024 // Bites 0.000000000125
                    var imetricaLibre = ((metricaLibre * 100).roundToInt() / 100.0).roundToInt()
                    var imetricaTotal = ((metricaTotal * 100).roundToInt() / 100.0).roundToInt()
                    DescView.text = "${dataFavBD[i].tipo_hardware} " + porcentajeHard  +  "% ("  + "${imetricaLibre}/${imetricaTotal} GB)"
                    MetricasView.text = fecUltAct

                }else{
                    var porcentajeHard = (dataFavBD[i].porcentaje).toDouble().roundToInt()
                    DescView.text = dataFavBD[i].tipo_hardware + ":" + porcentajeHard +  "%"
                    MetricasView.text = fecUltAct
                }


                when (dataFavBD[i].tipo_grafico) {
                    "grafico-circular" -> {
                        tituloView.text = dataFavBD[i].nom_svr
                        imageView.setImageResource(R.drawable.graficocircular)

                    }
                    "grafico-barras" -> {
                        tituloView.text = dataFavBD[i].nom_svr
                        imageView.setImageResource(R.drawable.graficobarras)

                    }
                    "grafico-lineas" -> {
                        tituloView.text = dataFavBD[i].nom_svr
                        imageView.setImageResource(R.drawable.graficolineas)

                    }
                    "grafico-gauge" -> {
                        tituloView.text = dataFavBD[i].nom_svr
                        imageView.setImageResource(R.drawable.graficogauge)

                    }
                    else -> {
                        tituloView.text = ""
                        mgDefaultMntFav!!.visibility = View.GONE
                        imageView.setImageResource(R.drawable.estrellafavoritos64)
                        imageView.visibility = View.GONE
                        DescView.text = ""
                        disableModal = true
                    }
                }

                imageView.setOnClickListener {

                    getModalFavMonitoreables(dataFavBD[i].id_accion, dataFavBD[i].tipo_grafico, dataFavBD[i].tipo_hardware, disableModal)


                }


                val layoutDetalle = LinearLayout(context)
                layoutDetalle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutDetalle.orientation = LinearLayout.VERTICAL


                layoutDetalle?.addView(tituloView)
                layoutDetalle?.addView(imageView)
                layoutDetalle?.addView(DescView)
                layoutDetalle?.addView(MetricasView)

                tmlListViewMonitores.addView(layoutDetalle)



                tm = 90
            }

            if(tmlListViewMonitores != null){
                linearLayoutMntFav = tmlListViewMonitores
            }

        }


        fun getModalFavMonitoreables(id_accion: Int, tipo_grafico: String, ihardware: String, disableModal: Boolean){

            if (!disableModal){
                showModalGrafico(id_accion, tipo_grafico, ihardware)
            }
        }


        fun showModalGrafico(id_accion: Int, tipoGrafico: String, hardware: String) {
            NegocioFragment.modal_id_accion = id_accion
            NegocioFragment.modal_tipoGrafico = tipoGrafico
            NegocioFragment.modal_hardware = hardware
            modal_monitores_activo = true
//            view.findNavController().navigate(R.id.seven_fragment)
            //(context as MainActivity).setSelectedItem(7)
        }



    }



    //****************************************************************************


    fun msgErrorMonitores(mensaje: String){

        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(mensaje.replace("ERROR-", ""))
            .setPositiveButton("Aceptar") { view, _ ->
                view.dismiss()
            }
        builder.create()
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }


    fun navBarraLateral(pos: Int){

        when(pos){
//            8 ->  findNavController().navigate(R.id.eight_fragment)
//            9 ->  findNavController().navigate(R.id.nine_fragment)
        }

    }







}