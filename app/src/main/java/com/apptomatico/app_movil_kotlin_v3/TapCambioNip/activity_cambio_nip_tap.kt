package com.apptomatico.app_movil_kotlin_v3.TapCambioNip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.tabs.TabLayout

class activity_cambio_nip_tap: AppCompatActivity() {
    private var viewPagerAdapter: ViewPagerAdapterNip? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private lateinit var databaseHelper: DatabaseHelper

    init{
        databaseHelper = DatabaseHelper.getInstance(this@activity_cambio_nip_tap)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cambio_nip_pager_activity)
        viewPager = findViewById(R.id.viewpager)

        //setting up the adapter
        viewPagerAdapter = ViewPagerAdapterNip(supportFragmentManager)

        // add the fragments
        viewPagerAdapter!!.add(cambionip_tap1(), "Cambio de NIP")

        //Set the adapter
        viewPager!!.setAdapter(viewPagerAdapter)

        // The Page (fragment) titles will be displayed in the
        // tabLayout hence we need to  set the page viewer
        // we use the setupWithViewPager().
        tabLayout = findViewById(R.id.tab_layout)
        tabLayout!!.setupWithViewPager(viewPager)
    }
}