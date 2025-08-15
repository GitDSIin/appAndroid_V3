package com.apptomatico.app_movil_kotlin_v3.ConsultaDinamica

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity
import com.apptomatico.app_movil_kotlin_v3.login.LoginActivity.Companion.webSocketService
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import com.otaliastudios.zoom.ZoomLayout
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConsultaDinamicaActivity: AppCompatActivity()  {
    lateinit var mTableLayout: TableLayout
    private lateinit var zoomDataBaseQuery: ZoomLayout
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imgButtonNuevaCOnsulta: ImageButton

    private var toolbar: androidx.appcompat.widget.Toolbar? = null
    var spinner_negocios: Spinner? = null
    var infoCOnsulta: String? = ""

    init{
        databaseHelper = DatabaseHelper.getInstance(this@ConsultaDinamicaActivity)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_dinamica)
        initObjects()

        zoomDataBaseQuery = findViewById(R.id.zoomDataBaseQuery)








        infoCOnsulta = intent.getStringExtra("info_conexion")

        val btnReturn = findViewById<View>(R.id.btnDBReturn) as ImageView
        btnReturn.setOnClickListener {
            finish()
        }
        imgButtonNuevaCOnsulta = findViewById<View>(R.id.imgButtonNuevaCOnsulta) as ImageButton

        imgButtonNuevaCOnsulta.setOnClickListener {
            showBottomSheetDialog()
        }






        var infoResult: List<com.apptomatico.app_movil_kotlin_v3.model.InfoAccion>  = databaseHelper.getAllInfoAccion()
        var result_query= infoResult[0].result
        title = ""



        // addHeaders(result_query)
        //addRows(result_query)
        var lblConcultandoLoad: TextView = findViewById(R.id.lblConcultandoLoad)
        var  progressBarBD: ProgressBar = findViewById(R.id.progressBarBD)

        lblConcultandoLoad.visibility = View.VISIBLE
        progressBarBD.visibility = View.VISIBLE
        val titulo_consulta = intent.getStringExtra("titulo_consulta")
        val nombre_equipo = intent.getStringExtra("nombre_negocio")
        var fecha_consulta = intent.getStringExtra("fecha_consulta")
        if(fecha_consulta == null || fecha_consulta == "null"){
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
            val formatted = current.format(formatter)
            fecha_consulta = formatted
        }

        var lblTitulo = findViewById<View>(R.id.lblTituloConsulta) as TextView
        lblTitulo.text = "$nombre_equipo\n$titulo_consulta\n$fecha_consulta"
        LoadDataTaskHeader(this@ConsultaDinamicaActivity, result_query, this@ConsultaDinamicaActivity).execute(0)
        LoadDataTask(this@ConsultaDinamicaActivity, result_query).execute(0)








        Handler().postDelayed({
            zoomDataBaseQuery!!.zoomTo(0F, true)

        }, 1000)



    }




    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === android.R.id.home) {
            finish() // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item)



    }




    private fun initObjects() {

        databaseHelper = DatabaseHelper.getInstance(this@ConsultaDinamicaActivity)

    }





    private fun getLayoutParams(): LayoutParams? {
        val params = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
        params.setMargins(2, 0, 0, 2)
        return params
    }


    private fun getTblLayoutParams(): TableLayout.LayoutParams? {
        return TableLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT)
    }

    private fun getTextView(id: Int, title: String, color: Int, typeface: Int, bgColor: Int): TextView? {
        val tv = TextView(this)
        tv.id = id
        tv.text = title.toUpperCase()
        tv.setTextColor(color)
        tv.setPadding(40, 40, 40, 40)
        tv.setTypeface(Typeface.DEFAULT, typeface)
        tv.setBackgroundColor(bgColor)
        tv.layoutParams = getLayoutParams()
        return tv
    }









    internal class LoadDataTaskHeader(activity: Activity?,result_array: String, Context: Context): AsyncTask<Int?, Int?, TableLayout?>() {
        var mActivity: Activity? = activity
        var result_query: String? = result_array
        val layoutINNER = TableLayout(mActivity)
        val layoutHEADER = TableLayout(mActivity)
        val Context = Context


        private fun getLayoutParams(): LayoutParams? {
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
            params.setMargins(2, 0, 0, 2)
            return params
        }


        private fun getTblLayoutParams(): TableLayout.LayoutParams? {
            return TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        }

        private fun getTextView(id: Int, title: String, color: Int, typeface: Int, bgColor: Int, widthInPercentOfScreenWidth: Int): TextView? {

            val screenWidth = DisplayMetrics()
            mActivity!!.windowManager.defaultDisplay.getMetrics(screenWidth)
            val tv = TextView(mActivity)
            tv.id = id
            tv.text = title.toUpperCase()
            tv.setTextColor(color)
            tv.setPadding(40, 40, 40, 40)
            tv.setTypeface(Typeface.DEFAULT, typeface)
            tv.setBackgroundColor(bgColor)
            tv.width  = widthInPercentOfScreenWidth * screenWidth.widthPixels / 100

            tv.layoutParams = getLayoutParams()
            return tv
        }


        fun addHeaders(result_query: String?){

            val tr = TableRow(mActivity)
            tr.layoutParams = getLayoutParams()

            val jArray = JSONArray(result_query)
            for (i in 0 until jArray.length()) {
                val `object` = jArray.optJSONObject(i)
                val iterator = `object`.keys()
                while (iterator.hasNext()) {
                    val currentKey = iterator.next()
                    tr.addView(getTextView(0, "$currentKey", Color.WHITE, Typeface.BOLD, R.color.colorPrimario, 50 ))
                    print(currentKey)
                }

                break

            }


            //layoutINNER.addView(tr, getTblLayoutParams())
            layoutHEADER.addView(tr, getTblLayoutParams())




        }




        override fun doInBackground(vararg params: Int?): TableLayout?{

            try {
                addHeaders(result_query)


            }catch (exception: Exception){
                return  null
            }



            return  layoutHEADER

        }



        override fun onPostExecute( newTable: TableLayout?) {


            if (newTable != null) {
                var tl: TableLayout? = mActivity?.findViewById(R.id.table_header)
                tl?.addView(newTable)
            }else{
                Toast.makeText(Context, "No es posible conectar con servidor remoto, contacte a su administrador ", Toast.LENGTH_LONG).show()

            }




        }




    }






    internal class LoadDataTask(activity: Activity?,result_array: String): AsyncTask<Int?, Int?, TableLayout?>() {
        var mActivity: Activity? = activity
        var result_query: String? = result_array
        val layoutINNER = TableLayout(mActivity)
        val layoutHEADER = TableLayout(mActivity)




        private fun getLayoutParams(): LayoutParams? {
            val params = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
            params.setMargins(2, 0, 0, 2)
            return params
        }


        private fun getTblLayoutParams(): TableLayout.LayoutParams? {
            return TableLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
        }

        private fun getTextView(id: Int, title: String, color: Int, typeface: Int, bgColor: Int, widthInPercentOfScreenWidth: Int): TextView? {
            val screenWidth = DisplayMetrics()
            mActivity!!.windowManager.defaultDisplay.getMetrics(screenWidth)
            val tv = TextView(mActivity)
            tv.id = id
            tv.text = title.toUpperCase()
            tv.setTextColor(color)
            tv.setPadding(40, 40, 40, 40)
            tv.setTypeface(Typeface.DEFAULT, typeface)
            tv.setBackgroundColor(bgColor)
            tv.width  = widthInPercentOfScreenWidth * screenWidth.widthPixels / 100
            tv.layoutParams = getLayoutParams()
            return tv
        }


        fun addHeaders(result_query: String?){

            val tr = TableRow(mActivity)
            tr.setLayoutParams(getLayoutParams())

            val jArray = JSONArray(result_query)
            for (i in 0 until jArray.length()) {
                val `object` = jArray.optJSONObject(i)
                val iterator = `object`.keys()
                while (iterator.hasNext()) {
                    val currentKey = iterator.next()
                    tr.addView(getTextView(0, "$currentKey", Color.WHITE, Typeface.BOLD, R.color.colorPrimario, 150))
                    print(currentKey)
                }

                break

            }


            //layoutINNER.addView(tr, getTblLayoutParams())
            layoutHEADER.addView(tr, getTblLayoutParams())




        }




        override fun doInBackground(vararg params: Int?): TableLayout?{

            try {


                val jArray = JSONArray(result_query)
                for (i in 0 until (jArray.length() ?: 0)) {
                    val data = jArray.optJSONObject(i)
                    if (data != null) {
                        val it = data.keys()
                        var k = 0
                        val tr = TableRow(mActivity)
                        tr.layoutParams = getLayoutParams()
                        while (it.hasNext()) {
                            val key = it.next()

                            try {

                                //  var ss = data.optString(key.toString()).toString()
                                //  tr.addView(getTextView(k + 1, ss, Color.BLACK, Typeface.NORMAL, ContextCompat.getColor(mActivity!!, R.color.colorAccent), 50))

                                //  k += 1


                                if (data[key.toString()] is JSONArray) {
                                    val arry = data.getJSONArray(key.toString())
                                    val size = arry.length()
                                    for (i in 0 until size) {

                                    }
                                } else if (data[key.toString()] is JSONObject) {

                                } else {


                                    tr.addView(getTextView(k + 1, data.optString(key.toString()), Color.BLACK, Typeface.NORMAL, ContextCompat.getColor(mActivity!!, R.color.black), 50))

                                    k += 1

                                }


                            } catch (e: Throwable) {
                                println("" + key + " : " + data.optString(key.toString()))
                                e.printStackTrace()
                            }
                        }


                        if (tr.getParent() != null) {
                            (tr.getParent() as ViewGroup).removeView(tr) // <- fix
                        }
                        layoutINNER.addView(tr, getTblLayoutParams())



                    }
                }

            }catch (exception: Exception){
                return  null
            }





            return  layoutINNER

        }



        override fun onPostExecute( newTable: TableLayout?) {
            var tl: TableLayout? = mActivity?.findViewById(R.id.tableInvoices)
            tl?.addView(newTable)
            var lblConsultaLoad: TextView? = mActivity?.findViewById(R.id.lblConcultandoLoad)
            var progressBar: ProgressBar? = mActivity?.findViewById(R.id.progressBarBD)
            lblConsultaLoad?.visibility = View.GONE
            progressBar?.visibility = View.GONE


        }

    }




    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.bottom_consultabd_dialog_layout)
        var lblDatosCOnexion =bottomSheetDialog.findViewById<TextView>(R.id.lblDatosCOnexion)
        var lblError =bottomSheetDialog.findViewById<TextView>(R.id.lblError)
        val textInputEditConsulta = bottomSheetDialog.findViewById<TextInputEditText>(R.id.textInputEditConsulta)
        val btnEjectDBAceptar = bottomSheetDialog.findViewById<Chip>(R.id.btnEjectDBAceptar)
        val progressAlertas =  bottomSheetDialog.findViewById<ProgressBar>(R.id.progressAlertas)


        var infCon = infoCOnsulta!!.split("|")


        lblDatosCOnexion!!.text = "Conectado a la base: ${infCon[0]}"
        textInputEditConsulta!!.setText("${infCon[1]}")
        btnEjectDBAceptar!!.setOnClickListener {
            lblError!!.visibility = View.GONE
            if (textInputEditConsulta!!.length() == 0){
                lblError!!.text = "Ingrese una consulta valida"
                lblError!!.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!textInputEditConsulta!!.text.toString().contains("select", ignoreCase = true)) {
                if (!textInputEditConsulta!!.text.toString().contains("delete", ignoreCase = true)) {
                    if (!textInputEditConsulta!!.text.toString().contains("update", ignoreCase = true)) {
                        if (!textInputEditConsulta!!.text.toString().contains("insert", ignoreCase = true)) {
                            lblError!!.text = "Ingrese una consulta valida"
                            lblError!!.visibility = View.VISIBLE
                            return@setOnClickListener
                        }
                    }
                }
            }

            var idDispositivo: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).toString()
            idDispositivo = removeLeadingZeroes(idDispositivo)
            idDispositivo =  removeTrailingZeroes(idDispositivo)
            progressAlertas!!.visibility = View.VISIBLE
            databaseHelper.deleteinfoAccion()

            var newCOnsulta = textInputEditConsulta!!.text.toString()
            newCOnsulta = newCOnsulta.toString().trim()
            val parresult = LoginActivity.bd_parametros_accion.dropLast(4)
            var parametros = "$parresult$newCOnsulta"
            webSocketService!!.sendMessage("{\"id_hardware\":\"${LoginActivity.bd_hardware_equipo}\",\"id_hardwareMovil\":\"$idDispositivo\",\"funcion\":\"getConsultaBDEquipo\",\"parametros\":\"$parametros\"}")

            Handler().postDelayed({
                progressAlertas!!.visibility = View.GONE
                finish()
            }, 2000)






        }



        bottomSheetDialog.show()
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

}