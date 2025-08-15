package com.apptomatico.app_movil_kotlin_v3.negocio


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.*
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import  com.apptomatico.app_movil_kotlin_v3.R
import java.io.File


class DownloadController(private val context: Context, private val url: String): AsyncTask<Void?, Void?, Boolean?>() {
    companion object {
        private const val FILE_NAME = "app_android.apk"
        private const val FILE_BASE_PATH = "file://"
        private const val MIME_TYPE = "application/vnd.android.package-archive"
        private const val PROVIDER_PATH = ".provider"
        private const val APP_INSTALL_PATH = "\"application/vnd.android.package-archive\""
    }
    fun enqueueDownload() {









        //////


    }

    override fun onPreExecute() {
        super.onPreExecute()

        Toast.makeText(context, context.getString(R.string.downloading), Toast.LENGTH_LONG)
            .show()
    }

    @SuppressLint("Range")
    override fun doInBackground(vararg p0: Void?): Boolean? {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("AppTomatiza - Descarga de Archivo")
        request.setDescription("La actualizacion se está descargando ...")

        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"/AppTomatiza/$FILE_NAME")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE)  as DownloadManager
        var downloadID = manager.enqueue(request)



        var finishDownload: Boolean = false
        var progress: Int
        while (!finishDownload) {
            val cursor: Cursor = manager.query(DownloadManager.Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                    }
                    DownloadManager.STATUS_PAUSED -> {
                    }
                    DownloadManager.STATUS_PENDING -> {
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        val total: Long = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val downloaded: Long = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                            progress = (downloaded * 100L / total).toInt()
                            Log.i("MYDESCARGA","$progress")
                            // if you use downloadmanger in async task, here you can use like this to display progress.
                            // Don't forget to do the division in long to get more digits rather than double.
                            // publishProgress((int) ((downloaded * 100L) / total));
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        progress = 100
                        Log.i("MYDESCARGA","$progress")
                        // if you use aysnc task
                        // publishProgress(100);
                        finishDownload = true



                    }
                }
            }
        }


        return true
    }

    override fun onPostExecute(result: Boolean?) {
        super.onPostExecute(result)

        var dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setCancelable(false)
        dialogBuilder.setMessage("La actualización se descargo de forma correcta, vaya a descargas para realizar la instalacion ")
            .setCancelable(false)
            .setPositiveButton("Ir a Descargas", DialogInterface.OnClickListener { _, _ ->

                var gpath: String = Environment.getExternalStorageDirectory().absolutePath
                var spath = "Download/AppTomatiza"
                var fullpath = gpath + File.separator + spath
                val uri: Uri = Uri.parse(fullpath)
                val accountsIntent = Intent(Intent.ACTION_PICK)
                accountsIntent.setDataAndType(uri, "*/*")
                context.startActivity(accountsIntent)


            })
            .setNeutralButton("Cerrar", DialogInterface.OnClickListener { view, _ ->
                view.dismiss()
            })

        var alert = dialogBuilder.create()
        alert.setTitle("")
        alert.show()

    }


}