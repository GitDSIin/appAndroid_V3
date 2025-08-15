package com.apptomatico.app_movil_kotlin_v3.servicios


import android.content.Context
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ResAsyncEngine {

    companion object{
        fun getRestEngine(databaseHelper: DatabaseHelper, context: Context): Retrofit {

            var dominio_actual = ""



            var entornoAct = databaseHelper.getControlEntornoConexion(context)
            if(entornoAct == 1){
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }else{
                dominio_actual = "https://${BuildConfig.DOMINIO_PORTAL}"
            }

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            var client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).writeTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS).addInterceptor(interceptor).addInterceptor(interceptor).build()
            var retrofit = Retrofit.Builder()
                .baseUrl("${dominio_actual}/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit

        }
    }

}