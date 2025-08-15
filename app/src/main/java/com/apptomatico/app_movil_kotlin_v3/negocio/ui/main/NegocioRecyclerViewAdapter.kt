package com.apptomatico.app_movil_kotlin_v3.negocio.ui.main


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.*
import android.provider.Settings
import android.telephony.TelephonyManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rommansabbir.animationx.Bounce
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXBounce
import com.rommansabbir.animationx.animationXFade
import com.squareup.picasso.Picasso
import com.tomergoldst.tooltips.ToolTipsManager
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.apptomatico.app_movil_kotlin_v3.BuildConfig

import com.apptomatico.app_movil_kotlin_v3.MainActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.idHardwareExpArc
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.origen_back_dashboard
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.valuesListEquipos
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.model.*
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment.Companion.valNegAccionesDinamicas
import com.apptomatico.app_movil_kotlin_v3.negocio.ParametrosAdapter
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import net.sqlcipher.database.SQLiteDatabase
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

//**
class NegocioRecyclerViewAdapter(val context: Context, private val contenido: String,
                                 private val base: String, private val container: View, private val xdpi: Float,private val webSocketService: EchoService?,private val nfragment: NegocioFragment): RecyclerView.Adapter<NegocioRecyclerViewAdapter.ViewHolder>(), ItemMoveCallbackNegocio.ItemTouchHelperContract {



    var ipPublic_Divice : String = ""
    // var arrUsuariosPermitidos: MutableList<String>  = ArrayList()
    private lateinit var databaseHelper_Data: DatabaseHelper




    val client by lazy { APIService.create() }

    //var values: List<Negocios_List> = ArrayList()
    //var valAccionesDinamicas : List<AcionesDinamicas_List> = ArrayList()


    var mapaEquipo = HashMap<Int, String>()

    var iEstadoNegActivo: List<com.apptomatico.app_movil_kotlin_v3.model.ControlEstadoEquipos>? = null
    var toolTipsManager: ToolTipsManager? = null

    private var mLastClickTime: Long = 0

    init {
        SQLiteDatabase.loadLibs(context)
        databaseHelper_Data = DatabaseHelper.getInstance(context)
        refreshFamiliasDB() }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.negocio_fragment, parent, false)
        return ViewHolder(view)
    }





    private fun showPopupMenuAcciones(view: View, nombreNegocio: String, idNegocio: Int, ip_publica: String,
                                      ip_local: String, hardware_key: String, puerto: Int, boveda: String, modelo_comunicacion: Int?, wakeon: Int?, modelComunicacion: Int?, iprogressCardViewEquipos: ProgressBar, estadoEquipo: Boolean) {

        var seMustraPopup = false
        var actividadesVisbles = 5
        if(xdpi < 360){
            actividadesVisbles = 3
        }
        var AccXNegocio = valNegAccionesDinamicas.filter { s -> s.id_negocio_id == idNegocio && s.screenshot_tipo != "sc_inicio_sesion" && s.screenshot_tipo != "sc_intervalo" && s.screenshot_tipo != "sc_ejecutar_programa" }
        AccXNegocio = AccXNegocio.distinctBy {
            it.id
        }

        var numAc = 0
        val popup = PopupMenu(context, view)
        var dominio_actual = ""


        var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }

        for (i in AccXNegocio) {
            if (i.screenshot_tipo != "" && i.screenshot_tipo != "sc_demanda"){
                continue
            }

            //EN CASO DE QUE EL EQUIPO ESTE INACTIVO SE HABILITA SOLO LA ACCION DE ENCENDIDO (SIEMPRE Y CUANDO EL EQUIPO TENGA ASIGNADA ESTA ACCION)
            if(!estadoEquipo){
                var imageUrl = ""
                numAc += 1
                if (numAc > actividadesVisbles){
                    if(i.comando == "start_svr"){
                        seMustraPopup = true
                        imageUrl = if (i.icono == ""){
                            "$dominio_actual/media/iconos/app_default.png"
                        }else{
                            "$dominio_actual/media/" + i.icono.replace("ejecutar_programa.png", "ejecutar_programa_eg.png")

                        }

                        var NomMenu = ""
                        if(i.titulo_aplicacion != "" && i.titulo_aplicacion.isNotEmpty()){
                            NomMenu = i.titulo_aplicacion
                        }else{
                            NomMenu = i.alias_aplicacion
                        }

                        popup.menu.add(NomMenu).setOnMenuItemClickListener {
                            getAndExecuteAccion(i, nombreNegocio, idNegocio, ip_publica, ip_local, hardware_key, puerto, boveda, modelComunicacion!!, iprogressCardViewEquipos)
                            true

                        }

                    }

                }



            }else{
                seMustraPopup = true
                databaseHelper_Data.activaAccionesFavoritas(i.id)
                var imageUrl = ""
                numAc += 1
                if (numAc > actividadesVisbles){
                    if (i.icono == ""){
                        imageUrl = "$dominio_actual/media/iconos/app_default.png"
                    }else{

                        imageUrl =   "$dominio_actual/media/" + i.icono.replace("ejecutar_programa.png", "ejecutar_programa_eg.png")
                    }

                    var NomMenu = ""
                    if(i.titulo_aplicacion != "" && i.titulo_aplicacion.isNotEmpty()){
                        NomMenu = i.titulo_aplicacion
                    }else{
                        NomMenu = i.alias_aplicacion
                    }

                    popup.menu.add(NomMenu).setOnMenuItemClickListener {
                        getAndExecuteAccion(i, nombreNegocio, idNegocio, ip_publica, ip_local, hardware_key, puerto, boveda, modelComunicacion!!, iprogressCardViewEquipos)
                        true

                    }

                }


            }

        }

        if(seMustraPopup){
            popup.show()
        }
    }


    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = valuesListEquipos[position]


        var dominio_actual = ""




        holder.btnAyuda.tag = "${item.id_negocio_id}4520999100178"
        val imageViewEncender = holder.btnAyuda

        if (item.estatus_equipo == "offline"){
            imageViewEncender.setColorFilter(context.resources.getColor(R.color.dg_gray)) //** Cambios para Actualizacion automatica JHE
        }

        imageViewEncender.isClickable = false
        if (item.modelo_comunicacion == 0){
            holder.btnInocoArqWS.visibility = View.GONE
            holder.btnInocoArqWS.tag = "${item.id_negocio_id}4520888110777"
            holder.btnInocoArqAPI.visibility = View.VISIBLE
            holder.btnInocoArqAPI.isClickable = false
            holder.btnInocoArqAPI.tag = "${item.id_negocio_id}4520888110776"
            if (item.estatus_equipo == "offline"){
                holder.btnInocoArqAPI.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
            }
            holder.btnInocoArqAPI.setOnClickListener {
                holder.btnInocoArqAPI.animationXFade(Fade.FADE_IN_DOWN)
                displayTooltipLeftBottonCloud(holder.btnInocoArqAPI, "Arquitectura API", "El equipo puede intercambiar cualquier flujo de datos, fiable y ordenadamente", Gravity.END)
            }
        }else if(item.modelo_comunicacion == 1){
            //tipoLicencia = "Cloud" Comentado a solicitud de AEG Junta 23/02/2023 JHE
            holder.btnInocoArqAPI.visibility = View.GONE
            holder.btnInocoArqAPI.tag = "${item.id_negocio_id}4520888110776"
            holder.btnInocoArqWS.visibility = View.VISIBLE
            holder.btnInocoArqWS.isClickable = false
            holder.btnInocoArqWS.tag = "${item.id_negocio_id}4520888110777"
            if (item.estatus_equipo == "offline"){
                holder.btnInocoArqWS.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
            }
            holder.btnInocoArqWS.setOnClickListener {
                holder.btnInocoArqWS.animationXFade(Fade.FADE_IN_DOWN)
                displayTooltipLeftBottonCloud( holder.btnInocoArqWS, "Arquitectura WebSocket", "El equipo puede intercambiar cualquier flujo de datos, fiable y ordenadamente", Gravity.END)
            }

        }else if(item.modelo_comunicacion == 2){
            holder.btnInocoArqAPI.visibility = View.GONE
            holder.btnInocoArqWS.visibility = View.GONE
        }else{
            holder.btnInocoArqAPI.visibility = View.GONE
            holder.btnInocoArqWS.visibility = View.GONE
        }


        if(item.equipo_virtual == 0){
            holder.btnInocoNube.visibility = View.GONE
            holder.btnInocoNube.tag = "${item.id_negocio_id}4520888110887"
            holder.btnInocoFisico.visibility = View.VISIBLE
            holder.btnInocoFisico.isClickable = false
            holder.btnInocoFisico.tag = "${item.id_negocio_id}4520888110886"
            if (item.estatus_equipo == "offline"){
                holder.btnInocoFisico.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
            }
            holder.btnInocoFisico.setOnClickListener {
                holder.btnInocoFisico.animationXFade(Fade.FADE_IN_DOWN)
                displayTooltipLeftBottonCloud(holder.btnInocoFisico, "Equipo Fisico", "EL equipo cuenta con partes físicas, tangibles, de un sistema informático, componentes eléctricos, electrónicos y electromecánicos", Gravity.END)
            }
        }else if(item.equipo_virtual ==1){
            holder.btnInocoFisico.visibility = View.GONE
            holder.btnInocoFisico.tag = "${item.id_negocio_id}4520888110886"
            holder.btnInocoNube.visibility = View.VISIBLE
            holder.btnInocoNube.isClickable = false
            holder.btnInocoNube.tag = "${item.id_negocio_id}4520888110887"
            if (item.estatus_equipo == "offline"){
                holder.btnInocoNube.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
            }
            holder.btnInocoNube.setOnClickListener {
                holder.btnInocoNube.animationXFade(Fade.FADE_IN_DOWN)
                displayTooltipLeftBottonCloud( holder.btnInocoNube, "Equipo Virtual", "El equipo está ubicado en la nube o en un centro de datos fuera de las instalaciones de la empresa, y comparte recursos de hardware y software con otros servidores virtuales", Gravity.END)
            }
        }else{
            holder.btnInocoNube.visibility = View.GONE
            holder.btnInocoFisico.visibility = View.GONE
        }

        var actividadesVisbles = 5
        if(xdpi < 360){
            actividadesVisbles = 3
        }

        holder.btnInocoUnimovil.tag = "${item.id_negocio_id}4520888110178"
        val imageViewUnimovil = holder.btnInocoUnimovil
        if (item.estatus_equipo == "offline"){
            imageViewUnimovil.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE

        }
        imageViewUnimovil.isClickable = false
        if(databaseHelper_Data.getEquipoTipoLic(item.id_negocio_id) == 1){
            //isUnimovil = "/Unimovil" Comentado a solicitud de AEG Junta 23/02/2023 JHE
            holder.btnInocoUnimovil.visibility = View.VISIBLE
        }else{
            holder.btnInocoUnimovil.visibility = View.GONE
        }
        holder.btnInocoUnimovil.setOnClickListener {
            holder.btnInocoUnimovil.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltipLeftBotton(holder.btnInocoUnimovil, "Licencia Unimovil", "El equipo solo es accesible para cierta cantidad de moviles", Gravity.END)
        }


        /*
        if(existeLic.count() > 0){
            for (i in 0 until existeLic.count()) {
                dominio_actual = existeLic[i].nombre_dominio
            }
        }

         */

        var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }


        //holder.idView.text = item.id_negocio_id.toString()
        if(item.catnombreneg  != null && item.catnombreneg != "null"){
            if(item.tipo_eq_clave != null && item.tipo_eq_clave != "null"){
                holder.contentView.text = item.catnombreneg + "/" + item.nombre
            }else{
                holder.contentView.text = item.catnombreneg + "/" + item.nombre
            }
        }else{
            holder.contentView.text =  item.nombre
        }


        holder.contentiplocal.text = item.ip_publica + " / " + item.ip_local + " : " + item.puerto


        val idNegocio = item.id_negocio_id
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        val idMovil = Licenciadb[0].id_movil
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        var existe_negocio =  databaseHelper_Data.getInfoNegocio(idNegocio.toString())
        when(item.tipo_eq_clave){
            "SVR-LNX" ->  holder.btnExecutar.setImageResource(R.drawable.linux_on)
            "SVR-WIN" -> holder.btnExecutar.setImageResource(R.drawable.windows_on)
            "SVR-MAC" -> holder.btnExecutar.setImageResource(R.drawable.mac_os_on)
        }
        holder.btnExecutar.tag = item.id_negocio_id
        holder.linearNegAccionesList.removeAllViews()
        holder.cardnegocio.tag = "${item.id_negocio_id}9999"

        var descripciones_acciones: String = ""
        var AccXNegocio = valNegAccionesDinamicas.filter { s -> s.id_negocio_id == idNegocio && s.screenshot_tipo != "sc_inicio_sesion" && s.screenshot_tipo != "sc_intervalo" && s.screenshot_tipo != "sc_ejecutar_programa"  }
        var numAc = 0
        for (i in AccXNegocio) {
            if (i.screenshot_tipo != "" && i.screenshot_tipo != "sc_demanda"){
                continue
            }
            databaseHelper_Data.activaAccionesFavoritas(i.id)
            if (numAc <  actividadesVisbles){ //Numero de Acciones predeterminadas falta parametrizar

                numAc += 1
                var imageUrl = ""
                if (i.icono == ""){

                    imageUrl = "$dominio_actual/media/iconos/app_default.png"
                }else{

                    imageUrl =   "$dominio_actual/media/" + i.icono.replace("ejecutar_programa.png", "ejecutar_programa_eg.png")


                }


                val imageView = ImageView(context)
                imageView.id = numAc
                imageView.contentDescription = "${i.descripcion}|${i.titulo_aplicacion}"
                imageView.setPadding(25, 0, 25, 16)
                if (item.estatus_equipo == "offline"){
                    imageView.setColorFilter(context.resources.getColor(R.color.dg_gray)) //** Cambios para Actualizacion automatica JHE
                }
                if (i.comando == "start_svr"){
                    imageView.tag = "${item.id_negocio_id}000000014834"
                }
                Picasso.get().load(imageUrl).resize(80, 80).into(imageView)

                val lblAccion = TextView(context)
                lblAccion.textSize = 0F
                lblAccion.text = "${i.descripcion}|${i.titulo_aplicacion}"
                lblAccion.visibility = View.GONE



                descripciones_acciones = "* ${i.descripcion}\n"

                imageView.setOnClickListener {
                    if(imageView.colorFilter != null){
                        return@setOnClickListener
                    }

                    if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                        return@setOnClickListener
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()


                    iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                    if (iEstadoNegActivo != null){
                        val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                        if (estActivo.count() > 0){
                            if (estActivo[0].estado_equipo == "Activo"){
                                imageView.animationXFade(Fade.FADE_IN_DOWN)
                                if (item.boveda_software == null || item.boveda_software == "") {
                                    Toast.makeText(context, "Falta establecer ruta de la Boveda de Software", Toast.LENGTH_LONG).show()
                                }else{

                                    getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                                }
                            }else{

                            }
                        }else if(i.comando == "start_svr"){
                            getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                        }else{

                        }

                    }else if(i.comando == "start_svr"){
                        getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)

                    }else{

                    }





                }
                holder.linearNegAccionesList.addView(imageView)
                holder.linearNegAccionesDescList.addView(lblAccion)





            }
        }


        if (AccXNegocio.size > actividadesVisbles){
            val imageViewMasAcciones = ImageView(context)
            imageViewMasAcciones.setPadding(25, 0, 25, 16)
            imageViewMasAcciones.setImageResource(R.drawable.mas_acciones)
            if (item.estatus_equipo == "offline"){
                imageViewMasAcciones.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
            }
            imageViewMasAcciones.setOnClickListener {

                iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                if (iEstadoNegActivo != null){
                    val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                    if (estActivo.count() > 0){
                        if (estActivo[0].estado_equipo == "Activo"){
                            showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, true)
                        }else{
                            showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                        }
                    }else{
                        showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)
                    }

                }else{
                    showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                }




            }
            holder.linearNegAccionesList.addView(imageViewMasAcciones)
        }


        if (existe_negocio == ""){
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val currentDateAndTime: String  =  simpleDateFormat.format(Date())
            databaseHelper_Data.addInfoNegocio(1, currentDateAndTime, idNegocio!!.toInt())
            databaseHelper_Data.updateInfoNegocio(1,currentDateAndTime, idNegocio!!.toInt())

            var fecUltAct = currentDateAndTime.split(" ")
            holder.fecUtlAct.tag = "${item.id_negocio_id}842644946549"
            holder.fecUtlAct.text = "(Ult.Act: " + fecUltAct[0] + " "


            holder.horaUtlAct.tag = "${item.id_negocio_id}8426449465492"
            holder.horaUtlAct.text = fecUltAct[1] + ")"


            try{


                if (item.estatus_equipo == "offline") {
                    holder.btnExecutar.setColorFilter(context.resources.getColor(R.color.dg_gray), PorterDuff.Mode.SRC_ATOP)
                }
                holder.linearNegAccionesList.removeAllViews()
                var AccXNegocio = valNegAccionesDinamicas.filter { s -> s.id_negocio_id == idNegocio && s.screenshot_tipo != "sc_inicio_sesion" && s.screenshot_tipo != "sc_intervalo" && s.screenshot_tipo != "sc_ejecutar_programa"  }
                var numAc = 0
                for (i in AccXNegocio) {
                    if (i.screenshot_tipo != "" && i.screenshot_tipo != "sc_demanda"){
                        continue
                    }
                    databaseHelper_Data.activaAccionesFavoritas(i.id)
                    if (numAc < actividadesVisbles){ //Numero de Acciones predeterminadas falta parametrizar

                        numAc += 1
                        var imageUrl = ""
                        if (i.icono == ""){

                            imageUrl = "$dominio_actual/media/iconos/app_default.png"
                        }else{

                            imageUrl =   "$dominio_actual/media/" + i.icono.replace("ejecutar_programa.png", "ejecutar_programa_eg.png")


                        }

                        val imageView = ImageView(context)
                        imageView.id = numAc
                        imageView.contentDescription = "${i.descripcion}|${i.titulo_aplicacion}"
                        imageView.setPadding(25, 0, 25, 16)
                        if (item.estatus_equipo == "offline") {
                            imageView.setColorFilter(context.resources.getColor(R.color.dg_gray)) //** Cambios para Actualizacion automatica JHE
                        }
                        Picasso.get().load(imageUrl).resize(80, 80).into(imageView)
                        if (i.comando == "start_svr"){
                            imageView.tag = "${item.id_negocio_id}000000014834"
                        }
                        imageView.setOnClickListener {
                            if(imageView.colorFilter != null){
                                return@setOnClickListener
                            }

                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                                return@setOnClickListener
                            }
                            mLastClickTime = SystemClock.elapsedRealtime()


                            iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                            if (iEstadoNegActivo != null){
                                val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                                if (estActivo.count() > 0){
                                    if (estActivo[0].estado_equipo == "Activo"){
                                        imageView.animationXFade(Fade.FADE_IN_DOWN)
                                        if (item.boveda_software == null || item.boveda_software == "") {
                                            Toast.makeText(context, "Falta establecer ruta de la Boveda de Software", Toast.LENGTH_LONG).show()
                                        }else{

                                            getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                                        }
                                    }else{

                                    }
                                }else if(i.comando == "start_svr"){
                                    getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                                }else{

                                }

                            }else if(i.comando == "start_svr"){
                                getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                            }else{

                            }





                        }




                        holder.linearNegAccionesList.addView(imageView)






                    }


                }

                if (AccXNegocio.size > actividadesVisbles){
                    val imageViewMasAcciones = ImageView(context)
                    imageViewMasAcciones.setPadding(25, 0, 25, 16)
                    imageViewMasAcciones.setImageResource(R.drawable.mas_acciones)
                    if (item.estatus_equipo == "offline"){
                        imageViewMasAcciones.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
                    }
                    imageViewMasAcciones.setOnClickListener {

                        iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                        if (iEstadoNegActivo != null){
                            val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                            if (estActivo.isNotEmpty()){
                                if (estActivo[0].estado_equipo == "Activo"){
                                    showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, true)
                                }else{
                                    showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)
                                }
                            }else{
                                showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                            }

                        }else{
                            showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)
                        }
                    }
                    holder.linearNegAccionesList.addView(imageViewMasAcciones)
                }

            }catch (e: Exception){

            }


        }else{
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val oldDate: Date =  simpleDateFormat.parse(existe_negocio)
            var fecUltAct = existe_negocio.split(" ")
            holder.fecUtlAct.tag = "${item.id_negocio_id}842644946549"
            holder.fecUtlAct.text = "(Ult.Act: " + fecUltAct[0] + " "
            holder.horaUtlAct.tag = "${item.id_negocio_id}8426449465492"
            holder.horaUtlAct.text = fecUltAct[1] + ")"

            val newDate = Date()
            var diff: Long = newDate.time - oldDate.time
            var difSegundos: Long  =  diff / 1000
            var difMinutos: Long = difSegundos / 60

            try{

                if (item.estatus_equipo == "offline") {
                    holder.btnExecutar.setColorFilter(context.resources.getColor(R.color.dg_gray))

                }
                holder.linearNegAccionesList.removeAllViews()
                var AccXNegocio = valNegAccionesDinamicas.filter { s -> s.id_negocio_id == idNegocio && s.screenshot_tipo != "sc_inicio_sesion" && s.screenshot_tipo != "sc_intervalo" && s.screenshot_tipo != "sc_ejecutar_programa"  }

                var numAc = 0
                for (i in AccXNegocio) {
                    if (i.screenshot_tipo != "" && i.screenshot_tipo != "sc_demanda"){
                        continue
                    }
                    databaseHelper_Data.activaAccionesFavoritas(i.id)
                    if (numAc < actividadesVisbles){ //Numero de Acciones predeterminadas falta parametrizar

                        numAc += 1
                        var imageUrl = ""
                        if (i.icono == ""){

                            imageUrl = "$dominio_actual/media/iconos/app_default.png"
                        }else{

                            imageUrl =   "$dominio_actual/media/" + i.icono.replace("ejecutar_programa.png", "ejecutar_programa_eg.png")


                        }

                        val imageView = ImageView(context)
                        imageView.id = numAc
                        imageView.contentDescription = "${i.descripcion}|${i.titulo_aplicacion}"
                        imageView.setPadding(25, 0, 25, 16)
                        if (item.estatus_equipo == "offline") {
                            imageView.setColorFilter(context.resources.getColor(R.color.dg_gray)) //** Cambios para Actualizacion automatica JHE
                        }
                        if (i.comando == "start_svr"){
                            imageView.tag = "${item.id_negocio_id}000000014834"
                        }
                        Picasso.get().load(imageUrl).resize(80, 80).into(imageView)

                        imageView.setOnClickListener {
                            if(imageView.colorFilter != null){
                                return@setOnClickListener
                            }

                            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                                return@setOnClickListener
                            }
                            mLastClickTime = SystemClock.elapsedRealtime()



                            iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                            if (iEstadoNegActivo != null){
                                val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                                if (estActivo.isNotEmpty()){
                                    if (estActivo[0].estado_equipo == "Activo"){
                                        imageView.animationXFade(Fade.FADE_OUT_RIGHT)
                                        if (item.boveda_software == null || item.boveda_software == "") {
                                            Toast.makeText(context, "Falta establecer ruta de la Boveda de Software", Toast.LENGTH_LONG).show()
                                        }else{

                                            getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                                        }
                                    }else{

                                    }
                                }else if(i.comando == "start_svr"){
                                    getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                                }else{

                                }

                            }else if(i.comando == "start_svr"){
                                getAndExecuteAccion(i, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion!!, holder.iprogressCardViewEquipos)
                            }else{

                            }

                        }
                        holder.linearNegAccionesList.addView(imageView)
                    }
                }

                if (AccXNegocio.size > actividadesVisbles){
                    val imageViewMasAcciones = ImageView(context)
                    imageViewMasAcciones.setPadding(25, 0, 25, 16)
                    imageViewMasAcciones.setImageResource(R.drawable.mas_acciones)
                    if (item.estatus_equipo == "offline"){
                        imageViewMasAcciones.setColorFilter(context.resources.getColor(R.color.dg_gray)) //**Cambios para Actualizacion automatica JHE
                    }
                    imageViewMasAcciones.setOnClickListener {

                        iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

                        if (iEstadoNegActivo != null){
                            val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                            if (estActivo.count() > 0){
                                if (estActivo[0].estado_equipo == "Activo"){
                                    showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, true)
                                }else{
                                    showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                                }
                            }else{
                                showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                            }

                        }else{
                            showPopupMenuAcciones(imageViewMasAcciones, item.nombre, idNegocio, item.ip_publica, item.ip_local, item.hardware_key, item.puerto, item.boveda_software, item.modelo_comunicacion, item.wake_on, item.modelo_comunicacion, holder.iprogressCardViewEquipos, false)

                        }




                    }
                    holder.linearNegAccionesList.addView(imageViewMasAcciones)
                }




            }catch (e: Exception){

            }

        }
        imageViewEncender.setOnClickListener {
            imageViewEncender.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(holder.linearNegAccionesList,holder.linearNegAccionesDescList, descripciones_acciones, item.nombre, 0)
        }


        holder.cardnegocio.setOnClickListener{

            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            holder.iprogressCardViewEquipos.visibility = View.VISIBLE
            iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

            if (iEstadoNegActivo != null){
                val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                if (estActivo.count() > 0){
                    if (estActivo[0].estado_equipo == "Activo"){

                    }else{
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                        return@setOnClickListener
                    }
                }else{
                    holder.iprogressCardViewEquipos.visibility = View.GONE
                    return@setOnClickListener
                }

            }else{
                holder.iprogressCardViewEquipos.visibility = View.GONE
                return@setOnClickListener
            }

            SharedApp.prefs.idNegocioF = item.id_negocio_id.toString() + "|" + item.ip_publica.toString() + "|" + item.hardware_key.toString() + "|" + item.puerto.toString() + "|" + item.boveda_software.toString() + "|" + item.nombre.toString() + "|cuadricula|" + item.ip_local
            //*******valida horario laboral
            val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                "licencias@dsiin.com",
                "xhceibEd"
            )

            val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data!!, context).create(APIService::class.java)
            val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

            result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                    holder.iprogressCardViewEquipos.visibility = View.GONE
                    Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                    val resultado = response.body()

                    if (resultado != null) {
                        val token_result = resultado.token.toString()


                        val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data!!, context).create(APIService::class.java)
                        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(idMovil, idNegocio, "$idDispositivo", "JWT $token_result", "application/json")
                        resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                                holder.iprogressCardViewEquipos.visibility = View.GONE
                                Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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
                                            holder.iprogressCardViewEquipos.visibility = View.GONE
                                            if (context is MainActivity) {
                                                origen_back_dashboard = 0
                                                (context as MainActivity).setSelectedItem(6)
                                            }

                                        } else {
                                            holder.iprogressCardViewEquipos.visibility = View.GONE
                                            Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                        }
                                    } else {

                                        holder.iprogressCardViewEquipos.visibility = View.GONE
                                        if (context is MainActivity) {
                                            origen_back_dashboard = 0
                                            (context as MainActivity).setSelectedItem(6)
                                        }
                                    }


                                } else {

                                    holder.iprogressCardViewEquipos.visibility = View.GONE
                                    if (context is MainActivity) {
                                        origen_back_dashboard = 0
                                        (context as MainActivity).setSelectedItem(6)
                                    }
                                }
                                ///////////////
                            }

                        })

                    }else{
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                    }

                }

            })

        }

        holder.contentView.setOnClickListener{

            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000){
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()

            holder.iprogressCardViewEquipos.visibility = View.VISIBLE
            iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

            if (iEstadoNegActivo != null){
                val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                if (estActivo.count() > 0){
                    if (estActivo[0].estado_equipo == "Activo"){

                    }else{
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                        return@setOnClickListener
                    }
                }else{
                    holder.iprogressCardViewEquipos.visibility = View.GONE
                    return@setOnClickListener
                }

            }else{
                holder.iprogressCardViewEquipos.visibility = View.GONE
                return@setOnClickListener
            }





            SharedApp.prefs.idNegocioF = item.id_negocio_id.toString() + "|" + item.ip_publica.toString() + "|" + item.hardware_key.toString() + "|" + item.puerto.toString() + "|" + item.boveda_software.toString() + "|" + item.nombre.toString() + "|cuadricula|" + item.ip_local


            //*******valida horario laboral
            val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                "licencias@dsiin.com",
                "xhceibEd"
            )

            val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data!!, context).create(APIService::class.java)
            val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

            result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                    holder.iprogressCardViewEquipos.visibility = View.GONE
                    Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                    val resultado = response.body()

                    if (resultado != null) {
                        val token_result = resultado.token.toString()


                        val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data!!, context).create(APIService::class.java)
                        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(idMovil, idNegocio, "$idDispositivo", "JWT $token_result", "application/json")
                        resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                                holder.iprogressCardViewEquipos.visibility = View.GONE
                                Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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
                                            holder.iprogressCardViewEquipos.visibility = View.GONE
                                            if (context is MainActivity) {
                                                origen_back_dashboard = 0
                                                (context as MainActivity).setSelectedItem(6)
                                            }

                                        } else {
                                            holder.iprogressCardViewEquipos.visibility = View.GONE
                                            Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                        }
                                    } else {

                                        holder.iprogressCardViewEquipos.visibility = View.GONE
                                        if (context is MainActivity) {
                                            origen_back_dashboard = 0
                                            (context as MainActivity).setSelectedItem(6)
                                        }
                                    }


                                } else {

                                    holder.iprogressCardViewEquipos.visibility = View.GONE
                                    if (context is MainActivity) {
                                        origen_back_dashboard = 0
                                        (context as MainActivity).setSelectedItem(6)
                                    }
                                }
                                ///////////////
                            }

                        })

                    }else{
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                    }

                }

            })

            /// valida horario laboral

        }

        holder.btnExecutar.setOnClickListener{
            holder.iprogressCardViewEquipos.visibility = View.VISIBLE
            iEstadoNegActivo = databaseHelper_Data.getControlEstadoEquipos()

            if (iEstadoNegActivo != null){
                val estActivo =  iEstadoNegActivo!!.filter { i -> i.equipo_id == item.id_negocio_id }
                if (estActivo.count() > 0){
                    if (estActivo[0].estado_equipo == "Activo"){

                    }else{
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                        return@setOnClickListener
                    }
                }else{
                    holder.iprogressCardViewEquipos.visibility = View.GONE
                    return@setOnClickListener
                }

            }else{
                holder.iprogressCardViewEquipos.visibility = View.GONE
                return@setOnClickListener
            }



            try {
                holder.btnExecutar.animationXBounce(Bounce.BOUNCE_IN_UP)
                SharedApp.prefs.idNegocioF = item.id_negocio_id.toString() + "|" + item.ip_publica.toString() + "|" + item.hardware_key.toString() + "|" + item.puerto.toString() + "|" + item.boveda_software.toString() + "|" + item.nombre.toString() + "|cuadricula|" + item.ip_local


                //*******valida horario laboral
                val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                    "licencias@dsiin.com",
                    "xhceibEd"
                )

                val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

                result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                    override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                        holder.iprogressCardViewEquipos.visibility = View.GONE
                        Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                        val resultado = response.body()

                        if (resultado != null) {
                            val token_result = resultado.token.toString()


                            val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                            val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(idMovil, idNegocio, "$idDispositivo", "JWT $token_result", "application/json")
                            resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                                override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                                    holder.iprogressCardViewEquipos.visibility = View.GONE
                                    Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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


                                                holder.iprogressCardViewEquipos.visibility = View.GONE
                                                if (context is MainActivity) {
                                                    (context as MainActivity).setSelectedItem(6)
                                                }


                                            } else {
                                                holder.iprogressCardViewEquipos.visibility = View.GONE
                                                Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                            }
                                        } else {

                                            holder.iprogressCardViewEquipos.visibility = View.GONE
                                            if (context is MainActivity) {
                                                (context as MainActivity).setSelectedItem(6)
                                            }

                                        }


                                    } else {

                                        holder.iprogressCardViewEquipos.visibility = View.GONE
                                        if (context is MainActivity) {
                                            (context as MainActivity).setSelectedItem(6)
                                        }
                                    }
                                    ///////////////
                                }

                            })

                        }else{
                            holder.iprogressCardViewEquipos.visibility = View.GONE
                        }

                    }

                })

                /// valida horario laboral



            }
            catch (e: Exception) {
                Toast.makeText(context, "El negocio no tiene acciones asignadas", Toast.LENGTH_LONG).show()
            }


        }


        if(valuesListEquipos.size - 1 == position){
            nfragment.actualizaInicio()
        }

    }

