package com.apptomatico.app_movil_kotlin_v3.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.Uri
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.GpsUtil
import com.apptomatico.app_movil_kotlin_v3.MainActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.UpdateVersion.UpdateVersionActivity
import com.apptomatico.app_movil_kotlin_v3.alertas.AlertasFragment
import com.apptomatico.app_movil_kotlin_v3.backup.AppExecutors
import com.apptomatico.app_movil_kotlin_v3.helpers.InputValidation
import com.apptomatico.app_movil_kotlin_v3.model.Alertas_List
import com.apptomatico.app_movil_kotlin_v3.negocio.ConsultaEstatusEquipoTask
import com.apptomatico.app_movil_kotlin_v3.negocio.DownloadController
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.licencia.LicenciaActivity
import com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil
import com.apptomatico.app_movil_kotlin_v3.model.DataValLicencia
import com.apptomatico.app_movil_kotlin_v3.model.GetUserNip
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.model.ModelLicencia
import com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil
import com.apptomatico.app_movil_kotlin_v3.model.User
import com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem
import com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest
import com.apptomatico.app_movil_kotlin_v3.model.ValNip
import com.apptomatico.app_movil_kotlin_v3.model.strValidacionAcesoNip
import com.apptomatico.app_movil_kotlin_v3.negocio.BackupDataBaseWorker
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.sqlcipher.database.SQLiteDatabase
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXFade
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStream
import java.io.PrintWriter
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Scanner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import kotlin.text.count
import kotlin.text.substring

class LoginActivity : AppCompatActivity(), View.OnClickListener {


    private val activity = this@LoginActivity
    private val permission = 101
    private var numUser = ""


    var ilatitud = ""
    var ilongitud = ""
    var netInfoMovil = ""
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    var GpsUtil: GpsUtil? = null




    val client by lazy { APIService.create() }
    var intentos: List<com.apptomatico.app_movil_kotlin_v3.model.RegLicenacia_List> =
        ArrayList()


    val TAG_LOGS = "PVDSI"


    private lateinit var nestedScrollView: NestedScrollView

    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout

    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var txtUltVersion: TextView

    private lateinit var appCompatButtonLogin: Button

    private lateinit var appCompatButtonLoginQA: Button

    private lateinit var btnRecuperaNIP: TextView

    private lateinit var btnimageUtlBiometrico: ImageView

    private lateinit var appBiometricLogin: Button

    private lateinit var appDesistalar: Button

    private lateinit var textViewLinkRegister: AppCompatTextView

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    private var alerta_titulo: String = ""
    private var alerta_desc: String = ""
    private var alerta_pendiente: String = ""
    private var id_alerta: String = ""

    private var svrWebSocket: String = ""

    lateinit var appExecutors: AppExecutors


    private var canAuthenticate = false
    private lateinit var propmtInfo: BiometricPrompt.PromptInfo

    lateinit var downloadController: DownloadController
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    lateinit var iConsultaEstatusEquipoTask: ConsultaEstatusEquipoTask


    private lateinit var chkEntornoProduccion: CheckBox


    var values: List<Alertas_List> = ArrayList()
    var favoritas: List<Alertas_List> =
        ArrayList()

    var progressbartop: ProgressBar? = null

    var lgprogressBar: ProgressBar? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var ECHO_URL_WEB_SOCKET: String = ""


