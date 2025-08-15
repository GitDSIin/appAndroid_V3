package com.apptomatico.app_movil_kotlin_v3.negocio


import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.apptomatico.app_movil_kotlin_v3.model.Licencia
import org.apache.commons.net.PrintCommandListener
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import java.io.FileInputStream
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class BackupDataBaseWorker(context: Context, params: WorkerParameters) : Worker(context,params) {
    var databaseHelper_Data: DatabaseHelper
    init{
        databaseHelper_Data = DatabaseHelper.getInstance(context)
    }

    override fun doWork(): Result {
        try {

            Log.i("MYFTPBK","Start connecting to FTP Server")
            val filePath =  inputData.getString("file_path")
            val movil_id =  inputData.getString("movil_id")
            val iftp =  inputData.getString("ftp")
            val iftpuser =  inputData.getString("ftpuser")
            val iftppwd =  inputData.getString("ftpwd")
            val iftppuerto =  inputData.getString("ftppuerto")
            val iIntervalo =  inputData.getString("intervalo")
            val iUnidad =  inputData.getString("unidad")





            val sdf = SimpleDateFormat("dd/M/yyyy_hh mm:ss")
            var currenDateNow: String = sdf.format(Date())
            currenDateNow = currenDateNow.replace(" ","_")
            currenDateNow = currenDateNow.replace("/","_")
            currenDateNow = currenDateNow.replace(":","_")





            if (movil_id != "null" && movil_id != ""){
                val ftpClient = FTPClient()
                ftpClient.addProtocolCommandListener(PrintCommandListener(PrintWriter(System.out)))
                ftpClient.connect(iftp!!, iftppuerto!!.toInt())
                val reply: Int = ftpClient.replyCode
                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect()
                    Log.i("MYFTPBK","Exception in connecting to FTP Server")
                    throw IOException("Exception in connecting to FTP Server")
                }

                Log.i("MYFTPBK","FTP Backup OK")
                if (ftpClient.login(iftpuser, iftppwd)) {
                    ftpClient.enterLocalPassiveMode()
                    ftpClient.setFileType(FTP.BINARY_FILE_TYPE)
                    val inp = FileInputStream(filePath)
                    var directory = "/files/backupDataBases"
                    ftpClient.changeWorkingDirectory(directory)
                    val result = ftpClient.storeFile("DBMovil_Cafeterias_Dev_$movil_id.db", inp)
                    inp.close()
                    if (result) {
                        ftpClient.logout()
                        ftpClient.disconnect()

                        databaseHelper_Data.addControlBitacoraSendBackupDB(iftp!!,iIntervalo!!.toInt(), iUnidad!!, currenDateNow)
                        Log.i("MYFTPBK","FTP Backup se cargo correctamente")
                    }

                }

            }


            return Result.success()
        } catch (e:Exception){
            return Result.failure()
        }

    }


}