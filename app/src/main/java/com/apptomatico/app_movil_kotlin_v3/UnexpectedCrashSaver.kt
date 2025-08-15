package com.apptomatico.app_movil_kotlin_v3

import android.content.Context
import java.io.FileOutputStream
import java.io.IOException

class UnexpectedCrashSaver(app: Context?) : Thread.UncaughtExceptionHandler {
    private val defaultUEH: Thread.UncaughtExceptionHandler
    private var app: Context? = null

    init {
        defaultUEH = Thread.getDefaultUncaughtExceptionHandler()
        this.app = app
    }

    override fun uncaughtException(t: Thread?, e: Throwable) {
        var arr =  e.stackTrace
        var report = """
            $e
            
            
            """.trimIndent()
        report += "--------- Stack trace ---------\n\n"
        for (i in arr.indices) {
            report += """    ${arr[i]}
"""
        }

        report += "-------------CAUSA-------------\n\n"
        val cause = e.cause
        if (cause != null) {
            report += """
                $cause
                
                
                """.trimIndent()
            arr = cause.stackTrace
            for (i in arr.indices) {
                report += """    ${arr[i]}
"""
            }
        }

        report += "-------------MENSAJE-------------\n\n"
        val mensaje = e.message

        if (mensaje != null) {
            report += """
                $mensaje
                
                
                """.trimIndent()
        }





        report += "-------------------------------\n\n"
        try {
            val trace: FileOutputStream = app!!.openFileOutput(
                "stack.trace",
                Context.MODE_PRIVATE
            )
            trace.write(report.toByteArray())
            trace.close()
        } catch (ioe: IOException) {
            // ...
        }
        defaultUEH.uncaughtException(t, e)
    }
}