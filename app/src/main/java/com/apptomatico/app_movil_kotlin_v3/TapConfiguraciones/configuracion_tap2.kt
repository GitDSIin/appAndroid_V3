package com.apptomatico.app_movil_kotlin_v3.TapConfiguraciones

import android.app.Activity.RESULT_OK
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

class configuracion_tap2 : Fragment() {
    private var btnMenosTmMonitoreables: Button? = null
    private var txtTmMonitoreables: TextInputEditText? = null
    private var btnMasTmMonitoreables: Button? = null
    private var btnGuardarConfGnrl: Button? = null
    private var btnCancelConfGnrl: Button? = null
    private var btnRestauraValoresDefecto: TextView? = null
    private var btnAyudaMonitoresupdate: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_configuracion2, container, false)
        val databaseHelper = DatabaseHelper.getInstance(container!!.context)
        var titulo1  = view.findViewById(R.id.title2) as TextView
        btnMenosTmMonitoreables = view.findViewById(R.id.btnMenosTmMnt) as Button
        txtTmMonitoreables = view.findViewById(R.id.textInputEditTimeMonitoreables) as TextInputEditText
        btnMasTmMonitoreables  = view.findViewById(R.id.btnMasTmMnt) as Button
        btnGuardarConfGnrl =  view.findViewById(R.id.appCompatButtonUpdConfGnrl) as Button
        btnCancelConfGnrl =  view.findViewById(R.id.appCompatButtonCancelarConfGnrl) as Button
        btnRestauraValoresDefecto =  view.findViewById(R.id.btnRestauraValoresDefecto) as TextView

        btnAyudaMonitoresupdate = view.findViewById(R.id.btnAyudaMonitoresupdate) as ImageView


        var intervaloMnt = databaseHelper.getConfiguracionMovil_IntervaloTmMnt()
        txtTmMonitoreables!!.setText(intervaloMnt)

        btnRestauraValoresDefecto!!.paintFlags = btnRestauraValoresDefecto!!.paintFlags  or Paint.UNDERLINE_TEXT_FLAG

        var configuraciondefault  = databaseHelper.getConfiguracionDefaultMovil()

        btnRestauraValoresDefecto!!.setOnClickListener {
            btnRestauraValoresDefecto!!.animationXFade(Fade.FADE_IN_DOWN)
            txtTmMonitoreables!!.setText("${configuraciondefault[0].get_conf_movil_monitores}")




        }

        btnMenosTmMonitoreables!!.setOnClickListener {
            var valActual = txtTmMonitoreables!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTmMonitoreables!!.setText(valActual)
        }

        btnMasTmMonitoreables!!.setOnClickListener {
            var valActual = txtTmMonitoreables!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 5){
                valActual = "5"
            }
            txtTmMonitoreables!!.setText(valActual)
        }



        btnCancelConfGnrl!!.setOnClickListener {
            activity?.finish()
        }

        btnGuardarConfGnrl!!.setOnClickListener{


            if(txtTmMonitoreables!!.text.toString() == ""){
                Toast.makeText(container!!.context, "El valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }



            if((txtTmMonitoreables!!.text.toString()).toInt() < 5){
                Toast.makeText(container!!.context, "El valor minimo permitido es 5 segundos", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if((txtTmMonitoreables!!.text.toString()).toLong() > 3600){
                Toast.makeText(container!!.context, "El valor maximo permitido es 1 hora (3600 segundos)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }


            databaseHelper.addControlEquipos()
            databaseHelper.updConfiguracionMovilMonitoreables(txtTmMonitoreables!!.text.toString().toInt())
            Toast.makeText(container!!.context, "Se guardo la configuracion de forma correcta", Toast.LENGTH_LONG).show()


            activity?.setResult(RESULT_OK)
            activity?.finish()
        }


        btnAyudaMonitoresupdate!!.setOnClickListener {
            btnAyudaMonitoresupdate!!.animationXFade(Fade.FADE_IN_DOWN)
            displayTooltip2(container!!.context, txtTmMonitoreables!!, "Configura cada cuánto tiempo requieres que se actualice la sección de Monitores en tu móvil.\n", "Valor máximo 3600 segundos", titulo1.text.toString())
        }



        return view

    }




    private fun displayTooltip2(context: Context,layout: TextInputEditText, mensaje: String, submensaje: String, titulo: String){
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