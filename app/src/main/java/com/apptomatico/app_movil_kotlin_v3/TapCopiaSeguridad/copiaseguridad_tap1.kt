package com.apptomatico.app_movil_kotlin_v3.TapCopiaSeguridad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import com.apptomatico.app_movil_kotlin_v3.negocio.BackupDataBaseWorker
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.google.android.material.textfield.TextInputEditText
import com.rommansabbir.animationx.Fade
import com.rommansabbir.animationx.animationXFade
import net.sqlcipher.database.SQLiteDatabase
import java.util.ArrayList
import java.util.concurrent.TimeUnit

class copiaseguridad_tap1: Fragment() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.modal_menu_configuracion_respaldo_db, container, false)
        val databaseHelper_Data = DatabaseHelper.getInstance(container!!.context)
        var btnMenosInterBkDb =  view?.findViewById(R.id.btnMenosInterBkDb) as Button
        var btnMasInterBkDb =  view?.findViewById(R.id.btnMasInterBkDb) as Button
        var textInputEditIntervaloBackupDB = view?.findViewById(R.id.textInputEditIntervaloBackupDB) as TextInputEditText
        var unidad_backup_spinner = view?.findViewById(R.id.unidad_backup_spinner) as Spinner

        var unidadBkList : java.util.ArrayList<com.apptomatico.app_movil_kotlin_v3.model.Unidades_Backup_DB_list> = ArrayList()
        unidadBkList.add(
            com.apptomatico.app_movil_kotlin_v3.model.Unidades_Backup_DB_list(
                "Minutos",
                1
            )
        )
        unidadBkList.add(
            com.apptomatico.app_movil_kotlin_v3.model.Unidades_Backup_DB_list(
                "Horas",
                2
            )
        )
        val arrayAdapter: ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Unidades_Backup_DB_list> = ArrayAdapter<com.apptomatico.app_movil_kotlin_v3.model.Unidades_Backup_DB_list>(requireContext(), android.R.layout.simple_spinner_item, unidadBkList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        unidad_backup_spinner!!.adapter = arrayAdapter

        textInputEditIntervaloBackupDB!!.setText(databaseHelper_Data.getIntervaloBackupDataBase().toString())

        var uniSel = databaseHelper_Data.getUnidadBackupDataBase()

        unidad_backup_spinner!!.setSelection(uniSel)

        var appCompatButtonUpdConfGnrl = view?.findViewById(R.id.appCompatButtonUpdConfGnrl) as Button
        var appCompatButtonCancelarConfGnrl = view?.findViewById(R.id.appCompatButtonCancelarConfGnrl) as Button
        btnMenosInterBkDb!!.setOnClickListener {
            var valActual = textInputEditIntervaloBackupDB!!.text.toString()
            valActual = (valActual.toInt() - 1).toString()
            if (valActual.toInt() < 15){
                valActual = "15"
            }
            textInputEditIntervaloBackupDB!!.setText(valActual)
        }

        btnMasInterBkDb!!.setOnClickListener {
            var valActual = textInputEditIntervaloBackupDB!!.text.toString()
            valActual = (valActual.toInt() + 1).toString()
            if (valActual.toInt() < 15){
                valActual = "15"
            }
            textInputEditIntervaloBackupDB!!.setText(valActual)
        }


        val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.DialogTheme)
        dialogBuilder.setView(view)
        dialogBuilder.setTitle("Copia de seguridad de la Aplicacion")
        appCompatButtonUpdConfGnrl!!.setOnClickListener{
            appCompatButtonUpdConfGnrl!!.animationXFade(Fade.FADE_IN_DOWN)
            val txtUnidad: String = unidad_backup_spinner!!.selectedItem.toString()

            databaseHelper_Data.updControlSendBackupDB(txtUnidad, (textInputEditIntervaloBackupDB.text.toString()).toInt())
            setPeriodicWorkRequest(databaseHelper_Data)
            Toast.makeText(context, "Se guardo la configuracion de forma correcta", Toast.LENGTH_LONG).show()
            activity?.finish()
        }
        appCompatButtonCancelarConfGnrl!!.setOnClickListener{
            appCompatButtonCancelarConfGnrl!!.animationXFade(Fade.FADE_IN_DOWN)
            activity?.finish()
        }







        return  view

    }



    private fun setPeriodicWorkRequest(databaseHelper_Data: DatabaseHelper) {
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        var Licenciadb: List<Licencia>  = databaseHelper_Data.getAllLicencia()
        var Movil_Id = Licenciadb[0].id_movil

        var datosBackupDb = databaseHelper_Data.getCOntrolBackupDB()


        if (datosBackupDb != ""){
            val reg = datosBackupDb.split("|")

            val Interval: Long = (reg[0]).toLong()
            var unidad = reg[1]
            var ftp = reg[2]
            var ftpuser = reg[3]
            var ftpwd = reg[4]
            var ftppuerto = reg[5]




            var tmUnit: TimeUnit = TimeUnit.MINUTES

            when (unidad) {
                "Minutos" ->
                    tmUnit = TimeUnit.MINUTES
                "Horas" ->
                    tmUnit = TimeUnit.HOURS
                "Dias" ->
                    tmUnit = TimeUnit.DAYS
                else -> { // Note the block

                }
            }

            val databasepath = requireContext().getDatabasePath("UserManager.db").toString()
            val data = Data.Builder()
            data.putString("file_path", databasepath)
            data.putString("movil_id", Movil_Id.toString())
            data.putString("ftp", ftp)
            data.putString("ftpuser", ftpuser)
            data.putString("ftpwd", ftpwd)
            data.putString("ftppuerto", ftppuerto)
            data.putString("intervalo", Interval.toString())
            data.putString("unidad", unidad)



            val periodicWorkRequest = PeriodicWorkRequest
                .Builder(BackupDataBaseWorker::class.java, Interval, tmUnit)
                .setInputData(data.build())
                .build()

            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork("JobBackupDb", ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest)

        }





    }

}