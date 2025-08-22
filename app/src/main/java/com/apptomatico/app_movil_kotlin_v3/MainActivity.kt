package com.apptomatico.app_movil_kotlin_v3

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptomatico.app_movil_kotlin_v3.ConsultaDinamica.ConsultaDinamicaActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Runnables.ConsultaEstatusEquiposActThread
import com.apptomatico.app_movil_kotlin_v3.Runnables.ConsultaInfoEquiposThread
import com.apptomatico.app_movil_kotlin_v3.Runnables.ConsultaInternetAntenaThread
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.TapCambioNip.activity_cambio_nip_tap
import com.apptomatico.app_movil_kotlin_v3.TapConfiguraciones.activity_configuracion_tap
import com.apptomatico.app_movil_kotlin_v3.TapCopiaSeguridad.activity_copia_seguridad
import com.apptomatico.app_movil_kotlin_v3.UpdateVersion.UpdateVersionActivity
import com.apptomatico.app_movil_kotlin_v3.alertas.AlertasFragment
import com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.AllAccionesFavoritas
import com.apptomatico.app_movil_kotlin_v3.browserapp.BrowserAppActivity
import com.apptomatico.app_movil_kotlin_v3.databinding.ActivityMainBinding
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_equipo
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_estatus
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_fecha
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_fecha_desde_hasta
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_marcadas
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.filtro_alertas_tipo
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_equipos
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_negocio
import com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosEjecucion
import com.apptomatico.app_movil_kotlin_v3.model.ItemParametrosAccDB
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.negocio.DownloadController
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment.Companion.framrNegocioList
import com.apptomatico.app_movil_kotlin_v3.negocio.ParametrosAdapter
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.ui.home.ItemAcciones
import com.apptomatico.app_movil_kotlin_v3.usuario.avatar_usuario
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import java.util.Calendar
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import kotlin.text.count
import kotlin.text.substring



class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener  {

    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val executorServiceAntena: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val executorServiceMonitores: ExecutorService = Executors.newSingleThreadScheduledExecutor()
    private val executorServiceEstatusEquipos: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()


    var catnegociosList : java.util.ArrayList<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list> = ArrayList()
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var configUsuMenu: MenuItem? = null
    private var conexionMenu: MenuItem? = null
    private var estatusAlertasMenu: MenuItem? = null
    private var toolbar: Toolbar? = null
    lateinit var navView: NavigationView
    lateinit var layoutnavbar: RelativeLayout
    private var  SmoothBottomBar: BottomNavigationView? = null
    lateinit var downloadController: DownloadController

    private lateinit var float_btn_alerta_usr: FloatingActionButton
    private lateinit var txt_alerta_usr: TextView

    var spinner_negocios: Spinner? = null
    var databaseHelper_Data: DatabaseHelper

    lateinit var popupMenuButton: PopupMenu
    private var ECHO_URL_WEB_SOCKET:  String = ""
    private var isWindowFocused = false
    private var isAppWentToBg = false
    private var isBackPressed = false

    var progressbartop: ProgressBar? = null

    private var timerAntena: Timer = Timer()
    private var timerEquipos: Timer = Timer()
    private var timerMonitores: Timer = Timer()
    private var timerEstatusEquipos: Timer = Timer()
    private var timerEstatusEquiposUnic: Timer = Timer()
    lateinit var  requestsw: Request

    var webSocketService: EchoService? = null
    var check = 0

    private var arrUsuariosPermitidos: MutableList<String>  = ArrayList()
    val imyConnectivityManager by lazy { MyConnectivityManager(this, lifecycleScope) }

    init{
        databaseHelper_Data = DatabaseHelper.getInstance(this@MainActivity)
        ECHO_URL_WEB_SOCKET = SharedApp.prefs.urlwebsocket.toString()

    }
    companion object {

        const val PERMISSION_REQUEST_STORAGE = 0
        var intervaloActualizacionMonitores: Long = 0


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        if (savedInstanceState != null) {
            LoginActivity.Islogin= false
            finish()
            val accountsIntent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(accountsIntent)
        }
        checkForCrash()


        // binding = ActivityMainBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContentView(R.layout.activity_main)
        SmoothBottomBar = findViewById(R.id.bottomBar)
        toolbar = findViewById(R.id.tool_bar_principal)
        setSupportActionBar(toolbar)
        toolbar!!.title = ""

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_fragment) as NavHostFragment
        navController = navHostFragment.navController

        navView = findViewById(R.id.nav_view_inicio)
        navView!!.visibility = View.GONE
        layoutnavbar = findViewById(R.id.layout_nav_bar_inicio)
        layoutnavbar!!.visibility = View.GONE
        navView!!.setNavigationItemSelectedListener(this@MainActivity)
        float_btn_alerta_usr = findViewById(R.id.float_btn_alerta_usr)
        txt_alerta_usr = findViewById(R.id.txt_alerta_usr)
        spinner_negocios = findViewById(R.id.inicio_negocios_spinner_toolbar)
        spinner_negocios!!.onItemSelectedListener = this@MainActivity

        progressbartop = findViewById<ProgressBar>(R.id.progressbartop)



        toolbar!!.setNavigationOnClickListener {
            if (navView!!.visibility == View.GONE) {
                navView!!.visibility = View.VISIBLE
                layoutnavbar!!.visibility = View.VISIBLE

            } else {
                navView!!.visibility = View.GONE
                layoutnavbar!!.visibility = View.GONE

            }
        }
        layoutnavbar.setOnClickListener {
            if (navView.visibility == View.GONE) {
                navView.visibility = View.VISIBLE
                layoutnavbar.visibility = View.VISIBLE

            } else {
                navView.visibility = View.GONE
                layoutnavbar.visibility = View.GONE
            }
        }

        chkExitNewVersion(true, false)
        setupSmoothBottomMenu()
        NewOnResume()
        getcambiosVersion()

        val sharedpreferences = getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        val autoStart = sharedpreferences.getString("autoStart", "");
        if (autoStart.equals("")) {
            AutoStartHelper.instance.getAutoStartPermission(this)
        }




        var entornoAct = databaseHelper_Data.getControlEntornoConexion(this@MainActivity)

        if(entornoAct == 1){
            var dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo =  removeTrailingZeroes(idDispositivo)
            requestsw = Request.Builder()
                .url("${dominio_actual}/api/get_acciones_tipo_monitoreables_list/?id_hardware=Todos&id_hardware_movil=$idDispositivo&id_cat_negocio=0")
                .header("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwiZXhwIjoxNjc5MDg4Mjc2LCJlbWFpbCI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwib3JpZ19pYXQiOjE2NzkwODEwNzZ9.s-hvTxqpjfRIRkydS9NASkTecKNCgMzhYvHbNep-GD0")
                .header("Content-Type", "application/json")
                .build()
        }else{
            var dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo =  removeTrailingZeroes(idDispositivo)
            requestsw = Request.Builder()
                .url("${dominio_actual}/api/get_acciones_tipo_monitoreables_list/?id_hardware=Todos&id_hardware_movil=$idDispositivo&id_cat_negocio=0")
                .header("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoxLCJ1c2VybmFtZSI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwiZXhwIjoxNjc5MDg4Mjc2LCJlbWFpbCI6Implc2NhbWlsbGFAZHNpaW4uY29tIiwib3JpZ19pYXQiOjE2NzkwODEwNzZ9.s-hvTxqpjfRIRkydS9NASkTecKNCgMzhYvHbNep-GD0")
                .header("Content-Type", "application/json")
                .build()
        }


