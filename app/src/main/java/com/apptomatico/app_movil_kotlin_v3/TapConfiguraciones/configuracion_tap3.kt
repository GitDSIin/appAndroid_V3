package com.apptomatico.app_movil_kotlin_v3.TapConfiguraciones

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

class configuracion_tap3 : Fragment() {
    private var btnGuardarConfGnrl: Button? = null
    private var btnCancelConfGnrl: Button? = null
    private var btnMenosTmEquipo: Button? = null
    private var textInputEditTimeEquipo: TextInputEditText? = null
    private var btnMasTmEquipo: Button? = null
    private var btnRestauraValoresDefecto: TextView? = null
    private var btnAyudaNegociosUpdate: ImageView? = null
    private var btnAyudaActualizacionEquipos: ImageView? = null


    private var textInputEditActualizacionEquipos: TextInputEditText? = null
    private var btnMenosActualizacionEquipos: Button? = null
    private var btnMasActualizacionEquipos: Button? = null






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion3, container, false)
        val databaseHelper = DatabaseHelper.getInstance(container!!.context)
        var titulo1  = view.findViewById(R.id.title2) as TextView
        var titulo2  = view.findViewById(R.id.title21) as TextView
        btnMenosTmEquipo = view.findViewById(R.id.btnMenosTmEquipo) as Button
        textInputEditTimeEquipo = view.findViewById(R.id.textInputEditTimeEquipo) as TextInputEditText
        btnMasTmEquipo = view.findViewById(R.id.btnMasTmEquipo) as Button
        btnRestauraValoresDefecto =  view.findViewById(R.id.btnRestauraValoresDefecto) as TextView
        btnGuardarConfGnrl =  view.findViewById(R.id.appCompatButtonUpdConfGnrl) as Button
        btnCancelConfGnrl = view.findViewById(R.id.appCompatButtonCancelarConfGnrl) as Button

        btnAyudaNegociosUpdate = view.findViewById(R.id.btnAyudaNegociosUpdate ) as ImageView
        btnAyudaActualizacionEquipos = view.findViewById(R.id.btnAyudaActualizacionEquipos) as ImageView

        textInputEditActualizacionEquipos = view.findViewById(R.id.textInputEditActualizacionEquipos) as TextInputEditText
        btnMenosActualizacionEquipos =  view.findViewById(R.id.btnMenosActualizacionEquipos) as Button
        btnMasActualizacionEquipos =  view.findViewById(R.id.btnMasActualizacionEquipos) as Button




        var intervaloEquipo = databaseHelper.getConfiguracionMovil_IntervaloTmEquipo()
        textInputEditTimeEquipo!!.setText(intervaloEquipo)


        var intervaloActEquipo = databaseHelper.getIntervaloActualizacionEquipos()
        textInputEditActualizacionEquipos!!.setText(intervaloActEquipo.toString())


        btnRestauraValoresDefecto!!.paintFlags = btnRestauraValoresDefecto!!.paintFlags  or Paint.UNDERLINE_TEXT_FLAG

        var configuraciondefault  = databaseHelper.getConfiguracionDefaultMovil()

        btnRestauraValoresDefecto!!.setOnClickListener {
            btnRestauraValoresDefecto!!.animationXFade(Fade.FADE_IN_DOWN)
            textInputEditTimeEquipo!!.setText("${configuraciondefault[0].get_conf_movil_estatus_equipos}")
            textInputEditActualizacionEquipos!!.setText("${configuraciondefault[0].get_conf_movil_info_equipos}")
        }


        btnMenosTmEquipo!!.setOnClickListener {
            var valActual = textInputEditTimeEquipo!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditTimeEquipo!!.setText(valActual)
        }

        btnMenosActualizacionEquipos!!.setOnClickListener {
            var valActual = textInputEditActualizacionEquipos!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditActualizacionEquipos!!.setText(valActual)

        }

        btnMasTmEquipo!!.setOnClickListener {
            var valActual = textInputEditTimeEquipo!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditTimeEquipo!!.setText(valActual)
        }


        btnMasActualizacionEquipos!!.setOnClickListener {
            var valActual =  textInputEditActualizacionEquipos!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            textInputEditActualizacionEquipos!!.setText(valActual)
        }


        btnCancelConfGnrl!!.setOnClickListener {

            activity?.finish()
        }

        btnGuardarConfGnrl!!.setOnClickListener{



            if(textInputEditTimeEquipo!!.text.toString() == ""){
                Toast.makeText(container!!.context, "Actualizacion de estatus equipos - el valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if((textInputEditTimeEquipo!!.text.toString()).toInt() < 5){
                Toast.makeText(container!!.context, "Actualizacion de estatus equipos - el valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if((textInputEditTimeEquipo!!.text.toString()).toLong() > 3600){
                Toast.makeText(container!!.context, "Actualizacion de estatus equipos - el valor maximo permitido es 1 hora (3600 segundos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            if(textInputEditActualizacionEquipos!!.text.toString() == ""){
                Toast.makeText(container!!.context, "Actualizacion de informacion en equipos - el valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            if((textInputEditActualizacionEquipos!!.text.toString()).toInt() < 5){
                Toast.makeText(container!!.context, "Actualizacion de informacion en equipos - el valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            if((textInputEditActualizacionEquipos!!.text.toString()).toLong() > 3600){
                Toast.makeText(container!!.context, "Actualizacion de informacion en equipos - el valor maximo permitido es 1 hora (3600 segundos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            databaseHelper.addControlEquipos()
            databaseHelper.updConfiguracionMovilEquipos(textInputEditTimeEquipo!!.text.toString().toInt())
            databaseHelper.updConfiguracionMovilEquiposMasOpciones(textInputEditActualizacionEquipos!!.text.toString().toInt())
            Toast.makeText(container!!.context, "Se guardo la configuracion de forma correcta", Toast.LENGTH_LONG).show()
            activity?.setResult(Activity.RESULT_OK)
            activity?.finish()
        }


        btnAyudaNegociosUpdate!!.setOnClickListener {
            btnAyudaNegociosUpdate!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context, textInputEditTimeEquipo!!, "Configura cada cuánto tiempo requieres que se actualice el estatus de tus equipos registrados en tu móvil.\n","Valor máximo 3600 segundos",titulo1.text.toString())

        }


        btnAyudaActualizacionEquipos!!.setOnClickListener {
            btnAyudaActualizacionEquipos!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context, textInputEditActualizacionEquipos!!, "Configura cada cuánto tiempo requieres que se actualice la información de tus equipos registrados en tu móvil.\n","Valor máximo 3600 segundos",titulo2.text.toString())

        }


        return view
    }


    private fun displayTooltip2(context: Context, layout: TextInputEditText, mensaje: String, submensaje: String, titulo:String){
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