package com.apptomatico.app_movil_kotlin_v3.TapConfiguraciones

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXFade
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltipUtils

class configuracion_tap1: Fragment() {
    private var btnMenosTmAlertas: Button? = null
    private var txtTmAlertas: TextInputEditText? = null
    private var btnMasTmAlertas: Button? = null
    private var btnGuardarConfGnrl: Button? = null
    private var btnCancelConfGnrl: Button? = null
    private var btnRestauraValoresDefecto: TextView? = null

    private var btnAyudaAlertasupdate: ImageView? = null
    private var btnAyudaAlertasmaximas: ImageView? = null
    private var btnAyudaPausaAlertas: ImageView? = null
    private var btnAyudaLimiteAlertas: ImageView? = null


    private var btnMenosTopAlertas: Button? = null
    private var txtTopAlertas: TextInputEditText? = null
    private var btnMasTopAlertas: Button? = null


    private var btnMenosPausaAlertas: Button? = null
    private var textInputEditPausaAlertas: TextInputEditText? = null
    private var btnMasPausaAlertas: Button? = null


    private var btnMenosLimiteAlertas: Button? = null
    private var textInputEditLimiteAlertas: TextInputEditText? = null
    private var btnMasLimiteAlertas: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion1, container, false)
        val databaseHelper = DatabaseHelper.getInstance(container!!.context)
        var titulo1  = view.findViewById(R.id.title2) as TextView
        var titulo2  = view.findViewById(R.id.title21) as TextView
        var titulo3 = view.findViewById(R.id.title211) as TextView
        var titulo4 = view.findViewById(R.id.title212) as TextView
        btnMenosTmAlertas = view.findViewById(R.id.btnMenosTmAlertas) as Button
        txtTmAlertas = view.findViewById(R.id.textInputEditTimeAlertas) as TextInputEditText
        btnMasTmAlertas = view.findViewById(R.id.btnMasTmAlertas) as Button
        btnGuardarConfGnrl = view.findViewById(R.id.appCompatButtonUpdConfGnrl) as Button
        btnCancelConfGnrl = view.findViewById(R.id.appCompatButtonCancelarConfGnrl) as Button
        btnRestauraValoresDefecto = view.findViewById(R.id.btnRestauraValoresDefecto) as TextView

        btnAyudaAlertasupdate = view.findViewById(R.id.btnAyudaAlertasupdate) as ImageView
        btnAyudaAlertasmaximas = view.findViewById(R.id.btnAyudaAlertasmaximas) as ImageView
        btnAyudaPausaAlertas = view.findViewById(R.id.btnAyudaPausaAlertas) as ImageView
        btnAyudaLimiteAlertas = view.findViewById(R.id.btnAyudaLimiteAlertas) as ImageView

        btnMenosTopAlertas = view.findViewById(R.id.btnMenosTopAlertas) as Button
        txtTopAlertas = view.findViewById(R.id.textInputEditTopAlertas) as TextInputEditText
        btnMasTopAlertas = view.findViewById(R.id.btnMasTopAlertas) as Button



        btnMenosPausaAlertas = view.findViewById(R.id.btnMenosPausaAlertas) as Button
        textInputEditPausaAlertas = view.findViewById(R.id.textInputEditPausaAlertas) as TextInputEditText
        btnMasPausaAlertas = view.findViewById(R.id.btnMasPausaAlertas) as Button


        btnMenosLimiteAlertas = view.findViewById(R.id.btnMenosLimiteAlertas) as Button
        textInputEditLimiteAlertas = view.findViewById(R.id.textInputEditLimiteAlertas) as TextInputEditText
        btnMasLimiteAlertas = view.findViewById(R.id.btnMasLimiteAlertas) as Button


        btnRestauraValoresDefecto!!.paintFlags = btnRestauraValoresDefecto!!.paintFlags  or Paint.UNDERLINE_TEXT_FLAG


        var configuraciondefault  = databaseHelper.getConfiguracionDefaultMovil()
        var intervaloAlt = databaseHelper.getConfiguracionMovil_IntervaloTmAlt()
        var ipausaAlt = databaseHelper.getControlPausaAlertas_IntervaloTmAlt()
        var iLimiteHis = databaseHelper.getHistoricoAlertas()


        txtTmAlertas!!.setText(intervaloAlt)
        var topAlerta = databaseHelper.getTopAlertas()
        if (topAlerta == null){
            topAlerta = 50
        }
        txtTopAlertas!!.setText("$topAlerta")

        var pausAlt = ipausaAlt[0].pa_tiempo
        if( pausAlt == null){
            pausAlt = 60
        }

        if(iLimiteHis == null){
            iLimiteHis = 15
        }

        textInputEditLimiteAlertas!!.setText("$iLimiteHis")


        textInputEditPausaAlertas!!.setText("$pausAlt")


        btnRestauraValoresDefecto!!.setOnClickListener {
            btnRestauraValoresDefecto!!.animationXFade(Fade.FADE_IN_DOWN)
            txtTmAlertas!!.setText("${configuraciondefault[0].get_conf_movil_alertas}")
            txtTopAlertas!!.setText("${configuraciondefault[0].get_conf_movil_alertas_recientes}")
            textInputEditPausaAlertas!!.setText("3600")
        }


        btnMenosTmAlertas!!.setOnClickListener {
            btnMenosTmAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual = txtTmAlertas!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTmAlertas!!.setText(valActual)
        }

        btnMasTmAlertas!!.setOnClickListener {
            btnMasTmAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual = txtTmAlertas!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTmAlertas!!.setText(valActual)
        }





        btnMenosTopAlertas!!.setOnClickListener {
            btnMenosTopAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual = txtTopAlertas!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTopAlertas!!.setText(valActual)
        }

        btnMasTopAlertas!!.setOnClickListener {
            btnMasTopAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual = txtTopAlertas!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTopAlertas!!.setText(valActual)
        }



        btnMenosPausaAlertas!!.setOnClickListener {
            btnMenosPausaAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual =  textInputEditPausaAlertas!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditPausaAlertas!!.setText(valActual)
        }

        btnMasPausaAlertas!!.setOnClickListener {
            btnMasPausaAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual =  textInputEditPausaAlertas!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditPausaAlertas!!.setText(valActual)
        }


        //**********************

        btnMenosLimiteAlertas!!.setOnClickListener {
            btnMenosLimiteAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual =  textInputEditLimiteAlertas!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 1){
                valActual = "1"
            }
            textInputEditLimiteAlertas!!.setText(valActual)
        }

        btnMasLimiteAlertas!!.setOnClickListener {
            btnMasLimiteAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            var valActual =  textInputEditLimiteAlertas!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 1){
                valActual = "1"
            }
            textInputEditLimiteAlertas!!.setText(valActual)
        }



        //**********************





        btnCancelConfGnrl!!.setOnClickListener {
            btnCancelConfGnrl!!.animationXFade(Fade.FADE_IN_DOWN)
            activity?.finish()
        }

        btnGuardarConfGnrl!!.setOnClickListener{
            btnGuardarConfGnrl!!.animationXFade(Fade.FADE_IN_DOWN)

            if(txtTmAlertas!!.text.toString() == "" ){
                Toast.makeText(container!!.context, "Actualizacion de alertas - el valor establecido debe ser mayor a 5", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((txtTmAlertas!!.text.toString()).toLong() < 5){
                Toast.makeText(container!!.context, "Actualizacion de alertas - el valor establecido debe ser mayor a 5", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((txtTmAlertas!!.text.toString()).toLong() > 3600){
                Toast.makeText(container!!.context, "Actualizacion de alertas - el valor maximo permitido es 1 hora (3600 segundos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if(txtTopAlertas!!.text.toString() == "" ){
                Toast.makeText(container!!.context, "Alertas recientes - el valor minimo permitido para el top es de 5 alertas", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((txtTopAlertas!!.text.toString()).toInt() < 5){
                Toast.makeText(container!!.context, "Alertas recientes - el valor minimo permitido para el top es de 5 alertas", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((txtTopAlertas!!.text.toString()).toInt() > 150){
                Toast.makeText(container!!.context, "Alertas recientes - el valor maximo permitido para el top es de 150 alertas", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((textInputEditPausaAlertas!!.text.toString()).toInt() < 5){
                Toast.makeText(container!!.context, "Pausar Alertas - el valor minimo permitido es de 5 alertas", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((textInputEditLimiteAlertas!!.text.toString()).toInt() < 1){
                Toast.makeText(container!!.context, "Historico Alertas - el valor minimo permitido para historico es de 1 dia", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            databaseHelper.addControlEquipos()
            databaseHelper.updConfiguracionMovilAlertas(txtTmAlertas!!.text.toString().toInt())
            databaseHelper.updConfiguracionTopAlertas(txtTopAlertas!!.text.toString().toInt())


            databaseHelper.addControlPausaAlertas()
            databaseHelper.updControlPausaAlertas(textInputEditPausaAlertas!!.text.toString().toInt())


            databaseHelper.addControlHistoricoAlertas()
            databaseHelper.updConfiguracionHistoricoALertas(textInputEditLimiteAlertas!!.text.toString().toInt())


            // databaseHelper.addControlTopAlertas()
            // databaseHelper.updConfiguracionTopAlertas(txtTopAlertas!!.text.toString().toInt())
            Toast.makeText(container!!.context, "Se guardo la configuracion de forma correcta", Toast.LENGTH_LONG).show()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()

        }




        btnAyudaAlertasupdate!!.setOnClickListener {
            btnAyudaAlertasupdate!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context, txtTmAlertas!!, "Configura cada cuánto tiempo requieres que se actualice la sección de alertas en tu móvil.\n","Valor máximo 3600 segundos", titulo1.text.toString())

        }

        btnAyudaAlertasmaximas!!.setOnClickListener {
            btnAyudaAlertasmaximas!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context, txtTopAlertas!!, "Configura la cantidad de alertas que requieres descargar del servidor cuando tu equipo no cuente con Internet.\n","Valor máximo 150 alertas", titulo2.text.toString())

        }


        btnAyudaPausaAlertas!!.setOnClickListener {
            btnAyudaPausaAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context,  textInputEditPausaAlertas!!, "Indica el tiempo en que las alertas estaran pausadas en el movil\n","no se recibiran alertas en el tiempo especificado", titulo3.text.toString())

        }


        btnAyudaLimiteAlertas!!.setOnClickListener {
            btnAyudaLimiteAlertas!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context,  textInputEditLimiteAlertas!!, "Indica la cantidad de días de historial de alertas que se almacenan en el móvil\n","las alertas se eliminarán automáticamente después de los días de historial establecidos", titulo4.text.toString())

        }




        return view
    }

    private fun displayTooltip2(context: Context,layout: TextInputEditText, mensaje: String, submensaje: String, titulo:String){
        var tooltip1: SimpleTooltip = SimpleTooltip.Builder(context)
            .anchorView(layout)
            .text(mensaje)
            .gravity(Gravity.CENTER)
            .dismissOnOutsideTouch(true)
            .dismissOnInsideTouch(false)
            .modal(true)
            .animated(false)
            .animationPadding(SimpleTooltipUtils.pxFromDp(50f))
            .contentView(R.layout.tooltip_custom_configuraciones, R.id.tv_text)
            .focusable(true)
            .build()
        val tit = tooltip1.findViewById<TextView>(R.id.wz_titulo)
        tit.text = titulo
        val ed = tooltip1.findViewById<TextView>(R.id.ed_text)
        ed.text = submensaje



        val btnClose = tooltip1.findViewById<View>(R.id.btn_close)
        btnClose.setOnClickListener {
            if (tooltip1.isShowing) tooltip1.dismiss()
        }

        tooltip1!!.show()
    }




}