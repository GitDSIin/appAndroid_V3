package com.apptomatico.app_movil_kotlin_v3.licencia


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.licencia.alta_nip
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.helpers.InputValidation
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.loadingDialog
import com.apptomatico.app_movil_kotlin_v3.model.*
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.sqlcipher.database.SQLiteDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class LicenciaActivity : Activity(), View.OnClickListener {

    private val activity =  this@LicenciaActivity
    private val permission = 101
    var token_notificacion: String = ""
    var intentos: List<RegLicenacia_List> = ArrayList()
    private lateinit var nestedScrollView: NestedScrollView
    private lateinit var textInputLicencia: TextInputEditText
    private lateinit var itextInputAuxNumerPhone: TextInputEditText
    private lateinit var appCompatButtonValLic: Button
    private lateinit var appQAConsulta: AppCompatButton
    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var  txtUltVersion: TextView



    val client by lazy { APIService.create() }

    val VersionSoftware = BuildConfig.VERSION_NAME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SQLiteDatabase.loadLibs(this)
        setContentView(R.layout.activity_licencia)

        initViews()
        initListeners()
        initObjects()

        txtUltVersion.text = "Version ${BuildConfig.VERSION_NAME}"

        val ALL_PERMISSIONS = 101

        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

        ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSIONS)


        var gpath: String = Environment.getExternalStorageDirectory().absolutePath
        var spath = "Download/AppTomatiza/app_android.apk"
        var fullpath = gpath + File.separator + spath

        deleteFiles(fullpath)

        valNoIntentosLic()


    }


    fun deleteFiles(folder: String?) {
        try {
            val dir = File(folder)
            if (!dir.exists()) return

            if (!dir.isDirectory) {
                val nombre = dir.name
                if (nombre.contains(".apk")) {
                    dir.delete()

                }


            }
        } catch (e: Exception) {


        }
    }







    private fun initViews() {

        nestedScrollView = findViewById<View>(R.id.nestedScrollViewLicencia) as NestedScrollView

        textInputLicencia = findViewById<View>(R.id.textInputLicencia) as TextInputEditText

        itextInputAuxNumerPhone = findViewById<View>(R.id.textInputAuxNumerPhone) as TextInputEditText

        appCompatButtonValLic = findViewById<View>(R.id.appCompatButtonValLic) as Button

        txtUltVersion = findViewById<View>(R.id.txtUltVersionLicencia) as TextView



    }

    /**
     * This method is to initialize listeners
     */
    private fun initListeners() {

        appCompatButtonValLic.setOnClickListener(this)


        // textViewLinkRegister.setOnClickListener(this)
    }
    private fun initObjects() {

        databaseHelper = DatabaseHelper.getInstance(activity)


    }


    override fun onStart() {
        super.onStart()

        validaExisteLicencia()


    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.appCompatButtonValLic -> verifyFromSQLite()

            }
        }
    }

    private fun verifyQA() {
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData(this.applicationContext).toString()

        Toast.makeText( this@LicenciaActivity, "ID: $idDispositivo , telefono: $phoneMovil", Toast.LENGTH_LONG).show()
    }

    private fun validaExisteLicencia(){
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        if (databaseHelper.checkLicencia(idDispositivo)){

            val accountsIntent = Intent(activity, LoginActivity::class.java)
            startActivity(accountsIntent)

            return
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


    private fun notification(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                token_notificacion = ""

                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token_notificacion = task.result.toString()

            // Log and toast
            println("Este es el token del dispositivo: $token_notificacion" )
        })
    }

    private fun appDesistalar(){
        val packageURI: Uri = Uri.parse("package:" + " com.apptomatico.app_movil_kotlin_v3")
        val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
        startActivity(uninstallIntent)
    }


    private fun verifyFromSQLite() {
        if (!checkForInternet( this@LicenciaActivity)) {
            var desc = "Conéctese a Internet, revise la configuracion de su red"
            var dialogBuilder = android.app.AlertDialog.Builder( this@LicenciaActivity)
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

        if (intentos.count() > 0) {
            if (intentos[0].intentos_fallidos > intentos[0].intentos_permitidos) {
                this.applicationContext.deleteDatabase("UserManager.db")
                showAlertDialog()
                appDesistalar()

                return
            }
        }
        notification()
        val movilVersion = "Android ${Build.VERSION.RELEASE}"
        val UserData =  com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val macMovil: String = getMac(this.applicationContext).toString()
        var phoneMovil: String = getMovilData(this.applicationContext).toString()
        val phoneSubscriberId = getMovilIdSuscriber(this.applicationContext)
        val idSIMMovil: String = getSIMMovil(this.applicationContext).toString()
        val imeiMovil: String = getIMEIMovil(this.applicationContext).toString()

        val userService: APIService = RestEngine.getRestEngineInicial(databaseHelper,  this@LicenciaActivity).create(APIService::class.java)
        val result: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)


        if (phoneMovil == "") {
            if (itextInputAuxNumerPhone.isVisible){
                phoneMovil = itextInputAuxNumerPhone.text.toString()
            }else{
                Toast.makeText( this@LicenciaActivity, "Ingrese un numero de telefono valido", Toast.LENGTH_LONG).show()
                itextInputAuxNumerPhone.visibility = View.VISIBLE
                return
            }


        }







        val loadingDialog = loadingDialog(this@LicenciaActivity)
        loadingDialog.startLoadingDialog()

        result .enqueue(object : Callback< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {

                loadingDialog.dismisDialog()

                Toast.makeText( this@LicenciaActivity, "No es posible conectar con el servidor remoto, intentelo mas tarde ${t.message}", Toast.LENGTH_LONG).show()
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onResponse(call: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {

                val resultado= response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    SharedApp.prefs.token = resultado.token.toString()



                    val getValLicencias: APIService = RestEngine.getRestEngineInicial(databaseHelper,  this@LicenciaActivity).create(APIService::class.java)
                    val result_subs: Call< com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil> = getValLicencias.getValLicencias(textInputLicencia.text.toString().trim(), "$idDispositivo", "$macMovil", "$phoneMovil", "$phoneSubscriberId","$token_notificacion", "$movilVersion","JWT $token", "application/json")

                    result_subs.enqueue(object : Callback< com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil> {
                        override fun onFailure(call: Call< com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil>, t: Throwable)
                        {
                            loadingDialog.dismisDialog()
                            println("Error  : " + t.stackTrace.toString())
                            println("Error  : " + t.message)
                            Toast.makeText( this@LicenciaActivity, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call< com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil>, response: Response< com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil>) {
                            val resultado= response.body()

                            if (resultado != null) {
                                val db: List< com.apptomatico.app_movil_kotlin_v3.model.ModelLicenciaMovil> =  resultado.data.toList()

                                var newLicencia =
                                     com.apptomatico.app_movil_kotlin_v3.model.Licencia(
                                        hardware_key = db[0].hardware_key,
                                        licencia_aceptacion = db[0].licencia_aceptacion,
                                        id_movil = db[0].movil_id,
                                        nommbre_movil = db[0].nom_movil,
                                        nombre_dominio = db[0].dominio_portal,
                                        version_software = VersionSoftware

                                    )

                                var ftpEnvioBackup = db[0].ftpbackup
                                var reg = ftpEnvioBackup.split("|")

                                val ftpserver  = reg[0]
                                val ftpuser = reg[1]
                                val ftppwd  = reg[2]
                                val ftppuerto  = reg[3]

                                databaseHelper.addLongitudNip(db[0].movil_longitud_min_nip, db[0].movil_longitud_max_nip)
                                databaseHelper.addLicencia(newLicencia)
                                databaseHelper.addParametrosLicencia(db[0].requiere_ubicacion.toString())

                                databaseHelper.addControlSendBackupDB(ftpserver,ftpuser,ftppwd,ftppuerto)

                                databaseHelper.deleteConfiguracionMovil()
                                databaseHelper.addConfiguracionMovil(60,60,5)


                                loadingDialog.dismisDialog()
                                val accountsIntent = Intent( this@LicenciaActivity, alta_nip::class.java)
                                //accountsIntent.putExtra("EMAIL", "")
                                emptyInputEditText()
                                startActivity(accountsIntent)
                                //Toast.makeText( this@LicenciaActivity, "Por favor registre un NIP", Toast.LENGTH_LONG).show()
                                return
                            }else{
                                val til: TextInputLayout = findViewById<View>(R.id.textInputLayoutPassword) as TextInputLayout
                                til.setError("Licencia incorrecta por favor vuelva a intentar")

                                loadingDialog.dismisDialog()
                                client.newAlerta("N/A", "$idDispositivo", "$phoneMovil","$macMovil", "$imeiMovil","Validacion de licencia fallido en dispositivo movil", "Se registro un  intento fallido de validacion de licencia en dispositivo movil","SN","JWT $token","application/json")
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ Toast.makeText( this@LicenciaActivity, "La licencia no es valida, por favor contacte a su proveedor de licencias", Toast.LENGTH_LONG).show() }, { throwable ->
                                        Toast.makeText( this@LicenciaActivity, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                                    }
                                    )


                                val handler = Handler()
                                handler.postDelayed({
                                    SharedApp.prefs.vistaError = "1"
                                    val accountsIntent = Intent(activity, Error::class.java)
                                    startActivity(accountsIntent)
                                }, 10000)





                            }



                        }

                    })



                }else{


                    loadingDialog.dismisDialog()
                    Toast.makeText( this@LicenciaActivity, "EL Servicio no se encuentra disponible", Toast.LENGTH_LONG).show()
                }





            }

        })





    }


    private fun valNoIntentosLic(){
        if(!checkForInternet( this@LicenciaActivity)){
            return
        }
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val UserData =  com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngineInicial(databaseHelper,  this@LicenciaActivity).create(APIService::class.java)
        val result: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText( this@LicenciaActivity, "Error servicio no disponible ${t.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response< com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado= response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val valIntentosApp: APIService = RestEngine.getRestEngineInicial(databaseHelper,  this@LicenciaActivity).create(APIService::class.java)
                    val resultRegLic: Call< com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia> = valIntentosApp.getRegLicencias("$idDispositivo",0,"JWT $token", "application/json")

                    resultRegLic.enqueue(object : Callback< com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia> {
                        override fun onFailure(call: Call< com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>, t: Throwable) {
                            Toast.makeText( this@LicenciaActivity, "Error servicio no disponible (Validacion Intentos Licencia)", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call< com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>, response: Response< com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>) {
                            val resultado_val= response.body()

                            if (resultado_val != null) {
                                intentos = resultado_val.data.toList()



                                if (intentos[0].intentos_fallidos > intentos[0].intentos_permitidos){
                                     this@LicenciaActivity.deleteDatabase("UserManager.db")
                                    showAlertDialog()
                                    appDesistalar()

                                }


                            }
                        }

                    })



                }
            }

        })



    }

    fun showAlertDialog() {
        val dialogBuilder = AlertDialog.Builder( this@LicenciaActivity)
        dialogBuilder.setTitle("¡Aviso!")
        dialogBuilder.setMessage("Se ha superado la cantidad máxima de intentos fallidos permitidos, la apliacion dejo se ser funcional")
        dialogBuilder.setPositiveButton("Aceptar", null)

        val b = dialogBuilder.create()
        b.show()
    }



    fun getMac(context: Context): String {
        val manager = context.getSystemService(WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.macAddress.toUpperCase()


    }


    fun getMovilIdSuscriber(context:Context):String{
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }

        val telNumber =  "SN"
        if (telNumber != null){
            return telNumber
        }else{

            return "S/N"

        }


    }

    fun getMovilData(context: Context):String{
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
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

    fun getSIMMovil(context:Context):String{
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }
        try {

            val simID: String = tm.getSimSerialNumber()
            if (simID != null) {
                return  simID
            }else{
                return "S/N"
            }

        } catch (ex: Exception) {
            return "S/N"
        }

    }

    fun getIMEIMovil(context:Context):String{
        val tm = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return ""
        }

        try {

            val IMEI: String = tm.getDeviceId()
            if (IMEI != null) {
                return IMEI
            }else{
                return "S/N"
            }


        } catch (ex: Exception) {
            return "S/N"
        }






    }


    private fun emptyInputEditText() {
        textInputLicencia.text = null

    }


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


}