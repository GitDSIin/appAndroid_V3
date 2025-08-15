package com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.EchoService
import com.apptomatico.app_movil_kotlin_v3.Sockets.Services.WebSocketServerConn
import com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.ui.main.AllAccionesFavoritasFragment
import com.apptomatico.app_movil_kotlin_v3.allaccionesfavoritas.ui.main.CustomAdapterAllAccionesFavoritas
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_equipos
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.header_id_negocio
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.webSocketService
import com.apptomatico.app_movil_kotlin_v3.negocio.DownloadController
import com.apptomatico.app_movil_kotlin_v3.session.SharedApp
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.ui.home.ItemAcciones
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.StreamAdapter
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.ArrayList

class AllAccionesFavoritas: Fragment() {

    lateinit var rootView: View

    private var toolbar: Toolbar? = null
    lateinit var layoutnavbar: RelativeLayout
    lateinit var navView: NavigationView
    private var ECHO_URL_WEB_SOCKET:  String = ""
    private var progressAllAcciones: ProgressBar? = null
    var spinner_equipos: Spinner? = null
    private var configUserMenu: MenuItem? = null
    private var conexionMenu: MenuItem? = null
    private var estatusAlertasMenu: MenuItem? = null

    private var REQUEST_GET_MAP_LOCATION = 0
    lateinit var downloadController: DownloadController
    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var EquiposSPinnerarrayAdapter: ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list>
    var check = 0
    private lateinit var databaseHelper_Data: DatabaseHelper
    init{

        ECHO_URL_WEB_SOCKET = SharedApp.prefs.urlwebsocket.toString()

    }
    companion object {
        var accionListAllAccionesFavoritas = ArrayList<ItemAcciones>()
        var AllAccionesFavoritasAdapter: CustomAdapterAllAccionesFavoritas?= null
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView =  inflater.inflate(R.layout.activity_all_acciones_favoritas, container, false)
        databaseHelper_Data = DatabaseHelper.getInstance(requireContext())
        progressAllAcciones =   rootView.findViewById<ProgressBar>(R.id.progressAllAcciones)
        val btnModalAllAccionesFavoritasReturn = rootView.findViewById<View>(R.id.btnModalAllAccionesFavoritasReturn) as ImageView
        spinner_equipos = rootView.findViewById(R.id.inicio_equipos_spinner_toolbar)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar)


        with(rootView) {
            navBar.visibility = View.GONE
            if (spinner_equipos != null) {
                getAllChildExercisesFromParentIDEquipos()

            }

            setupWebSocketService()



            btnModalAllAccionesFavoritasReturn.setOnClickListener {
                findNavController().navigate(R.id.first_fragment)

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


            // generaLayoutAllAcciones()
        }





        return rootView

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val childFragment: Fragment = AllAccionesFavoritasFragment()
        val transaction: FragmentTransaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, childFragment).commit()
    }

    private fun getAllChildExercisesFromParentIDEquipos() {
        var lstEquipos: List<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list>
        if (header_id_negocio == "Todos"){
            lstEquipos = databaseHelper_Data.GetListaEquiposHeardeBar(0)
        }else{
            lstEquipos = databaseHelper_Data.GetListaEquiposHeardeBar(header_id_negocio.toInt())
        }

        EquiposSPinnerarrayAdapter = ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Cat_Negocios_list>(requireContext(), android.R.layout.simple_spinner_item,  lstEquipos)
        EquiposSPinnerarrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_equipos!!.adapter = EquiposSPinnerarrayAdapter
        spinner_equipos!!.setSelection(0,false)



        spinner_equipos!!.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {

                header_id_equipos =   lstEquipos[position!!].id
                generaLayoutAllAcciones()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

    }


    private fun generaLayoutAllAcciones(){
        var idNegocio = 0
        var idEquipos = 0
        if (header_id_negocio != "Todos"){
            idNegocio = header_id_negocio.toInt()
        }
        if(header_id_equipos != "Todos"){
            idEquipos = header_id_equipos.toInt()
        }



        var  listAlertas =  databaseHelper_Data.getListAccionesDinamicas_Panel(idNegocio, idEquipos)
        accionListAllAccionesFavoritas.clear()
        for (i in  listAlertas) {
            var alias= i.alias_aplicacion
            var tipo = i.tipo_id

            accionListAllAccionesFavoritas.add(ItemAcciones(i.id,"$alias", "", "$tipo","${i.comando}", i.id_negocio_id.toString(), i.nombre_equipo, false))
        }
        AllAccionesFavoritasAdapter!!.notifyDataSetChanged()



    }




    private fun setupWebSocketService() {
        var dominio_actual = ""
        var idDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        /*
        if(existeLic.count() > 0){
            for (i in 0 until existeLic.count()) {
                dominio_actual = existeLic[i].nombre_dominio
            }
        }

         */

        var entornoAct = databaseHelper_Data.getControlEntornoConexion(requireContext())
        if(entornoAct == 1){
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }else{
            dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
        }

        /*
        webSocketService = provideWebSocketService(
                scarlet = provideScarlet(
                        client = provideOkhttp(),
                        lifecycle = provideLifeCycle(),
                        streamAdapterFactory = provideStreamAdapterFactory(),
                )
        )

         */


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

    /**
     * Implementacion de las dependencias para WebSocket
     *
     */
    private fun provideWebSocketService(scarlet: Scarlet) = scarlet.create(EchoService::class.java)

    private fun provideScarlet(
        client: OkHttpClient,
        lifecycle: Lifecycle,
        streamAdapterFactory: StreamAdapter.Factory,
    ) =
        Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory(ECHO_URL_WEB_SOCKET))
            .lifecycle(lifecycle)
            .addStreamAdapterFactory(streamAdapterFactory)
            .build()


    //private fun provideOkhttp() =
    //   OkHttpClient.Builder()
    //      .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
    //     .build()

    private fun provideOkhttp(): OkHttpClient {
        var wsidDispositivo: String = Settings.Secure.getString(requireContext().contentResolver, Settings.Secure.ANDROID_ID).toString()
        wsidDispositivo = removeLeadingZeroes(wsidDispositivo)
        wsidDispositivo =  removeTrailingZeroes(wsidDispositivo)
        val requestIterator = Interceptor { chain ->
            val url = chain.request()
                .url
                .newBuilder()
                .addQueryParameter("token","$wsidDispositivo")
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request = request)
        }

        return  OkHttpClient.Builder()
            .addInterceptor(requestIterator)
            .build()
    }


    private fun provideLifeCycle() = AndroidLifecycle.ofApplicationForeground(requireActivity().application)


    private fun provideStreamAdapterFactory() = RxJava2StreamAdapterFactory()

    /**
     *Finaliza implementacion de dependencias WebSocket
     */


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








}