        try{
            if(LoginActivity.Companion.webSocketService != null){
                webSocketService = LoginActivity.Companion.webSocketService
            }else{

                webSocketService = provideWebSocketService(
                    scarlet = provideScarlet(
                        client = provideOkhttp(),
                        lifecycle = provideLifeCycle(),
                        streamAdapterFactory = provideStreamAdapterFactory(),
                    )
                )
                LoginActivity.Companion.webSocketService =  webSocketService
            }

        }catch (ex: Exception){

            var dialogBuilder = AlertDialog.Builder(this@MainActivity)
            dialogBuilder.setMessage("Vuelve a iniciar sesión")
                .setCancelable(false)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    (

                            sesionCaducada()

                            )
                })
            var alert = dialogBuilder.create()
            alert.setTitle("La sesion ha caducado")
            alert.show()


        }



        SQLiteDatabase.loadLibs(this)
        var intervaloEstatus =  databaseHelper_Data.getIntervaloEstatusEquipos()
        intervaloEstatus = (intervaloEstatus * 5000) / 5
        var intervaloActualizacion =  databaseHelper_Data.getIntervaloActualizacionEquipos()
        intervaloActualizacion = (intervaloActualizacion * 5000) / 5
        intervaloActualizacionMonitores =
            java.lang.Long.valueOf(databaseHelper_Data.getConfiguracionMovil_IntervaloTmMnt())
        intervaloActualizacionMonitores = (intervaloActualizacionMonitores * 5000) / 5


        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)

        notification()
        startTimerValAntenaActiva(intervaloEstatus.toLong())
        startTimerUpdateEquipos(intervaloActualizacion.toLong(), Movil_Id, 20000)
       // startTimerUpdateMonitore(intervaloActualizacionMonitores, idDispositivo, Movil_Id, 0)
        startTimerValEquipoActivo(intervaloEstatus.toLong())
       // startTimerValEquipoUnicActivo(intervaloEstatus.toLong())




    }


    //override fun onBackPressed() {
    // super.onBackPressed()

    //   Toast.makeText(this@MainActivity, "prueba", Toast.LENGTH_LONG).show()

    // }

    @SuppressLint("MissingInflatedId")
    fun getcambiosVersion(){
        val cambios =  databaseHelper_Data.getMsgCambiosVersion()
        if(cambios != "NT"){
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.layout_mesnajes_usuario,null)
            val ilblmsjusr = view.findViewById<TextView>(R.id.lbl_titulo_msj_usr)
            var icambios = cambios.replace("|", "\n")
            ilblmsjusr.text = "Mejoras realizadas: \n\n ${icambios}"

            var dialogBuilder = AlertDialog.Builder(this@MainActivity)
            dialogBuilder .setView(view)
            dialogBuilder.setCancelable(false)
                .setCancelable(false)
                .setPositiveButton("Aceptar") { view, _ ->
                    databaseHelper_Data.updCambiosVersion(0)
                    view.dismiss()
                }

            var alert = dialogBuilder.create()
            alert.setTitle("Aplicacion actualizada a la version ${BuildConfig.VERSION_NAME}")
            alert.show()
        }


    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2  && data != null) {
            val updmenu = data.getIntExtra("ACTUALIZA-MENU", 0)
            if(updmenu == 1){
                updateavatarmenuuser()
            }

        }

    }

    fun NewOnResume(){


        updListCatNegocio()

    }

    override fun onStart() {

        applicationWillEnterForeground()
        super.onStart()
    }

    override fun onStop() {
        super.onStop()

        applicationdidenterbackground()
    }

    fun applicationdidenterbackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true

            pauseTimer()
        }
    }



    private fun applicationWillEnterForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false
            getAllChildExercisesFromParentID() //ACTUALIZA COMBO NEGOCIOS
            //#CORECCION DE ERROR DATABASE
            SQLiteDatabase.loadLibs(this)
            var intervaloEstatus =  databaseHelper_Data.getIntervaloEstatusEquipos()
            intervaloEstatus = (intervaloEstatus * 10000) / 5



            var intervaloActualizacion =  databaseHelper_Data.getIntervaloActualizacionEquipos()
            intervaloActualizacion = (intervaloActualizacion * 5000) / 5
            intervaloActualizacionMonitores =
                java.lang.Long.valueOf(databaseHelper_Data.getConfiguracionMovil_IntervaloTmMnt())
            intervaloActualizacionMonitores = (intervaloActualizacionMonitores * 5000) / 5


            var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
            var Movil_Id = Licenciadb[0].id_movil
            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo = removeTrailingZeroes(idDispositivo)



            timerAntena = Timer()
            timerEquipos = Timer()
            timerMonitores = Timer()
            timerEstatusEquipos = Timer()
            timerEstatusEquiposUnic = Timer()
            startTimerValAntenaActiva(intervaloEstatus.toLong())
            startTimerUpdateEquipos(intervaloActualizacion.toLong(), Movil_Id, 20000)
            //startTimerUpdateMonitore(intervaloActualizacionMonitores, idDispositivo, Movil_Id, 0)
            startTimerValEquipoActivo(intervaloEstatus.toLong())
            //startTimerValEquipoUnicActivo(intervaloEstatus.toLong())



        }
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        isWindowFocused = hasFocus
        if (isBackPressed && !hasFocus) {
            isBackPressed = false
            isWindowFocused = true
        }
        super.onWindowFocusChanged(hasFocus)
    }



    private fun pauseTimer() {
        try {
            timerAntena.cancel()
            timerEquipos.cancel()
            timerMonitores.cancel()
            timerEstatusEquipos.cancel()
            timerEstatusEquiposUnic.cancel()
        }catch (ex: Exception){

        }

    }


    fun pauseTimerMonitores(){
        timerMonitores.cancel()


    }

    fun  activaTinerMonitores(){
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo = removeTrailingZeroes(idDispositivo)
        timerMonitores = Timer()
        //startTimerUpdateMonitore(intervaloActualizacionMonitores, idDispositivo, Movil_Id, 0)
    }




    private fun startTimerValAntenaActiva(intervalo: Long) {


        timerAntena.schedule(object : TimerTask() {
            override fun run() {

                try {

                    var worked = ConsultaInternetAntenaThread(
                        this@MainActivity,
                        conexionMenu!!,
                        estatusAlertasMenu!!,
                        databaseHelper_Data,
                        imyConnectivityManager
                    )
                    executorServiceAntena.execute(worked)


                } catch (ex: Exception) {
                    // executorServiceAntena.shutdown()
                }


            }
        }, 0, intervalo)






    }

    private fun startTimerUpdateEquipos(intervalo: Long, Movil_Id: Int, delayTimer: Long){
        timerEquipos.schedule(object : TimerTask() {
            override fun run() {
                try{


                    if (checkForInternet(this@MainActivity)) {
                        var ctxNavFragment: Fragment? = supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        ctxFragment?.view?.tag

                        var worked = ConsultaInfoEquiposThread(
                            databaseHelper_Data,
                            this@MainActivity,
                            Movil_Id,
                            progressbartop!!,
                            ctxFragment?.view?.tag.toString()
                        )
                        executorService.execute(worked)

                    }


                }catch (ex: Exception){
                    // executorService.shutdown()
                }

            }
        }, delayTimer, intervalo)


    }


//    private fun startTimerUpdateMonitore(intervalo: Long, idDispositivo: String, Movil_Id: Int, delayTimer: Long){
//        timerMonitores.scheduleAtFixedRate(object : TimerTask() {
//            override fun run() {
//                try{
//                    if (checkForInternet(this@MainActivity)) {
//                        var ctxNavFragment: Fragment? = supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
//                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
//                        ctxFragment?.view?.tag
//                        var worked =  ConsultaInfoMonitoresThread(databaseHelper_Data, this@MainActivity, idDispositivo, Movil_Id, linearLayoutMntFav,  LoginActivity.webSocketService, true,   requestsw, ctxFragment?.view?.tag.toString())
//                        executorServiceMonitores.execute(worked)
//                    }
//                } catch (ex: Exception){
//                    databaseHelper_Data.addControlServerSocketLog("startTimerUpdateMonitore: ${ex.message}")
//                    //  executorService.shutdown()
//                }
//            }
//        }, delayTimer, intervalo)
//
//
//
//
//    }


