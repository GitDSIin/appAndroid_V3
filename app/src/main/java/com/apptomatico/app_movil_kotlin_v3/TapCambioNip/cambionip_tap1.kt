package com.apptomatico.app_movil_kotlin_v3.TapCambioNip

import android.content.Context
import android.content.DialogInterface
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXFade
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class cambionip_tap1: Fragment() {

    private var vwpopupModalConfiguracion: View? = null
    private var tbNipAnterior: TextInputEditText? = null
    private var tbNuevoNip: TextInputEditText? = null
    private var tbConfirmaNuevoNip: TextInputEditText? = null
    private var btnUpdNip: Button? = null
    private var btnCancelarUpddNip: Button? = null
    private var btnRecuperaNIP: TextView? = null
    private lateinit var textInputLayoutOldNip: TextInputLayout
    private lateinit var textInputLayoutNewNip: TextInputLayout
    private lateinit var textInputLayoutConfirNip: TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_configuracion, container, false)
        val databaseHelper_Data = DatabaseHelper.getInstance(container!!.context)

        tbNipAnterior =  view.findViewById(R.id.textInputEditNipAnterior) as TextInputEditText
        tbNuevoNip =  view.findViewById(R.id.textInputEditNuevoNip) as TextInputEditText
        tbConfirmaNuevoNip =  view.findViewById(R.id.textInputEditConfirmaNuevoNip) as TextInputEditText
        btnUpdNip =  view.findViewById(R.id.appCompatButtonUpdNip) as Button
        btnCancelarUpddNip =  view.findViewById(R.id.appCompatButtonCancelarUpdNip) as Button
        btnRecuperaNIP = view.findViewById(R.id.btnRecuperaNIP) as TextView

        textInputLayoutOldNip =  view.findViewById<View>(R.id.textInputLayoutOldNip) as TextInputLayout
        textInputLayoutNewNip =  view.findViewById<View>(R.id.textInputLayoutNewNip) as TextInputLayout
        textInputLayoutConfirNip =  view.findViewById<View>(R.id.textInputLayoutConfNip) as TextInputLayout


        btnRecuperaNIP!!.paintFlags = btnRecuperaNIP!!.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        //////
        btnUpdNip!!.setOnClickListener{
            btnUpdNip!!.animationXFade(Fade.FADE_IN_DOWN)

            if(!checkForInternet(requireContext())){

                Toast.makeText(context, "Es necesario una conexión a internet para realizar esta acción", Toast.LENGTH_LONG).show()

            }else{
                val lgMin = databaseHelper_Data.getLongitudMinNIP()
                val lgMax = databaseHelper_Data.getLongitudMaxNIP()

                if(tbNipAnterior!!.text.toString() == ""){
                    Toast.makeText(context, "Ingrese el NIP Actual", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }


                if(tbNuevoNip!!.text.toString() == ""){
                    Toast.makeText(context, "Nuevo NIP: Ingrese un valor", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(tbConfirmaNuevoNip!!.text.toString() == ""){
                    Toast.makeText(context, "Confirmacion de NIP: Ingrese un valor", Toast.LENGTH_LONG).show()
                    return@setOnClickListener

                }

                if(tbNuevoNip!!.text!!.length < lgMin){
                    Toast.makeText(context, "Nuevo NIP: debe ser de al menos de $lgMin dígitos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener

                }

                if(tbConfirmaNuevoNip!!.text!!.length < lgMin){
                    Toast.makeText(context, "Confirmacion de NIP: debe ser de al menos de $lgMin dígitos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }


                if(tbNuevoNip!!.text!!.length > lgMax){
                    Toast.makeText(context, "Nuevo NIP: debe ser tener maximo  $lgMax dígitos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(tbConfirmaNuevoNip!!.text!!.length > lgMax){
                    Toast.makeText(context, "Confirmacion de NIP: debe ser tener maximo  $lgMax dígitos", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(!databaseHelper_Data.checkSeguridad(tbNipAnterior!!.text.toString())){
                    Toast.makeText(context, "EL NIP actual no es valido", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                if(tbConfirmaNuevoNip!!.text.toString() != tbNuevoNip!!.text.toString()){
                    Toast.makeText(context, "El nuevo NIP no coincide con la confirmación de NIP", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }


                updNipMovil(tbNuevoNip!!.text.toString(),databaseHelper_Data, container!!.context)
                Toast.makeText(context, "Se actualizo NIP de forma correcta", Toast.LENGTH_LONG).show()

                activity?.finish()


            }



        }
        btnRecuperaNIP!!.setOnClickListener {

            if(!checkForInternet(requireContext())){

                Toast.makeText(context, "Es necesario una conexión a internet para realizar esta acción", Toast.LENGTH_LONG).show()

            }else{
                btnRecuperaNIP!!.animationXFade(Fade.FADE_IN_DOWN)
                recuperaNipMovil(databaseHelper_Data)
            }





        }
        btnCancelarUpddNip!!.setOnClickListener{
            btnCancelarUpddNip!!.animationXFade(Fade.FADE_IN_DOWN)
            activity?.finish()
        }




        return  view
    }






    private fun recuperaNipMovil(databaseHelper_Data: DatabaseHelper){
        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, requireContext()).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible 2", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context!!).create(APIService::class.java)
                    val resultUpdNip: Call<ResponseBody> = negociosService.recuperaNipMovil("$idDispositivo", "JWT $token", "application/json")

                    resultUpdNip.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {


                            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(context!!)

                            dialogBuilder.setMessage("Se envió un correo a la dirección relacionada al dispositivo, por favor revise su bandeja de entrada")
                                .setCancelable(false)
                                .setPositiveButton("Aceptar", DialogInterface.OnClickListener{
                                        dialog, id ->(

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





    private fun updNipMovil(nuevoNip: String, databaseHelper_Data: DatabaseHelper, context: Context){
        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()

        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible 2", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context!!).create(APIService::class.java)
                    val resultUpdNip: Call<ResponseBody> = negociosService.getActualizaNIP("$nuevoNip", "$idDispositivo", "JWT $token", "application/json")

                    resultUpdNip.enqueue(object : Callback<ResponseBody> {
                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                            databaseHelper_Data.updNIP(tbNuevoNip!!.text.toString(), tbNipAnterior!!.text.toString())
                            tbNipAnterior!!.setText("")
                            tbNuevoNip!!.setText("")
                            tbConfirmaNuevoNip!!.setText("")
                            Toast.makeText(context, "Se actualizo el NIP de forma correcta", Toast.LENGTH_LONG).show()

                        }

                    })


                }
            }

        })

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





}