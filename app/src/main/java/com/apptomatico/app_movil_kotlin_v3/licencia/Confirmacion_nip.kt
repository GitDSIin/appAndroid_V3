package com.apptomatico.app_movil_kotlin_v3.licencia

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.widget.NestedScrollView
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.helpers.InputValidation
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.loadingDialog
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.sqlcipher.database.SQLiteDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.text.count
import kotlin.text.substring

class Confirmacion_nip: AppCompatActivity(), View.OnClickListener {


    private val activity = this@Confirmacion_nip
    val client by lazy { APIService.create() }
    private lateinit var nestedScrollViewNip: NestedScrollView

    private lateinit var textInputLayoutEmail: TextInputLayout
    private lateinit var textInputLayoutPassword: TextInputLayout

    private lateinit var textInputEditTextEmail: TextInputEditText
    private lateinit var textInputEditTextPassword: TextInputEditText

    private lateinit var appCompatButtonLogin: Button

    private lateinit var textViewLinkRegister: AppCompatTextView

    private lateinit var inputValidation: InputValidation
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var  txtUltVersion: TextView
    var valNIP: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirma_nip)
        supportActionBar!!.hide()

        // initializing the views
        initViews()

        // initializing the listeners
        initListeners()

        // initializing the objects
        initObjects()

        txtUltVersion.text = "Version ${BuildConfig.VERSION_NAME}"

        valNIP = intent.getStringExtra("NIP")



    }


    private fun initViews() {

        nestedScrollViewNip = findViewById<View>(R.id.nestedScrollViewNipSc) as NestedScrollView


        textInputLayoutPassword = findViewById<View>(R.id.textInputLayoutPasswordSc) as TextInputLayout


        textInputEditTextPassword = findViewById<View>(R.id.textInputEditTextPasswordSc) as TextInputEditText

        appCompatButtonLogin = findViewById<View>(R.id.appCompatButtonLoginSc) as Button

        txtUltVersion = findViewById<View>(R.id.txtUltVersionConfNIP) as TextView



    }

    private fun initListeners() {

        appCompatButtonLogin.setOnClickListener(this)
        // textViewLinkRegister.setOnClickListener(this)
    }

    private fun initObjects() {

        databaseHelper = DatabaseHelper.getInstance(activity)
        inputValidation = InputValidation(activity)

    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.appCompatButtonLoginSc -> verifyFromSQLite()

        }
    }


    private fun verifyFromSQLite() {


        val nipConfirmacion = textInputEditTextPassword.text.toString().trim()

        if (nipConfirmacion != valNIP){
            var dialogBuilder = AlertDialog.Builder(this@Confirmacion_nip)
            dialogBuilder.setCancelable(false)
            dialogBuilder.setMessage("EL NIP que ha introducido no es correcto")
                .setCancelable(false)
                .setNeutralButton("Aceptar", DialogInterface.OnClickListener { view, _ ->


                    val intent = Intent(this@Confirmacion_nip, alta_nip::class.java)
                    startActivity(intent)
                    view.dismiss()
                    finish()
                    return@OnClickListener
                })

            var alert = dialogBuilder.create()
            alert.setTitle("")
            alert.show()



        }else {
            confirmacionNIPExitosa()
        }







    }

    private fun confirmacionNIPExitosa(){
        var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val macMovil: String = getMac(this.applicationContext).toString()
        val phoneMovil:String = getMovilData(this.applicationContext).toString()
        val idSIMMovil:String =  getSIMMovil(this.applicationContext).toString()
        val imeiMovil:String = getIMEIMovil(this.applicationContext).toString()

        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(this)
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper.getAllLicencia()
        var nombre_Movil = Licenciadb[0].nommbre_movil


        val nipConfirmacion = textInputEditTextPassword.text.toString().trim()

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper, this@Confirmacion_nip).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        val loadingDialog = loadingDialog(this@Confirmacion_nip)
        loadingDialog.startLoadingDialog()

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                loadingDialog.dismisDialog()
                Toast.makeText(this@Confirmacion_nip, "No es posible conectar con servidor remoto, contacte a su administrador ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultadotoken= response.body()

                if (resultadotoken != null) {
                    val token = resultadotoken.token.toString()
                    SharedApp.prefs.token = resultadotoken.token.toString()

                    val userValNip: APIService = RestEngine.getRestEngine(databaseHelper, this@Confirmacion_nip).create(APIService::class.java)
                    val resultNip: Call<com.apptomatico.app_movil_kotlin_v3.model.ValNip> = userValNip.newNip("$nipConfirmacion","$idDispositivo","JWT $token", "application/json")
                    resultNip.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.ValNip> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.ValNip>, t: Throwable) {
                            loadingDialog.dismisDialog()
                            Toast.makeText(this@Confirmacion_nip, "No fue posible registrar el NIP", Toast.LENGTH_LONG).show()
                            return
                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.ValNip>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.ValNip>) {
                            val resultado= response.body()
                            if (resultado == null) {

                                client.newAlerta("N/A", "$idDispositivo", "$phoneMovil","$macMovil", "$imeiMovil","Acceso de ingreso fallido desde dispositivo movil", "Se registro un  intento de ingreso fallido a la aplicacion android desde un  dispositivo movil, nip no valido","$nombre_Movil","JWT $token","application/json")
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ Toast.makeText(this@Confirmacion_nip, "Dispositivo no Autorizado", Toast.LENGTH_LONG).show() }, { throwable ->

                                        Toast.makeText(this@Confirmacion_nip, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                                    }
                                    )
                                loadingDialog.dismisDialog()
                                Toast.makeText(this@Confirmacion_nip, "El Nip no es valido", Toast.LENGTH_LONG).show()
                                return
                            }else{
                                var newSeguridad =
                                    com.apptomatico.app_movil_kotlin_v3.model.Seguridad(
                                        nip = "$nipConfirmacion"
                                    )
                                databaseHelper.addSeguridad(newSeguridad)


                                loadingDialog.dismisDialog()
                                val accountsIntent = Intent(activity, LoginActivity::class.java)
                                startActivity(accountsIntent)
                                //Toast.makeText(this@alta_nip, "Confirma Nip", Toast.LENGTH_LONG).show()
                                return
                            }


                        }

                    })


                }



            }

        })
        //}


    }

    fun getMac(context: Context): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return info.macAddress.toUpperCase()
    }


    fun getMovilData(context: Context):String{
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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


    fun getSIMMovil(context: Context):String{
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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

    fun getIMEIMovil(context:Context):String{
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
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


}