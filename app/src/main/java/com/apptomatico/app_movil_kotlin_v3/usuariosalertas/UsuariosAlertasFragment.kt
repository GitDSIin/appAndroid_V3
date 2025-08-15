package com.apptomatico.app_movil_kotlin_v3.usuariosalertas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosRecAlertasMovil
import com.apptomatico.app_movil_kotlin_v3.model.MovilUsuPermitidosRecAlertasMovil_list
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.usuariosalertas.ui.UsuariosAlertasAdapter
import com.google.gson.Gson
import com.rommansabbir.animationx.Rotate
import com.rommansabbir.animationx.animationXFade
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class UsuariosAlertasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsuariosAlertasAdapter
    private lateinit var databaseHelper_Data: DatabaseHelper
    private lateinit var btnCancelarAlertausu: ImageView
    var spinner_equipos: Spinner? = null
    var idEquipo = 0


    private var ordenAscEquipo = true
    private var ordenAscUsuario = true
    private var ordenAscAlerta = true

    // Simula datos recibidos


    companion object {
        var listaUsuarios: List<MovilUsuPermitidosRecAlertasMovil_list>? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
        return inflater.inflate(R.layout.fragment_usuarios_alertas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.recyclerUsuarios)
        recyclerView.layoutManager = LinearLayoutManager(context)
        btnCancelarAlertausu = view.findViewById<ImageView>(R.id.btnCancelarAlertausu)
        spinner_equipos = view.findViewById<Spinner>(R.id.frm_equipos_spinner_toolbar)

        val tvNombreEquipo = view.findViewById<TextView>(R.id.tvNombreEquipo)
        val tvNombreUsuario = view.findViewById<TextView>(R.id.tvNombreUsuario)
        val tvAlerta = view.findViewById<TextView>(R.id.tvAlerta)

        tvNombreEquipo.setOnClickListener {
            ordenarPorNombreEquipo()
            tvNombreEquipo.text = if (ordenAscEquipo) "Equipo ▲" else "Equipo ▼"
        }

        tvNombreUsuario.setOnClickListener {
            ordenarPorNombreUsuario()
            tvNombreUsuario.text = if (ordenAscUsuario) "Nombre ▲" else "Nombre ▼"
        }

        tvAlerta.setOnClickListener {
            ordenarPorEnvioAlerta()
            tvAlerta.text = if (ordenAscAlerta) "Envia Alerta ▲" else "Envia Alerta ▼"
        }


        val btnActivarTodos = view.findViewById<Button>(R.id.btnActivarTodos)

        var estadoGlobal = true // true = activar, false = desactivar



        var lstEquipos = databaseHelper_Data.GetListaEquiposHeardeBar(0)

        val adapterSpinner = ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list>(requireContext(), R.layout.spinner_selected_item,  lstEquipos)
        adapterSpinner.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner_equipos!!.adapter = adapterSpinner
        spinner_equipos!!.setSelection(0,false)


        spinner_equipos!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                if(lstEquipos[position!!].id == "Todos"){
                    idEquipo = 0
                }else{
                    idEquipo = lstEquipos[position!!].id.toInt()
                }
                listaUsuarios  = databaseHelper_Data.listarUsuarios(idEquipo)
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }


        btnActivarTodos.setOnClickListener {
            adapter.actualizarTodos(estadoGlobal)
            btnActivarTodos.text = if (estadoGlobal) "Desactivar todos los usuarios" else "Activar todos los usuarios"
            estadoGlobal = !estadoGlobal
        }


        var dominio_actual = ""
        var entornoAct = databaseHelper_Data.getControlEntornoConexion(requireContext())
        if (entornoAct == 1) {
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        } else {
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }

        btnCancelarAlertausu.setOnClickListener{
            btnCancelarAlertausu.animationXFade(Rotate.ROTATE_IN)
            activity?.finish()
        }

        var listIdEquipos = databaseHelper_Data.obtenerIdsComoCadena()
        listaUsuarios  = databaseHelper_Data.listarUsuarios(idEquipo)


        if(listaUsuarios!!.isEmpty()){
            listaUsuarios = mutableListOf()
        }

        adapter = UsuariosAlertasAdapter(requireContext())

        recyclerView.adapter = adapter

        val client2 = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()


        val request = Request.Builder()
            .url("${dominio_actual}/api/get_usuarios_envio_alertas_moviles_negocio/?negocio_listado=$listIdEquipos")
            .header("Authorization", "JWT ${BuildConfig.TOKEN_MAESTRO}")
            .header("Content-Type", "application/json")
            .build()



        client2.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Toast.makeText(requireContext(), "Error al recuperar informacion de usuarios de equipo ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful && response.code == 200) {
                    val resData = response.body!!.string()
                    var gsonData = Gson()
                    var mMineUserEntity: DataUsuariosPermitidosRecAlertasMovil? = gsonData?.fromJson(resData, DataUsuariosPermitidosRecAlertasMovil::class.java)
                    for (item in  mMineUserEntity!!.data) {
                        databaseHelper_Data.upsertUsuario(item)
                    }

                    listaUsuarios = databaseHelper_Data.listarUsuarios(idEquipo)
                    requireActivity().runOnUiThread {
                        adapter.notifyDataSetChanged()
                    }

                }
            }

        })

    }




    private fun ordenarPorNombreEquipo() {
        // Asegura que la lista no sea nula y sea mutable
        val listaMutable = listaUsuarios?.toMutableList() ?: mutableListOf()

        // Ordenar según el valor de la bandera
        if (ordenAscEquipo) {
            listaMutable.sortBy { it.nombre?.lowercase() ?: "" }
        } else {
            listaMutable.sortByDescending { it.nombre?.lowercase() ?: "" }
        }

        // Actualiza la referencia global y refresca el RecyclerView
        listaUsuarios = listaMutable
        adapter.notifyDataSetChanged()

        // Cambia la dirección para el siguiente clic
        ordenAscEquipo = !ordenAscEquipo
    }

    private fun ordenarPorNombreUsuario() {
        // Convierte listaUsuarios en mutable para ordenar
        val listaMutable = listaUsuarios?.toMutableList() ?: mutableListOf()

        // Aplica orden ascendente o descendente según la bandera
        if (ordenAscUsuario) {
            listaMutable.sortBy { it.usuario?.lowercase() ?: "" }
        } else {
            listaMutable.sortByDescending { it.usuario?.lowercase() ?: "" }
        }

        // Reemplaza la lista global por la ordenada
        listaUsuarios = listaMutable

        // Notifica al adaptador que la lista cambió
        adapter.notifyDataSetChanged()

        // Invierte el sentido para el próximo clic
        ordenAscUsuario = !ordenAscUsuario
    }


    private fun ordenarPorEnvioAlerta() {
        // Convierte listaUsuarios en mutable para ordenar
        val listaMutable = listaUsuarios?.toMutableList() ?: mutableListOf()

        // Aplica orden ascendente o descendente según la bandera
        if (ordenAscAlerta) {
            listaMutable.sortBy { it.alerta_movil ?: 0 }
        } else {
            listaMutable.sortByDescending { it.alerta_movil ?: 0 }
        }

        // Reemplaza la lista global por la ordenada
        listaUsuarios = listaMutable

        // Notifica al adaptador que la lista cambió
        adapter.notifyDataSetChanged()

        // Invierte el sentido para el próximo clic
        ordenAscAlerta = !ordenAscAlerta
    }







}
