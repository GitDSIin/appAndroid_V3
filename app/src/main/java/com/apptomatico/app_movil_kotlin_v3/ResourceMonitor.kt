package com.apptomatico.app_movil_kotlin_v3

import android.util.Log
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import javax.mail.*
import javax.mail.internet.*

object MemoryUtilsMail {

    private const val TAG = "MemoryUtils"
    private const val MEMORY_THRESHOLD: Long = 200 * 1024 * 1024 // 50 MB (ajusta este umbral según sea necesario)

    // Método para obtener el uso de memoria de la aplicación y verificar si supera el umbral
    fun checkMemoryUsageAndSendAlert(actividad: String, databaseHelper_Data: DatabaseHelper) {
        val usedMemory = getUsedMemory() // Obtener la memoria usada por la app en bytes
        Log.d(TAG, "Used Memory: ${usedMemory / (1024 * 1024)} MB")

        // Si el uso de memoria excede el umbral, enviamos un correo de alerta
        if (usedMemory > MEMORY_THRESHOLD) {
            var Lic: List<com.apptomatico.app_movil_kotlin_v3.model.Licencia>  = databaseHelper_Data.getAllLicencia()
            var nombre_Movil = Lic[0].nommbre_movil
            val subject = "Alerta de Uso de Memoria - Umbral Superado -$nombre_Movil"
            val body = "Se ha superado el umbral de uso de memoria en la aplicación:\n\n" +
                    "Nombre de la Actividad: $actividad \n\n" +
                    "Uso de Memoria: ${usedMemory / (1024 * 1024)} MB\n\n" +
                    "Por favor, revise el estado de la aplicación."

            // Enviar correo en un hilo de fondo
            sendEmailInBackground(subject, body)
        }
    }

    // Método para obtener la memoria utilizada por la aplicación
    private fun getUsedMemory(): Long {
        val runtime = Runtime.getRuntime()

        // Total de memoria utilizada por la aplicación
        val totalMemory = runtime.totalMemory() // Memoria total ocupada por la JVM
        val freeMemory = runtime.freeMemory()   // Memoria libre dentro de la JVM

        // Memoria utilizada (total - libre)
        return totalMemory - freeMemory
    }

    // Método para enviar un correo electrónico en un hilo de fondo
    private fun sendEmailInBackground(subject: String, body: String) {
        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        executor.execute {
            sendEmail(subject, body)
        }
    }

    // Método para enviar un correo electrónico de alerta
    private fun sendEmail(subject: String, body: String) {
        // Configuración para el servidor de correo
        val to = "jescamilla@dsiin.com" // Reemplaza con la dirección de correo electrónico del destinatario
        val from = "licencias@dsiin.com" // Reemplaza con tu dirección de correo electrónico
        val host = "mail.dsiin.com" // Servidor SMTP (por ejemplo, smtp.gmail.com para Gmail)

        val properties = Properties() // Aquí es donde necesitamos importar 'Properties'
        properties["mail.smtp.host"] = host
        properties["mail.smtp.port"] = "587" // Puerto para TLS

        // Aquí debes autenticarte con tus credenciales de correo
        properties["mail.smtp.auth"] = "true"
        properties["mail.smtp.starttls.enable"] = "true"

        val session = Session.getInstance(properties, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication("licencias@dsiin.com", "JEscamilla1DSI")
            }
        })

        try {
            // Crear un objeto MimeMessage para enviar el correo
            val message = MimeMessage(session)
            message.setFrom(InternetAddress(from))
            message.addRecipient(Message.RecipientType.TO, InternetAddress(to))
            message.subject = subject
            message.setText(body)

            // Enviar el mensaje
            Transport.send(message)
            Log.d(TAG, "Correo enviado con éxito.")

        } catch (e: MessagingException) {
            e.printStackTrace()
            Log.e(TAG, "Error al enviar el correo: ${e.message}")
        }
    }
}
