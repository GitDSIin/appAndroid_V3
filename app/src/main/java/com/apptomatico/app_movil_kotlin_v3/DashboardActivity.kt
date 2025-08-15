package com.apptomatico.app_movil_kotlin_v3

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.WebSocketServerConn
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.webSocketService
import com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosEjecucion
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import net.sqlcipher.database.SQLiteDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity: Fragment() {

    lateinit var rootView: View
    var idnegocio = ""
    var ipPublica = ""
    var idHardware = ""
    var iPuerto = ""
    var bovedaSoftware = ""
    var iNombrenegocio = ""
    var iEstiloDasboard = ""
    var ipLocal = ""

    private lateinit var databaseHelper_Data: DatabaseHelper
    lateinit var progressDashboardCenter: ProgressBar
    private lateinit var appBarConfiguration: AppBarConfiguration
    var arrUsuariosPermitidos: MutableList<String>  = java.util.ArrayList()


    companion object{
        var iAlertDialogScDash: androidx.appcompat.app.AlertDialog? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        try{
            rootView =  inflater.inflate(R.layout.activity_dashboard, container, false)
            databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
            val btnModalAllDashboardReturn = rootView.findViewById<View>(R.id.btnModalAllDashboardReturn) as ImageView
            val reg = SharedApp.prefs.idNegocioF.toString().split("|")
            progressDashboardCenter = rootView.findViewById(R.id.progressDashboardCenter)
            progressDashboardCenter.visibility = View.GONE

            val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar)


            idnegocio = reg[0]
            ipPublica = reg[1]
            idHardware = reg[2]
            iPuerto = reg[3]
            bovedaSoftware = reg[4]
            iNombrenegocio = reg[5]
            iEstiloDasboard = reg[6]
            ipLocal = reg[7]


            with(rootView){

                navBar.visibility = View.GONE
                setupWebSocketService()

                val estDasC: ImageView = findViewById(R.id.idEstiloDashbpoardC)
                estDasC.setOnClickListener {

                    /*
                    val testFragment = HomeFragment()
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.MenuDashView, testFragment).commitNow()
                    testFragment.cambiaLayoutToGrid()

                     */

                }
                val estDasL: ImageView = findViewById(R.id.idEstiloDashbpoardL)
                estDasL.setOnClickListener {

                    /*
                    val testFragment = HomeFragment()
                    val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.MenuDashView, testFragment).commitNow()
                    testFragment.cambiaLayoutToList()

                     */
                }

                btnModalAllDashboardReturn.setOnClickListener {

                    when(LoginActivity.origen_back_dashboard){

                        0 -> findNavController().navigate(R.id.first_fragment)
                        1 -> findNavController().navigate(R.id.fourth_fragment)

                    }



                }


                requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
                    val selectedItemId: Int = requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar).selectedItemId
                    findNavController().navigateUp()
                    if (selectedItemId == 0){
                        findNavController().navigate(R.id.first_fragment)
                    }

                    if(selectedItemId == 1){
                        findNavController().navigate(R.id.second_fragment)
                    }

                    if(selectedItemId == 2){
                        findNavController().navigate(R.id.third_fragment)
                    }

                    if(selectedItemId == 3){
                        findNavController().navigate(R.id.fourth_fragment)
                    }
                }

            }

            return rootView


        }catch (e: java.lang.Exception) {
            Log.e("ERRORFRAGMENT", "onCreateView", e)
            throw e
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupWebSocketService() {
        var dominio_actual = ""
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(requireContext())
        var existeLic = databaseHelper_Data.getAllLicencia()
        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        var entornoAct = databaseHelper_Data.getControlEntornoConexion(requireContext())
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }

        WebSocketServerConn(
            databaseHelper_Data,
            requireActivity(),
            null,
            null,
            webSocketService,
            dominio_actual,
            idDispositivo
        ).observeConnection()

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

    public fun DisableProgressBarDasboard(){
        try{
            progressDashboardCenter.visibility = View.GONE
        }catch (exception: Exception){
            return
        }

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
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
                    dialogBuilder.setMessage("$msgUsuario al equipo, si toma una captura de pantalla en sesiones desconectadas es probable que visualice la imagen con un fondo negro. ¿Desea continuar?")
                        .setCancelable(false)
                        .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                        .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->

                            var ibluider = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            ibluider.setTitle("Recuperando Screenshot")
                            ibluider.setMessage("Espere un momento...").setCancelable(false)

                            iAlertDialogScDash = ibluider.create()
                            iAlertDialogScDash!!.show()

                            var  ButtonNt = iAlertDialogScDash!!.getButton(
                                AlertDialog.BUTTON_NEUTRAL);
                            ButtonNt.visibility = View.INVISIBLE

                            Handler().postDelayed(
                                Runnable { ButtonNt.visibility = View.VISIBLE },
                                15000
                            )
                            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getScreenShot\",\"parametros\":\"ALL|Todos\"}")

                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("$NomNegocio")
                    alert.show()

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
                    var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
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
                    alert.show()
                }


            }
        }else{
            var dialogBuilder = android.app.AlertDialog.Builder(requireContext())
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
            alert.show()
        }



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

                                NegocioFragment.iAlertDialogSc = iBuilder.create()
                                NegocioFragment.iAlertDialogSc!!.show()
                                NegocioFragment.iAlertDialogSc!!.getButton(AlertDialog.BUTTON_NEUTRAL).visibility = View.INVISIBLE

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

    fun getSKRespuetaExploradorARchivos(respuesta: String){
        Toast.makeText(requireContext(), "$respuesta", Toast.LENGTH_LONG).show()
    }


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
           // 8 ->  findNavController().navigate(R.id.eight_fragment)
           // 9 ->  findNavController().navigate(R.id.nine_fragment)
        }

    }





}