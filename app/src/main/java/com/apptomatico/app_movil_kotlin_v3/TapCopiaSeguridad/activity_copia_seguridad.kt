package com.apptomatico.app_movil_kotlin_v3.TapCopiaSeguridad

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.tabs.TabLayout

class activity_copia_seguridad: AppCompatActivity() {
    private var viewPagerAdapter: ViewPagerAdapterCopiaSeguridad? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private lateinit var databaseHelper: DatabaseHelper

    init{
        databaseHelper = DatabaseHelper.getInstance(this@activity_copia_seguridad)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.copia_seguridad_pager_activity)
        viewPager = findViewById(R.id.viewpager)

        //setting up the adapter
        viewPagerAdapter = ViewPagerAdapterCopiaSeguridad(supportFragmentManager)

        // add the fragments
        viewPagerAdapter!!.add(copiaseguridad_tap1(), "Copia de Seguridad")

        //Set the adapter
        viewPager!!.adapter = viewPagerAdapter

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout!!.setupWithViewPager(viewPager)
    }
}