//FIN HOLDER




    override fun getItemCount(): Int = valuesListEquipos.size

    fun getAndExecuteAccion(info_app: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List, NomNegocio: String, idNegocio: Int, ip_publica: String, ip_local: String, hardware_key: String, puerto: Int, boveda: String, tipoComunicacion: Int, iprogressCardViewEquipos: ProgressBar?){

        mLastClickTime = SystemClock.elapsedRealtime()


        //arrUsuariosPermitidos.clear()
        // var IdAccion = view.id

        iprogressCardViewEquipos!!.tag = "010203040506${idNegocio}"

        if (info_app.tipo_id == 1){ // Ejecuta cualquier aplicacion

            if(info_app.comando == "calc"){

                var dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage("Desea ejecutar  ${info_app.aplicacion_nombre_aplicacion}")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (

                                getGetUsuariosPermitidosXEquipo(info_app, NomNegocio, idNegocio, ip_publica, ip_local, hardware_key, puerto, boveda, tipoComunicacion, iprogressCardViewEquipos)

                                )
                    })
                    .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                        view.dismiss()
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()





            }else if(info_app.comando == "termina_proceso_en_equipo"){


                var dialogBuilder = AlertDialog.Builder(context)
                dialogBuilder.setMessage("Desea finalizar el proceso  ${info_app.aplicacion_nombre_aplicacion}")
                    .setCancelable(true)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        (

                                sendTerminaProcesoEquipo("${info_app.alias_aplicacion}", "$idNegocio", "$ip_publica", ip_local, "$hardware_key", "$puerto", "${info_app.aplicacion_ruta_aplicacion}",
                                    "${info_app.aplicacion_nombre_aplicacion}", "${info_app.aplicacion_parametros_aplicacion}", tipoComunicacion)

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
                                RebootNegocio(NomNegocio, "$hardware_key", "$ip_publica", "$puerto", "$ip_local", tipoComunicacion)
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()


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
                                LogOffNegocio(NomNegocio, "$hardware_key", "$ip_publica", "$puerto", "$ip_local", tipoComunicacion)
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()

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
                                ShutdownNegocio(NomNegocio, "$hardware_key", "$idNegocio", "$ip_publica", "$puerto", "$ip_local", tipoComunicacion)
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()

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
                                StartNegocio(NomNegocio, "$hardware_key", "$idNegocio", "$ip_publica", "$puerto", "$ip_local", tipoComunicacion)
                                )
                    })
                var alert = dialogBuilder.create()
                alert.setTitle("$NomNegocio")
                alert.show()
            }



        }


        if (info_app.tipo_id == 4){//Base de datos
            // val accountsIntent = Intent(context, TableLayoutTest::class.java)

            // context.startActivity(accountsIntent)
            ConsultaDinamicaBdNegocio(info_app, "$hardware_key", "$ip_publica", "$ip_local", puerto, NomNegocio,  tipoComunicacion, iprogressCardViewEquipos!!)

        }

        if (info_app.tipo_id == 5){//Consulta estado del servidor
            SharedApp.prefs.idNegocioF = info_app.id_negocio_id.toString() + "|" + ip_publica +  "|" + hardware_key + "|" + puerto + "|"  + ip_local
            if(tipoComunicacion == 1) {
                LoginActivity.accionejecutandose = true
            }

            estatusActNegocio(info_app.id, info_app.origen, NomNegocio, hardware_key,  tipoComunicacion, iprogressCardViewEquipos!!)



        }

        if (info_app.tipo_id == 6){//Esplorador de Archivos
            if(tipoComunicacion == 1){
                LoginActivity.accionejecutandose = true
            }
            ExplorarDirectorio(info_app.exploracion_ruta, hardware_key, idNegocio, NomNegocio, ip_publica, puerto, ip_local,  tipoComunicacion, iprogressCardViewEquipos!!)
        }


        if (info_app.tipo_id == 7){//Screenshot bajo demanda del servidor

            ScreenshotServidor(hardware_key, idNegocio, NomNegocio, ip_publica, puerto, boveda, ip_local, tipoComunicacion,  iprogressCardViewEquipos!!)
        }

        if (info_app.tipo_id == 8){//Elimina archivo del servidor
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

                            chkDelArcDirServidor(hardware_key, idNegocio, NomNegocio, ip_publica, puerto,
                                "${info_app.eliminacion_tipo}", "${info_app.eliminacion_ruta_archivo}",
                                "${info_app.eliminacion_nombre_archivo}", ip_local,  tipoComunicacion)

                            )
                })
                .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("$NomNegocio")
            alert.show()



        }


        if (info_app.tipo_id == 9){//Informa si un programa esta en ejecucion



            ConsultaInfoProgramaExec(info_app, "$hardware_key", "$ip_publica", puerto, NomNegocio, ip_local, tipoComunicacion)
        }

        if (info_app.tipo_id == 16) {//CONSULTA API

            ConsultaApiExtNegocio(info_app, "$hardware_key", "$ip_publica", "$ip_local", puerto, NomNegocio,  tipoComunicacion, iprogressCardViewEquipos!!)
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
        tipoComunicacion: Int,
        iprogressCardViewEquipos: ProgressBar?
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
            iprogressCardViewEquipos?.visibility = View.VISIBLE

            val getVigenciaLic = RestEngine.getRestEngineInicial(databaseHelper_Data, context).create(APIService::class.java)
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
                    iprogressCardViewEquipos?.visibility = View.GONE

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

                        listViewActivos.adapter = ArrayAdapter(context, android.R.layout.simple_list_item_single_choice, listaActivos)
                        listViewActivos.choiceMode = ListView.CHOICE_MODE_SINGLE
                        listViewActivos.setOnItemClickListener { _, _, pos, _ ->
                            usuarioSeleccionado = arrActivos[pos]

                            // Limpiar selección de la lista de inactivos
                            listViewInactivos.clearChoices()
                            (listViewInactivos.adapter as ArrayAdapter<*>).notifyDataSetChanged()

                            listViewActivos.setItemChecked(pos, true) // marcar el actual
                        }

                        listViewInactivos.adapter = object : ArrayAdapter<String>(
                            context,
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
                    iprogressCardViewEquipos?.visibility = View.GONE
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
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle(titulo)
            .setMessage("No existen usuarios activos en el equipo")
            .setCancelable(false)
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }







    fun refreshFamiliasDB() {

        tskgetListEquipos(context, container, databaseHelper_Data, this@NegocioRecyclerViewAdapter).execute()

        /*
        databaseHelper_Data.desactivaTodasAccionesFavoritas()

        if (LoginActivity.header_id_negocio != "Todos"){
                valuesListEquipos = databaseHelper_Data.getListEquipos(LoginActivity.header_id_negocio.toInt())

        }else{
                valuesListEquipos = databaseHelper_Data.getListEquipos(0)
        }

        var numeroequipos = valuesListEquipos.count()
        if(numeroequipos > 0){
            val params: ViewGroup.LayoutParams = container!!.layoutParams
            params.height = dpToPx((numeroequipos * 180) + 150, context!!)
            container!!.layoutParams = params
        }
        valNegAccionesDinamicas = databaseHelper_Data.getListAccionesDinamicas()
        this@NegocioRecyclerViewAdapter.notifyDataSetChanged()
        */
    }


    class tskgetListEquipos(context: Context,container: View, databaseHelper_Data: DatabaseHelper, adapEquipos: NegocioRecyclerViewAdapter) : AsyncTask<String?, String?, List<Negocios_List>?>() {

        val context = context
        val container = container
        val adapEquipos = adapEquipos
        val databaseHelper_Data = databaseHelper_Data

        override fun doInBackground(vararg params: String?): List<Negocios_List>? {
            databaseHelper_Data.desactivaTodasAccionesFavoritas()
            valNegAccionesDinamicas = databaseHelper_Data.getListAccionesDinamicas()

            return if (LoginActivity.header_id_negocio != "Todos"){
                databaseHelper_Data.getListEquipos(LoginActivity.header_id_negocio.toInt())

            }else{
                databaseHelper_Data.getListEquipos(0)
            }
        }


        override  fun onPostExecute(listNeg: List<Negocios_List>?) {

            if (listNeg == null) {
                return
            }

            valuesListEquipos = listNeg!!
            var numeroequipos = listNeg.count()

            if(numeroequipos > 0){
                val params: ViewGroup.LayoutParams = container!!.layoutParams
                params.height = dpToPx((numeroequipos * 180) + 150, context!!)
                container!!.layoutParams = params
            }

            adapEquipos.notifyDataSetChanged()



        }

        private fun dpToPx(dp: Int, context: Context): Int {
            val density: Float = context.resources
                .displayMetrics.density
            return (dp.toFloat() * density).roundToInt()
        }



    }





    fun dpToPx(dp: Int, context: Context): Int {
        val density: Float = context.resources
            .displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }


    fun LogOffNegocio(nomNegocio: String, idHardware: String, ipNegocio: String, iPuertoNegocio: String, iPLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$iPLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

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

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$iPLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("LogOff", "1000003", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(context, "Se ejecuto la accion", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })
        }else if(tipoComunicacion == 1){
            var parametros = "LogOff"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")

        }



    }

    fun RebootNegocio(nomNegocio: String, idHardware: String, ipNegocio: String, iPuertoNegocio: String, ipLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil



        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

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

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("Reboot", "1000001", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(context, "Se ejecuto la accion", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })
        }else if(tipoComunicacion == 1){
            var parametros = "Reboot"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }




    }

    fun WakeOnLanEquipo(nomNegocio: String, idHardware: String, idNegocio: String, ipNegocio: String, iPuertoNegocio: String, iPLocal: String) {

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        var versionSoftware  = "${BuildConfig.VERSION_NAME}"
        databaseHelper_Data.updVersionLicencia(versionSoftware)
        val versionInstalada = databaseHelper_Data.getVersionActSoftware()

        val userService: APIService = RestEngine.getRestEngineInicial(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token
                    val getDirMac: APIService = RestEngine.getRestEngineInicial(databaseHelper_Data, context).create(APIService::class.java)
                    val result_dir_mac: Call<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo> = getDirMac.getDirMacXEquipo(idHardware, ".", ".", "JWT $token", "application/json")
                    result_dir_mac.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo>, t: Throwable) {

                        }

                        override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo>) {
                            if (response.code() == 200) {
                                val resultado = response.body()
                                val adaptadores_equipo = resultado!!.data

                                for (res in adaptadores_equipo) {


                                    try {
                                        var values_red: List<com.apptomatico.app_movil_kotlin_v3.model.ControlRedMovil> = java.util.ArrayList()
                                        val adaptador = res.adaptador
                                        var ipAddress = ipNegocio
                                        val macAddress = res.dir_mac
                                        var ipPublic_Divice = ""
                                        values_red = databaseHelper_Data.getConexionRed()
                                        for (i in 0 until values_red.count()) {
                                            ipPublic_Divice = values_red[i].ip_publica
                                        }
                                        if (ipPublic_Divice.replace(" ", "") == ipAddress.replace(" ", "")) {
                                            ipAddress = iPLocal
                                        }



                                        Thread(Runnable { magicPacket(macAddress, ipAddress, iPuertoNegocio.toInt()) }).start()
                                        // val Client = UDP_Client()
                                        //  Client.Message = "Your message";
                                        // Client.NachrichtSenden();
                                        //Thread(Runnable { WakeOnLan.sendWakeOnLan(ipAddress, macAddress)}).start()


                                        //WakeOnLan.sendWakeOnLan(ipAddress, macAddress)

                                    } catch (ex: NetworkOnMainThreadException) {
                                        print(ex.message)
                                    }


                                }


                            } else {
                                Toast.makeText(context, "No fue posible recuperar informacion del encendido del  Equipo, intentelo mas tarde", Toast.LENGTH_LONG).show()
                            }
                        }

                    })


                }
            }

        })


    }

    fun magicPacket(mac: String, ip: String, PORT: Int = 7) {
        // throws IllegalThreadStateException without declaration
        //val t = thread {
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
        //}
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




    fun StartNegocio(nomNegocio: String, idHardware: String, idNegocio: String, ipNegocio: String, iPuertoNegocio: String, iPLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()
                    val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                    val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> = negociosService.getEnciendeEquipo("$idHardware", "JWT $token", "application/json")
                    resultEnciendeEquipo.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
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
                            var desc = "No fue posible encender el equipo, por favor contacte a su administrador"
                            var dialogBuilder = android.app.AlertDialog.Builder(context)
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
                var dialogBuilder = android.app.AlertDialog.Builder(context)
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




    fun ShutdownNegocio(nomNegocio: String, idHardware: String, idNegocio: String, ipNegocio: String, iPuertoNegocio: String, iPLocal: String, tipoComunicacion: Int) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var Movil_Nom = Licenciadb[0].nommbre_movil

        if(tipoComunicacion == 0){

            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$iPLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

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

                        val getLogOffNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$iPLocal", databaseHelper_Data).create(APIService::class.java)

                        val result_LogOff: Call<String> = getLogOffNegocio.setStateServer("Shutdown", "1000002", "$Movil_Id", "$Movil_Nom", "$nomNegocio", "$resultado", "application/json")

                        result_LogOff.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null && resultado != "Not Authorized") {

                                    Toast.makeText(context, "El servidor se esta apagado", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                                }

                            }

                        })


                    } else {
                        Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()

                    }

                }

            })

            updBanderaNegocio(idNegocio)
        }else if(tipoComunicacion == 1){
            var parametros = "Shutdown"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"$parametros\"}")


        }





    }


    fun updBanderaNegocio(idNegocio: String){
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()

                    client.getEstatusNegocioSvr("$idNegocio", "JWT $token", "application/json")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            refreshFamiliasDB()
                            Toast.makeText(context, "Se actualizo estatus en Servidor de Licencias", Toast.LENGTH_LONG).show()
                        }, { throwable ->
                            Toast.makeText(context, "Error: ${throwable.message}", Toast.LENGTH_LONG).show()
                        }
                        )


                }
            }

        })
    }


    fun estatusActNegocio(id_accion: Int, id_origen: Int, NomNegocio: String,hardware_key: String,tipocomunicacion: Int,iprogressCardViewEquipos: ProgressBar){
        LoginActivity.isReloadViewMonitores = 0
        if(tipocomunicacion == 0){
            /*
            val accountsIntent = Intent(context, MonitorActivity::class.java)
            accountsIntent.putExtra("id_accion", id_accion.toString())
            accountsIntent.putExtra("id_origen", id_origen.toString())
            accountsIntent.putExtra("nombre_equipo", "$NomNegocio")
            accountsIntent.putExtra("hardware_key", "$hardware_key")
            accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
            context.startActivity(accountsIntent)

             */
        }else if(tipocomunicacion == 1){
            iprogressCardViewEquipos.visibility = View.VISIBLE
            var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo =  removeTrailingZeroes(idDispositivo)


            LoginActivity.estatusequipo_id_accion = id_accion
            LoginActivity.estatusequipo_id_origen = id_origen
            LoginActivity.idHardwareExpArc = hardware_key

            webSocketService!!.sendMessage("{\"id_hardware\":\"$hardware_key\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getMonitoreoEquipo\",\"parametros\":\"ALL\"}")

            Handler().postDelayed(Runnable {
                iprogressCardViewEquipos.visibility = View.GONE
            }, 3000)

        }




    }



    fun ExplorarDirectorio(ruta: String, idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int, ipLocal: String, tipocomunicacion: Int, iprogressCardViewEquipos: ProgressBar){
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        var rutaArchivosDir = ruta
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil




        if(tipocomunicacion == 0){

            val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
                "licencias@dsiin.com",
                "xhceibEd"
            )
            val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
            val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
            result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
                override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                    Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                    val resultado = response.body()
                    if (resultado != null) {
                        val token = resultado.token.toString()

                        val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                        val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                        resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                                Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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
                                            val accountsIntent = Intent(context, activity_directorio_negocios::class.java)
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
                                            context.startActivity(accountsIntent)

                                             */
                                        } else {

                                            Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        /*

                                        val accountsIntent = Intent(context, activity_directorio_negocios::class.java)
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
                                        context.startActivity(accountsIntent)

                                         */
                                    }


                                } else {
                                    /*
                                    val accountsIntent = Intent(context, activity_directorio_negocios::class.java)
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
                                    context.startActivity(accountsIntent)

                                     */
                                }
                                ///////////////
                            }

                        })


                    }
                }

            })

        }else if(tipocomunicacion == 1){
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace("\\\\","\\")
                rutaArchivosDir = rutaArchivosDir!!.replace("\\","\\\\")
                rutaArchivosDir  = rutaArchivosDir!!.replace("\\", "%2F")
            }
            if (rutaArchivosDir != null) {
                rutaArchivosDir = rutaArchivosDir!!.replace(":", "%3A")
            }

            iprogressCardViewEquipos.visibility = View.VISIBLE
            idHardwareExpArc = idHardware

            LoginActivity.nomEquipoExpArc = nomNegocio
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getArchivosEquipo\",\"parametros\":\"$rutaArchivosDir\"}")

            Handler().postDelayed(Runnable {
                iprogressCardViewEquipos.visibility = View.GONE
            }, 3000)
        }
    }



    fun ScreenshotServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int, iBovedaNegocios: String, ipLocal: String, tipocomunicacion: Int, iprogressCardViewEquipos: ProgressBar){

        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        var configApiVMEquipo = databaseHelper_Data.getControlEquiposAPIVM(idNegocio)


        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                    val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                    resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                            Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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

                                if (horaInicial != "SN" && horaFinal != "SN") {
                                    if (horaFinal == "00:00:00"){
                                        horaFinal = "23:59:59"
                                    }
                                    if (currenTime > horaInicial && currenTime < horaFinal && dia_semana == 1) {

                                        if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){

                                            iprogressCardViewEquipos.visibility = View.VISIBLE
                                            ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token, iprogressCardViewEquipos)
                                            Handler().postDelayed({
                                                if(  iprogressCardViewEquipos.isVisible){
                                                    iprogressCardViewEquipos.visibility = View.GONE
                                                } }, 5000)


                                        }else{
                                            if (tipocomunicacion == 0){
                                                /*
                                                val accountsIntent = Intent(context, ImageViewActivity::class.java)
                                                accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                                accountsIntent.putExtra("id_hardware", idHardware)
                                                accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                                accountsIntent.putExtra("ip_negocio", ipNegocio)
                                                accountsIntent.putExtra("ip_local", ipLocal)
                                                accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                                accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                                accountsIntent.putExtra("info", "")
                                                context.startActivity(accountsIntent)

                                                 */
                                            }else {
                                                LoginActivity.accionejecutandose = true
                                                iprogressCardViewEquipos.visibility = View.VISIBLE
                                                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                                                Handler().postDelayed({
                                                    if( iprogressCardViewEquipos.isVisible){
                                                        iprogressCardViewEquipos.visibility = View.GONE
                                                    }

                                                }, 5000)

                                            }
                                        }








                                    } else {

                                        Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
                                    }
                                } else {

                                    if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){

                                        iprogressCardViewEquipos.visibility = View.VISIBLE
                                        ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token, iprogressCardViewEquipos)
                                        Handler().postDelayed({
                                            if(  iprogressCardViewEquipos.isVisible){
                                                iprogressCardViewEquipos.visibility = View.GONE
                                            } }, 5000)

                                    }else{
                                        if (tipocomunicacion == 0) {
                                            /*
                                            val accountsIntent = Intent(context, ImageViewActivity::class.java)
                                            accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                            accountsIntent.putExtra("id_hardware", idHardware)
                                            accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                            accountsIntent.putExtra("ip_negocio", ipNegocio)
                                            accountsIntent.putExtra("ip_local", ipLocal)
                                            accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                            accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                            accountsIntent.putExtra("info", "")
                                            context.startActivity(accountsIntent)

                                             */
                                        }else{
                                            LoginActivity.accionejecutandose = true
                                            iprogressCardViewEquipos.visibility = View.VISIBLE
                                            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                                            Handler().postDelayed({
                                                if( iprogressCardViewEquipos.isVisible){
                                                    iprogressCardViewEquipos.visibility = View.GONE
                                                }

                                            }, 5000)

                                        }
                                    }




                                }


                            } else {

                                if (configApiVMEquipo.isNotEmpty() && configApiVMEquipo[0].screenshot_vm > 0){

                                    iprogressCardViewEquipos.visibility = View.VISIBLE
                                    ScreenshotServidorXAPI(idHardware, idNegocio, nomNegocio,tipocomunicacion, token, iprogressCardViewEquipos)
                                    Handler().postDelayed({
                                        if(  iprogressCardViewEquipos.isVisible){
                                            iprogressCardViewEquipos.visibility = View.GONE
                                        } }, 5000)

                                }else{

                                    if (tipocomunicacion == 0) {
                                        /*
                                        val accountsIntent = Intent(context, ImageViewActivity::class.java)
                                        accountsIntent.putExtra("bovedaSoftware", iBovedaNegocios)
                                        accountsIntent.putExtra("id_hardware", idHardware)
                                        accountsIntent.putExtra("ip_puerto", iPuertoNegocio.toString())
                                        accountsIntent.putExtra("ip_negocio", ipNegocio)
                                        accountsIntent.putExtra("ip_local", ipLocal)
                                        accountsIntent.putExtra("nombre_negocio", nomNegocio)
                                        accountsIntent.putExtra("tipo_comunicacion", tipocomunicacion.toString())
                                        accountsIntent.putExtra("info", "")
                                        context.startActivity(accountsIntent)

                                         */
                                    } else{
                                        LoginActivity.accionejecutandose = true
                                        iprogressCardViewEquipos.visibility = View.VISIBLE
                                        webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"chkUsuActivosWindows\",\"parametros\":\"screenshot\"}")
                                        Handler().postDelayed({
                                            if( iprogressCardViewEquipos.isVisible){
                                                iprogressCardViewEquipos.visibility = View.GONE
                                            }
                                        }, 5000)

                                    }
                                }



                            }
                            ///////////////
                        }

                    })


                } else {
                    Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
                }
            }

        })





    }

    fun ScreenshotServidorXAPI(idHardware: String, idNegocio: Int, nomNegocio: String ,tipocomunicacion: Int, token: String, iprogressCardViewEquipos: ProgressBar){


        val negociosService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val resultEnciendeEquipo: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> = negociosService.getScreenshotEquipoGcVm("$idHardware", "JWT $token", "application/json")
        resultEnciendeEquipo.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo> {
            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>) {
                val resultadoAccion = response.body()
                if (resultadoAccion != null) {
                    var respuesta = resultadoAccion.data.toString()
                    iprogressCardViewEquipos.visibility = View.GONE
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
                    context.startActivity(accountsIntent)

                     */



                }
            }

            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>, t: Throwable) {
                var desc = "No fue posible generar screenshot del equipo, por favor contacte a su administrador"
                var dialogBuilder = android.app.AlertDialog.Builder(context)
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




    fun chkDelArcDirServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int,
                             tipoEliminacio: String, rutaEliminacion: String, archivoEliminacion: String, ipLocal: String,  tipoComunicacion: Int){


        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil


        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )

        val userService: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)
        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado = response.body()
                if (resultado != null) {
                    val token = resultado.token.toString()


                    val getHorario: APIService = RestEngine.getRestEngine(databaseHelper_Data, context).create(APIService::class.java)
                    val resultHorario: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> = getHorario.getHorarioMovil(Movil_Id, idNegocio, "$idDispositivo", "JWT $token", "application/json")
                    resultHorario.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil> {
                        override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>, t: Throwable) {
                            Toast.makeText(context, "Error al recuperar horarios movil", Toast.LENGTH_LONG).show()
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
                                            tipoEliminacio, rutaEliminacion, archivoEliminacion, ipLocal, tipoComunicacion)
                                    } else {

                                        Toast.makeText(context, "El dispositivo se encuentra fuera del horario laboral para este negocio", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
                }
            }

        })






    }

    fun DelArcDirServidor(idHardware: String, idNegocio: Int, nomNegocio: String, ipNegocio: String, iPuertoNegocio: Int,
                          tipoEliminacio: String, rutaEliminacion: String, archivoEliminacion: String, ipLocal: String, tipoComunicacion: Int){

        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()



        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipNegocio, iPuertoNegocio.toString(), ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val startAppCal: APIService = RestEngine.getRestEngineNegocio(ipNegocio, iPuertoNegocio.toString(), "$ipLocal", databaseHelper_Data).create(APIService::class.java)
                        val result_app: Call<String> = startAppCal.delArchDirNegocio("$rutaEliminacion", "$archivoEliminacion", "$tipoEliminacio", false, "$resultado", "application/json")
                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                Toast.makeText(context, "Error al eliminar elemento del Servidor", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                if (resultado != null) {
                                    Toast.makeText(context, "$resultado", Toast.LENGTH_LONG).show()
                                }

                            }

                        })

                    }
                }

            })
        }else if(tipoComunicacion == 1){

            var ruta = "$rutaEliminacion|$archivoEliminacion|$tipoEliminacio|false"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setDelArchivosEquipo\",\"parametros\":\"$ruta\"}")



        }







    }


    fun ConsultaInfoProgramaExec(accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List, idHardware: String, ipNegocio: String, iPuertoNegocio: Int, iNomNegocio: String, ip_local: String, tipoComunicacion: Int){
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ip_local", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

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
                        val resultAccion: Call<ResponseBody> = getTokenNegocio.getInfoProgramSvr("${accion.estado_programa_nombre}", "$resultado", "application/json")
                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Toast.makeText(context, "Error al recuperar informacion del porgrama", Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                val resultJson = response.body()!!.string()

                                var dialogBuilder = AlertDialog.Builder(context)
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
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaProgramaEquipo\",\"parametros\":\"$parametros\"}")

        }




    }

    fun ConsultaDinamicaBdNegocio(accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List, idHardware: String, ipNegocio: String, ipLocal: String, iPuertoNegocio: Int, iNomNegocio: String, tipoComunicacion: Int, iprogressCardViewEquipos: ProgressBar){

        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

        if(tipoComunicacion == 0){
            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio("$ipNegocio", "$iPuertoNegocio", "$ipLocal", databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            databaseHelper_Data.deleteinfoAccion()
            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
                    dialogBuilder.setMessage(desc)
                        .setCancelable(false)
                        .setNegativeButton("Aceptar", DialogInterface.OnClickListener { view, _ ->

                            view.dismiss()
                        })
                    var alert = dialogBuilder.create()
                    alert.setTitle("")
                    alert.show()
                    //Toast.makeText(context, "Error al recuperar token del negocio", Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val resultado = response.body()
                    if (resultado != null && resultado != "Not Authorized") {

                        val resultAccion: Call<ResponseBody> = getTokenNegocio.getConsultaDinamicaBdSvr("${accion.origen}", "${accion.id}",
                            "${accion.id_negocio_id}", "${accion.tipo_id}", "$resultado", "application/json")

                        resultAccion.enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                if (response.code() == 500) {


                                    Toast.makeText(context, "Error al momento de ejecutar el script en la base de datos, por favor revise la configuracion de la accion, Error: ${response.message()}", Toast.LENGTH_LONG).show()
                                    return
                                }
                                if (response.code() == 404) {
                                    Toast.makeText(context, "Error al momento de ejecutar el script en la base de datos, por favor revise la configuracion de la accion, Error: ${response.message()}", Toast.LENGTH_LONG).show()
                                    return
                                }
                                val resultJson = response.body()!!.string()
                                if (resultJson.contains("INSERT_OK")) {

                                    Toast.makeText(context, "Se inserto registro(s) correctamente", Toast.LENGTH_LONG).show()

                                } else if (resultJson.contains("DELETE_OK")) {
                                    Toast.makeText(context, "Se eliminaron registro(s) correctamente", Toast.LENGTH_LONG).show()
                                } else if (resultJson.contains("UPDATE_OK")) {
                                    Toast.makeText(context, "Se actualizan registro(s) correctamente", Toast.LENGTH_LONG).show()
                                } else {

                                    var newInfoAccion =
                                        com.apptomatico.app_movil_kotlin_v3.model.InfoAccion(
                                            result = resultJson
                                        )
                                    databaseHelper_Data.addInfoAccion(newInfoAccion)
//                                    val accountsIntent = Intent(context, ConsultaDinamicaActivity::class.java)
//                                    accountsIntent.putExtra("titulo_consulta", "${accion.titulo_aplicacion}")
//                                    accountsIntent.putExtra("nombre_negocio", "$iNomNegocio")
//                                    context.startActivity(accountsIntent)
                                }


                            }


                        })

                    } else {
                        Toast.makeText(context, "No tiene permisos para realizar esta accion", Toast.LENGTH_LONG).show()
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
                        val resultado = listaActualizada.joinToString("-") {
                            "${it.campo}=${it.valor}(${it.tipo})"
                        }
                        resParams = resultado

                        iprogressCardViewEquipos.visibility = View.VISIBLE
                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.accionejecutandose = true
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo = "$idHardware"
                        LoginActivity.bd_parametros_accion =
                            "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"

                        val parametros = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                        webSocketService?.sendMessage(
                            "{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}"
                        )

                        Handler().postDelayed({
                            iprogressCardViewEquipos.visibility = View.GONE
                        }, 3000)
                    } else {
                        Toast.makeText(context, "Acción cancelada", Toast.LENGTH_SHORT).show()
                    }
                }

            }else{

                iprogressCardViewEquipos.visibility = View.VISIBLE
                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.accionejecutandose = true
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo ="$idHardware"
                LoginActivity.bd_parametros_accion = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                var parametros = "${accion.origen}|${accion.id}|${accion.id_negocio_id}|${accion.tipo_id}|get|NULL|$resParams"
                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")

                Handler().postDelayed(Runnable {
                    iprogressCardViewEquipos.visibility = View.GONE
                }, 3000)

            }
        }




    }


    fun mostrarDialogoTablaDeParametros(
        lista: ArrayList<ItemParametrosAccDB>,
        consulta: String,
        onComplete: (ArrayList<ItemParametrosAccDB>?) -> Unit
    ) {
        val recyclerView = RecyclerView(context).apply {
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

        val container = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            val padding = (16 * context.resources.displayMetrics.density).toInt() // 16dp
            setPadding(padding, 0, padding, 0)
        }

        val headerView = LayoutInflater.from(context).inflate(R.layout.item_parametro_header, container, false)
        container.addView(headerView)
        container.addView(recyclerView)

        val scrollView = ScrollView(context)
        scrollView.addView(container)

        val dialog = AlertDialog.Builder(context)
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
                        Toast.makeText(context, "Falta valor para ${item.campo}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    when (item.tipo?.uppercase()) {
                        "INTEGER" -> if (valor.toIntOrNull() == null) {
                            Toast.makeText(context, "${item.campo} debe ser número entero", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "DECIMAL" -> if (valor.toDoubleOrNull() == null) {
                            Toast.makeText(context, "${item.campo} debe ser número decimal", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        "TEXT" -> {} // Nada extra
                        else -> {
                            Toast.makeText(context, "Tipo no reconocido: ${item.tipo}", Toast.LENGTH_SHORT).show()
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














    fun ConsultaApiExtNegocio(accion: com.apptomatico.app_movil_kotlin_v3.model.AcionesDinamicas_List, idHardware: String, ipNegocio: String, ipLocal: String, iPuertoNegocio: Int, iNomNegocio: String, tipoComunicacion: Int, iprogressCardViewEquipos: ProgressBar){

        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()

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

                        iprogressCardViewEquipos.visibility = View.VISIBLE
                        databaseHelper_Data.deleteinfoAccion()
                        LoginActivity.accionejecutandose = true
                        LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                        LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                        LoginActivity.bd_hardware_equipo ="$idHardware"
                        LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                        var parametros = "${accion.id}|$resParams"
                        webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")

                        Handler().postDelayed(Runnable {
                            iprogressCardViewEquipos.visibility = View.GONE
                        }, 3000)
                    }else{
                        Toast.makeText(context, "Accion cancelada", Toast.LENGTH_SHORT).show()
                    }



                }
            }else{

                iprogressCardViewEquipos.visibility = View.VISIBLE
                databaseHelper_Data.deleteinfoAccion()
                LoginActivity.accionejecutandose = true
                LoginActivity.bd_titulo_consulta = "${accion.titulo_aplicacion}"
                LoginActivity.bd_nombre_equipo = "$iNomNegocio"
                LoginActivity.bd_hardware_equipo ="$idHardware"
                LoginActivity.bd_parametros_accion = "${accion.id}|$resParams"
                var parametros = "${accion.id}|$resParams"
                webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getApiExtEquipo\",\"parametros\":\"$parametros\"}")

                Handler().postDelayed(Runnable {
                    iprogressCardViewEquipos.visibility = View.GONE
                }, 3000)

            }
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
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "S/N"
        }

        val telNumber =   "S/N"
        if (telNumber != null){
            return telNumber
        }else{

            return "S/N"

        }


    }

    ///Funcion para ejecutar un programa

    fun sendTerminaProcesoEquipo(accion: String, idnegocio: String, ipPublica: String, ipLocal: String, idHardware: String, iPuerto: String, ruta_app: String, nombre_app: String, parametros_app: String, tipoComunicacion: Int ) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if(tipoComunicacion == 0){

            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
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
                                    Toast.makeText(context, "Se finalizo en el equipo", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                Toast.makeText(context, "Se finalizo en el equipo", Toast.LENGTH_LONG).show()
                            }

                        })


                    } else {

                        Toast.makeText(context, "No esta autorizado a realizar esta accion", Toast.LENGTH_LONG).show()
                    }

                }

            })


        }else if(tipoComunicacion == 1){


            var parametros = "$nombre_app"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"setTerminaProcesoEquipo\",\"parametros\":\"$parametros\"}")


        }


    }
    fun showExecuteProgramServer(accion: String, idnegocio: String, ipPublica: String, ipLocal: String, idHardware: String, iPuerto: String, ruta_app: String, nombre_app: String, parametros_app: String, tipoComunicacion: Int, usuarioejecucion: String, aplicacionusr: String, aplicacionpwd: String ) {
        var idDispositivo: String = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val phoneMovil:String = getMovilData().toString()
        val phoneSubscriberId = getMovilIdSuscriber()
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil
        var nombre_Movil = Licenciadb[0].nommbre_movil


        if(tipoComunicacion == 0){


            val getTokenNegocio: APIService = RestEngine.getRestEngineNegocio(ipPublica, iPuerto, ipLocal, databaseHelper_Data).create(APIService::class.java)
            val result_token: Call<String> = getTokenNegocio.getTokenNegocio("$idHardware", "application/json")

            result_token.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    var desc = "No fue posible conectar con el equipo por alguna de esta razones:  \n\n 1) El equipo esta apagado \n\n 2) El equipo no esta disponible en la red \n\n 3) El dispositivo se encuentra desconectado de Internet"
                    var dialogBuilder = android.app.AlertDialog.Builder(context)
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
                        val result_app: Call<String> = startAppCal.startAPPServidor("$Movil_Id", "$nombre_Movil", "$ruta_app", "$nombre_app", "$parametros_app", "$resultado", "application/json")

                        result_app.enqueue(object : Callback<String> {
                            override fun onFailure(call: Call<String>, t: Throwable) {
                                if (t.message == "timeout") {
                                    Toast.makeText(context, "La apliacion se esta ejecutando", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                                }

                            }

                            override fun onResponse(call: Call<String>, response: Response<String>) {
                                val resultado = response.body()
                                Toast.makeText(context, "La apliacion se esta ejecutando", Toast.LENGTH_LONG).show()
                            }

                        })


                    } else {

                        Toast.makeText(context, "No esta autorizado a realizar esta accion", Toast.LENGTH_LONG).show()
                    }

                }

            })


        }else if(tipoComunicacion == 1){

            LoginActivity.accionejecutandose = true
            var parametros = "$ruta_app|$nombre_app|$parametros_app|$usuarioejecucion|$aplicacionusr|$aplicacionpwd"
            webSocketService!!.sendMessage("{\"id_hardware\":\"$idHardware\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"exeAplicacionEquipo\",\"parametros\":\"$parametros\"}")


        }


    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //val idView: TextView = view.findViewById(R.id.item_number)
        val contentView: TextView = view.findViewById(R.id.NegocioNombreContent)
        val contentiplocal: TextView = view.findViewById(R.id.iplocal)
        val cardnegocio: CardView = view.findViewById(R.id.cardNegocio)
        val btnExecutar: ImageView = view.findViewById(R.id.btnExecuteNegocioApp)

        //val btnLogOff: ImageView = view.findViewById(R.id.btnLogOff)
        //val btnReboot: ImageView = view.findViewById(R.id.btnReboot)
        //val btnShutdown: ImageView = view.findViewById(R.id.btnShutdown)
        //val btnDirectorio: ImageView = view.findViewById(R.id.btnDirectorio)
        //val btnScreenShot: ImageView = view.findViewById(R.id.btnScreenShot)
        val linearNegAccionesList: LinearLayout = view.findViewById(R.id.linearNegAccionesList)
        val linearNegAccionesDescList: LinearLayout = view.findViewById(R.id.linearNegAccionesDescList)
        val relativeLeftButtonsEquipo: RelativeLayout = view.findViewById(R.id.relativeLeftButtonsEquipo)
        //val btnMasAcciones: ImageView = view.findViewById(R.id.btnMasAcciones)
        // val btnEntServer: ImageView = view.findViewById(R.id.btnEstServer)
        val fecUtlAct: TextView = view.findViewById(R.id.fecUtlAct)
        val horaUtlAct: TextView = view.findViewById(R.id.horaUtlAct)
        val btnInocoUnimovil: ImageView = view.findViewById(R.id.btnUnimovil)

        val btnInocoArqWS: ImageView = view.findViewById(R.id.btnArqWsIcon)
        val btnInocoArqAPI: ImageView = view.findViewById(R.id.btnArqApiIcon)
        val btnInocoNube: ImageView = view.findViewById(R.id.btnNubeEquipo)
        val btnInocoFisico: ImageView = view.findViewById(R.id.btnFisicoEquipo)


        val btnAyuda: ImageView = view.findViewById(R.id.btnAyuda)
        val iprogressCardViewEquipos: ProgressBar = view.findViewById(R.id.progressCardViewEquipos)

        //val linearlayoutmain: ConstraintLayout = view.findViewById(R.id.main)

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }


    fun showUpdateDialog(holder: ViewHolder, valores: com.apptomatico.app_movil_kotlin_v3.model.Negocios_List) {

    }

    fun updateUsuario(valores: com.apptomatico.app_movil_kotlin_v3.model.Usuario_Upd) {

    }






    private fun checkForInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

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


    //////


    private fun getIpAddress(): String? {
        var ip = ""
        try {
            val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface
                .getNetworkInterfaces()
            while (enumNetworkInterfaces.hasMoreElements()) {
                val networkInterface: NetworkInterface = enumNetworkInterfaces
                    .nextElement()
                val enumInetAddress: Enumeration<InetAddress> = networkInterface
                    .getInetAddresses()
                while (enumInetAddress.hasMoreElements()) {
                    val inetAddress: InetAddress = enumInetAddress.nextElement()
                    if (inetAddress.isSiteLocalAddress()) {
                        ip += inetAddress.getHostAddress()
                    }
                }
            }
        } catch (e: SocketException) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            ip += """
                Something Wrong! ${e.toString().toString()}
                
                """.trimIndent()
        }



        return ip
    }




    class GetPublicIP : AsyncTask<String?, String?, String?>() {
        protected override fun doInBackground(vararg strings: String?): String {
            var publicIP = ""
            try {
                val s = Scanner(
                    URL(
                        "https://api.ipify.org")
                        .openStream(), "UTF-8")
                    .useDelimiter("\\A")
                publicIP = s.next()
                println("My current IP address is $publicIP")
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


    private fun displayTooltipLeftBottonCloud(layout: ImageView, iTitulo: String, iSubTitulo: String, gr: Int) {


        var tooltip1: SimpleTooltip = SimpleTooltip.Builder(context)
            .anchorView(layout)
            .text(iSubTitulo)
            .gravity(gr)
            .dismissOnOutsideTouch(true)
            .dismissOnInsideTouch(false)
            .modal(true)
            .animated(false)
            .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
            .maxWidth(R.dimen.simpletooltip_max_width)
            .contentView(R.layout.tooltip_custom, R.id.tv_text)
            .focusable(true)
            .build()
        val titulo = tooltip1.findViewById<TextView>(R.id.wz_titulo)
        titulo.text = iTitulo

        val ed = tooltip1.findViewById<TextView>(R.id.ed_text)
        ed.text = ""


        val btnClose = tooltip1.findViewById<View>(R.id.btn_close)
        btnClose.setOnClickListener {
            if (tooltip1.isShowing) tooltip1.dismiss()
        }
        tooltip1.findViewById<View>(R.id.btn_next).visibility = View.GONE

        tooltip1!!.show()





    }


    private fun displayTooltipLeftBotton(layout: ImageView, iTitulo: String, iSubTitulo: String, gr: Int) {


        var tooltip1: SimpleTooltip = SimpleTooltip.Builder(context)
            .anchorView(layout)
            .text(iSubTitulo)
            .gravity(gr)
            .dismissOnOutsideTouch(true)
            .dismissOnInsideTouch(false)
            .modal(true)
            .animated(false)
            .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
            .maxWidth(R.dimen.simpletooltip_max_width)
            .contentView(R.layout.tooltip_custom, R.id.tv_text)
            .focusable(true)
            .build()

        val titulo = tooltip1.findViewById<TextView>(R.id.wz_titulo)
        titulo.text = iTitulo

        val ed = tooltip1.findViewById<TextView>(R.id.ed_text)
        ed.text = ""


        val btnClose = tooltip1.findViewById<View>(R.id.btn_close)
        btnClose.setOnClickListener {
            if (tooltip1.isShowing) tooltip1.dismiss()
        }
        tooltip1.findViewById<View>(R.id.btn_next).visibility = View.GONE

        tooltip1!!.show()





    }

    private fun displayTooltip2(layout: LinearLayout,layoutDesc: LinearLayout, mensaje: String, itituloHeader: String, position: Int){
        val child: View = layout?.getChildAt(position) ?: return
        var idescripcion = ""
        var iTitulo = ""

        if (child is ImageView) {
            //val lblDescripcion: TextView? = layoutDesc?.getChildAt(position) as TextView?

            if(position >= 5){
                idescripcion ="Mas acciones"
                iTitulo = ""
            }else{
                //val iDsSplit = lblDescripcion.text.toString().split("|")
                val iDsSplit = child.contentDescription.split("|")
                idescripcion = iDsSplit[0]
                iTitulo = iDsSplit[1]
            }

            var tooltip1: SimpleTooltip = SimpleTooltip.Builder(context)
                .anchorView(child)
                .text(idescripcion)
                .gravity(Gravity.TOP)
                .dismissOnOutsideTouch(true)
                .dismissOnInsideTouch(false)
                .modal(true)
                .animated(false)
                .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
                .contentView(R.layout.tooltip_custom, R.id.tv_text)
                .focusable(true)
                .build()

            val titulo = tooltip1.findViewById<TextView>(R.id.wz_titulo)
            titulo.text = itituloHeader

            val ed = tooltip1.findViewById<TextView>(R.id.ed_text)
            ed.text = iTitulo


            val btnClose = tooltip1.findViewById<View>(R.id.btn_close)
            btnClose.setOnClickListener {
                if (tooltip1.isShowing) tooltip1.dismiss()
            }

            val btnAtras = tooltip1.findViewById<View>(R.id.btn_back)
            if (position > 0){
                btnAtras.visibility = View.VISIBLE
                btnAtras.setOnClickListener {
                    if (tooltip1.isShowing) tooltip1.dismiss()
                    displayTooltip2(layout, layoutDesc,"", itituloHeader,position - 1)
                }

            }else{
                btnAtras.visibility = View.GONE
            }

            tooltip1.findViewById<View>(R.id.btn_next).setOnClickListener {
                if (tooltip1.isShowing) tooltip1.dismiss()
                displayTooltip2(layout, layoutDesc,"",itituloHeader,position + 1)
            }

            tooltip1!!.show()

        }

    }

    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(valuesListEquipos, i, i + 1)

                var c = 0
                for (element in valuesListEquipos) {
                    //Log.i("POSITION", element.nombre)
                    databaseHelper_Data.updateOrdenamientoEquipos(element.id_negocio_id, c)
                    c += 1
                }
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(valuesListEquipos, i, i - 1)

                var c = 0
                for (element in valuesListEquipos) {
                    //Log.i("POSITION", element.nombre)
                    databaseHelper_Data.updateOrdenamientoEquipos(element.id_negocio_id, c)
                    c += 1
                }

            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRowSelected(myViewHolder: ViewHolder?) {

    }

    override fun onRowClear(myViewHolder: ViewHolder?) {

    }


////

}
