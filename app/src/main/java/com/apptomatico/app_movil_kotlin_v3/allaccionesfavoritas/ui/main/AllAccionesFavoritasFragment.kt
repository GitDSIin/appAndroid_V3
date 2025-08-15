package com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.ui.main

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.AllAccionesFavoritas.Companion.AllAccionesFavoritasAdapter
import com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.AllAccionesFavoritas.Companion.accionListAllAccionesFavoritas
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.ui.home.ItemAcciones
import net.sqlcipher.database.SQLiteDatabase
import kotlin.math.roundToInt

class AllAccionesFavoritasFragment: Fragment() {



    var iwebSocketService: EchoService? = null
    private lateinit var databaseHelper_Data: DatabaseHelper
    var root: View? = null

    private var Negocioid: Int  = 0
    private var Equipoid: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            Negocioid = it.getInt(AllAccionesFavoritasFragment.ARG_COLUMN_ID_NEGOCIO)
            Equipoid = it.getInt(AllAccionesFavoritasFragment.ARG_COLUMN_ID_EQUIPO)

        }



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        val reg = SharedApp.prefs.idNegocioF.toString().split("|")





        root = inflater.inflate(R.layout.activity_all_acciones_favoritas_fragment, container, false)


        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
        val idMovil = Licenciadb[0].id_movil




        var  listAlertas =  databaseHelper_Data.getListAccionesDinamicas_Panel(Negocioid, Equipoid)

        for (i in  listAlertas) {
            var alias= i.alias_aplicacion
            var tipo = i.tipo_id

            accionListAllAccionesFavoritas.add(
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


        AllAccionesFavoritasAdapter = CustomAdapterAllAccionesFavoritas(accionListAllAccionesFavoritas, context, databaseHelper_Data, iwebSocketService,  container)


        val grDh: GridView = root!!.findViewById(R.id.gridDashboardAllAccFav)


        grDh.adapter= AllAccionesFavoritasAdapter




        return  root
    }



    private fun removeTrailingZeroes(s: String): String {
        var index: Int = s.length - 1
        while (index > 0) {
            if (s[index] != '0') {
                break
            }
            index--
        }
        return s.substring(0, index + 1)
    }

    private fun removeLeadingZeroes(s: String): String {
        var index: Int = 0
        while (index < s.length - 1) {
            if (s[index] != '0') {
                break
            }
            index++
        }
        return s.substring(index)
    }

    fun updAlto(container: View?){
        val numeroacciones = accionListAllAccionesFavoritas.size
        if(numeroacciones > 0){
            val params: ViewGroup.LayoutParams = container!!.layoutParams
            params.height = dpToPx((numeroacciones * 240) + 1200, requireContext())
            container!!.layoutParams = params
        }

    }

    fun dpToPx(dp: Int, context: Context): Int {
        val density: Float = context.resources
            .displayMetrics.density
        return (dp.toFloat() * density).roundToInt()
    }

    companion object {
        const val ARG_COLUMN_ID_NEGOCIO = "column-id-negocio"
        const val ARG_COLUMN_ID_EQUIPO = "column-id-equipo"
        @JvmStatic
        fun newInstance(webSocketService: EchoService?, Negocioid: Int, Equipoid: Int) = AllAccionesFavoritasFragment().apply {
            arguments = Bundle().apply {
                iwebSocketService = webSocketService
                putInt(AllAccionesFavoritasFragment.ARG_COLUMN_ID_NEGOCIO, Negocioid)
                putInt(AllAccionesFavoritasFragment.ARG_COLUMN_ID_EQUIPO, Equipoid)
            }
        }
    }

}