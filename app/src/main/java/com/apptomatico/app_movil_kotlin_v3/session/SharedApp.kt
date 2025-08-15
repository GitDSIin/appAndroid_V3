package com.apptomatico.app_movil_kotlin_v3.session

import android.app.Application

class SharedApp: Application() {

    companion object {
        lateinit var prefs: Session
    }
    override fun onCreate() {
        super.onCreate()
        prefs = Session(applicationContext)
    }


}