package com.apptomatico.app_movil_kotlin_v3.ui.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.interfaz.APIService
import com.apptomatico.app_movil_kotlin_v3.servicios.RestEngine
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.rommansabbir.animationx.Zoom
import com.rommansabbir.animationx.animationXFade
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomAdapterDahsboard: BaseAdapter {

    var accionList = ArrayList<ItemAcciones>()
    var context: Context? = null
    var nameView: View? = null
    var databaseHelper: DatabaseHelper? = null


    constructor(accionList: ArrayList<ItemAcciones>, context: Context?, databaseHelper: DatabaseHelper?): super(){
        this.accionList = accionList
        this.context = context
        this.databaseHelper = databaseHelper
    }



    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val accionGridList = this.accionList[position]
        var inflator = this.context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
        var image: ImageView

        nameView = inflator.inflate(R.layout.items_dashboard, null)


        nameView!!.findViewById<LinearLayout>(R.id.layoutItemDashboard).setBackgroundColor(Color.parseColor("#F2F3F4"))

        var bm: Bitmap? = BitmapFactory.decodeResource(this.context!!.resources, R.drawable.estrellafavoritoon)
        var uri_icono = ""









        if (accionGridList.tipo == "5"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.monitoreo)
            uri_icono = "@drawable/accfavmonitoreorecursos"
        }else if(accionGridList.tipo == "2"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.acciones64)
            if (accionGridList.comando == "reboot_svr"){ //Reinicia Servidor
                uri_icono = "@drawable/accfavreiniciarservidor"
            }
            if (accionGridList.comando == "log_off_svr"){ //Cerrar Sesion del Servidor
                uri_icono = "@drawable/accfavcerrarsesiondefault"
            }
            if (accionGridList.comando == "shutdown_svr"){ //Cerrar Sesion del Servidor
                uri_icono = "@drawable/accfavapagarservidordefault"
            }
            if (accionGridList.comando == "start_svr"){ //Cerrar Sesion del Servidor
                uri_icono = "@drawable/enciendeequipory"
            }

        }else if(accionGridList.tipo == "1"){
            if(accionGridList.comando == "termina_proceso_en_equipo"){
                nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.aplicaciones64)
                uri_icono = "@drawable/accfavcerrarprograma"
            }else{
                nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.aplicaciones64)
                uri_icono = "@drawable/accfavejecutarprograma"
            }

        }else if(accionGridList.tipo == "3"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.api64)
            uri_icono = "@drawable/accfavprogramadefault"

        }else if(accionGridList.tipo == "4"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.basedatos64)
            uri_icono = "@drawable/accfavdatabasedefault"

        }else if(accionGridList.tipo == "6"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.exploracion64)
            uri_icono = "@drawable/accfavexploradorarchivosd"

        }else if(accionGridList.tipo == "7"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.screenshot64)
            uri_icono = "@drawable/accfavscreenshotd"

        }else if(accionGridList.tipo == "8"){
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.borrardirorfile)
            uri_icono = "@drawable/accfaveliminararchivo"
        }else{
            nameView!!.findViewById<ImageView>(R.id.imageDynamicDasboard).setImageResource(R.drawable.negociox64)
            uri_icono = "@drawable/estrellafavoritoon"
        }

        image =  nameView!!.findViewById(R.id.imageFavoritos) as ImageView
        // if (accionGridList.favorita!!){
        var listAccFav = databaseHelper?.getAccionesFavoritasListData()
        if (listAccFav != null) {
            var existeFav =  listAccFav.filter { s -> s.idAccion == accionGridList.id_accion }
            if(existeFav.isNotEmpty()){
                image.tag = 0
                image.setImageResource(R.drawable.estrellafavoritoon)
                // image.layoutParams.height = 60
                // image.layoutParams.width = 60
            }else{
                image.tag = 1
                image.setImageResource(R.drawable.estrellafavoritooff)
                //image.layoutParams.height = 60
                // image.layoutParams.width = 60
            }
        }else{
            image.tag = 1
            image.setImageResource(R.drawable.estrellafavoritooff)
            // image.layoutParams.height = 60
            // image.layoutParams.width = 60
        }

        //   }else{
        //      image.tag = 1
        //     image.setImageResource(R.drawable.estrellafavoritooff)
        // }

        image .setOnClickListener {
            image.animationXFade(Zoom.ZOOM_IN)
            var tgImage =  image.tag

            if (tgImage == 0){
                delFavoritosMovil(accionGridList.id_accion, true)
                image.setImageResource(R.drawable.estrellafavoritooff)
                image.tag = 1


                this.databaseHelper?.delAccionesFavoritas(accionGridList.id_accion!!)

            }else{
                addFavoritosMovil(accionGridList.id_accion, true)

                image.setImageResource(R.drawable.estrellafavoritoon)
                image.tag = 0



                Log.i("ERRORFAV", "${accionGridList.id_accion!!}")
                Log.i("ERRORFAV", "${accionGridList.accion!!}")
                Log.i("ERRORFAV", "${accionGridList.comando!!}")
                Log.i("ERRORFAV", "${accionGridList.id_Equipo.toString()}")
                Log.i("ERRORFAV", "${accionGridList.nomServidor!!}")
                Log.i("ERRORFAV", "${accionGridList.tipo!!.toInt()}")
                Log.i("ERRORFAV", "${bm!!}")
                Log.i("ERRORFAV", "${uri_icono}")



                this.databaseHelper?.delAccionesFavoritas(accionGridList.id_accion!!)

                this.databaseHelper?.addAccionesFavoritas(accionGridList.id_accion!!, accionGridList.accion!!,accionGridList.comando!!, accionGridList.id_Equipo.toString(),accionGridList.nomServidor!!,accionGridList.tipo!!.toInt(),bm!!, uri_icono)


            }


        }




        var nomSvr = accionGridList.nomServidor
        nameView!!.findViewById<TextView>(R.id.tituloItemDashboard).text= accionGridList.accion!!.replace("-$nomSvr", "")
        nameView!!.findViewById<TextView>(R.id.tituloItemDashboard).setBackgroundColor(Color.parseColor("#566573"))

        return  nameView!!

    }

    fun delFavoritosMovil(id_accion: Int?, tipoAccion: Boolean):Boolean{
        var idDispositivo: String = Settings.Secure.getString(this.context!!.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)

        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var esFavorito =  false
        val userService: APIService = RestEngine.getRestEngine(databaseHelper!!,  this.context!!).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado= response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val addFavorito: APIService = RestEngine.getRestEngine(databaseHelper!!, context!!).create(APIService::class.java)
                    val resultFavorito: Call<String> = addFavorito.delFavoritosMovil("$idDispositivo","$id_accion","JWT $token", "application/json")

                    resultFavorito.enqueue(object : Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(context, "Error al establecer accion como favorito ", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            val resultado_addFav = response.body()

                            if (resultado_addFav == "ok") {
                                // Toast.makeText(context, "Se elimino la accion de favoritos", Toast.LENGTH_LONG).show()

                            }else{
                                //  Toast.makeText(context, "Error al establecer accion como favorito ", Toast.LENGTH_LONG).show()
                            }
                        }

                    })


                }



            }

        })


        return  esFavorito

    }


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


    fun addFavoritosMovil(id_accion: Int?, tipoAccion: Boolean):Boolean{
        var idDispositivo: String = Settings.Secure.getString(context!!.contentResolver, Settings.Secure.ANDROID_ID).toString()
        idDispositivo = removeLeadingZeroes(idDispositivo)
        idDispositivo =  removeTrailingZeroes(idDispositivo)
        val UserData = com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest(
            "licencias@dsiin.com",
            "xhceibEd"
        )
        var esFavorito =  false
        val userService: APIService = RestEngine.getRestEngine(databaseHelper!!, context!!).create(APIService::class.java)
        val result: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> = userService.submit(UserData)

        result.enqueue(object : Callback<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem> {
            override fun onFailure(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, t: Throwable) {
                Toast.makeText(context, "Error servicio no disponible ", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>, response: Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>) {
                val resultado= response.body()

                if (resultado != null) {
                    val token = resultado.token.toString()
                    val addFavorito: APIService = RestEngine.getRestEngine(databaseHelper!!, context!!).create(APIService::class.java)
                    val resultFavorito: Call<String> = addFavorito.addFavoritosMovil("$idDispositivo","$id_accion","JWT $token", "application/json")

                    resultFavorito.enqueue(object : Callback<String> {
                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Toast.makeText(context, "Error al establecer accion como favorito ", Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(call: Call<String>, response: Response<String>) {
                            val resultado_addFav = response.body()

                            if (resultado_addFav == "ok") {
                                //Toast.makeText(context, "Se agrego la accion a favoritos", Toast.LENGTH_LONG).show()

                            }else{
                                // Toast.makeText(context, "Error al establecer accion como favorito ", Toast.LENGTH_LONG).show()
                            }
                        }

                    })


                }



            }

        })


        return  esFavorito

    }

    override fun getItem(position: Int): Any {
        return accionList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return accionList.size
    }


}