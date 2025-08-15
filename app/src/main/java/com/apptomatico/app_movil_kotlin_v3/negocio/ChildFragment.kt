package com.apptomatico.app_movil_kotlin_v3.negocio

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.webSocketService
import com.apptomatico.app_movil_kotlin_v3.negocio.NegocioFragment.Companion.NegociosRecyclerView
import com.apptomatico.app_movil_kotlin_v3.negocio.ui.main.ItemMoveCallbackNegocio
import com.apptomatico.app_movil_kotlin_v3.negocio.ui.main.NegocioRecyclerViewAdapter
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp

class ChildFragment: Fragment() {

    private var columnCount = 1




    companion object{
        var NegociosHomeAdapter: NegocioRecyclerViewAdapter?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.negocios_fragment_list, container, false)

        val screenWidth = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(screenWidth)

        val widthPantalla: Float = screenWidth.xdpi


        val contenido: String = "application/x-www-form-urlencoded"
        val BaseSuscriptor: String? = SharedApp.prefs.database_front

        with(view) {
            NegociosRecyclerView = view.findViewById(R.id.list)
            NegociosRecyclerView?.layoutManager = when {
                columnCount <= 1 -> LinearLayoutManager(requireContext())
                else -> GridLayoutManager(requireContext(), columnCount)
            }


            val parentFrag: NegocioFragment? = this@ChildFragment.parentFragment as NegocioFragment?


            NegociosHomeAdapter = NegocioRecyclerViewAdapter(
                requireContext(),
                contenido,
                BaseSuscriptor!!,
                container!!,
                widthPantalla,
                webSocketService,
                parentFrag!!
            )

            val callback: ItemTouchHelper.Callback = ItemMoveCallbackNegocio(NegociosHomeAdapter!!)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(NegociosRecyclerView!!)



            NegociosRecyclerView?.adapter = NegociosHomeAdapter



        }


        return view
    }
}