//    private fun startTimerValEquipoActivo(intervalo: Long) {
//        timerEstatusEquipos.schedule(object : TimerTask() {
//            override fun run() {
//
//                try {
//                    val Equipos = databaseHelper_Data.getControlEquipos()
//                    var dominio_actual = ""
//                    var entornoAct = databaseHelper_Data.getControlEntornoConexion(this@MainActivity)
//                    if(entornoAct == 1){
//                        dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
//                    }else{
//                        dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
//                    }
//
//                    if (Equipos.isNotEmpty()){
//                        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
//                        idDispositivo = removeLeadingZeroes(idDispositivo)
//                        idDispositivo =  removeTrailingZeroes(idDispositivo)
//
//                        var ctxNavFragment: Fragment? = supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
//                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
//                        ctxFragment?.view?.tag
//
//                        var worked = ConsultaEstatusEquiposActThread(
//                            databaseHelper_Data,
//                            this@MainActivity,
//                            framrNegocioList!!,
//                            Equipos,
//                            dominio_actual,
//                            1,
//                            LoginActivity.webSocketService,
//                            idDispositivo,
//                            ctxFragment?.view?.tag.toString(),
//                            imyConnectivityManager
//                        )
//                        executorServiceEstatusEquipos.execute(worked)
//                    }
//
//                } catch (ex: Exception) {
//                    // executorServiceAntena.shutdown()
//                }
//
//            }
//        }, 0, intervalo)
//
//
//    }



    private fun startTimerValEquipoActivo(intervalo: Long) {
        timerEstatusEquipos.schedule(object : TimerTask() {
            @RequiresApi(Build.VERSION_CODES.N)
            @SuppressLint("HardwareIds")
            override fun run() {
                val Equipos = databaseHelper_Data.getControlEquipos()
                if (Equipos.isEmpty()) return

                // Llamada a corutina desde el hilo principal de Android
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        var dominio_actual = ""
                        val entornoAct = databaseHelper_Data.getControlEntornoConexion(this@MainActivity)
                        dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"

                        var idDispositivo: String = Settings.Secure.getString(
                            contentResolver,
                            Settings.Secure.ANDROID_ID
                        )

                           idDispositivo = removeLeadingZeroes(idDispositivo)
                           idDispositivo =  removeTrailingZeroes(idDispositivo)

                        val ctxNavFragment: Fragment? =
                            supportFragmentManager.findFragmentByTag("main_fragment") as? NavHostFragment
                        val ctxFragment: Fragment? = ctxNavFragment?.childFragmentManager?.fragments?.first()
                        val fragmentTag = ctxFragment?.view?.tag.toString()

                        val worked = ConsultaEstatusEquiposActThread(
                            databaseHelper_Data,
                            this@MainActivity,
                            framrNegocioList!!,
                            Equipos,
                            dominio_actual,
                            1,
                            LoginActivity.webSocketService,
                            idDispositivo,
                            fragmentTag,
                            imyConnectivityManager
                        )

                        // Ejecutar la función suspend run()
                        worked.run() // <- aquí se llama directamente a la suspend function

                    } catch (ex: Exception) {
                        Log.e("TIMER_EQUIPO", "Error en Timer: ${ex.message}")
                    }
                }
            }
        }, 0, intervalo)
    }







    fun getAllChildExercisesFromParentID() {
        databaseHelper_Data.checkifexistvalueTodos(this@MainActivity)
        val lstNegocios = databaseHelper_Data.GetListaNegocios()
        val arrayAdapter: ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list> = ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list>(this, R.layout.spinner_selected_item, lstNegocios)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)


        spinner_negocios!!.adapter = arrayAdapter
        if(header_id_negocio == "Todos"){
            spinner_negocios!!.setSelection(0,false)
        }else{
            val indice = lstNegocios.indexOfFirst { it.id == header_id_negocio }
            spinner_negocios!!.setSelection(indice,false)
        }

    }




    private fun chkExitNewVersion(muestraAlerta: Boolean, opMenu: Boolean){

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        var versionSoftware  = "${BuildConfig.VERSION_NAME}"
        databaseHelper_Data.updVersionLicencia(versionSoftware)
        val versionInstalada = databaseHelper_Data.getVersionActSoftware()

        val userService: APIService = RestEngine.getRestEngineInicial(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object :
            Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                SharedApp.prefs.nuevaVersion = "0"
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token

                    val getValVersion: APIService = RestEngine.getRestEngineInicial(databaseHelper_Data,  this@MainActivity).create(APIService::class.java)
                    val result_version: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles> = getValVersion.getUltVersionMoviles(idDispositivo, "appAndroid", "$versionInstalada", "JWT $token", "application/json")
                    result_version.enqueue(object :
                        Callback<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>, t: Throwable) {
                            SharedApp.prefs.nuevaVersion = "0"
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>) {
                            if (response.code() == 200) {
                                val resultado = response.body()
                                val UltVersion = resultado!!.data[0].ultima_version


                                SharedApp.prefs.nuevaVersion = "$UltVersion"
                                float_btn_alerta_usr.visibility = View.VISIBLE
                                txt_alerta_usr.visibility = View.VISIBLE

                                if(muestraAlerta){
                                    var apkUrl = ""
                                    var entornoAct = databaseHelper_Data.getControlEntornoConexion(this@MainActivity)
                                    if(entornoAct == 1){
                                        apkUrl = "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"

                                    }else{
                                        apkUrl = "https://${BuildConfig.DOMINIO_PORTAL}/apliacion_latest_android/app_android.apk"

                                    }
                                    downloadController = DownloadController(this@MainActivity, apkUrl)


                                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                                    dialogBuilder.setCancelable(true)
                                    dialogBuilder.setMessage("La actualización $UltVersion está disponible. Al descargar la última actualización, obtendrá las últimas funciones, mejoras y correcciones de errores")
                                        .setCancelable(true)
                                        .setPositiveButton("Descargar", DialogInterface.OnClickListener { view, _ ->

                                            checkStoragePermission(UltVersion)
                                        })
                                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                                            view.dismiss()
                                        })


                                    var alert = dialogBuilder.create()
                                    alert.setTitle("")
                                    alert.show()


                                }


                            }else{
                                SharedApp.prefs.nuevaVersion = "0"
                                if (opMenu){
                                    Toast.makeText(this@MainActivity, "La aplicacion esta actualizada", Toast.LENGTH_LONG).show()

                                }
                            }

                        }

                    })


                }else{
                    SharedApp.prefs.nuevaVersion = "0"
                }
            }

        })

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

                val accountsIntent = Intent(this@MainActivity, UpdateVersionActivity::class.java)
                accountsIntent.putExtra("ult_version", ultimaVersion)
                startActivity(accountsIntent)
            }
        }else{
            if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
            ) {


                val accountsIntent = Intent(this@MainActivity, UpdateVersionActivity::class.java)
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
                    PERMISSION_REQUEST_STORAGE
                )
            }
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_usuario_principal, menu)
        configUsuMenu = menu!!.findItem(R.id.btn_usuario_configuracion)
        conexionMenu = menu!!.findItem(R.id.btn_conexion_status)
        estatusAlertasMenu = menu!!.findItem(R.id.btn_alertas_estatus)

        var imgbd : Bitmap? = databaseHelper_Data.getAvatarUser()
        if(imgbd != null){
            val d: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(imgbd, 100, 100, true))
            configUsuMenu!!.icon = d
        }






        databaseHelper_Data.addControlPausaAlertas()
        var ipausaAlt = databaseHelper_Data.getControlPausaAlertas_IntervaloTmAlt()
        if (ipausaAlt[0].pa_estatus == 0){
            estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
        }else{
            estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfverde)
        }


        configUsuMenu?.isVisible = true




        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId

        return if (id == R.id.btn_usuario_configuracion) {

            val menuItemView = findViewById<View>(R.id.btn_usuario_configuracion) // SAME ID AS MENU ID



            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()


            val popupMenu = PopupMenu(this, menuItemView)
            popupMenu.inflate(R.menu.popup_menu_configuracion)


            val str = SpannableStringBuilder("Version Actual ${BuildConfig.VERSION_NAME}")
            str.setSpan(StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            str.setSpan(
                ForegroundColorSpan(Color.rgb(0,0,0)),
                0, str.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            popupMenu.menu.getItem(0).title = str

            var fec_hasta = databaseHelper_Data.getFecHasta()
            fec_hasta = fec_hasta.replace("T", " ")


            var dias_vigencia = databaseHelper_Data.getDiasFinVigencia().toInt() * -1



            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = calender.get(Calendar.MONTH) + 1
            val day = calender.get(Calendar.DAY_OF_MONTH)
            val Horas = calender.get(Calendar.HOUR)
            val Minutos = calender.get(Calendar.MINUTE)
            val Segundos = calender.get(Calendar.SECOND)
            val todayDate = "$year-$month-$day $Horas:$Minutos:$Segundos"

            val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fec_hasta)
            val calender2 = Calendar.getInstance()
            calender2.time = convertedTodayDateHasta
            calender2.add(Calendar.DATE, dias_vigencia)

            val year_Hasta = calender2.get(Calendar.YEAR)
            val month_Hasta = calender2.get(Calendar.MONTH) + 1
            val day_Hasta = calender2.get(Calendar.DAY_OF_MONTH)
            val Horas_Hasta = calender2.get(Calendar.HOUR)
            val Minutos_Hasta = calender2.get(Calendar.MINUTE)
            val Segundos_Hasta = calender2.get(Calendar.SECOND)
            val todayDate_Hasta = "$year_Hasta-$month_Hasta-$day_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta"

            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDate: Date =  simpleDateFormat.parse(todayDate)


            val simpleDateFormat_Hasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDate_Hasta: Date =  simpleDateFormat_Hasta.parse(todayDate_Hasta)




            val cmp = convertedTodayDate.compareTo(convertedTodayDate_Hasta)
            when {
                cmp > 0 -> {
                    System.out.printf("is after ")
                    val strVigencia = SpannableStringBuilder("Vigencia $fec_hasta")
                    strVigencia.setSpan(StyleSpan(Typeface.BOLD), 0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    strVigencia.setSpan(ForegroundColorSpan(Color.RED), 9, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    strVigencia.setSpan(
                        ForegroundColorSpan(Color.rgb(0,0,0)),
                        0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    popupMenu.menu.getItem(1).title = strVigencia

                    float_btn_alerta_usr.visibility = View.VISIBLE
                    txt_alerta_usr.visibility = View.VISIBLE

                }
                cmp < 0 -> {
                    val strVigencia = SpannableStringBuilder("Vigencia $fec_hasta")
                    strVigencia.setSpan(StyleSpan(Typeface.BOLD), 0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    strVigencia.setSpan(
                        ForegroundColorSpan(Color.rgb(0,0,0)),
                        0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    popupMenu.menu.getItem(1).title = strVigencia
                }
                else -> {
                    val strVigencia = SpannableStringBuilder("Vigencia $fec_hasta")
                    strVigencia.setSpan(StyleSpan(Typeface.BOLD), 0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    strVigencia.setSpan(
                        ForegroundColorSpan(Color.rgb(0,0,0)),
                        0, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    popupMenu.menu.getItem(1).title = strVigencia
                }
            }




            val usuarioActual = databaseHelper_Data.getUserName()
            val strUsuarioActual = SpannableStringBuilder("Usuario: $usuarioActual")
            strUsuarioActual.setSpan(StyleSpan(Typeface.BOLD), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            strUsuarioActual.setSpan(
                ForegroundColorSpan(Color.rgb(0,0,0)),
                0, strUsuarioActual.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            popupMenu.menu.getItem(2).title = strUsuarioActual


            if( SharedApp.prefs.nuevaVersion != "0"){
                val strBuscaAct = SpannableStringBuilder("Hay una nueva actualización (${SharedApp.prefs.nuevaVersion})")
                strBuscaAct.setSpan(StyleSpan(Typeface.BOLD), 0, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                strBuscaAct.setSpan(ForegroundColorSpan(Color.rgb(52,134,118)), 29, 38, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                //strBuscaAct.setSpan(ForegroundColorSpan(Color.rgb(164,159,158)), 0, 27, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                popupMenu.menu.getItem(8).title =  strBuscaAct
            }else{
                val strBuscaAct = SpannableStringBuilder("Buscar Actualizaciones")
                popupMenu.menu.getItem(8).title =  strBuscaAct
            }




            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_avatar -> {
                        val accountsIntent = Intent(this@MainActivity, avatar_usuario::class.java)
                        startActivityForResult(accountsIntent,2)
                    }
                    R.id.opcion_cambio_nip -> {
                        //cMneuUser = cMneuUser(this@MainActivity, idDispositivo, databaseHelper_Data)
                        // cMneuUser.showModalConfiguracion()

                        val accountsIntent = Intent(this@MainActivity, activity_cambio_nip_tap::class.java)
                        startActivity(accountsIntent)


                    }
                    R.id.opcion_cinfiguracion -> {
                        //cMneuUser = cMneuUser(this@MainActivity, idDispositivo, databaseHelper_Data)
                        //cMneuUser.showModalConfiguracionGneral()

                        val accountsIntent = Intent(this@MainActivity, activity_configuracion_tap::class.java)
                        startActivity(accountsIntent)


                    }
                    R.id.opcion_confbackupftp -> {
                        //cMneuUser = cMneuUser(this@MainActivity, idDispositivo, databaseHelper_Data)
                        //cMneuUser.showModalConfiguracionEnvioDB()

                        val accountsIntent = Intent(this@MainActivity, activity_copia_seguridad::class.java)
                        startActivity(accountsIntent)

                    }
                    R.id.opcion_buscar_actualizacion -> {
                        chkExitNewVersion(true, true)


                    }

                    R.id.opcion_auxiliar_ayuda -> {
                        val url = "https://apptomatico.com/"
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.main_fragment, BrowserAppActivity.newInstance(url), "BrowserAppActivity")
                            .commitNow()

                    }
                    R.id.opcion_salir -> {
                        /**
                         * ESTA VARIABLE SE UTILIZA PARA INDICAR QUE EL  USUARIO HA INICIADO SESION
                         */
                        LoginActivity.Islogin= false
                        /**
                         *
                         */
                        finish()
                        val accountsIntent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(accountsIntent)

                    }

                    // R.id.opcion_entorno_servidor -> {

                    //     showBottomSheetDialogEntorno()

                    // }


                }


                true
            })

            popupMenu.show()



            true
        } else if (id == R.id.btn_alertas_estatus){

            databaseHelper_Data.addControlPausaAlertas()


            val menuItemView = findViewById<View>(R.id.btn_alertas_estatus) // SAME ID AS MENU ID
            val popupMenu = PopupMenu(this, menuItemView)
            popupMenu.inflate(R.menu.menu_pausa_alertas)

            var ipausaAlt = databaseHelper_Data.getControlPausaAlertas_IntervaloTmAlt()

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.opcion_1m -> {

                        val cdInicio = LocalDateTime.now()
                        val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedI = cdInicio.format(formatterI)

                        val cdFin = LocalDateTime.now().plusSeconds(59)
                        val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedF = cdFin.format(formatterF)


                        databaseHelper_Data.updControlPausaAlertas(0, "$formattedI", "$formattedF")
                        val id_alerta = databaseHelper_Data.getMaxIdAlerta()
                        databaseHelper_Data.addNewAlertaDB(id_alerta + 1, "APPTOMATICO",  "Notificaciones pausadas temporalmente; Tiempo:1 minuto, Inicio: $formattedI Fin: $formattedF", "APPTOMATICO", 6, "", true)
                        var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        if (ctxFragment?.view?.tag == "tgAlertasFragment") {

                            var idNegocio = 0
                            if (header_id_negocio != "Todos"){
                                idNegocio = header_id_negocio.toInt()
                            }
                            var idEquipo = 0
                            if(header_id_equipos != "Todos"){
                                idEquipo = header_id_equipos.toInt()
                            }

                            val topAlertas = databaseHelper_Data.getTopAlertas()
                            val resultado = databaseHelper_Data.getAlertasListData(
                                idNegocio,
                                idEquipo,
                                filtro_alertas_estatus,
                                filtro_alertas_fecha,
                                filtro_alertas_fecha_desde_hasta,
                                0,
                                topAlertas,
                                filtro_alertas_tipo,
                                "",
                                LoginActivity.filtro_alertas_marcadas
                            )

                            resultado.sortedByDescending { it.fecha }
                           // AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)



                        }

                        estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
                        Toast.makeText(this@MainActivity, "Alertas pausadas durante 1 minuto", Toast.LENGTH_LONG).show()

                    }

                    R.id.opcion_15m -> {
                        val cdInicio = LocalDateTime.now()
                        val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedI = cdInicio.format(formatterI)

                        val cdFin = LocalDateTime.now().plusSeconds(900)
                        val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedF = cdFin.format(formatterF)


                        databaseHelper_Data.updControlPausaAlertas(0, "$formattedI", "$formattedF")
                        val id_alerta = databaseHelper_Data.getMaxIdAlerta()
                        databaseHelper_Data.addNewAlertaDB(id_alerta + 1, "APPTOMATICO",  "Notificaciones pausadas temporalmente; Tiempo:15 minutos, Inicio: $formattedI Fin: $formattedF", "APPTOMATICO", 6, "", true)
                        var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        if (ctxFragment?.view?.tag == "tgAlertasFragment") {

                            var idNegocio = 0
                            if (header_id_negocio != "Todos"){
                                idNegocio = header_id_negocio.toInt()
                            }
                            var idEquipo = 0
                            if(header_id_equipos != "Todos"){
                                idEquipo = header_id_equipos.toInt()
                            }

                            val topAlertas = databaseHelper_Data.getTopAlertas()
                            val resultado = databaseHelper_Data.getAlertasListData(
                                idNegocio,
                                idEquipo,
                                filtro_alertas_estatus,
                                filtro_alertas_fecha,
                                filtro_alertas_fecha_desde_hasta,
                                0,
                                topAlertas,
                                filtro_alertas_tipo,
                                "",
                                LoginActivity.filtro_alertas_marcadas
                            )


                            resultado.sortedByDescending { it.fecha }
                           // AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)

                        }



                        estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
                        Toast.makeText(this@MainActivity, "Alertas pausadas durante 15 minutos", Toast.LENGTH_LONG).show()

                    }

                    R.id.opcion_1h -> {
                        val cdInicio = LocalDateTime.now()
                        val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedI = cdInicio.format(formatterI)

                        val cdFin = LocalDateTime.now().plusSeconds(3600)
                        val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedF = cdFin.format(formatterF)


                        databaseHelper_Data.updControlPausaAlertas(0, "$formattedI", "$formattedF")
                        val id_alerta = databaseHelper_Data.getMaxIdAlerta()
                        databaseHelper_Data.addNewAlertaDB(id_alerta + 1, "APPTOMATICO",  "Notificaciones pausadas temporalmente; Tiempo:1 hora, Inicio: $formattedI Fin: $formattedF", "APPTOMATICO", 6, "", true)
                        var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        if (ctxFragment?.view?.tag == "tgAlertasFragment") {

                            var idNegocio = 0
                            if (header_id_negocio != "Todos"){
                                idNegocio = header_id_negocio.toInt()
                            }
                            var idEquipo = 0
                            if(header_id_equipos != "Todos"){
                                idEquipo = header_id_equipos.toInt()
                            }

                            val topAlertas = databaseHelper_Data.getTopAlertas()
                            val resultado = databaseHelper_Data.getAlertasListData(
                                idNegocio,
                                idEquipo,
                                filtro_alertas_estatus,
                                filtro_alertas_fecha,
                                filtro_alertas_fecha_desde_hasta,
                                0,
                                topAlertas,
                                filtro_alertas_tipo,
                                "",
                                LoginActivity.filtro_alertas_marcadas

                            )


                            resultado.sortedByDescending { it.fecha }
                           // AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)

                        }

                        estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
                        Toast.makeText(this@MainActivity, "Alertas pausadas durante 1 hora", Toast.LENGTH_LONG).show()

                    }

                    R.id.opcion_6h -> {
                        val cdInicio = LocalDateTime.now()
                        val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedI = cdInicio.format(formatterI)

                        val cdFin = LocalDateTime.now().plusSeconds(21600)
                        val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedF = cdFin.format(formatterF)


                        databaseHelper_Data.updControlPausaAlertas(0, "$formattedI", "$formattedF")
                        val id_alerta = databaseHelper_Data.getMaxIdAlerta()
                        databaseHelper_Data.addNewAlertaDB(id_alerta + 1, "APPTOMATICO",  "Notificaciones pausadas temporalmente; Tiempo:6 horas, Inicio: $formattedI Fin: $formattedF", "APPTOMATICO", 6, "", true)
                        var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        if (ctxFragment?.view?.tag == "tgAlertasFragment") {

                            var idNegocio = 0
                            if (header_id_negocio != "Todos"){
                                idNegocio = header_id_negocio.toInt()
                            }
                            var idEquipo = 0
                            if(header_id_equipos != "Todos"){
                                idEquipo = header_id_equipos.toInt()
                            }

                            val topAlertas = databaseHelper_Data.getTopAlertas()
                            val resultado = databaseHelper_Data.getAlertasListData(
                                idNegocio,
                                idEquipo,
                                filtro_alertas_estatus,
                                filtro_alertas_fecha,
                                filtro_alertas_fecha_desde_hasta,
                                0,
                                topAlertas,
                                filtro_alertas_tipo,
                                "",
                                LoginActivity.filtro_alertas_marcadas
                            )


                            resultado.sortedByDescending { it.fecha }
                           // AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)

                        }


                        estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
                        Toast.makeText(this@MainActivity, "Alertas pausadas durante 6 horas", Toast.LENGTH_LONG).show()

                    }

                    R.id.opcion_1d -> {
                        val cdInicio = LocalDateTime.now()
                        val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedI = cdInicio.format(formatterI)

                        val cdFin = LocalDateTime.now().plusSeconds(86400)
                        val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val formattedF = cdFin.format(formatterF)


                        databaseHelper_Data.updControlPausaAlertas(0, "$formattedI", "$formattedF")
                        val id_alerta = databaseHelper_Data.getMaxIdAlerta()
                        databaseHelper_Data.addNewAlertaDB(id_alerta + 1, "APPTOMATICO",  "Notificaciones pausadas temporalmente; Tiempo:1 dia, Inicio: $formattedI Fin: $formattedF", "APPTOMATICO", 6, "", true)
                        var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                        var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                        if (ctxFragment?.view?.tag == "tgAlertasFragment") {

                            var idNegocio = 0
                            if (header_id_negocio != "Todos"){
                                idNegocio = header_id_negocio.toInt()
                            }
                            var idEquipo = 0
                            if(header_id_equipos != "Todos"){
                                idEquipo = header_id_equipos.toInt()
                            }

                            val topAlertas = databaseHelper_Data.getTopAlertas()
                            val resultado = databaseHelper_Data.getAlertasListData(
                                idNegocio,
                                idEquipo,
                                filtro_alertas_estatus,
                                filtro_alertas_fecha,
                                filtro_alertas_fecha_desde_hasta,
                                0,
                                topAlertas,
                                filtro_alertas_tipo,
                                "",
                                filtro_alertas_marcadas
                            )
                            resultado.sortedByDescending { it.fecha }
                           // AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)

                        }


                        estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfgris)
                        Toast.makeText(this@MainActivity, "Alertas pausadas durante 1 dia", Toast.LENGTH_LONG).show()

                    }



                }
                true

            })

            if (ipausaAlt[0].pa_estatus == 0){

                val cdInicio = LocalDateTime.now()
                val formatterI = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val formattedI = cdInicio.format(formatterI)


                val cdFin = LocalDateTime.now().plusSeconds(ipausaAlt[0].pa_tiempo.toLong())
                val formatterF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                val formattedF = cdFin.format(formatterF)
                databaseHelper_Data.updControlPausaAlertas(1, "$formattedI", "$formattedF")
                estatusAlertasMenu!!.icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ntfverde)
                Toast.makeText(this@MainActivity, "Alertas Activadas", Toast.LENGTH_LONG).show()

            }else{

                popupMenu.show()

            }






            true
        }else super.onOptionsItemSelected(item)
    }

    //set an active fragment programmatically
    fun setSelectedItem(pos:Int){
        when(pos){
            0 ->  SmoothBottomBar!!.selectedItemId = R.id.first_fragment
            1 ->  SmoothBottomBar!!.selectedItemId = R.id.second_fragment
            2 ->  SmoothBottomBar!!.selectedItemId = R.id.third_fragment
            3 ->  SmoothBottomBar!!.selectedItemId = R.id.fourth_fragment
            6 ->  SmoothBottomBar!!.selectedItemId = R.id.six_fragment
//            7 ->  SmoothBottomBar!!.selectedItemId = R.id.seven_fragment
//            8 ->  SmoothBottomBar!!.selectedItemId = R.id.eight_fragment
            else -> { // Note the block
                print("null")
            }
        }


    }



    //set badge indicator
    fun setBadge(pos:Int, numbg: Int){
        val menuItemId: Int = SmoothBottomBar!!.menu.getItem(pos).itemId
        val badgeDrawable = SmoothBottomBar!!.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true
        badgeDrawable.maxCharacterCount = 5
        badgeDrawable.number = numbg

    }
    //remove badge indicator
    fun removeBadge(pos:Int){
        val menuItemId: Int = SmoothBottomBar!!.menu.getItem(pos).itemId
        val badgeDrawable = SmoothBottomBar!!.getOrCreateBadge(menuItemId)
        badgeDrawable.isVisible = true
        badgeDrawable.maxCharacterCount = 5
        badgeDrawable.number = 0
    }

    fun updateavatarmenuuser(){
        invalidateOptionsMenu()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupSmoothBottomMenu() {
        popupMenuButton = PopupMenu(this, null)
        popupMenuButton.inflate(R.menu.menu_bottom)
        //val menu = popupMenu.menu
        //binding.bottomBar.setupWithNavController(menu, navController)
        SmoothBottomBar!!.setupWithNavController(navController)
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(++check > 1) {
            try{
                val lstNegocios = databaseHelper_Data.GetListaNegocios()
                if(position < lstNegocios.size ) {
                    header_id_negocio = lstNegocios[position].id
                }

                var idNegocio = 0
                if (header_id_negocio != "Todos"){
                    idNegocio = header_id_negocio.toInt()
                }

                var idEquipo = 0
                if(LoginActivity.header_id_equipos != "Todos"){
                    idEquipo = LoginActivity.header_id_equipos.toInt()
                }


                var ctxNavFragment: Fragment? = supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
                var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
                ctxFragment?.view?.tag
                if(ctxFragment?.view?.tag == "tgMonitoresFragment"){
//                    MonitoreablesFragment.MonitoresAccionList = databaseHelper_Data.getListTableMonitoreables(idNegocio, idEquipo)
//                    MonitoreablesFragment.adapterMonitores!!.notifyDataSetChanged()
                }else if(ctxFragment?.view?.tag == "InicioEquiposFragment"){
                    LoginActivity.valuesListEquipos = databaseHelper_Data.getListEquipos(idNegocio)
//                    ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()
//                    ChildFragment.NegociosHomeAdapter!!.refreshFamiliasDB()
//                    ChildFragment.NegociosHomeAdapter!!.notifyDataSetChanged()

                }else if(ctxFragment?.view?.tag == "tgEquiposUnicamenteFragment"){
                    LoginActivity.valuesListEquiposUnicamente = databaseHelper_Data.getListEquipos(idNegocio)
//                    NegociosUnicamenteFragment.NegociosUnicamenteHomeAdapter!!.notifyDataSetChanged()

                }else if(ctxFragment?.view?.tag == "tgAlertasFragment"){

                    val topAlertas = databaseHelper_Data.getTopAlertas()
                    val resultado = databaseHelper_Data.getAlertasListData(
                        idNegocio,
                        filtro_alertas_equipo,
                        filtro_alertas_estatus,
                        filtro_alertas_fecha,
                        filtro_alertas_fecha_desde_hasta,
                        0,
                        topAlertas,
                        filtro_alertas_tipo,
                        "",
                        filtro_alertas_marcadas
                    )


                    resultado.sortedByDescending { it.fecha }
                  //  AlertasFragment.ALertasRcViewAdapter?.actualizarLista(resultado, true)

                }

                var ctxNavFragmentAllFav: Fragment? = supportFragmentManager?.findFragmentByTag("AllAccionesFavoritasFragment") as? AllAccionesFavoritas
                if(ctxNavFragmentAllFav != null && ctxNavFragmentAllFav.isVisible){
                    var  listAlertas =  databaseHelper_Data.getListAccionesDinamicas_Panel(idNegocio, idEquipo)
                   AllAccionesFavoritas.accionListAllAccionesFavoritas.clear()
                    for (i in  listAlertas) {
                        var alias= i.alias_aplicacion
                        var tipo = i.tipo_id

                        AllAccionesFavoritas.accionListAllAccionesFavoritas.add(
                            ItemAcciones(
                                i.id,
                                "$alias",
                                "",
                                "$tipo",
                                "${i.comando}",
                                i.id_negocio_id.toString(),
                                i.nombre_equipo,
                                false
                            )
                        )
                    }
                    AllAccionesFavoritas.AllAccionesFavoritasAdapter!!.notifyDataSetChanged()

                }





            } catch (ex: Exception){
                // executorService.shutdown()
            }


        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.TerminosyCondiciones) {

            val url = "https://apptomatico.com/"
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, BrowserAppActivity.newInstance(url), "BrowserAppActivity")
                .commitNow()

        }

        if (id == R.id.PoliticaPrivacidad) {
            val url = "https://apptomatico.com/"
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, BrowserAppActivity.newInstance(url), "BrowserAppActivity")
                .commitNow()

        }

        if (id == R.id.CentrodeAyuda) {
            val url = "https://apptomatico.com/"
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment, BrowserAppActivity.newInstance(url), "BrowserAppActivity")
                .commitNow()

        }

        if (id == R.id.AlertasEliminadasMenu) {




            var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
            var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
            if (ctxFragment?.view?.tag == "InicioEquiposFragment") {
                LoginActivity.origen_back_dashboard = 0
//                val fmract = ctxFragment as NegocioFragment
//                fmract.navBarraLateral(8)
            }



            if (ctxFragment?.view?.tag == "tgAlertasFragment") {
                LoginActivity.origen_back_dashboard = 1
                val fmract = ctxFragment as AlertasFragment
//                fmract.navBarraLateral(8)
            }


            if (ctxFragment?.view?.tag == "tgMonitoresFragment") {
                LoginActivity.origen_back_dashboard = 2
//                val fmract = ctxFragment as MonitoreablesFragment
//                fmract.navBarraLateral(8)
            }

            if (ctxFragment?.view?.tag == "tgEquiposUnicamenteFragment") {
                LoginActivity.origen_back_dashboard = 3
//                val fmract = ctxFragment as NegociosUnicamenteFragment
//                fmract.navBarraLateral(8)
            }

            if(ctxFragment?.view?.tag == "tgDashboardFragment"){
                LoginActivity.origen_back_dashboard = 0
                val fmract = ctxFragment as DashboardActivity
                fmract.navBarraLateral(8)
            }

            // supportFragmentManager.beginTransaction()
            //   .replace(R.id.main_fragment, AlertasEliminadasFragment(), "AlertasEliminadasFragment")
            //.commitNow()

        }

        if (id == R.id.HistoricoScreenshotMenu) {

            var ctxNavFragment: NavHostFragment? =  supportFragmentManager?.findFragmentByTag("main_fragment") as? NavHostFragment
            var ctxFragment: Fragment? =  ctxNavFragment?.childFragmentManager?.fragments?.first()
            if (ctxFragment?.view?.tag == "InicioEquiposFragment") {
//                val fmract = ctxFragment as NegocioFragment
//                fmract.navBarraLateral(9)
            }

            if (ctxFragment?.view?.tag == "tgEquiposUnicamenteFragment") {
//                val fmract = ctxFragment as NegociosUnicamenteFragment
//                fmract.navBarraLateral(9)
            }

            if (ctxFragment?.view?.tag == "tgAlertasFragment") {
//                val fmract = ctxFragment as AlertasFragment
//                fmract.navBarraLateral(9)
            }


            if (ctxFragment?.view?.tag == "tgMonitoresFragment") {
//                val fmract = ctxFragment as MonitoreablesFragment
//                fmract.navBarraLateral(9)
            }

            if(ctxFragment?.view?.tag == "tgDashboardFragment"){
                val fmract = ctxFragment as DashboardActivity
                fmract.navBarraLateral(9)
            }

            //  val accountsIntent = Intent(this@AlertasActivity, HistoricoScreenshot::class.java)
            //this@AlertasActivity.startActivity(accountsIntent)
            // finish()

            //  supportFragmentManager.beginTransaction()
            //      .replace(R.id.main_fragment, HistoricoScreenshot(), "HistoricoScreenshotFragment")
            //      .commitNow()

        }

        if (navView.visibility == View.VISIBLE) {
            navView.visibility = View.GONE
            layoutnavbar.visibility = View.GONE
        }


        return false
    }





    //***************************************************
    private fun updListCatNegocio(){
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        val myTraceToken: Trace = FirebasePerformance.getInstance().newTrace("trace_main_listaneg_token")
        myTraceToken.start()


        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                val lstNegocios = databaseHelper_Data.GetListaNegocios()
                lstNegocios.forEach {
                    if (!catnegociosList.contains(
                            com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list(
                                "${lstNegocios}",
                                "${it.id}"
                            )
                        )) { // <- look for item!

                        catnegociosList.add(
                            com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list(
                                "${it.nombre}",
                                "${it.id}"
                            )
                        )

                    }
                }
                getAllChildExercisesFromParentID()
                myTraceToken.stop()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
                    val resultUpdNip: Call<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List> = negociosService.getListCatNegocios("$idDispositivo", "JWT $token", "application/json")

                    val myTraceDet: Trace = FirebasePerformance.getInstance().newTrace("trace_main_listaneg_det")
                    myTraceDet.start()

                    resultUpdNip.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List>, t: Throwable) {
                            myTraceDet.stop()
                            Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List>) {
                            val resultadoAccion = response.body()
                            if (resultadoAccion != null) {
                                var valnegocios: List<com.apptomatico.app_movil_kotlin_v3.model.CatNegocio_List> = ArrayList()
                                valnegocios = resultadoAccion.data.toList()
                                valnegocios.forEach {

                                    val neg =
                                        com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list(
                                            "${it.nombre}",
                                            "${it.id}"
                                        )
                                    if (!databaseHelper_Data.CheckExistNegocio(neg)) {
                                        databaseHelper_Data.addNegocio(neg)
                                    }



                                    if (!catnegociosList.contains(
                                            com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list(
                                                "${it.nombre}",
                                                "${it.id}"
                                            )
                                        )) { // <- look for item!

                                        catnegociosList.add(
                                            com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list(
                                                "${it.nombre}",
                                                "${it.id}"
                                            )
                                        )

                                    }
                                }
                                getAllChildExercisesFromParentID()

                            }
                            myTraceDet.stop()
                        }

                    })


                }
                myTraceToken.stop()
            }

        })
    }


    //***************************************************


    private fun checkForInternet(context: Context): Boolean {


        val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager


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




    fun getMovilData():String{
        val tm = this@MainActivity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

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
        val tm = this@MainActivity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }

        val telNumber =   "S/N"
        if (telNumber != null){
            return telNumber
        }else{

            return "S/N"

        }


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
    fun View.showSnackbar(msgId: Int, length: Int) {
        showSnackbar(context.getString(msgId), length)
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

    private fun provideOkhttp(): OkHttpClient{
        var wsidDispositivo: String = Settings.Secure.getString(this@MainActivity.contentResolver, Settings.Secure.ANDROID_ID).toString()
        wsidDispositivo = removeLeadingZeroes(wsidDispositivo)
        wsidDispositivo =  removeTrailingZeroes(wsidDispositivo)
        val requestIterator = Interceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("token","$wsidDispositivo")
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request = request)
        }

        return  OkHttpClient.Builder()
            .addInterceptor(requestIterator)
            .build()
    }


    private fun provideLifeCycle() = AndroidLifecycle.ofApplicationForeground(application)


    private fun provideStreamAdapterFactory() = RxJava2StreamAdapterFactory()

    /**
     *Finaliza implementacion de dependencias WebSocket
     */

    private fun sesionCaducada(){
        LoginActivity.Islogin= false

        finish()
        val accountsIntent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(accountsIntent)
    }



    private fun checkForCrash() {



        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        //val crash_never_ask_again = preferences.getBoolean("crash_never_ask_again", false)
        //if (crash_never_ask_again) //Previously user check the checkbox of never ask me again about sending crash dialog
        //   return
        val dialog_message =
            "En la última ejecución, el programa encontró un error, le pedimos disculpas por esto, puede enviarnos la información del error para corregir este error en futuras actualizaciones."
        val button_positive_text = "Enviar"
        val button_negative_text = "Cerrar"
        val checkbox_text = "Nunca preguntes de nuevo"
        val email = "jescamilla@dsiin.com"
        var line: String
        var trace = ""
        try {
            val reader = BufferedReader(InputStreamReader(this@MainActivity.openFileInput("stack.trace")))

            var line2 = reader.readLine()
            while (line2 != null) {
                // Your code
                line2 = reader.readLine()
                trace += """
                $line2
                
                """.trimIndent()
            }



        } catch (fnfe: FileNotFoundException) {
            // ...
        } catch (ioe: IOException) {
            // ...
        }
        if (trace.length < 10) //We didn't have any crash
            return

        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.alertdialogerror, null)


        val textviewMsgUsr = view.findViewById(R.id.txttitulomsgerr) as TextView
        textviewMsgUsr.text = "$dialog_message"

        val textviewMsg = view.findViewById(R.id.txtmsgusererr) as EditText


        val textview = view.findViewById(R.id.textmsgerrorerr) as TextView
        textview.text = "$trace"


        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setCancelable(true)
        //builder.setIcon(R.drawable.ic_setting);
        //builder.setMessage(dialog_message)
        builder.setTitle("Informe de Error")
        builder.setView(view)
        builder.setCancelable(false)
        val finalTrace = trace
        builder.setPositiveButton(
            button_positive_text
        ) { dialog, which ->


            var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
            var nombre_Movil = Licenciadb[0].nommbre_movil

            RemoteSendTraserEmail(finalTrace, textviewMsg.text.toString(), nombre_Movil).execute()

            val dir: File = filesDir
            val file = File(dir, "stack.trace")
            val deleted: Boolean = file.delete()
        }


        builder.setNegativeButton(
            button_negative_text
        ) { dialog, which ->
            this@MainActivity.deleteFile("stack.trace")
            val checkbox_checked = preferences.getBoolean("checkbox_checked", false)
            if (checkbox_checked) {
                val editor = preferences.edit()
                editor.putBoolean("crash_never_ask_again", true)
                editor.apply()
            }
            dialog.dismiss()
        }


        val alert = builder.create()
        alert.show()
    }

