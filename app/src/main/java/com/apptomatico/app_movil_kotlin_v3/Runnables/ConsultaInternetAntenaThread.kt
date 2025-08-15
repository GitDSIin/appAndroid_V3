package com.apptomatico.app_movil_kotlin_v3.Runnables

import android.app.Activity
import android.net.ConnectivityManager
import android.os.Build
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.apptomatico.app_movil_kotlin_v3.MyConnectivityManager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ConsultaInternetAntenaThread(context: Activity, conexionMenu: MenuItem, estatusAlertasMenu: MenuItem, databaseHelper_Data: DatabaseHelper, imyConnectivityManager: MyConnectivityManager): Runnable  {
    var context = context
    var conexionMenu = conexionMenu
    var databaseHelper_Data = databaseHelper_Data
    var estatusAlertasMenu = estatusAlertasMenu
    var myConnectivityManager = imyConnectivityManager

    @RequiresApi(Build.VERSION_CODES.M)
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)


    @RequiresApi(Build.VERSION_CODES.N)
    override fun run() {
        //#VALIDA SI CONTINUA VIGENTE LAS ALERTAS PAUSADAS'
        var ipausaAlt = databaseHelper_Data.getControlPausaAlertas_IntervaloTmAlt()
        validaPausaAlertas(ipausaAlt[0].pa_fecha_fin)

        // var ipausaAlt = databaseHelper_Data.getControlPausaAlertas_IntervaloTmAlt()
        // if (ipausaAlt[0].pa_estatus == 0){
        //     if (ipausaAlt[0].pa_fecha_fin  != null && ipausaAlt[0].pa_fecha_fin  != ""){
        //        validaPausaAlertas(ipausaAlt[0].pa_fecha_fin)
        //    }
        // }
        //FINALIZA VALIDACION

        //****************************************




        val connectionState =  myConnectivityManager.connectionAsStateFlow.value

        ConnectivityUiView(connectionState)






        //***************************************

        /*
        val client2 = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build()


        val request = Request.Builder()
            .url("https://www.google.com/")
            .build()



        try {



            client2.newCall(request).execute().use { response ->
                if (response.isSuccessful) {

                    context.runOnUiThread {
                        conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionon)
                    }



                }else{
                    context.runOnUiThread {
                        conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionoff)
                    }
                }


            }



        }catch (ex: SocketTimeoutException){
            context.runOnUiThread {
                conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionoff)
            }

        } catch (e: IOException) {

            context.runOnUiThread {
                conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionoff)
            }

        }catch (e: Exception) {

            context.runOnUiThread {
                conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionoff)
            }
        }

         */


    }



    fun ConnectivityUiView(isOnline: Boolean) {
        if(isOnline){
            context.runOnUiThread {
                conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionon)
            }
        }else{
            context.runOnUiThread {
                conexionMenu!!.icon = ContextCompat.getDrawable(context, R.drawable.conexionoff)
            }
        }


    }





    fun validaPausaAlertas(fec_hasta: String){

        if(fec_hasta == ""){
            return
        }
        val calender = Calendar.getInstance()
        val year = calender.get(Calendar.YEAR)
        val month = calender.get(Calendar.MONTH) + 1
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val Horas = calender.get(Calendar.HOUR)
        val Minutos = calender.get(Calendar.MINUTE)
        val Segundos = calender.get(Calendar.SECOND)
        val todayDate = "$year-$month-$day $Horas:$Minutos:$Segundos"
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val convertedTodayDate: Date =  simpleDateFormat.parse(todayDate)



        val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val convertedTodayDateHasta: Date =  simpleDateFormatHasta.parse(fec_hasta)
        val calender2 = Calendar.getInstance()
        calender2.time = convertedTodayDateHasta

        val year_Hasta = calender2.get(Calendar.YEAR)
        val month_Hasta = calender2.get(Calendar.MONTH) + 1
        val day_Hasta = calender2.get(Calendar.DAY_OF_MONTH)
        val Horas_Hasta = calender2.get(Calendar.HOUR)
        val Minutos_Hasta = calender2.get(Calendar.MINUTE)
        val Segundos_Hasta = calender2.get(Calendar.SECOND)
        val todayDate_Hasta = "$year_Hasta-$month_Hasta-$day_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta"

        val simpleDateFormat_Hasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val convertedTodayDate_Hasta: Date =  simpleDateFormat_Hasta.parse(todayDate_Hasta)

        val cmp = convertedTodayDate_Hasta.compareTo(convertedTodayDate)
        when {
            cmp > 0 -> {
                System.out.printf("is after ")


            }
            cmp < 0 -> {
                var ipausaAlt = databaseHelper_Data.getControlPausaAlertas_IntervaloTmAlt()
                if (ipausaAlt[0].pa_estatus == 0){
                    databaseHelper_Data.updControlPausaEstatus(1)
                    context.runOnUiThread {
                        estatusAlertasMenu!!.icon =
                            ContextCompat.getDrawable(context, R.drawable.ntfverde)
                    }
                }else{
                    context.runOnUiThread {
                        estatusAlertasMenu!!.icon =
                            ContextCompat.getDrawable(context, R.drawable.ntfverde)
                    }
                }


            }
            else -> {

            }
        }




    }
}