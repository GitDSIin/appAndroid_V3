package com.apptomatico.app_movil_kotlin_v3.servicios



import android.annotation.SuppressLint
import android.content.Context
import net.sqlcipher.database.SQLiteDatabase
import android.os.AsyncTask
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.gson.GsonBuilder
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit


class RestEngine {


    companion object{

        fun getRestEngineInicial(databaseHelper_Data: DatabaseHelper, context: Context): Retrofit{

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(interceptor).build()
            var retrofit: Retrofit
            var entornoAct = databaseHelper_Data.getControlEntornoConexion(context)
            if(entornoAct == 1){

                retrofit = Retrofit.Builder()
                    .baseUrl("https://${BuildConfig.DOMINIO_PORTAL}/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }else{

                retrofit = Retrofit.Builder()
                    .baseUrl("https://${BuildConfig.DOMINIO_PORTAL}/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }



            return retrofit
        }


        fun getRestEngine(databaseHelper: DatabaseHelper, context: Context): Retrofit{

            var dominio_actual = ""


            var entornoAct = databaseHelper.getControlEntornoConexion(context)
            if(entornoAct == 1){
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }else{
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("${dominio_actual}/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit

        }


        fun fireBase_getRestEngine(databaseHelper: SQLiteDatabase): Retrofit{

            var dominio_actual = ""


            var entornoAct = fb_getControlEntornoConexion(databaseHelper)
            if(entornoAct == 1){
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }else{
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("${dominio_actual}/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit

        }

        @SuppressLint("Range")
        fun fb_getControlEntornoConexion(databaseHelper: SQLiteDatabase): Int {
            //#CORECCION DE ERROR DATABASE

            val db = databaseHelper
            db.enableWriteAheadLogging()
            val Query = "SELECT entorno_activo FROM control_entorno_conexion WHERE id_entorno = 1"
            val cursor = db.rawQuery(Query, null)
            var svrEntorno = 0
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    svrEntorno  = cursor.getInt(cursor.getColumnIndex("entorno_activo"))


                } while (cursor.moveToNext())
                cursor.close()
            }
            //db.close()
            return   svrEntorno
        }


        fun getRestEngineNegocio(ApiUrl: String, ApiPuerto: String, ipLocal: String, databaseHelper: DatabaseHelper): Retrofit{

            var values: List<com.apptomatico.app_movil_kotlin_v3.model.ControlRedMovil> = ArrayList()


            val gson = GsonBuilder()
                .setLenient()
                .create()

            var ipPublicaNegocio = ApiUrl
            var ipPublic_Divice = ""
            //var task = GetPublicIP().execute()
            //val ipPublic_Divice = task.get().toString()

            values =  databaseHelper.getConexionRed()
            for (i in 0 until values.count()) {
                ipPublic_Divice = values[i].ip_publica
            }


            if (ipPublic_Divice.replace(" ", "") == ApiUrl.replace(" ", "")){
                ipPublicaNegocio = "$ipLocal"
            }


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("http://$ipPublicaNegocio:$ApiPuerto/")
                //.addConverterFactory(ScalarsConverterFactory.create()) //important
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()

            return retrofit




        }


        fun getRestEngineNegocioCorrutines(ApiUrl: String, ApiPuerto: String, ipLocal: String, databaseHelper: DatabaseHelper): Retrofit{

            var values: List<com.apptomatico.app_movil_kotlin_v3.model.ControlRedMovil> = ArrayList()



            var ipPublicaNegocio = ApiUrl
            var ipPublic_Divice = ""
            //var task = GetPublicIP().execute()
            //val ipPublic_Divice = task.get().toString()

            values =  databaseHelper.getConexionRed()
            for (i in 0 until values.count()) {
                ipPublic_Divice = values[i].ip_publica
            }


            if (ipPublic_Divice.replace(" ", "") == ApiUrl.replace(" ", "")){
                ipPublicaNegocio = "$ipLocal"
            }


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("http://$ipPublicaNegocio:$ApiPuerto/")
                //.addConverterFactory(ScalarsConverterFactory.create()) //important
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit




        }


        fun getRestEngineNegocio_30sg(ApiUrl: String, ApiPuerto: String, ipLocal: String, databaseHelper: DatabaseHelper): Retrofit{

            var values: List<com.apptomatico.app_movil_kotlin_v3.model.ControlRedMovil> = ArrayList()


            val gson = GsonBuilder()
                .setLenient()
                .create()

            var ipPublicaNegocio = ApiUrl
            var ipPublic_Divice = ""
            //var task = GetPublicIP().execute()
            //val ipPublic_Divice = task.get().toString()

            values =  databaseHelper.getConexionRed()
            for (i in 0 until values.count()) {
                ipPublic_Divice = values[i].ip_publica
            }


            if (ipPublic_Divice.replace(" ", "") == ApiUrl.replace(" ", "")){
                ipPublicaNegocio = "$ipLocal"
            }


            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("http://$ipPublicaNegocio:$ApiPuerto/")
                //.addConverterFactory(ScalarsConverterFactory.create()) //important
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit




        }


    }




    class GetPublicIP : AsyncTask<String?, String?, String?>() {
        protected override fun doInBackground(vararg strings: String?): String {
            var publicIP = ""
            try {
                val s = Scanner(
                    URL(
                        "https://api.ipify.org")
                        .openStream(), "UTF-8")
                    .useDelimiter("\\A")
                publicIP = s.next()
                println("My current IP address is $publicIP")
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return publicIP
        }

        protected fun onPostExecute(publicIp: String) {
            super.onPostExecute(publicIp)
            return

            //Here 'publicIp' is your desire public IP
        }
    }



    class GetDominioActual : AsyncTask<String?, String?, String?>() {
        protected override fun doInBackground(vararg strings: String?): String {
            var dominioActual = ""
            try {
                val s = Scanner(
                    URL(
                        "https://dsiin.com/noborrar_subdominios")
                        .openStream(), "UTF-8")
                    .useDelimiter("\\A")
                dominioActual = s.next()
                println("El dominio Actual es $dominioActual")
            } catch (e: IOException) {
                return ""
            }
            return dominioActual
        }

        protected fun onPostExecute(publicIp: String) {
            super.onPostExecute(publicIp)
            return

            //Here 'publicIp' is your desire public IP
        }
    }




}