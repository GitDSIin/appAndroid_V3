package com.apptomatico.app_movil_kotlin_v3.session


import android.content.Context
import android.content.SharedPreferences



class Session(cntx: Context) {

    val PREFS_NAME = "com.apptomatico.app_movil_kotlin_v3"
    val PREFS_NAME_VERSION = "com.version"
    val SUBSCRIPTOR_NAME = "SUBSCRIPTOR_NAME"
    val ID_DISPOSITIVO = "0"
    val SUBSCRIPTOR_DB = "PVDSI"
    val SUBSCRIPTOR_DISPOSITIVO = "0"
    val SUBSCRIPTOR_TOKEN = ""
    val NUEVA_VERSION_APP = "0"
    val URL_WEB_SOCKET = ""
    val VISTA_ACT = ""
    val USER_DATABASE_FRONT = ""
    val VISTA_ERROR = "0"
    val ID_NEGOCIO = "0"
    val IP_NEGOCIO = "0"
    val HARDWARE_NEGOCIO = "0"

    private var prefs: SharedPreferences = cntx.getSharedPreferences(PREFS_NAME, 0)

    private var prefs_version: SharedPreferences = cntx.getSharedPreferences(PREFS_NAME_VERSION, 0)
    //Ventanas

    var vistaAct: String?
        get() = prefs.getString(VISTA_ACT, "")
        set(value) = prefs.edit().putString(VISTA_ACT, value).apply()


    //Usuario

    var nombre: String?
        get() = prefs.getString(SUBSCRIPTOR_NAME, "")
        set(value) = prefs.edit().putString(SUBSCRIPTOR_NAME, value).apply()

    //Dispositivo

    //Base local
    var database: String?
        get() = prefs.getString(SUBSCRIPTOR_DB, "")
        set(value) = prefs.edit().putString(SUBSCRIPTOR_DB, value).apply()

    //Base principal
    var database_front: String?
        get() = prefs.getString(USER_DATABASE_FRONT, "")
        set(value) = prefs.edit().putString(USER_DATABASE_FRONT, value).apply()

    var idDispositivo: String?
        get() = prefs.getString(ID_DISPOSITIVO, "")
        set(value) = prefs.edit().putString(ID_DISPOSITIVO, value).apply()


    var token: String?
        get() = prefs.getString(SUBSCRIPTOR_TOKEN, "")
        set(value) = prefs.edit().putString(SUBSCRIPTOR_TOKEN, value).apply()


    var nuevaVersion: String?
        get() = prefs_version.getString(NUEVA_VERSION_APP, "")
        set(value) = prefs_version.edit().putString(NUEVA_VERSION_APP, value).apply()

    var urlwebsocket: String?
        get() = prefs_version.getString(URL_WEB_SOCKET, "")
        set(value) = prefs_version.edit().putString(URL_WEB_SOCKET, value).apply()


    var vistaError: String?
        get() = prefs.getString(VISTA_ERROR, "")
        set(value) = prefs.edit().putString(VISTA_ERROR, value).apply()

    var idNegocioF: String?
        get() = prefs.getString(ID_NEGOCIO, "")
        set(value) = prefs.edit().putString(ID_NEGOCIO, value).apply()

    var ipNegocioF: String?
        get() = prefs.getString(IP_NEGOCIO, "")
        set(value) = prefs.edit().putString(IP_NEGOCIO, value).apply()

    var hardwareNegocioF: String?
        get() = prefs.getString(HARDWARE_NEGOCIO, "")
        set(value) = prefs.edit().putString(HARDWARE_NEGOCIO, value).apply()



}