//********************************************************

    fun ConsultaInfoProgramaExec(accion: com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List, idHardware: String, ipNegocio: String, iPuertoNegocio: Int, iNomNegocio: String, ipLocal: String, tipoComunicacion: Int){
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    // Toast.makeText(requireContext(), "Error al recuperar token del negocio", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {
                        val resultAccion: Call<ResponseBody> = getTokenNegocio.getInfoProgramSvr("${accion.estado_programa_nombre}", "$resultado", "application/json")
                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(this@MainActivity, "Error al recuperar informacion del porgrama", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                val resultJson = response.body()!!.string()

                                var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                                dialogBuilder.setMessage("$resultJson")
                                    .setCancelable(false)
                                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
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
        }else if(tipoComunicacion == 1){
            var parametros = "${accion.estado_programa_nombre}"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaProgramaEquipo\",\"parametros\":\"$parametros\"}")

        }


    }


    fun chkDelArcDirServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int,
                             tipoEliminacio: String, rutaEliminacion: String, archivoEliminacion: String, ipLocal: String, tipoComunicacion: Int){


        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@MainActivity, "No fue posible conectar con el servidor remoto, intentelo mas tarde ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
                    val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                    resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>) {
                            val resHorariosmv = response.body()
                            if (resHorariosmv != null) {
                                val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> = resHorariosmv.data.toList()
                                val smDateFormat = SimpleDateFormat("HH:mm:ss")
                                val currenTime: String = smDateFormat.format(Date())
                                val horaInicial = horario[0].horario_desde
                                var horaFinal = horario[0].horario_hasta
                                val dia_semana = horario[0].dia_semana
                                if (horaFinal == "00:00:00"){
                                    horaFinal = "23:59:59"
                                }

                                if (horaInicial != "SN" && horaFinal != "SN") {
                                    if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1) {

                                        DelArcDirServidor(idHardware, idNegocio, nomNegocio, ipNegocio, iPuertoNegocio,
                                            tipoEliminacio, rutaEliminacion, archivoEliminacion, ipLocal,tipoComunicacion)
                                    } else {

                                        Toast.makeText(this@MainActivity, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    DelArcDirServidor(idHardware, idNegocio, nomNegocio, ipNegocio, iPuertoNegocio,
                                        tipoEliminacio, rutaEliminacion, archivoEliminacion, ipLocal, tipoComunicacion)
                                }


                            } else {
                                DelArcDirServidor(idHardware, idNegocio, nomNegocio, ipNegocio, iPuertoNegocio,
                                    tipoEliminacio, rutaEliminacion, archivoEliminacion, ipLocal,tipoComunicacion)
                            }
                            ///////////////
                        }

                    })


                } else {
                    Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
                }
            }

        })



    }

    fun DelArcDirServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int,
                          tipoEliminacio: String, rutaEliminacion: String, archivoEliminacion: String, ipLocal: String,tipoComunicacion: Int){

        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipNegocio, iPuertoNegocio.toString(), ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->
                            //  val accountsIntent = Intent(requireContext(), NegocioActivity::class.java)
                            // startActivity(accountsIntent)

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    // Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(ipNegocio, iPuertoNegocio.toString(), ipLocal, databaseHelper_Data).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.delArchDirNegocio("$rutaEliminacion", "$archivoEliminacion", "$tipoEliminacio", false, "$resultado", "application/json")
                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(this@MainActivity, "Error al eliminar elemento del Servidor", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null) {
                                    Toast.makeText(this@MainActivity, "$resultado", Toast.LENGTH_LONG).show()
                                }

                            }

                        })

                    }
                }

            })
        }else if(tipoComunicacion == 1){

            var ruta = "$rutaEliminacion|$archivoEliminacion|$tipoEliminacio|false"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setDelArchivosEquipo\",\"parametros\":\"$ruta\"}")



        }






    }


    fun getGetUsuariosPermitidosXEquipo(info_app: com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List, tipoComunicacion : Int
    ) {
        if (!info_app.aplicacion_usuario.isNullOrEmpty() && info_app.aplicacion_usuario != "SN") {

            showExecuteProgramServer("${info_app.alias_aplicacion}",
                "${info_app.id_negocio_id}",
                "${info_app.ip_publica}",
                "${info_app.hardware_key}",
                "${info_app.puerto}",
                "${info_app.aplicacion_ruta_aplicacion}",
                "${info_app.aplicacion_nombre_aplicacion}",
                "${info_app.aplicacion_parametros_aplicacion}",
                "${info_app.ip_local}", tipoComunicacion,
                "SN", "${info_app.aplicacion_usuario}",
                "${info_app.aplicacion_pwd}"
            )

        } else {

            val getVigenciaLic = RestEngine.getRestEngineInicial(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
            val result_usuarios_permitidos = getVigenciaLic.getUsuarioPermitidosEjecEquipo(
                info_app.id_negocio_id,
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
                            showNoUsuariosDialog(info_app.nombre_negocio)
                            return
                        }

                        val layoutInflater = LayoutInflater.from(this@MainActivity)
                        val view = layoutInflater.inflate(R.layout.dialog_usuarios, null)

                        val listViewActivos = view.findViewById<ListView>(R.id.listActivos)
                        val listViewInactivos = view.findViewById<ListView>(R.id.listInactivos)
                        val tvMostrarInactivos = view.findViewById<TextView>(R.id.tvMostrarInactivos)

                        val arrActivos = activos.map { it.id_usuario_activo_id }
                        val arrInactivos = inactivos.map { it.id_usuario_activo_id }

                        val listaActivos = arrActivos.map { it.replace("_", " ") }
                        val listaInactivos = arrInactivos.map { it.replace("_", " ") }

                        var usuarioSeleccionado: String? = null

                        listViewActivos.adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_single_choice, listaActivos)
                        listViewActivos.choiceMode = ListView.CHOICE_MODE_SINGLE
                        listViewActivos.setOnItemClickListener { _, _, pos, _ ->
                            usuarioSeleccionado = arrActivos[pos]

                            // Limpiar selección de la lista de inactivos
                            listViewInactivos.clearChoices()
                            (listViewInactivos.adapter as ArrayAdapter<*>).notifyDataSetChanged()

                            listViewActivos.setItemChecked(pos, true) // marcar el actual
                        }

                        listViewInactivos.adapter = object : ArrayAdapter<String>(
                            this@MainActivity,
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

                        val dialog = AlertDialog.Builder(this@MainActivity)
                            .setView(view)
                            .setPositiveButton("Aceptar") { dialogInterface, _ ->
                                usuarioSeleccionado?.let { idUsuario ->
                                    showExecuteProgramServer(
                                        "${info_app.alias_aplicacion}",
                                        "${info_app.id_negocio_id}",
                                        "${info_app.ip_publica}",
                                        "${info_app.hardware_key}",
                                        "${info_app.puerto}",
                                        "${info_app.aplicacion_ruta_aplicacion}",
                                        "${info_app.aplicacion_nombre_aplicacion}",
                                        "${info_app.aplicacion_parametros_aplicacion}",
                                        "${info_app.ip_local}",
                                        tipoComunicacion,
                                        idUsuario,
                                        "${info_app.aplicacion_usuario}",
                                        "${info_app.aplicacion_pwd}")


                                }
                                dialogInterface.dismiss()
                            }
                            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                                dialogInterface.dismiss()
                            }
                            .create()

                        dialog.show()

                    } else {
                        showNoUsuariosDialog(info_app.nombre_negocio)
                    }
                }

                override fun onFailure(call: Call<DataUsuariosPermitidosEjecucion>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al recuperar listado de usuarios permitidos del equipo: ${t.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }

    private fun showNoUsuariosDialog(titulo: String) {
        androidx.appcompat.app.AlertDialog.Builder(this@MainActivity)
            .setTitle(titulo)
            .setMessage("No existen usuarios activos en el equipo")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    fun RebootNegocio(nomNegocio: String, idHardware: String, ipNegocio: String, iPuertoNegocio: String, ipLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    // Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("Reboot", "1000001", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(this@MainActivity, "Se ejecuto la accion", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })
        }else if(tipoComunicacion == 1){
            var parametros = "Reboot"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }




    }



    fun showExecuteProgramServer(accion: String, idnegocio: String, ipPublica: String, idHardware: String, iPuerto: String, ruta_app: String, nombre_app: String, parametros_app: String, ipLocal: String, tipoComunicacion: Int , usuarioejecucion: String, aplicacionusr: String, aplicacionpwd: String) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.startAPPServidor("$Movil_Id", "$nombre_Movil", "$ruta_app", "$nombre_app", "$parametros_app", "$resultado", "application/json")

                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                if (t.message == "timeout") {
                                    Toast.makeText(this@MainActivity, "La apliacion se esta ejecutando", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                Toast.makeText(this@MainActivity, "La apliacion se esta ejecutando", Toast.LENGTH_LONG).show()
                            }

                        })


                    } else {

                        Toast.makeText(this@MainActivity, "No esta autorizado a realizar esta accion", Toast.LENGTH_LONG).show()
                    }

                }

            })
        }else if(tipoComunicacion == 1){


            var parametros = "$ruta_app|$nombre_app|$parametros_app|$usuarioejecucion|$aplicacionusr|$aplicacionpwd"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"exeAplicacionEquipo\",\"parametros\":\"$parametros\"}")


        }



    }


    fun sendTerminaProcesoEquipo(accion: String, idnegocio: String, ipPublica: String, ipLocal: String, idHardware: String, iPuerto: String, ruta_app: String, nombre_app: String, parametros_app: String, tipoComunicacion: Int ) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if(tipoComunicacion == 0){

            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)
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

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.endAPPServidor("$Movil_Id", "$nombre_Movil", "$ruta_app", "$nombre_app", "$parametros_app", "$resultado", "application/json")

                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                if (t.message == "timeout") {
                                    Toast.makeText(this@MainActivity, "Se finalizo en el equipo", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                Toast.makeText(this@MainActivity, "Se finalizo en el equipo", Toast.LENGTH_LONG).show()
                            }

                        })


                    } else {

                        Toast.makeText(this@MainActivity, "No esta autorizado a realizar esta accion", Toast.LENGTH_LONG).show()
                    }

                }

            })


        }else if(tipoComunicacion == 1){


            var parametros = "$nombre_app"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setTerminaProcesoEquipo\",\"parametros\":\"$parametros\"}")


        }


    }



    fun ShutdownNegocio(nomNegocio: String, idHardware: String, idNegocio: String, ipNegocio: String, iPuertoNegocio: String, ipLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("Shutdown", "1000002", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(this@MainActivity, "El servidor se esta apagado", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })

            updBanderaNegocio(idNegocio)

        }else if(tipoComunicacion == 1){
            var parametros = "Shutdown"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }




    }


    fun LogOffNegocio(nomNegocio: String, idHardware: String, ipNegocio: String, iPuertoNegocio: String, ipLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //  Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("LogOff", "1000003", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(this@MainActivity, "Se ejecuto la accion", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })
        }else if(tipoComunicacion == 1){
            var parametros = "LogOff"
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }


    }



    fun updBanderaNegocio(idNegocio: String){
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error servicio no disponible 6", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val client by lazy { APIService.create() }
                    client.getEstatusNegocioSvr("$idNegocio", "JWT $token", "application/json")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            Toast.makeText(this@MainActivity, "Se actualizo estatus en Servidor de Licencias", Toast.LENGTH_LONG).show()
                        }, { throwable ->
                            Toast.makeText(this@MainActivity, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                        }
                        )
                }
            }

        })
    }


    fun StartNegocio(nomNegocio: String, idHardware: String, idNegocio: String, ipNegocio: String, iPuertoNegocio: String, iPLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
                    val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> = negociosService.getEnciendeEquipo("$idHardware", "JWT $token", "application/json")
                    resultEnciendeEquipo.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
                        override fun onResponse(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                            response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>
                        ) {
                            val resultadoAccion = response.body()
                            if (resultadoAccion != null) {
                                var respuesta = resultadoAccion.data.toString()
                                Toast.makeText(this@MainActivity, "$respuesta", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(
                            call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>,
                            t: Throwable
                        ) {
                            var desc = "No fue posible encender el equipo, por favor contacte a su administrador"
                            var dialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)
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

            }

            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                var desc = "No fue posible recuperar token de acceso en la accion de encendido de equipo"
                var dialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)
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




    fun ConsultaDinamicaBdNegocio(accion: com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List, idHardware: String, ipNegocio: String, iPuertoNegocio: Int, iNomNegocio: String, ipLocal: String, tipoComunicacion: Int){

        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            databaseHelper_Data.deleteinfoAccion()
            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = AlertDialog.Builder(this@MainActivity)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(requireContext(), "Error al recuperar token del negocio", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val resultAccion: Call<ResponseBody> = getTokenNegocio.getConsultaDinamicaBdSvr("${accion.origen}", "${accion.id}",
                            "${accion.id_negocio_id}", "${accion.tipo_id}", "$resultado", "application/json")

                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                                Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                val resultJson = response.body()!!.string()
                                if (resultJson.contains("INSERT_OK")) {

                                    Toast.makeText(this@MainActivity, "Se inserto registro(s) correctamente", Toast.LENGTH_LONG).show()

                                } else if (resultJson.contains("DELETE_OK")) {
                                    Toast.makeText(this@MainActivity, "Se eliminaron registro(s) correctamente", Toast.LENGTH_LONG).show()
                                } else if (resultJson.contains("UPDATE_OK")) {
                                    Toast.makeText(this@MainActivity, "Se actualizan registro(s) correctamente", Toast.LENGTH_LONG).show()
                                } else {

                                    var newInfoAccion =
                                        com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
                                            result = resultJson
                                        )
                                    databaseHelper_Data.addInfoAccion(newInfoAccion)
                                    val accountsIntent = Intent(this@MainActivity, ConsultaDinamicaActivity::class.java)
                                    accountsIntent.putExtra("titulo_consulta", "${accion.titulo_aplicacion}")
                                    accountsIntent.putExtra("nombre_negocio", "$iNomNegocio")
                                    startActivity(accountsIntent)
                                }


                            }


                        })

                    } else {
                        Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
                    }

                }

            })
        }else if(tipoComunicacion == 1){


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

                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo ="$idHardware"
                        LoginActivity.bd_parametros_accion = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                        var parametros = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL"
                        LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")


                    }else{
                        Toast.makeText(this@MainActivity, "Accion cancelada", Toast.LENGTH_SHORT).show()
                    }


                }
            }else{

                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo ="$idHardware"
                LoginActivity.bd_parametros_accion = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                var parametros = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL"
                LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")


            }




        }


    }




    //**************************

    fun mostrarDialogoTablaDeParametros(
        lista: java.util.ArrayList<ItemParametrosAccDB>,
        consulta: String,
        onComplete: (java.util.ArrayList<ItemParametrosAccDB>?) -> Unit
    ) {
        val recyclerView = RecyclerView(this@MainActivity).apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ParametrosAdapter(lista, consulta) { cadena, valor ->
                obtenerNombreDeCampoPorValor(cadena, valor) ?: valor
            }

            val divider = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.divider_line)?.let {
                divider.setDrawable(it)
            }
            addItemDecoration(divider)

        }

        val container = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * context.resources.displayMetrics.density).toInt() // 16dp
            setPadding(padding, 0, padding, 0)
        }

        val headerView = LayoutInflater.from(this@MainActivity).inflate(R.layout.item_parametro_header, container, false)
        container.addView(headerView)
        container.addView(recyclerView)

        val scrollView = ScrollView(this@MainActivity)
        scrollView.addView(container)

        val dialog = AlertDialog.Builder(this@MainActivity)
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
                        Toast.makeText(this@MainActivity, "Falta valor para ${item.campo}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    when (item.tipo?.uppercase()) {
                        "INTEGER" -> if (valor.toIntOrNull() == null) {
                            Toast.makeText(this@MainActivity, "${item.campo} debe ser número entero", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "DECIMAL" -> if (valor.toDoubleOrNull() == null) {
                            Toast.makeText(this@MainActivity, "${item.campo} debe ser número decimal", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "TEXT" -> {} // Nada extra
                        else -> {
                            Toast.makeText(this@MainActivity, "Tipo no reconocido: ${item.tipo}", Toast.LENGTH_SHORT).show()
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





    fun ConsultaApiExtNegocio(accion: com.apptomatico.app_movil_kotlin_v3.model.Favoritos_List, idHardware: String, ipNegocio: String, iPuertoNegocio: Int, iNomNegocio: String, ipLocal: String, tipoComunicacion: Int){

        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        if(tipoComunicacion == 1){

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

                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo ="$idHardware"
                        LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                        var parametros = "${accion.id}|$resParams"
                        LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")

                    }else{
                        Toast.makeText(this@MainActivity, "Accion cancelada", Toast.LENGTH_SHORT).show()
                    }



                }
            }else{

                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo ="$idHardware"
                LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                var parametros = "${accion.id}|$resParams"
                LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")



            }


        }
    }


    fun estatusActNegocio(id_accion: Int, id_origen: Int, NomNegocio: String, hardware_key: String, tipocomunicacion: Int){
        LoginActivity.isReloadViewMonitores = 0
        if(tipocomunicacion == 0){



        }else if(tipocomunicacion == 1){
            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo =  removeTrailingZeroes(idDispositivo)


            LoginActivity.estatusequipo_id_accion = id_accion
            LoginActivity.estatusequipo_id_origen = id_origen
            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$hardware_key\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getMonitoreoEquipo\",\"parametros\":\"ALL\"}")


        }

    }


    fun ExplorarDirectorio(ruta: String, idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int, ipLocal: String, tipocomunicacion: Int){
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil


        if(tipocomunicacion == 0){
            val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                "licencias@dsiin.com",
                "xhceibEd"
            )

            val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
            val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
            result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "No fue posible conectar con el servidor remoto, intentelo mas tarde", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                    val resultado = response.body()
                    if (resultado != null) {
                        val token = resultado.token.toString()

                        val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
                        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                        resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                                Toast.makeText(this@MainActivity, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>) {
                                val resHorariosmv = response.body()
                                if (resHorariosmv != null) {
                                    val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> = resHorariosmv.data.toList()
                                    val smDateFormat = SimpleDateFormat("HH:mm:ss")
                                    val currenTime: String = smDateFormat.format(Date())
                                    val horaInicial = horario[0].horario_desde
                                    var horaFinal = horario[0].horario_hasta
                                    val dia_semana = horario[0].dia_semana
                                    if (horaFinal == "00:00:00"){
                                        horaFinal = "23:59:59"
                                    }

                                    if (horaInicial != "SN" && horaFinal != "SN") {
                                        if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1) {
                                            /*
                                            val accountsIntent = Intent(requireContext(), activity_directorio_negocios::class.java)
                                            accountsIntent.putExtra("nom_negocio", nomNegocio)
                                            accountsIntent.putExtra("ip_negocio", ipNegocio)
                                            accountsIntent.putExtra("ip_local", ipLocal)
                                            accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                            accountsIntent.putExtra("id_hardware", idHardware)
                                            accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                            accountsIntent.putExtra("phoneMovil", phoneMovil)
                                            accountsIntent.putExtra("rutaArchivosDir", ruta)
                                            accountsIntent.putExtra("phoneSubscriberId", phoneSubscriberId)
                                            accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                            requireContext().startActivity(accountsIntent)

                                             */
                                        } else {

                                            Toast.makeText(this@MainActivity, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                        }
                                    } else {

                                        /*
                                        val accountsIntent = Intent(requireContext(), activity_directorio_negocios::class.java)
                                        accountsIntent.putExtra("nom_negocio", nomNegocio)
                                        accountsIntent.putExtra("ip_negocio", ipNegocio)
                                        accountsIntent.putExtra("ip_local", ipLocal)
                                        accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                        accountsIntent.putExtra("id_hardware", idHardware)
                                        accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                        accountsIntent.putExtra("phoneMovil", phoneMovil)
                                        accountsIntent.putExtra("rutaArchivosDir", ruta)
                                        accountsIntent.putExtra("phoneSubscriberId", phoneSubscriberId)
                                        accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                        requireContext().startActivity(accountsIntent)

                                         */
                                    }


                                } else {
                                    /*
                                    val accountsIntent = Intent(requireContext(), activity_directorio_negocios::class.java)
                                    accountsIntent.putExtra("nom_negocio", nomNegocio)
                                    accountsIntent.putExtra("ip_negocio", ipNegocio)
                                    accountsIntent.putExtra("ip_local", ipLocal)
                                    accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                    accountsIntent.putExtra("id_hardware", idHardware)
                                    accountsIntent.putExtra("id_dispositivo", idDispositivo)
                                    accountsIntent.putExtra("phoneMovil", phoneMovil)
                                    accountsIntent.putExtra("rutaArchivosDir", ruta)
                                    accountsIntent.putExtra("phoneSubscriberId", phoneSubscriberId)
                                    accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                    requireContext().startActivity(accountsIntent)

                                     */
                                }
                                ///////////////
                            }

                        })


                    }
                }

            })

        }else if(tipocomunicacion == 1){
            var rutaArchivosDir = ruta
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace("\\\\","\\")
                rutaArchivosDir = rutaArchivosDir!!.replace("\\","\\\\")
                rutaArchivosDir  = rutaArchivosDir!!.replace("\\", "%2F")
            }
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace(":", "%3A")
            }

            LoginActivity.idHardwareExpArc = idHardware
            LoginActivity.nomEquipoExpArc = nomNegocio

            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getArchivosEquipo\",\"parametros\":\"$rutaArchivosDir\"}")

        }




        /*

         */




    }


    fun ScreenshotServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int, iBovedaNegocios: String, ipLocal: String,tipocomunicacion: Int){

        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this@MainActivity)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil


        var configApiVMEquipo = databaseHelper_Data.getControlEquiposAPIVM(idNegocio)


        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error servicio no disponible 8", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
                    val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                    resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>) {
                            val resHorariosmv = response.body()
                            if (resHorariosmv != null) {
                                val horario: List<com.apptomatico.app_movil_kotlin_v3.model.RegHorarios_Movil> = resHorariosmv.data.toList()
                                val smDateFormat = SimpleDateFormat("HH:mm:ss")
                                val currenTime: String = smDateFormat.format(Date())
                                val horaInicial = horario[0].horario_desde
                                var horaFinal = horario[0].horario_hasta
                                val dia_semana = horario[0].dia_semana
                                if (horaFinal == "00:00:00"){
                                    horaFinal = "23:59:59"
                                }
                                if (horaInicial != "SN" && horaFinal != "SN") {
                                    if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1) {

                                        if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){


                                            ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token)


                                        }else{

                                            if(tipocomunicacion == 0){

                                                /*
                                                val accountsIntent = Intent(requireContext(), ImageViewActivity::class.java)
                                                accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                                accountsIntent.putExtra("id_hardware", idHardware)
                                                accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                                accountsIntent.putExtra("ip_negocio", ipNegocio)
                                                accountsIntent.putExtra("ip_local", ipLocal)
                                                accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                                accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                                requireContext().startActivity(accountsIntent)

                                                 */
                                            }else{

                                                LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")



                                            }
                                        }


                                    } else {

                                        Toast.makeText(this@MainActivity, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                    }
                                } else {

                                    if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){


                                        ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token)

                                    }else{

                                        if(tipocomunicacion == 0){
                                            /*
                                            val accountsIntent = Intent(requireContext(), ImageViewActivity::class.java)
                                            accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                            accountsIntent.putExtra("id_hardware", idHardware)
                                            accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                            accountsIntent.putExtra("ip_negocio", ipNegocio)
                                            accountsIntent.putExtra("ip_local", ipLocal)
                                            accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                            accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                            requireContext().startActivity(accountsIntent)

                                             */
                                        }else{


                                            LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")


                                        }
                                    }


                                }


                            } else {

                                if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){


                                    ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token)




                                }else{
                                    if(tipocomunicacion == 0){
                                        /*
                                        val accountsIntent = Intent(requireContext(), ImageViewActivity::class.java)
                                        accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                        accountsIntent.putExtra("id_hardware", idHardware)
                                        accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                        accountsIntent.putExtra("ip_negocio", ipNegocio)
                                        accountsIntent.putExtra("ip_local", ipLocal)
                                        accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                        accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                        requireContext().startActivity(accountsIntent)

                                         */

                                    }else{

                                        LoginActivity.webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")

                                    }
                                }
                            }
                            ///////////////
                        }

                    })


                } else {
                    Toast.makeText(this@MainActivity, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
                }
            }

        })





    }


    fun ScreenshotServidorXAPI(idHardware: String, idNegocio: Int, nomNegocio: String ,tipocomunicacion: Int, token: String){


        val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
        val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> = negociosService.getScreenshotEquipoGcVm("$idHardware", "JWT $token", "application/json")
        resultEnciendeEquipo.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>) {
                val resultadoAccion = response.body()
                if (resultadoAccion != null) {
                    var respuesta = resultadoAccion.data.toString()

                    /*
                    val accountsIntent = Intent(requireContext(), ImageViewActivity::class.java)
                    accountsIntent.putExtra("bovedaSoftware", "")
                    accountsIntent.putExtra("id_hardware", idHardware)
                    accountsIntent.putExtra("ip_puerto", "")
                    accountsIntent.putExtra("ip_negocio", "")
                    accountsIntent.putExtra("ip_local", "")
                    accountsIntent.putExtra("nombre_negocio", nomNegocio)
                    accountsIntent.putExtra("tipo_comunicacion", "1")
                    accountsIntent.putExtra("url_imagen", "$respuesta")
                    requireContext().startActivity(accountsIntent)

                     */



                }
            }

            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>, t: Throwable) {
                var desc = "No fue posible generar screenshot del equipo, por favor contacte a su administrador"
                var dialogBuilder = android.app.AlertDialog.Builder(this@MainActivity)
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








//*********************************************************


    private fun notification(){
        var token_notificacion = ""
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token_notificacion = task.result.toString()
            // Log and toast
            Log.i("UPDTOKENFMC","$token_notificacion" )
            val userService: APIService = RestEngine.getRestEngineInicial(databaseHelper_Data, this@MainActivity).create(APIService::class.java)
            val result_tokenfmc: Call<ResponseBody> = userService.setActTokenFMCMovil(idDispositivo, "$token_notificacion", "JWT ${BuildConfig.TOKEN_MAESTRO}", "application/json")
            result_tokenfmc.enqueue(object :  Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful && response.code() == 200) {
                        Log.e("UPDTOKENFMC", "Se actualizo token de notificacion")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("UPDTOKENFMC", t.message.toString())
                }

            })

        })
    }





}
