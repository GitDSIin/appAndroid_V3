package com.apptomatico.app_movil_kotlin_v3.TapConfiguraciones

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.usuariosalertas.UsuariosAlertasFragment
import com.google.android.material.tabs.TabLayout
import org.imaginativeworld.oopsnointernet.callbacks.ConnectionCallback
import org.imaginativeworld.oopsnointernet.dialogs.signal.NoInternetDialogSignal

class activity_configuracion_tap: AppCompatActivity() {
    private var viewPagerAdapter: ViewPagerAdapter? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private lateinit var databaseHelper: DatabaseHelper

    init{
        databaseHelper = DatabaseHelper.getInstance(this@activity_configuracion_tap)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuracion_pager_activity)
        viewPager = findViewById(R.id.viewpager)

        // No Internet Dialog: Signal
        NoInternetDialogSignal.Builder(
            this,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }

                cancelable = true // Optional
                noInternetConnectionTitle = "Sin internet" // Optional
                noInternetConnectionMessage =
                    "Comprueba tu conexión a Internet e inténtalo de nuevo." // Optional
                showInternetOnButtons = true // Optional
                pleaseTurnOnText = "por favor enciende" // Optional
                wifiOnButtonText = "Wifi" // Optional
                mobileDataOnButtonText = "Datos móviles" // Optional

                onAirplaneModeTitle = "Sin internet" // Optional
                onAirplaneModeMessage = "Ha activado el modo avión." // Optional
                pleaseTurnOffText = "Por favor apague" // Optional
                airplaneModeOffButtonText = "Modo avión" // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()

        //setting up the adapter
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        // add the fragments
        viewPagerAdapter!!.add(configuracion_tap1(), "Alertas")
        viewPagerAdapter!!.add(configuracion_tap2(), "Monitores")
        viewPagerAdapter!!.add(configuracion_tap3(), "Equipos")
        viewPagerAdapter!!.add(UsuariosAlertasFragment(), "Alertas Usuarios")
        //Set the adapter
        viewPager!!.setAdapter(viewPagerAdapter)

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout!!.setupWithViewPager(viewPager)
    }
}