    companion object {



        var webSocketService: EchoService? = null;

        var accionesRealizadas_Archivos: Int = 0
        var estatusequipo_id_accion: Int = 0
        var estatusequipo_id_origen: Int = 0
        var bd_titulo_consulta: String = ""
        var bd_nombre_equipo: String = ""
        var bd_hardware_equipo: String = ""
        var bd_parametros_accion: String = ""
        var idHardwareExpArc: String = ""
        var nomEquipoExpArc: String = ""
        var isReloadViewMonitores: Int = 0
        var contextMonitores: Context? = null
        var idHardwareMonitores: String = ""
        var valuesListEquipos: List<com.apptomatico.app_movil_kotlin_v3.model.Negocios_List> =
            ArrayList()
        var valuesListEquiposUnicamente: List<com.apptomatico.app_movil_kotlin_v3.model.Negocios_List> =
            ArrayList()
        var header_id_negocio: String = "Todos"
        var header_id_equipos: String = "Todos"
        var filtro_alertas_estatus: Int = 0
        var filtro_alertas_equipo: Int = 0
        var filtro_alertas_fecha: Int = 0
        var filtro_alertas_tipo: Int = 0
        var filtro_alertas_marcadas: Int = 0
        var filtro_alertas_fecha_desde_hasta = ""
        var filtro_alertas_eliminadas_estatus: Int = 0
        var filtro_alertas_eliminadas_fecha: Int = 0
        var filtro_alertas_eliminadas_fecha_desde_hasta = ""

        var accionejecutandose: Boolean = false

        var gtotal_alerta_pendientes: Int = 0
        var Islogin = false
        var hrIntervaloBitacora: Int = 60

        var color_carrousel_mnt: Boolean = true

        var origen_back_dashboard: Int = 0

        var loading_alertas = true
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        // Thread.sleep(1000) COMENTADO POR PRUEBAS EN PERFORMANCE LOGIN 08AGOSTO2023
        if (SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                val getpermission = Intent()
                getpermission.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivity(getpermission)
            }
        }
        if (Islogin) {
            super.onCreate(savedInstanceState)
            SQLiteDatabase.loadLibs(this)
            setContentView(R.layout.activity_login)

            initViews()
            // initializing the listeners
            initListeners()
            // initializing the objects
            initObjects()



            chkALertaPendiente(intent.extras)

        } else{

            setTheme(R.style.AppTheme)
            super.onCreate(savedInstanceState)
            SQLiteDatabase.loadLibs(this)
            setContentView(R.layout.activity_login)

            initViews()
            // initializing the listeners
            initListeners()
            // initializing the objects
            initObjects()

            chkALertaPendiente(intent.extras)


            // No Internet Dialog: Signal
            NoInternetDialogSignal.Builder(
                this,
                lifecycle
            ).apply {
                dialogProperties.apply {
                    connectionCallback = object : ConnectionCallback { // Optional
                        override fun hasActiveConnection(hasActiveConnection: Boolean) {

                            CargaInicialEquipos()

                        }
                    }

                    cancelable = false // Optional
                    noInternetConnectionTitle = "Sin internet" // Optional
                    noInternetConnectionMessage =
                        "Comprueba tu conexión a Internet e inténtalo de nuevo." // Optional
                    showInternetOnButtons = true // Optional
                    pleaseTurnOnText = "por favor enciende" // Optional
                    wifiOnButtonText = "Wifi" // Optional
                    mobileDataOnButtonText = "Datos móviles" // Optional

                    onAirplaneModeTitle = "Sin internet" // Optional
                    onAirplaneModeMessage = "Ha activado el modo avión." // Optional
                    pleaseTurnOffText = "Por favor apague" // Optional
                    airplaneModeOffButtonText = "Modo avión" // Optional
                    showAirplaneModeOffButtons = true // Optional
                }
            }.build()


            //Se comentan lineas para la actualizacion de la version del movil
            //********************Actualizacion*************
            //var appUpdater = AppUpdater(this)
            //appUpdater.setUpdateFrom(UpdateFrom.XML)
            //appUpdater.setDisplay(Display.NOTIFICATION)
            //appUpdater.setDisplay(Display.DIALOG)
            //appUpdater.setButtonDoNotShowAgain("")
            //appUpdater.setUpdateXML("https://dsiin.com/aplicativos_cafeterias/AplicacionesMoviles/update.xml")
            //appUpdater.start()
            //********************Fin de Actualizacion****************

            lgprogressBar = findViewById<ProgressBar>(R.id.lgprogressBar)

            supportActionBar!!.hide()

            //*****SE ESTABLECE POR DEFECTO EL ENTORNO DE DESARROLLO
            databaseHelper.addControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP)
            databaseHelper.updControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP)
            //***************************************************

            // chkEntornoProduccion.isChecked = databaseHelper.getControlEntornoConexion(this@LoginActivity) == 1

            /*
            chkEntornoProduccion.setOnCheckedChangeListener { _, isChecked ->

                if(isChecked){
                    databaseHelper.addControlEntornoConexion(1)
                    databaseHelper.updControlEntornoConexion(1)
                }else {
                    databaseHelper.addControlEntornoConexion(0)
                    databaseHelper.updControlEntornoConexion(0)
                }

            }
             */





            //CARGA PERFIL DEL USUARIO ENCABEZADOS Y FILTROS DESDE LA BASE LOCAL
            val iPerfil = databaseHelper.getPerfilFiltroEncabezado()
            if (iPerfil.isNotEmpty()) {
                header_id_negocio = iPerfil[0].header_id_negocio
                header_id_equipos = iPerfil[0].header_id_equipos
                filtro_alertas_estatus = iPerfil[0].filtro_alertas_estatus
                filtro_alertas_equipo = iPerfil[0].filtro_alertas_equipo
                filtro_alertas_fecha = iPerfil[0].filtro_alertas_fecha
                filtro_alertas_fecha_desde_hasta = iPerfil[0].filtro_alertas_fecha_desde_hasta
                filtro_alertas_eliminadas_estatus = iPerfil[0].filtro_alertas_eliminadas_estatus
                filtro_alertas_eliminadas_fecha = iPerfil[0].filtro_alertas_eliminadas_fecha
                filtro_alertas_eliminadas_fecha_desde_hasta =
                    iPerfil[0].filtro_alertas_eliminadas_fecha_desde_hasta
            }
            //
            val existeLic = validaExisteLicencia()
            valNoIntentosLic()
            if (existeLic) {

                //#CORECCION DE ERROR DATABASE
                SQLiteDatabase.loadLibs(this)
                var Licenciadb: List<Licencia> = databaseHelper.getAllLicencia()
                var Movil_Id = Licenciadb[0].id_movil
                databaseHelper.delEquiposNoAsignadosAMovil(Movil_Id)
                databaseHelper.updateEstadoInactivoEquipos()
                databaseHelper.updateEstadoInactivoAccionesEquipos()
                databaseHelper.delBDControlAccSocketXEquipoAll()
                // databaseHelper.delControlEstadoEquipos()

                gtotal_alerta_pendientes = databaseHelper.getTotalAlertasSinLeer()
                leerLogAlertas()
            }
            RequestTaskGetAlertasPendientes()
            /*
            svrWebSocket = databaseHelper.getControlServerSocket()
            if (svrWebSocket != "" && svrWebSocket.isNotEmpty()) {
                ECHO_URL_WEB_SOCKET = "wss://$svrWebSocket/ws/autenticacion/loby/"
                SharedApp.prefs.urlwebsocket = "wss://$svrWebSocket/ws/autenticacion/loby/"
                setupWebSocketService()
            }
            */


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_NUMBERS
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    permission
                )
            }



            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {

                    if (it) {

                    } else {
                        Snackbar.make(
                            findViewById<View>(android.R.id.content).rootView,
                            "Conceda permiso de notificación desde la configuración de la aplicación",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }


            val permissionCheck: Int = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                // ask permissions here using below code

            }


            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1
                )
            }



            if (isLocationEnabled()) {
                GpsUtil = GpsUtil(this)

            } else {
                if (databaseHelper.CheckRequiereUbicacion() && !isLocationEnabled()) {
                    var desc =
                        "Para poder utilizar la aplicación App Cafeterías es necesario que el dispositivo tenga activada la ubicación"
                    var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
                    dialogBuilder.setCancelable(false)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                            startActivity(intent)

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()


                }


            }




            try {
                databaseHelper.DeleteAccesosFallidosBiometria()
            } catch (ex: Exception) {

                print("Error Delete Biometria")
            }

            //txtUltVersion.text = "Version ${BuildConfig.VERSION_NAME}"

            txtUltVersion.text =""

            btnimageUtlBiometrico.setOnClickListener {
                btnimageUtlBiometrico.animationXFade(Fade.FADE_IN_DOWN)
                setupAuth()
                verifyBiometricAuthenticate()
            }



            btnRecuperaNIP!!.setOnClickListener {
                if (!checkForInternet(this@LoginActivity)) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Es necesario una conexión a internet para realizar esta acción",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    btnRecuperaNIP!!.animationXFade(Fade.FADE_IN_DOWN)
                    recuperaNipMovil(databaseHelper)
                }
            }





            setPeriodicWorkRequest()
            setupAuth()
            verifyBiometricAuthenticate()
            CargaInicialEquipos()
            chkExitNewVersion()
        }


    }




    override fun onStart() {
        super.onStart()
        validaExisteLicencia()
    }


    fun getCurrentSsid(context: Context): String? {
        var ssid: String? = null
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
                val connectionInfo: WifiInfo? = wifiManager.connectionInfo
                if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                    ssid = connectionInfo.getBSSID()
                }
            }
        }
        return ssid
    }


    private fun String.isAppInstalled(activity: Activity): Boolean {
        val pm = activity.packageManager!!
        try {
            pm.getPackageInfo(this, PackageManager.GET_ACTIVITIES)
            Toast.makeText(this@LoginActivity, "La apliacion sigue instalada", Toast.LENGTH_LONG)
                .show()
            return true
        } catch (e: PackageManager.NameNotFoundException) {

        }
        return false
    }

    /*
    GeoLocaclizacion
     */
    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    /**
     * FUNCION PARA VALIDAR SI EXISTE UNA SESION ACTIVA Y SE REQUIERE VISUALIZAR UNA ALERTA
     */

    private fun chkALertaPendiente(bundle: Bundle?) {

        if (bundle != null) {


            if (intent.hasExtra("titulo_alerta")) {
                alerta_titulo = intent.getStringExtra("titulo_alerta").toString()
            }
            if (intent.hasExtra("desc_alerta")) {
                alerta_desc = intent.getStringExtra("desc_alerta").toString()
            }
            if (intent.hasExtra("accion_alerta")) {
                alerta_pendiente = intent.getStringExtra("accion_alerta").toString()
            }
            if (intent.hasExtra("id_alerta")) {
                id_alerta = intent.getStringExtra("id_alerta").toString()
            }

            for (key in intent.extras?.keySet()!!) {
                var value = intent.extras?.get(key)
                Log.e("LOGINW", "Alerta Extras: $key")
                if (key == "titulo_alerta") {
                    alerta_pendiente = "desc_alerta"
                    alerta_titulo = value.toString()


                }
                if (key == "descripcion_alerta") {
                    alerta_desc = value.toString()
                }
            }


        }

        if (alerta_pendiente != null && alerta_pendiente != "") {

            if (Islogin) {

                val fragment = AlertasFragment()
                val bundle = Bundle()
                bundle.putString("titulo_alerta", alerta_titulo)
                bundle.putString("desc_alerta", alerta_desc)
                bundle.putString("id_alerta", id_alerta)
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitNow()

                // val accountsIntent = Intent(activity, MainActivity::class.java)
                // accountsIntent.putExtra("titulo_alerta", alerta_titulo)
                //  accountsIntent.putExtra("desc_alerta", alerta_desc)
                // accountsIntent.putExtra("id_alerta", id_alerta)
                // startActivity(accountsIntent)
                intent.removeExtra("accion_alerta")
                alerta_pendiente = ""
                intent.removeExtra("titulo_alerta")
                alerta_titulo = ""
                intent.removeExtra("desc_alerta")
                alerta_desc = ""
                finish()

            }

        }
    }



    private fun setupAuth() {

        if (!checkForInternet(this@LoginActivity)) {
            return
        }


        if (androidx.biometric.BiometricManager.from(this).canAuthenticate(
                androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
                        or androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
            ) == androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
        ) {


            canAuthenticate = true

            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Inicio de  sesión biométrico")
                .setSubtitle("Inicie sesión con su credencial biométrica \n Version de la app ${BuildConfig.VERSION_NAME}")
                .setAllowedAuthenticators(
                    androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
                )
                .setConfirmationRequired(false)
                .setNegativeButtonText("Utilizar NIP")
                .build()

        }


    }


    ////////


    /**
     * This method is to initialize views
     */
    private fun initViews() {

        nestedScrollView = findViewById<View>(R.id.nestedScrollView) as NestedScrollView

        // textInputLayoutEmail = findViewById<View>(R.id.textInputLayoutEmail) as TextInputLayout
        textInputLayoutPassword =
            findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout

        //textInputEditTextEmail = findViewById<View>(R.id.textInputEditTextEmail) as TextInputEditText
        textInputEditTextPassword =
            findViewById<View>(R.id.textInputEditTextPassword) as TextInputEditText

        txtUltVersion = findViewById<View>(R.id.txtUltVersion) as TextView

        appCompatButtonLogin = findViewById<View>(R.id.appCompatButtonLogin) as Button

        appCompatButtonLoginQA = findViewById<View>(R.id.appCompatButtonLoginQA) as Button

        btnRecuperaNIP = findViewById(R.id.btnRecuperaNIP) as TextView

        progressbartop = findViewById<View>(R.id.progressbartop) as ProgressBar

        btnimageUtlBiometrico = findViewById<View>(R.id.imageUtlBiometrico) as ImageView


        chkEntornoProduccion = findViewById<View>(R.id.chkEntornoProduccion) as CheckBox

        // textViewLinkRegister = findViewById<View>(R.id.textViewLinkRegister) as AppCompatTextView


    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {

        appCompatButtonLogin.setOnClickListener(this)

        appCompatButtonLoginQA.setOnClickListener(this)


        //appBiometricLogin.setOnClickListener(this)

        // textViewLinkRegister.setOnClickListener(this)
    }


    /**
     * This method is to initialize objects to be used
     */
    private fun initObjects() {

        databaseHelper = DatabaseHelper.getInstance(activity)
        inputValidation = InputValidation(activity)
        databaseHelper.addConfiguracionMovilEquiposMasOpciones()
        //  appExecutors = AppExecutors()


    }


    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonLogin -> verifyFromSQLite()

        }
    }


    private fun appInstall() {
        val FILE_NAME = "app_android.apk"
        val FILE_BASE_PATH = "file://"
        val PROVIDER_PATH = ".provider"
        val MIME_TYPE = "application/vnd.android.package-archive"
        val url =
            "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"
        var destination =
            this@LoginActivity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/"
        destination += FILE_NAME

        val uri = Uri.parse("$FILE_BASE_PATH$destination")

        val file = File(destination)
        if (file.exists()) file.delete()

        val downloadManager =
            this@LoginActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)
        request.setMimeType(MIME_TYPE)
        request.setTitle(this@LoginActivity.getString(R.string.title_file_download))
        request.setDescription(this@LoginActivity.getString(R.string.downloading))

        // set destination
        request.setDestinationUri(uri)

        showInstallOption(destination, uri)
        // Enqueue a new download and same the referenceId
        downloadManager.enqueue(request)
        Toast.makeText(
            this@LoginActivity,
            this@LoginActivity.getString(R.string.downloading),
            Toast.LENGTH_LONG
        )
            .show()


    }

    private fun showInstallOption(
        destination: String,
        uri: Uri
    ) {

        val FILE_NAME = "app_android.apk"
        val FILE_BASE_PATH = "file://"
        val PROVIDER_PATH = ".provider"
        val url = "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"
        val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
        // set BroadcastReceiver to install app when .apk is downloaded
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(
                context: Context,
                intent: Intent
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        BuildConfig.APPLICATION_ID + PROVIDER_PATH,
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                    // finish()
                } else {
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(
                        uri,
                        APP_INSTALL_PATH
                    )
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                    // finish()
                }
            }
        }
        ContextCompat.registerReceiver(
            this@LoginActivity,
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )


    }


    private fun appDesistalar() {
        val packageURI: Uri =
            Uri.parse("package:" + "com.apptomatico.app_movil_kotlin_v3")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
        startActivity(uninstallIntent)
    }


    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */


    private fun verifyBiometricAuthenticate() {
        if (!checkForInternet(this@LoginActivity)) {
            return
        }
        if (databaseHelper.CheckRequiereUbicacion() && !isLocationEnabled()) {
            //Actividade 14.- Acceso - Revisar que no te pida activar GPS para poder acceder a la APP - JJHE 04-Agosto-2022

            var desc =
                "Para poder utilizar la aplicación App Cafeterías es necesario que el dispositivo tenga activada la ubicación"
            var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setMessage(desc)
                .setCancelable(false)
                .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)

                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("")
            alert.show()


        }
        if (canAuthenticate) {
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())


            executor = ContextCompat.getMainExecutor(this)
            biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {


                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)


                        if (errorCode == ERROR_USER_CANCELED) {
                            databaseHelper.addSeguridadBiometrica(
                                "",
                                currentDate,
                                "authentication_error"
                            )

                            var numeroIntentos = databaseHelper.CheckAccesosFallidosBiometria()
                            if (numeroIntentos) {
                                biometricPrompt.cancelAuthentication()
                            } else {
                                //finish()
                                //startActivity(intent)

                            }


                        }

                    }

                    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)


                        var tipoAcceso = ""
                        if (result.authenticationType == 1) {
                            tipoAcceso = "Contraseña del dispositivo"
                        } else {
                            tipoAcceso = "Biometria"
                        }

                        databaseHelper.addSeguridadBiometrica(
                            tipoAcceso,
                            currentDate,
                            "authentication_succeeded"
                        )

                        verifyAccesoBiometrico()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        databaseHelper.addSeguridadBiometrica(
                            "",
                            currentDate,
                            "authentication_error"
                        )

                        var numeroIntentos = databaseHelper.CheckAccesosFallidosBiometria()
                        if (numeroIntentos) {
                            biometricPrompt.cancelAuthentication()
                        }
                    }


                })


            biometricPrompt.authenticate(promptInfo)


        }


    }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun verifyAccesoBiometrico() {

        if (!checkForInternet(this@LoginActivity)) {
            var desc = "Conéctese a Internet, revise la configuracion de su red"
            var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
            dialogBuilder.setMessage(desc)
                .setCancelable(false)
                .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("")
            alert.show()
            return
        }


        if (GpsUtil != null) {
            GpsUtil?.getLastLocation(this, object : GpsUtil.LocationCallbackInterface {
                override fun onLocationResult(
                    latitude: String,
                    longitude: String
                ) {
                    ilatitud = latitude
                    ilongitud = longitude
                }

                override fun onLocationFailure() {
                    TODO("Not yet implemented")
                }
            })



        } else {
            ilatitud = "0.0"
            ilongitud = "0.0"
        }




        try {

            var task = GetPublicIP().execute()
            val ipPublic_Divice = task.get().toString()
            databaseHelper.delAControlRed()
            databaseHelper.addCOntrolDB(ipPublic_Divice)

        } catch (ex: Exception) {

        }

        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val macMovil: String = getMac(this.applicationContext).toString()
        val phoneMovil: String = getMovilData(this.applicationContext).toString()
        val idSIMMovil: String = getSIMMovil(this.applicationContext).toString()
        val imeiMovil: String = getIMEIMovil(this.applicationContext).toString()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        var Licenciadb: List<Licencia>  = databaseHelper.getAllLicencia()
        var nombre_Movil = Licenciadb[0].nommbre_movil


        val UserData = UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
        val result: Call<UserDataCollectionItem> =
            userService.submit(UserData)

        // val loadingDialog = loadingDialog(this@LoginActivity)
        // loadingDialog.startLoadingDialog()

        lgprogressBar!!.visibility = View.VISIBLE

        result.enqueue(object :
            Callback<UserDataCollectionItem> {
            override fun onFailure(
                call: Call<UserDataCollectionItem>,
                t: Throwable
            ) {
                lgprogressBar!!.visibility = View.GONE
                /*
                var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
                dialogBuilder.setMessage("No fue posible conectar con servidor remoto, revice su configuracion de red - ${t.message}")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("")
                alert.show()
                 */

                // Toast.makeText(this@LoginActivity, "Error servicio no disponible 1", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<UserDataCollectionItem>,
                response: Response<UserDataCollectionItem>
            ) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    SharedApp.prefs.token = resultado.token.toString()


                    val userValNip: APIService =
                        RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
                    val resultNip: Call<ValNip> =
                        userValNip.valNip(
                            "authbiometrica",
                            "$idDispositivo",
                            "$ilatitud",
                            "$ilongitud",
                            "$netInfoMovil",
                            "JWT $token",
                            "application/json"
                        )

                    resultNip.enqueue(object :
                        Callback<ValNip> {
                        override fun onFailure(
                            call: Call<ValNip>,
                            t: Throwable
                        ) {
                            lgprogressBar!!.visibility = View.GONE
                            // valNoIntentosLic()
                            var validacion: strValidacionAcesoNip? =
                                databaseHelper.CheckIntentosNIP()
                            Toast.makeText(
                                this@LoginActivity,
                                "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                Toast.LENGTH_LONG
                            ).show()

                            if (!validacion.esValido) {
                                this@LoginActivity.deleteDatabase("UserManager.db")
                                showAlertDialog()
                                appDesistalar()
                            }


                            return
                        }

                        @SuppressLint("CheckResult")
                        override fun onResponse(
                            call: Call<ValNip>,
                            response: Response<ValNip>
                        ) {
                            val resultado = response.body()
                            if (resultado == null) {

                                client.newAlerta(
                                    "N/A",
                                    "$idDispositivo",
                                    "$phoneMovil",
                                    "$macMovil",
                                    "$imeiMovil",
                                    "Acceso de ingreso fallido desde dispositivo movil",
                                    "Se registro un  intento de ingreso fallido a la aplicacion android, funcion  biometrica - retrofit userValNip.valNip - Response regreso un valor null",
                                    "$nombre_Movil",
                                    "JWT $token",
                                    "application/json"
                                )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Dispositivo no Autorizado-Se registro una alerta en administrador con el detalle",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }, { throwable ->
                                        //Toast.makeText(this@LoginActivity, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                                    }
                                    )


                                lgprogressBar!!.visibility = View.GONE
                                //Toast.makeText(this@LoginActivity, "El Nip no es valido", Toast.LENGTH_LONG).show()
                                //valNoIntentosLic()
                                var validacion: strValidacionAcesoNip? =
                                    databaseHelper.CheckIntentosNIP()
                                // Toast.makeText(this@LoginActivity, "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}", Toast.LENGTH_LONG).show()

                                if (!validacion!!.esValido) {
                                    this@LoginActivity.deleteDatabase("UserManager.db")
                                    showAlertDialog()
                                    appDesistalar()
                                }
                                return
                            } else {
                                val dblgNip: List<GetUserNip> =
                                    resultado.data.toList()

                                databaseHelper.delLongitudNip()
                                databaseHelper.addLongitudNip(
                                    dblgNip[0].movil_longitud_min_nip,
                                    dblgNip[0].movil_longitud_max_nip
                                )

                                val getLicencias: APIService =
                                    RestEngine.getRestEngine(databaseHelper, this@LoginActivity)
                                        .create(APIService::class.java)
                                val result_subs: Call<DataValLicencia> =
                                    getLicencias.getLicencias(
                                        "$idDispositivo",
                                        "JWT $token",
                                        "application/json"
                                    )

                                result_subs.enqueue(object :
                                    Callback<DataValLicencia> {
                                    override fun onFailure(
                                        call: Call<DataValLicencia>,
                                        t: Throwable
                                    ) {

                                        client.newAlerta(
                                            "N/A",
                                            "$idDispositivo",
                                            "$phoneMovil",
                                            "$macMovil",
                                            "$imeiMovil",
                                            "Acceso dispositivo movil no registrado",
                                            "Se registro un  intento de ingreso fallido a la aplicacion android, funcion  biometrica - retrofit getLicencias.getLicencias - ${t.message}",
                                            "$nombre_Movil",
                                            "JWT $token",
                                            "application/json"
                                        )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Dispositivo no Autorizado-Se registro una alerta en administrador con el detalle",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }, { throwable ->
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Error: ${throwable.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            )
                                        lgprogressBar!!.visibility = View.GONE
                                        // valNoIntentosLic()
                                        var validacion: strValidacionAcesoNip? =
                                            databaseHelper.CheckIntentosNIP()
                                        //Toast.makeText(this@LoginActivity, "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}", Toast.LENGTH_LONG).show()

                                        if (!validacion!!.esValido) {
                                            this@LoginActivity.deleteDatabase("UserManager.db")
                                            showAlertDialog()
                                            appDesistalar()
                                        }
                                    }

                                    override fun onResponse(
                                        call: Call<DataValLicencia>,
                                        response: Response<DataValLicencia>
                                    ) {
                                        if (response.code() == 200) {
                                            val resultado = response.body()
                                            if (resultado != null) {
                                                val db: List<ModelLicencia> =
                                                    resultado.data.toList()
                                                val resultIdMovil = db[0].movil_id
                                                val resultRequiereUbicacion =
                                                    db[0].requiere_ubicacion
                                                SharedApp.prefs.idDispositivo =
                                                    db[0].movil_id.toString()

                                                databaseHelper.addControlTopAlertas()
                                                //databaseHelper.updConfiguracionTopAlertas(db[0].scroll_alertas)

                                                databaseHelper.deleteUser()
                                                val usernew =
                                                    User(
                                                        1,
                                                        db[0].alias_movil,
                                                        "",
                                                        ""
                                                    )
                                                databaseHelper.addUser(usernew)

                                                databaseHelper.deleteServerSocket()
                                                databaseHelper.addControlServerSocket(
                                                    db[0].svr_web_socket,
                                                    db[0].svr_puerto_web_socket
                                                )

                                                if (svrWebSocket == "" || svrWebSocket.isEmpty()) {


                                                    //if (chkEntornoProduccion.isChecked){
                                                    databaseHelper.addControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP.toInt())
                                                    databaseHelper.updControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP.toInt())
                                                    // }else{
                                                    //    databaseHelper.addControlEntornoConexion(0)
                                                    //   databaseHelper.updControlEntornoConexion(0)
                                                    //}


                                                    svrWebSocket =
                                                        databaseHelper.getControlServerSocket()
                                                    if (svrWebSocket != "" && svrWebSocket.isNotEmpty()) {

                                                        if (BuildConfig.ENTORNO_ACT_APP.toInt() == 1){
                                                            ECHO_URL_WEB_SOCKET = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            SharedApp.prefs.urlwebsocket = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            setupWebSocketService()
                                                        }else{
                                                            ECHO_URL_WEB_SOCKET = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            SharedApp.prefs.urlwebsocket = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            setupWebSocketService()
                                                        }





                                                    }
                                                }

                                                databaseHelper.deleteParametrosLicencia()
                                                databaseHelper.addParametrosLicencia("$resultRequiereUbicacion")


                                                databaseHelper.addVigenciaLicencia(
                                                    db[0].fecha_desde,
                                                    db[0].fecha_hasta,
                                                    db[0].dias_vigencia_x_vencer,
                                                    db[0].intervalo_vigencia_x_vencer
                                                )

                                                databaseHelper.updVigenciaLicencia(
                                                    db[0].fecha_desde,
                                                    db[0].fecha_hasta,
                                                    db[0].dias_vigencia_x_vencer,
                                                    db[0].intervalo_vigencia_x_vencer
                                                )


                                                //Valida horario laboraol de la apliacion
                                                val getHorario: APIService =
                                                    RestEngine.getRestEngine(databaseHelper, this@LoginActivity)
                                                        .create(APIService::class.java)
                                                val resultHorario: Call<DataHorariosMovil> =
                                                    getHorario.getHorarioMovil(
                                                        resultIdMovil,
                                                        0,
                                                        "$idDispositivo",
                                                        "JWT $token",
                                                        "application/json"
                                                    )
                                                resultHorario.enqueue(object : Callback<DataHorariosMovil> {
                                                    override fun onFailure(
                                                        call: Call<DataHorariosMovil>,
                                                        t: Throwable
                                                    ) {
                                                        lgprogressBar!!.visibility = View.GONE
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Error al recuperar horarios movil",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }

                                                    override fun onResponse(
                                                        call: Call<DataHorariosMovil>,
                                                        response: Response<DataHorariosMovil>
                                                    ) {
                                                        val resHorariosmv = response.body()
                                                        if (resHorariosmv != null) {
                                                            val horario: List<RegHorarios_Movil> =
                                                                resHorariosmv.data.toList()
                                                            val smDateFormat =
                                                                SimpleDateFormat("HH:mm:ss")
                                                            val currenTime: String =
                                                                smDateFormat.format(Date())
                                                            val horaInicial =
                                                                horario[0].horario_desde
                                                            var horaFinal = horario[0].horario_hasta
                                                            val dia_semana = horario[0].dia_semana
                                                            val iilatitud = horario[0].ult_latitud
                                                            val iilongitud = horario[0].ult_longitud
                                                            val iirangomts = horario[0].rango_metros
                                                            if (horaFinal == "00:00:00"){
                                                                horaFinal = "23:59:59"
                                                            }

                                                            if (iilatitud.toDouble() != 0.0 && iilongitud.toDouble() != 0.0) {
                                                                //Para calcular la posicion permitida dle movil se implementan los principios de la formula Haversine
                                                                var haversine_d =
                                                                    iirangomts * 0.0000449 / 5
                                                                //Fromula para calcular distancia +- Norte/Sur,
                                                                // Maxima latitud al Norte = suma el valor de d a la latitud inicial
                                                                var ltNorteMax =
                                                                    iilatitud.toDouble() + haversine_d.toDouble()
                                                                //Maxima latitud al SUR  = resta el valor de d a la latitud inicial
                                                                var ltSurMax =
                                                                    iilatitud.toDouble() - haversine_d.toDouble()

                                                                //Formula para calcular distancia +- Este/Oste
                                                                //Se obtienen el Coseno de la latitud(En radianes)
                                                                var cosLatutud = Math.cos(
                                                                    Math.toRadians(iilatitud.toDouble())
                                                                )

                                                                // Maxima longitud al Este= longitud + (valor de d /cos(latitud))
                                                                var ltEsteMax =
                                                                    iilongitud.toDouble() + (haversine_d / cosLatutud)
                                                                //Maxima longitud al Oeste = longitud - (valor de d /cos(latitud))
                                                                var ltOesteMax =
                                                                    iilongitud.toDouble() - (haversine_d / cosLatutud)


                                                                if (ilatitud.toDouble() in ltSurMax..ltNorteMax) {
                                                                    if (ilongitud.toDouble() in ltOesteMax..ltEsteMax) {

                                                                    } else {
                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        Toast.makeText(
                                                                            this@LoginActivity,
                                                                            "El dispositivo se encuentra fuera del area de operacion",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                        return
                                                                    }
                                                                } else {
                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@LoginActivity,
                                                                        "El dispositivo se encuentra fuera del area de operacion",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    return
                                                                }

                                                            }



                                                            if (horaInicial != "SN" && horaFinal != "SN") {


                                                                if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1
                                                                ) {

                                                                    if (alerta_pendiente != null && alerta_pendiente != "") {


                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        val accountsIntent = Intent(
                                                                            activity,
                                                                            MainActivity::class.java
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "titulo_alerta",
                                                                            alerta_titulo
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "desc_alerta",
                                                                            alerta_desc
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "id_alerta",
                                                                            id_alerta
                                                                        )
                                                                        startActivity(accountsIntent)
                                                                        intent.removeExtra("accion_alerta")
                                                                        alerta_pendiente = ""
                                                                        intent.removeExtra("titulo_alerta")
                                                                        alerta_titulo = ""
                                                                        intent.removeExtra("desc_alerta")
                                                                        alerta_desc = ""


                                                                    } else {

                                                                        try {
                                                                            databaseHelper.DeleteAccesosFallidosBiometria()
                                                                        } catch (ex: Exception) {

                                                                        }


                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        val accountsIntent = Intent(
                                                                            activity,
                                                                            MainActivity::class.java
                                                                        )
                                                                        startActivity(accountsIntent)
                                                                        //Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_LONG).show()
                                                                    }


                                                                } else {
                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@LoginActivity,
                                                                        "El dispositivo se encuentra fuera del horario laboral",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            } else {

                                                                if (alerta_pendiente != null && alerta_pendiente != "") {


                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    val accountsIntent = Intent(
                                                                        activity,
                                                                        MainActivity::class.java
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "titulo_alerta",
                                                                        alerta_titulo
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "desc_alerta",
                                                                        alerta_desc
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "id_alerta",
                                                                        id_alerta
                                                                    )
                                                                    startActivity(accountsIntent)
                                                                    intent.removeExtra("accion_alerta")
                                                                    alerta_pendiente = ""
                                                                    intent.removeExtra("titulo_alerta")
                                                                    alerta_titulo = ""
                                                                    intent.removeExtra("desc_alerta")
                                                                    alerta_desc = ""


                                                                } else {

                                                                    try {
                                                                        databaseHelper.DeleteAccesosFallidosBiometria()
                                                                    } catch (ex: Exception) {

                                                                    }


                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    val accountsIntent = Intent(
                                                                        activity,
                                                                        MainActivity::class.java
                                                                    )
                                                                    startActivity(accountsIntent)


                                                                }
                                                            }


                                                        } else {

                                                            try {
                                                                databaseHelper.DeleteAccesosFallidosBiometria()
                                                            } catch (ex: Exception) {

                                                            }

                                                            lgprogressBar!!.visibility = View.GONE
                                                            val accountsIntent = Intent(
                                                                activity,
                                                                MainActivity::class.java
                                                            )
                                                            startActivity(accountsIntent)

                                                        }


                                                    }

                                                })


                                            } else {
                                                lgprogressBar!!.visibility = View.GONE
                                                var fec_hasta = databaseHelper.getFecHasta()

                                                if (fec_hasta != "") {
                                                    fec_hasta = fec_hasta.replace("T", " ")
                                                    val dateHasta =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                                            fec_hasta
                                                        )
                                                    val smDateFormatAct =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                    val currenTimeAct: String =
                                                        smDateFormatAct.format(Date())
                                                    val dateAct =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                                            currenTimeAct
                                                        )

                                                    if (dateAct.compareTo(dateHasta)!! > 0) {
                                                        // Toast.makeText(this@LoginActivity, "La licencia del movil finalizo, por favor contecte a su administrador si desea renovarla.", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Dispositivo no Registrado",
                                                            Toast.LENGTH_LONG
                                                        ).show()

                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Dispositivo no Registrado",
                                                        Toast.LENGTH_LONG
                                                    ).show()

                                                }

                                                validaLicenciaVencida()
                                                // valNoIntentosLic()
                                                var validacion: strValidacionAcesoNip? =
                                                    databaseHelper.CheckIntentosNIP()
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                if (!validacion.esValido) {
                                                    this@LoginActivity.deleteDatabase("UserManager.db")
                                                    showAlertDialog()
                                                    appDesistalar()
                                                }
                                            }


                                        } else if (response.code() == 404) {
                                            lgprogressBar!!.visibility = View.GONE
                                            try {
                                                val jObjError =
                                                    JSONObject(response.errorBody()!!.string())
                                                val ref: String = jObjError.getString("ERROR")
                                                Toast.makeText(
                                                    this@LoginActivity, ref,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } catch (e: java.lang.Exception) {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    e.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                        }


                                    }

                                })


                            }
                        }

                    })


                }
            }

        })


    }

    private fun validaExisteLicencia(): Boolean {
        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        if (!databaseHelper.checkLicencia(idDispositivo)) {


            val accountsIntent = Intent(this@LoginActivity, LicenciaActivity::class.java)
            startActivity(accountsIntent)
            Toast.makeText(this@LoginActivity, "Ingresa Licencia", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        return true

    }



    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun verifyFromSQLite() {

        if (!checkForInternet(this@LoginActivity)) {
            var desc = "Conéctese a Internet, revise la configuracion de su red"
            var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
            dialogBuilder.setMessage(desc)
                .setCancelable(false)
                .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("")
            alert.show()
            return
        }


        //** try {

        if (GpsUtil != null) {
            GpsUtil?.getLastLocation(this, object : GpsUtil.LocationCallbackInterface {
                override fun onLocationResult(
                    latitude: String,
                    longitude: String
                ) {
                    ilatitud = latitude
                    ilongitud = longitude
                }

                override fun onLocationFailure() {
                    TODO("Not yet implemented")
                }
            })
        } else {
            ilatitud = "0.0"
            ilongitud = "0.0"
        }

        if (intentos.count() > 0) {

            if (intentos[0].intentos_fallidos > intentos[0].intentos_permitidos) {
                this.applicationContext.deleteDatabase("UserManager.db")
                showAlertDialog()
                appDesistalar()

                return
            }
        }


        netInfoMovil = getInfoNetwork()
        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val macMovil: String = getMac(this.applicationContext).toString()
        val phoneMovil: String = getMovilData(this.applicationContext).toString()
        val idSIMMovil: String = getSIMMovil(this.applicationContext).toString()
        val imeiMovil: String = getIMEIMovil(this.applicationContext).toString()
        val nip = textInputEditTextPassword.text.toString().trim()

        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        var Licenciadb: List<Licencia>  = databaseHelper.getAllLicencia()
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if (!inputValidation.isInputEditTextFilled(
                textInputEditTextPassword,
                textInputLayoutPassword,
                getString(R.string.error_message_email)
            )
        ) {
            return
        }


        if (!databaseHelper.checkLicencia(idDispositivo)) {


            val accountsIntent = Intent(activity, LicenciaActivity::class.java)
            startActivity(accountsIntent)
            Toast.makeText(this@LoginActivity, "Ingresa Licencia", Toast.LENGTH_LONG).show()
            return
        }



        try {

            var task = GetPublicIP().execute()
            val ipPublic_Divice = task.get().toString()
            if (ipPublic_Divice == "") {
                /*
                                var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
                                dialogBuilder.setMessage("No fue posible conectar a internet, revice su configuracion de red")
                                    .setCancelable(false)
                                    .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                                        view.dismiss()
                                    })
                                var alert = dialogBuilder.create()
                                alert.setTitle("")
                                alert.show()

                 */
            }
            databaseHelper.delAControlRed()
            databaseHelper.addCOntrolDB(ipPublic_Divice)

        } catch (ex: Exception) {
            // Toast.makeText(this@LoginActivity, "", Toast.LENGTH_LONG).show()
        }


        val UserData = UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
        val result: Call<UserDataCollectionItem> =
            userService.submit(UserData)

        // val loadingDialog = loadingDialog(this@LoginActivity)
        // loadingDialog.startLoadingDialog()
        lgprogressBar!!.visibility = View.VISIBLE
        result.enqueue(object :
            Callback<UserDataCollectionItem> {
            override fun onFailure(
                call: Call<UserDataCollectionItem>,
                t: Throwable
            ) {
                lgprogressBar!!.visibility = View.GONE

            }

            override fun onResponse(
                call: Call<UserDataCollectionItem>,
                response: Response<UserDataCollectionItem>
            ) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    SharedApp.prefs.token = resultado.token.toString()


                    val userValNip: APIService =
                        RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
                    val resultNip: Call<ValNip> =
                        userValNip.valNip(
                            "$nip",
                            "$idDispositivo",
                            "$ilatitud",
                            "$ilongitud",
                            "$netInfoMovil",
                            "JWT $token",
                            "application/json"
                        )

                    resultNip.enqueue(object :
                        Callback<ValNip> {
                        override fun onFailure(
                            call: Call<ValNip>,
                            t: Throwable
                        ) {
                            lgprogressBar!!.visibility = View.GONE
                            // Toast.makeText(this@LoginActivity, "El Nip no es valido", Toast.LENGTH_LONG).show()
                            //valNoIntentosLic()
                            var validacion: strValidacionAcesoNip? =
                                databaseHelper.CheckIntentosNIP()
                            Toast.makeText(
                                this@LoginActivity,
                                "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                Toast.LENGTH_LONG
                            ).show()

                            if (!validacion.esValido) {
                                this@LoginActivity.deleteDatabase("UserManager.db")
                                showAlertDialog()
                                appDesistalar()
                            }
                            return
                        }

                        override fun onResponse(
                            call: Call<ValNip>,
                            response: Response<ValNip>
                        ) {
                            val resultado = response.body()
                            if (resultado == null) {

                                client.newAlerta(
                                    "N/A",
                                    "$idDispositivo",
                                    "$phoneMovil",
                                    "$macMovil",
                                    "$imeiMovil",
                                    "Acceso de ingreso fallido desde dispositivo movil",
                                    "Se registro un  intento de ingreso fallido a la aplicacion android, funcion  NIP - retrofit userValNip.valNip - response regreso un valor null",
                                    "$nombre_Movil",
                                    "JWT $token",
                                    "application/json"
                                )
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "Dispositivo no Autorizado-Se registro una alerta en administrador con el detalle",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }, { throwable ->
                                        //Toast.makeText(this@LoginActivity, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                                    }
                                    )
                                lgprogressBar!!.visibility = View.GONE
                                // Toast.makeText(this@LoginActivity, "El Nip no es valido", Toast.LENGTH_LONG).show()
                                // valNoIntentosLic()
                                var validacion: strValidacionAcesoNip? =
                                    databaseHelper.CheckIntentosNIP()
                                Toast.makeText(
                                    this@LoginActivity,
                                    "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                    Toast.LENGTH_LONG
                                ).show()

                                if (!validacion.esValido) {
                                    this@LoginActivity.deleteDatabase("UserManager.db")
                                    showAlertDialog()
                                    appDesistalar()
                                }
                                return
                            } else {

                                val dblgNip: List<GetUserNip> =
                                    resultado.data.toList()

                                databaseHelper.delLongitudNip()
                                databaseHelper.addLongitudNip(
                                    dblgNip[0].movil_longitud_min_nip,
                                    dblgNip[0].movil_longitud_max_nip
                                )


                                val getLicencias: APIService =
                                    RestEngine.getRestEngine(databaseHelper, this@LoginActivity)
                                        .create(APIService::class.java)
                                val result_subs: Call<DataValLicencia> =
                                    getLicencias.getLicencias(
                                        "$idDispositivo",
                                        "JWT $token",
                                        "application/json"
                                    )

                                result_subs.enqueue(object :
                                    Callback<DataValLicencia> {
                                    override fun onFailure(
                                        call: Call<DataValLicencia>,
                                        t: Throwable
                                    ) {

                                        client.newAlerta(
                                            "N/A",
                                            "$idDispositivo",
                                            "$phoneMovil",
                                            "$macMovil",
                                            "$imeiMovil",
                                            "Acceso dispositivo movil no registrado",
                                            "Se registro un  intento de ingreso fallido a la aplicacion android, funcion  NIP - retrofit getLicencias.getLicencias - ${t.message}",
                                            "$nombre_Movil",
                                            "JWT $token",
                                            "application/json"
                                        )
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe({
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Dispositivo no Autorizado-Se registro una alerta en administrador con el detalle",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }, { throwable ->
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "Error: ${throwable.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                            )
                                        lgprogressBar!!.visibility = View.GONE
                                        // valNoIntentosLic()
                                        var validacion: strValidacionAcesoNip? =
                                            databaseHelper.CheckIntentosNIP()
                                        Toast.makeText(
                                            this@LoginActivity,
                                            "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        if (!validacion.esValido) {
                                            this@LoginActivity.deleteDatabase("UserManager.db")
                                            showAlertDialog()
                                            appDesistalar()
                                        }
                                    }

                                    override fun onResponse(
                                        call: Call<DataValLicencia>,
                                        response: Response<DataValLicencia>
                                    ) {


                                        if (response.code() == 200) {
                                            val resultado = response.body()
                                            if (resultado != null) {
                                                val db: List<ModelLicencia> =
                                                    resultado.data.toList()
                                                val resultIdMovil = db[0].movil_id
                                                val resultRequiereUbicacion =
                                                    db[0].requiere_ubicacion
                                                SharedApp.prefs.idDispositivo =
                                                    db[0].movil_id.toString()

                                                databaseHelper.deleteUser()
                                                val usernew =
                                                    User(
                                                        1,
                                                        db[0].alias_movil,
                                                        "",
                                                        ""
                                                    )
                                                databaseHelper.addUser(usernew)

                                                databaseHelper.addControlTopAlertas()
                                                //databaseHelper.updConfiguracionTopAlertas(db[0].scroll_alertas)

                                                databaseHelper.deleteServerSocket()
                                                databaseHelper.addControlServerSocket(
                                                    db[0].svr_web_socket,
                                                    db[0].svr_puerto_web_socket
                                                )

                                                databaseHelper.deleteConfDefault()
                                                databaseHelper.addConfiguracionDefault(
                                                    db[0].conf_movil_alertas,
                                                    db[0].conf_movil_alertas_recientes,
                                                    db[0].conf_movil_monitores,
                                                    db[0].conf_movil_estatus_equipos,
                                                    db[0].conf_movil_info_equipos
                                                )

                                                if (svrWebSocket == "" || svrWebSocket.isEmpty()) {

                                                    //if (chkEntornoProduccion.isChecked){
                                                    databaseHelper.addControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP.toInt())
                                                    databaseHelper.updControlEntornoConexion(BuildConfig.ENTORNO_ACT_APP.toInt())
                                                    //}else{
                                                    //    databaseHelper.addControlEntornoConexion(0)
                                                    //    databaseHelper.updControlEntornoConexion(0)
                                                    //}

                                                    svrWebSocket =
                                                        databaseHelper.getControlServerSocket()
                                                    if (svrWebSocket != "" && svrWebSocket.isNotEmpty()) {

                                                        if (BuildConfig.ENTORNO_ACT_APP.toInt() == 1){
                                                            ECHO_URL_WEB_SOCKET = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            SharedApp.prefs.urlwebsocket = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            setupWebSocketService()
                                                        }else{
                                                            ECHO_URL_WEB_SOCKET = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            SharedApp.prefs.urlwebsocket = "wss://${BuildConfig.DOMINIO_PORTAL}/ws/autenticacion/loby/"
                                                            setupWebSocketService()
                                                        }


                                                    }
                                                }



                                                databaseHelper.deleteParametrosLicencia()
                                                databaseHelper.addParametrosLicencia("$resultRequiereUbicacion")

                                                databaseHelper.addVigenciaLicencia(
                                                    db[0].fecha_desde,
                                                    db[0].fecha_hasta,
                                                    db[0].dias_vigencia_x_vencer,
                                                    db[0].intervalo_vigencia_x_vencer
                                                )

                                                databaseHelper.updVigenciaLicencia(
                                                    db[0].fecha_desde,
                                                    db[0].fecha_hasta,
                                                    db[0].dias_vigencia_x_vencer,
                                                    db[0].intervalo_vigencia_x_vencer
                                                )

                                                //Valida horario laboraol de la apliacion
                                                val getHorario: APIService =
                                                    RestEngine.getRestEngine(databaseHelper, this@LoginActivity)
                                                        .create(APIService::class.java)
                                                val resultHorario: Call<DataHorariosMovil> =
                                                    getHorario.getHorarioMovil(
                                                        resultIdMovil,
                                                        0,
                                                        "$idDispositivo",
                                                        "JWT $token",
                                                        "application/json"
                                                    )
                                                resultHorario.enqueue(object :
                                                    Callback<DataHorariosMovil> {
                                                    override fun onFailure(
                                                        call: Call<DataHorariosMovil>,
                                                        t: Throwable
                                                    ) {
                                                        lgprogressBar!!.visibility = View.GONE
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Error al recuperar horarios movil",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }

                                                    override fun onResponse(
                                                        call: Call<DataHorariosMovil>,
                                                        response: Response<DataHorariosMovil>
                                                    ) {
                                                        val resHorariosmv = response.body()
                                                        if (resHorariosmv != null) {
                                                            val horario: List<RegHorarios_Movil> =
                                                                resHorariosmv.data.toList()
                                                            val smDateFormat =
                                                                SimpleDateFormat("HH:mm:ss")
                                                            val currenTime: String =
                                                                smDateFormat.format(Date())
                                                            val horaInicial =
                                                                horario[0].horario_desde
                                                            var horaFinal = horario[0].horario_hasta
                                                            val dia_semana = horario[0].dia_semana
                                                            val iilatitud = horario[0].ult_latitud
                                                            val iilongitud = horario[0].ult_longitud
                                                            val iirangomts = horario[0].rango_metros

                                                            if (horaFinal == "00:00:00"){
                                                                horaFinal = "23:59:59"
                                                            }

                                                            if (iilatitud.toDouble() != 0.0 && iilongitud.toDouble() != 0.0) {
                                                                //Para calcular la posicion permitida dle movil se implementan los principios de la formula Haversine
                                                                var haversine_d =
                                                                    iirangomts * 0.0000449 / 5
                                                                //Fromula para calcular distancia +- Norte/Sur,
                                                                // Maxima latitud al Norte = suma el valor de d a la latitud inicial
                                                                var ltNorteMax =
                                                                    iilatitud.toDouble() + haversine_d.toDouble()
                                                                //Maxima latitud al SUR  = resta el valor de d a la latitud inicial
                                                                var ltSurMax =
                                                                    iilatitud.toDouble() - haversine_d.toDouble()

                                                                //Formula para calcular distancia +- Este/Oste
                                                                //Se obtienen el Coseno de la latitud(En radianes)
                                                                var cosLatutud = Math.cos(
                                                                    Math.toRadians(iilatitud.toDouble())
                                                                )

                                                                // Maxima longitud al Este= longitud + (valor de d /cos(latitud))
                                                                var ltEsteMax =
                                                                    iilongitud.toDouble() + (haversine_d / cosLatutud)
                                                                //Maxima longitud al Oeste = longitud - (valor de d /cos(latitud))
                                                                var ltOesteMax =
                                                                    iilongitud.toDouble() - (haversine_d / cosLatutud)


                                                                if (ilatitud.toDouble() in ltSurMax..ltNorteMax) {
                                                                    if (ilongitud.toDouble() in ltOesteMax..ltEsteMax) {

                                                                    } else {
                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        Toast.makeText(
                                                                            this@LoginActivity,
                                                                            "El dispositivo se encuentra fuera del area de operacion",
                                                                            Toast.LENGTH_LONG
                                                                        ).show()
                                                                        return
                                                                    }
                                                                } else {
                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@LoginActivity,
                                                                        "El dispositivo se encuentra fuera del area de operacion",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                    return
                                                                }

                                                            }





                                                            if (horaInicial != "SN" && horaFinal != "SN") {

                                                                if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1
                                                                ) {

                                                                    if (alerta_pendiente != null && alerta_pendiente != "") {


                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        val accountsIntent = Intent(
                                                                            activity,
                                                                            MainActivity::class.java
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "titulo_alerta",
                                                                            alerta_titulo
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "desc_alerta",
                                                                            alerta_desc
                                                                        )
                                                                        accountsIntent.putExtra(
                                                                            "id_alerta",
                                                                            id_alerta
                                                                        )
                                                                        startActivity(accountsIntent)
                                                                        intent.removeExtra("accion_alerta")
                                                                        alerta_pendiente = ""
                                                                        intent.removeExtra("titulo_alerta")
                                                                        alerta_titulo = ""
                                                                        intent.removeExtra("desc_alerta")
                                                                        alerta_desc = ""


                                                                    } else {

                                                                        try {
                                                                            databaseHelper.DeleteAccesosFallidosBiometria()
                                                                        } catch (ex: Exception) {

                                                                            print("Error Delete Biometria")
                                                                        }
                                                                        lgprogressBar!!.visibility = View.GONE
                                                                        val accountsIntent = Intent(
                                                                            activity,
                                                                            MainActivity::class.java
                                                                        )
                                                                        startActivity(accountsIntent)
                                                                        //Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_LONG).show()
                                                                    }


                                                                } else {
                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@LoginActivity,
                                                                        "El dispositivo se encuentra fuera del horario laboral",
                                                                        Toast.LENGTH_LONG
                                                                    ).show()
                                                                }
                                                            } else {

                                                                if (alerta_pendiente != null && alerta_pendiente != "") {


                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    val accountsIntent = Intent(
                                                                        activity,
                                                                        MainActivity::class.java
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "titulo_alerta",
                                                                        alerta_titulo
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "desc_alerta",
                                                                        alerta_desc
                                                                    )
                                                                    accountsIntent.putExtra(
                                                                        "id_alerta",
                                                                        id_alerta
                                                                    )
                                                                    startActivity(accountsIntent)
                                                                    intent.removeExtra("accion_alerta")
                                                                    alerta_pendiente = ""
                                                                    intent.removeExtra("titulo_alerta")
                                                                    alerta_titulo = ""
                                                                    intent.removeExtra("desc_alerta")
                                                                    alerta_desc = ""


                                                                } else {
                                                                    try {
                                                                        databaseHelper.DeleteAccesosFallidosBiometria()
                                                                    } catch (ex: Exception) {
                                                                        print("Error Delete Biometria" + ex.message)
                                                                    }
                                                                    lgprogressBar!!.visibility = View.GONE
                                                                    val accountsIntent = Intent(
                                                                        activity,
                                                                        MainActivity::class.java
                                                                    )
                                                                    startActivity(accountsIntent)

                                                                    //  Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_LONG).show()
                                                                }
                                                            }


                                                        } else {
                                                            try {
                                                                databaseHelper.DeleteAccesosFallidosBiometria()
                                                            } catch (ex: Exception) {
                                                                print("Error Delete Biometria")
                                                            }
                                                            lgprogressBar!!.visibility = View.GONE
                                                            val accountsIntent = Intent(
                                                                activity,
                                                                MainActivity::class.java
                                                            )
                                                            startActivity(accountsIntent)
                                                            ///Toast.makeText(this@LoginActivity, "Ok", Toast.LENGTH_LONG).show()
                                                        }


                                                    }

                                                })


                                                ///////////


                                            } else {
                                                lgprogressBar!!.visibility = View.GONE

                                                var fec_hasta = databaseHelper.getFecHasta()

                                                if (fec_hasta != "") {
                                                    fec_hasta = fec_hasta.replace("T", " ")
                                                    val dateHasta =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                                            fec_hasta
                                                        )
                                                    val smDateFormatAct =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                                    val currenTimeAct: String =
                                                        smDateFormatAct.format(Date())
                                                    val dateAct =
                                                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                                                            currenTimeAct
                                                        )

                                                    if (dateAct.compareTo(dateHasta)!! > 0) {
                                                        //  Toast.makeText(this@LoginActivity, "La licencia del movil finalizo, por favor contecte a su administrador si desea renovarla.", Toast.LENGTH_SHORT).show()
                                                    } else {
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "Dispositivo no Registrado",
                                                            Toast.LENGTH_LONG
                                                        ).show()

                                                    }
                                                } else {
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Dispositivo no Registrado",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }

                                                validaLicenciaVencida()
                                                // valNoIntentosLic()
                                                var validacion: strValidacionAcesoNip? =
                                                    databaseHelper.CheckIntentosNIP()
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    "El Nip no es valido, intentos restantes ${validacion!!.intentos_restantes}",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                if (!validacion.esValido) {
                                                    this@LoginActivity.deleteDatabase("UserManager.db")
                                                    showAlertDialog()
                                                    appDesistalar()
                                                }

                                            }
                                        } else if (response.code() == 404) {
                                            lgprogressBar!!.visibility = View.GONE
                                            try {
                                                val jObjError =
                                                    JSONObject(response.errorBody()!!.string())
                                                val ref: String = jObjError.getString("ERROR")
                                                Toast.makeText(
                                                    this@LoginActivity, ref,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } catch (e: java.lang.Exception) {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    e.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }

                                        }


                                    }

                                })


                            }
                        }

                    })


                }
            }

        })


        //** }  catch (ex: Exception) {

        //**     Toast.makeText(this@LoginActivity, "No es posible conectar con servidor remoto, contacte a su administrador", Toast.LENGTH_LONG).show()
        //**  }
    }


    private fun validaLicenciaVencida() {
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper.getAllLicencia()
        val idMovil = Licenciadb[0].id_movil

        val userService: APIService =
            RestEngine.getRestEngineInicial(databaseHelper, this@LoginActivity).create(APIService::class.java)
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
                    val token = resultado.token

                    val getVigenciaLic: APIService =
                        RestEngine.getRestEngineInicial(databaseHelper, this@LoginActivity).create(APIService::class.java)
                    val result_vigencia_lic: Call<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil> =
                        getVigenciaLic.getLicenciaVencida(
                            "$idMovil",
                            "JWT $token",
                            "application/json"
                        )

                    result_vigencia_lic.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil> {
                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil>
                        ) {
                            val resultado_vigencia = response.body()
                            if (resultado_vigencia != null) {
                                if (resultado_vigencia.data == "ok") {
                                    //Toast.makeText(this@LoginActivity, "La licencia del movil finalizo, por favor contecte a su administrador si desea renovarla.", Toast.LENGTH_SHORT).show()

                                    var dialogBuilder =
                                        android.app.AlertDialog.Builder(this@LoginActivity)
                                    dialogBuilder.setCancelable(false)
                                    dialogBuilder.setMessage("La licencia del movil finalizo, por favor contecte a su administrador si desea renovarla.")
                                        .setCancelable(true)
                                        .setPositiveButton(
                                            "Aceptar",
                                            DialogInterface.OnClickListener { view, _ ->

                                                view.dismiss()

                                            })

                                    var alert = dialogBuilder.create()
                                    alert.setTitle("")
                                    alert.show()

                                }
                            }

                        }

                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil>,
                            t: Throwable
                        ) {
                            Log.i("VERSION", "Error al recuperar la vigencia del movil")
                        }

                    })

                    // client.getLicenciaVencida("$idMovil","JWT $token","application/json")
                    //   .subscribeOn(Schedulers.io())
                    //  .observeOn(AndroidSchedulers.mainThread())
                    // .subscribe()


                }


            }

            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                TODO("Not yet implemented")
            }

        })


    }


    private fun sin(x: Double): Double {
        var result: Double = 0.0
        for (n in 0..40) {
            val anterior = result
            result = result + (pot(-1.0, n) / fact(2 * n) * pot(x, 2 * n))
            if (anterior == result) break
        }
        return result
    }

    private fun fact(a: Int): Double {
        var result: Double = 1.0
        for (i in a downTo 1)
            result = result * i
        return result
    }

    private fun pot(a: Double, b: Int): Double {
        var result: Double = 1.0
        for (i in 1..b)
            result = result * a
        return result
    }


    private fun valNoIntentosLic() {
        if (!checkForInternet(this@LoginActivity)) {
            return
        }

        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        if (!databaseHelper.checkLicencia(idDispositivo)) {
            return
        }
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)
        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                /*
                var dialogBuilder = android.app.AlertDialog.Builder(this@LoginActivity)
                dialogBuilder.setMessage("No fue posible conectar con servidor remoto, revice su configuracion de red")
                    .setCancelable(false)
                    .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("")
                alert.show()

                 */
                //Toast.makeText(this@LoginActivity, "Error servicio no disponible 2", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val valIntentosApp: APIService =
                        RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
                    val resultRegLic: Call<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia> =
                        valIntentosApp.getRegLicencias(
                            "$idDispositivo",
                            0,
                            "JWT $token",
                            "application/json"
                        )

                    resultRegLic.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia> {
                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                this@LoginActivity,
                                "Error servicio no disponible (Validacion Intentos Licencia)",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>
                        ) {
                            val resultado_val = response.body()

                            if (resultado_val != null) {
                                intentos = resultado_val.data.toList()

                                databaseHelper.deleteNumeroIntentosNIP()
                                databaseHelper.addNumeroIntentosNIP(intentos[0].intentos_permitidos)

                                //if (intentos[0].intentos_fallidos > intentos[0].intentos_permitidos) {
                                //   this@LoginActivity.deleteDatabase("UserManager.db")
                                //    showAlertDialog()
                                //appDesistalar()
                                //}


                            }


                        }

                    })


                }
            }

        })


    }

    fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this@LoginActivity)
        dialogBuilder.setTitle("¡Aviso!")
        dialogBuilder.setMessage("Se ha superado la cantidad máxima de intentos fallidos permitidos, la apliacion dejo se ser funcional")
        dialogBuilder.setPositiveButton("Aceptar", null)

        val b = dialogBuilder.create()
        b.show()
    }


    fun getMac(context: Context): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.macAddress.toUpperCase()
    }

    fun getMovilData(context: Context): String {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
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

    fun getSIMMovil(context: Context): String {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return "S/N"
        }
        try {

            val simID: String = tm.getSimSerialNumber()
            if (simID != null) {
                return simID
            } else {
                return "S/N"
            }

        } catch (ex: Exception) {
            return "S/N"
        }


    }

    fun getIMEIMovil(context: Context): String {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_NUMBERS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            return ""
        }

        try {

            val IMEI: String = tm.getDeviceId()
            if (IMEI != null) {
                return IMEI
            } else {
                return "S/N"
            }


        } catch (ex: Exception) {
            return "S/N"
        }


    }


    private fun getInfoNetwork(): String {
        // Invoking the Wifi Manager
        var infoRed = ""
        val tipo_conexion = isNetworkAvailable(this@LoginActivity)
        if (tipo_conexion == "WIFI") {
            val wifiManager =
                applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // Method to get the current connection info
            val wInfo = wifiManager.connectionInfo

            // Extracting the information from the received connection info
            val ipAddress = wInfo.ipAddress
            val linkSpeed = wInfo.linkSpeed
            val networkID = wInfo.networkId
            val ssid = wInfo.ssid
            val hssid = wInfo.hiddenSSID
            val bssid = wInfo.bssid


            infoRed = "Tipo de Conexion: WIFI " +
                    "IP Address: $ipAddress " +
                    "Link Speed: $linkSpeed " +
                    "Network ID: $networkID " +
                    "SSID: $ssid " +
                    "Hidden SSID:  $hssid " +
                    "BSSID: $bssid "
        }
        if (tipo_conexion == "DATOS_CELULAR") {
            val telephonyManager =
                applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


            // val infoTelephone = networkTypeClass(telephonyManager.networkType)
            infoRed = "Tipo de Conexion: " +
                    "IP Address: SN " +
                    "Link Speed:SN" +
                    "Network ID:SN" +
                    "SSID:SN" +
                    "Hidden SSID: SN" +
                    "BSSID: SN"

        }
        return infoRed
    }

    fun networkTypeClass(networkType: Int): String {
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN,
            TelephonyManager.NETWORK_TYPE_GSM
                -> return "2G"
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA
                -> return "3G"
            TelephonyManager.NETWORK_TYPE_LTE
                -> return "4G"
            else -> return "Desconocida"
        }
    }


    private fun isNetworkAvailable(context: Context): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return ""
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return ""
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WIFI"
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "DATOS_CELULAR"
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ETHERNET"
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "BLUETOOTH"
                else -> ""
            }
        } else {
            var estado = connectivityManager.activeNetworkInfo?.isConnected ?: ""
            if (estado == true) {
                return "OTROS MEDIOS"
            } else {
                return ""
            }


        }
    }


    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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


    private fun getLocalIpAddress(): String {
        try {

            val wifiManager: WifiManager =
                this.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
            return ipToString(wifiManager.connectionInfo.ipAddress)
        } catch (ex: Exception) {
            Log.e("IP Address", ex.toString())
        }

        return "0.0.0.0"
    }

    private fun ipToString(i: Int): String {
        return (i and 0xFF).toString() + "." +
                (i shr 8 and 0xFF) + "." +
                (i shr 16 and 0xFF) + "." +
                (i shr 24 and 0xFF)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    /**
     * This method is to empty all input edit text
     */
    private fun emptyInputEditText() {
        textInputEditTextEmail.text = null
        textInputEditTextPassword.text = null
    }


    class GetPublicIP : AsyncTask<String?, String?, String?>() {
        protected override fun doInBackground(vararg strings: String?): String {
            var publicIP = ""
            try {
                // val connection = URL("https://api.ipify.org").openStream() as InputStream
                val url = URL("https://api.ipify.org")
                val con: URLConnection = url.openConnection()
                con.connectTimeout = 5000
                con.readTimeout = 5000
                val resultConn: InputStream = con.getInputStream()

                val s = Scanner(resultConn, "UTF-8")
                    .useDelimiter("\\A")
                publicIP = s.next()

            } catch (e: IOException) {
                e.printStackTrace()
            }
            return publicIP
        }

        protected fun onPostExecute(publicIp: String) {
            super.onPostExecute(publicIp)
            return

            //Here 'publicIp' is your desire public IP
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

    class GetDominioActual : AsyncTask<String?, String?, String?>() {
        protected override fun doInBackground(vararg strings: String?): String {
            var dominioActual = ""
            try {
                val s = Scanner(
                    URL(
                        "https://dsiin.com/noborrar_subdominios"
                    )
                        .openStream(), "UTF-8"
                )
                    .useDelimiter("\\A")
                dominioActual = s.next()
                println("El dominio Actual es $dominioActual")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return dominioActual
        }

        protected fun onPostExecute(publicIp: String) {
            super.onPostExecute(publicIp)
            return

            //Here 'publicIp' is your desire public IP
        }
    }


    private fun setPeriodicWorkRequest() {

        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        if (!databaseHelper.checkLicencia(idDispositivo)) {
            return
        }
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        var datosBackupDb = databaseHelper.getCOntrolBackupDB()


        if (datosBackupDb != "") {
            val reg = datosBackupDb.split("|")

            val Interval: Long = (reg[0]).toLong()
            var unidad = reg[1]
            var ftp = reg[2]
            var ftpuser = reg[3]
            var ftpwd = reg[4]
            var ftppuerto = reg[5]


            var tmUnit: TimeUnit = TimeUnit.MINUTES

            when (unidad) {
                "Minutos" ->
                    tmUnit = TimeUnit.MINUTES
                "Horas" ->
                    tmUnit = TimeUnit.HOURS
                "Dias" ->
                    tmUnit = TimeUnit.DAYS
                else -> { // Note the block

                }
            }

            val databasepath = this.getDatabasePath("UserManager.db").toString()
            val data = Data.Builder()
            data.putString("file_path", databasepath)
            data.putString("movil_id", Movil_Id.toString())
            data.putString("ftp", ftp)
            data.putString("ftpuser", ftpuser)
            data.putString("ftpwd", ftpwd)
            data.putString("ftppuerto", ftppuerto)
            data.putString("intervalo", Interval.toString())
            data.putString("unidad", unidad)


            val periodicWorkRequest = PeriodicWorkRequest
                .Builder(BackupDataBaseWorker::class.java, Interval, tmUnit)
                .setInputData(data.build())
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "JobBackupDb",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
            )


        }


    }


    private fun CargaInicialEquipos() {
        try {
            //#CORECCION DE ERROR DATABASE
            SQLiteDatabase.loadLibs(this)
            var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
                databaseHelper.getAllLicencia()
            var Movil_Id = Licenciadb[0].id_movil
            iConsultaEstatusEquipoTask = ConsultaEstatusEquipoTask(
                databaseHelper,
                this@LoginActivity,
                Movil_Id,
                progressbartop!!
            )
            iConsultaEstatusEquipoTask.getAllEquipos()


        } catch (ex: Exception) {
            println(ex.message)
        }
    }


    private fun RequestTaskGetAlertasPendientes() {
        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        if (!databaseHelper.checkLicencia(idDispositivo)) {
            return
        }
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        val lsUsuBloc = databaseHelper.obtCadenaIdsUsuAlertasBloqeuados()
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia> =
            databaseHelper.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper, this@LoginActivity).create(APIService::class.java)
        var result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)
        var topAlerta = databaseHelper.getTopAlertas()
        if (topAlerta == null) {
            topAlerta = 50
        }
        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()
                    val resultAlertas: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas> =
                        userService.getAlertasXNegocioXMovil(
                            "$Movil_Id",
                            "$idDispositivo",
                            "$topAlerta",
                            lsUsuBloc,
                            "JWT $token",
                            "application/json"
                        )
                    resultAlertas.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas> {
                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>,
                            t: Throwable
                        ) {

                        }

                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>
                        ) {
                            val resultadoAlt = response.body()
                            if (resultadoAlt != null) {
                                values = resultadoAlt.data.toList()
                                for (i in values) {
                                    if (!databaseHelper.CheckIfAlertaExist(i.id)) {
                                        try {
                                            databaseHelper.addAlertaDB(
                                                i.id,
                                                i.alerta,
                                                i.descripcion,
                                                i.nombre_equipo,
                                                i.tipo_alerta,
                                                i.fecha_alerta,
                                                "",
                                                i.evento_id_id
                                            )

                                        } catch (ex: Exception) {

                                        }

                                    }else{

                                        //  databaseHelper.updControlAlertaDB(
                                        //      i.id,
                                        //     i.evento_id_id
                                        //)

                                        //databaseHelper.updControlAlertaEliminadaDB(
                                        //   i.id,
                                        //  i.evento_id_id
                                        //)


                                    }
                                }

                            }

                        }

                    })

                }


            }

        })


    }


    private fun chkExitNewVersion() {

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var idDispositivo: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        var versionSoftware = "${BuildConfig.VERSION_NAME}"
        databaseHelper.updVersionLicencia(versionSoftware)
        val versionInstalada = databaseHelper.getVersionActSoftware()

        val userService: APIService =
            RestEngine.getRestEngineInicial(databaseHelper, this@LoginActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)

        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                SharedApp.prefs.nuevaVersion = "0"
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token

                    val getValVersion: APIService =
                        RestEngine.getRestEngineInicial(databaseHelper, this@LoginActivity).create(APIService::class.java)
                    val result_version: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles> =
                        getValVersion.getUltVersionMoviles(
                            idDispositivo,
                            "appAndroid",
                            "$versionInstalada",
                            "JWT $token",
                            "application/json"
                        )
                    result_version.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles> {
                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>,
                            t: Throwable
                        ) {
                            SharedApp.prefs.nuevaVersion = "0"
                        }

                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>
                        ) {
                            if (response.code() == 200) {
                                val resultado = response.body()
                                val UltVersion = resultado!!.data[0].ultima_version

                                var apkUrl = ""
                                var entornoAct = databaseHelper.getControlEntornoConexion(this@LoginActivity)
                                if(entornoAct == 1){
                                    apkUrl = "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"

                                }else{
                                    apkUrl = "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"

                                }

                                downloadController = DownloadController(this@LoginActivity, apkUrl)


                                SharedApp.prefs.nuevaVersion = "$UltVersion"
                                var dialogBuilder =
                                    android.app.AlertDialog.Builder(this@LoginActivity)
                                dialogBuilder.setCancelable(true)
                                dialogBuilder.setMessage("La actualización $UltVersion está disponible. Al descargar la última actualización, obtendrá las últimas funciones, mejoras y correcciones de errores")
                                    .setCancelable(true)
                                    .setPositiveButton(
                                        "Descargar",
                                        DialogInterface.OnClickListener { view, _ ->

                                            checkStoragePermission(UltVersion)
                                        })
                                    .setNeutralButton(
                                        "Cancelar",
                                        DialogInterface.OnClickListener { view, _ ->
                                            view.dismiss()
                                        })


                                var alert = dialogBuilder.create()
                                alert.setCanceledOnTouchOutside(false)
                                alert.setTitle("")
                                alert.show()
                                //  alert.getButton(AlertDialog.BUTTON_POSITIVE)
                                //  alert.getButton(AlertDialog.BUTTON_NEGATIVE).setPadding(10,10,10,10)


                            } else {
                                SharedApp.prefs.nuevaVersion = "0"
                            }

                        }

                    })


                } else {
                    SharedApp.prefs.nuevaVersion = "0"
                }
            }

        })

    }

    fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission)

    fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    fun AppCompatActivity.requestPermissionsCompat(
        permissionsArray: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }

    fun View.showSnackbar(msg: String, length: Int) {
        showSnackbar(msg, length, null, {})
    }

    fun View.showSnackbar(
        msgId: Int,
        length: Int,
        actionMessageId: Int,
        action: (View) -> Unit
    ) {
        showSnackbar(context.getString(msgId), length, context.getString(actionMessageId), action)
    }

    fun View.showSnackbar(
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(this, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        }
    }

    private fun checkStoragePermission(ultimaVersion: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivity(intent)
                } catch (ex: java.lang.Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivity(intent)
                }

            } else {

                val accountsIntent = Intent(this@LoginActivity, UpdateVersionActivity::class.java)
                accountsIntent.putExtra("ult_version", ultimaVersion)
                startActivity(accountsIntent)
            }
        }else{
            if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
            ) {


                val accountsIntent = Intent(this@LoginActivity, UpdateVersionActivity::class.java)
                accountsIntent.putExtra("ult_version", ultimaVersion)
                startActivity(accountsIntent)

            } else {

                requestStoragePermission()
            }
        }
    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            var InicioLayout: ConstraintLayout = findViewById(R.id.InicioLayout)
            InicioLayout.showSnackbar(
                R.string.storage_access_required,
                Snackbar.LENGTH_INDEFINITE, org.imaginativeworld.oopsnointernet.R.string.ok
            ) {
                requestPermissionsCompat(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MainActivity.PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                MainActivity.PERMISSION_REQUEST_STORAGE
            )
        }
    }


    private fun recuperaNipMovil(databaseHelper_Data: DatabaseHelper) {
        var idDispositivo: String = Settings.Secure.getString(
            this@LoginActivity.contentResolver,
            Settings.Secure.ANDROID_ID
        ).toString()

        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService =
            RestEngine.getRestEngine(databaseHelper_Data, this@LoginActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> =
            userService.submit(UserData)
        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                t: Throwable
            ) {
                Toast.makeText(
                    this@LoginActivity,
                    "Error servicio no disponible 2",
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onResponse(
                call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>,
                response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>
            ) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService =
                        RestEngine.getRestEngine(databaseHelper_Data, this@LoginActivity).create(APIService::class.java)
                    val resultUpdNip: Call<ResponseBody> = negociosService.recuperaNipMovil(
                        "$idDispositivo",
                        "JWT $token",
                        "application/json"
                    )

                    resultUpdNip.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {


                            val dialogBuilder =
                                androidx.appcompat.app.AlertDialog.Builder(this@LoginActivity)

                            dialogBuilder.setMessage("Se envió un correo a la dirección relacionada al dispositivo, por favor revise su bandeja de entrada")
                                .setCancelable(false)
                                .setPositiveButton(
                                    "Aceptar",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        (

                                                dialog.dismiss()

                                                )
                                    })
                            var alert = dialogBuilder.create()
                            alert.setTitle("Recuperación de NIP")
                            alert.show()


                        }

                    })


                }
            }

        })

    }


    private fun setupWebSocketService() {

        webSocketService = provideWebSocketService(
            scarlet = provideScarlet(
                client = provideOkhttp(),
                lifecycle = provideLifeCycle(),
                streamAdapterFactory = provideStreamAdapterFactory(),
            )
        )

    }


    /**
     * Implementacion de las dependencias para WebSocket
     *
     */
    private fun provideWebSocketService(scarlet: Scarlet) = scarlet.create(EchoService::class.java)

    private fun provideScarlet(
        client: OkHttpClient,
        lifecycle: Lifecycle,
        streamAdapterFactory: StreamAdapter.Factory,
    ) =
        Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(ECHO_URL_WEB_SOCKET))
            .lifecycle(lifecycle)
            .addStreamAdapterFactory(streamAdapterFactory)
            .build()


    //private fun provideOkhttp() =
    //   OkHttpClient.Builder()
    //      .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    //     .build()

    private fun provideOkhttp(): OkHttpClient {
        var wsidDispositivo: String = Settings.Secure.getString(
            this@LoginActivity.contentResolver,
            Settings.Secure.ANDROID_ID
        ).toString()
        wsidDispositivo = removeLeadingZeroes(wsidDispositivo)
        wsidDispositivo = removeTrailingZeroes(wsidDispositivo)
        print(wsidDispositivo)
        val requestIterator = Interceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("token", "$wsidDispositivo")
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request = request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(requestIterator)
            .build()
    }


    private fun provideLifeCycle() = AndroidLifecycle.ofApplicationForeground(application)


    private fun provideStreamAdapterFactory() = RxJava2StreamAdapterFactory()

    /**
     *Finaliza implementacion de dependencias WebSocket
     */


    fun leerLogAlertas() {
        val logFolder =
            File(Environment.getExternalStorageDirectory().absolutePath + "/Download/AppTomatiza")
        if (!logFolder.exists()) {
            return
        }

        val file = File(logFolder, "log_alertas_dev.txt")


        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            var id_alerta: Int?
            var titulo_alerta: String?
            var descripcion_alerta: String?
            var nom_negocio: String
            var tipo_alerta: Int?
            var fecha_alerta: String?
            while (br.readLine().also { line = it } != null) {
                var splLine = line!!.split(" | ")


                if (splLine.count() < 6) {
                    continue
                }
                fecha_alerta = splLine[0]
                if (!splLine[1].isValidInt()) {
                    continue
                }
                id_alerta = splLine[1].toInt()
                titulo_alerta = splLine[2]
                descripcion_alerta = splLine[3]
                nom_negocio = splLine[4]
                if (!splLine[5].isValidInt()) {
                    continue
                }
                tipo_alerta = splLine[5].toInt()

                if (!databaseHelper.CheckIfAlertaExist(id_alerta)) {
                    databaseHelper.addAlertaDB(
                        id_alerta,
                        titulo_alerta!!,
                        descripcion_alerta!!,
                        nom_negocio!!,
                        tipo_alerta!!,
                        fecha_alerta!!,
                        "",
                        0

                    )
                }


            }
            br.close()

            val writer = PrintWriter(file)
            writer.print("")
            writer.close()

        } catch (e: IOException) {

        }


    }

    fun String.isValidInt() = try {
        toInt().toString() == this
    } catch (x: NumberFormatException) {
        false
    }


    private fun requestPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 0)
            } catch (e: java.lang.Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 0)
            }
        }
    }




}
