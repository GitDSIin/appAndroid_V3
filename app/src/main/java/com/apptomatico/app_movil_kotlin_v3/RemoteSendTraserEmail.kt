package com.apptomatico.app_movil_kotlin_v3

import android.os.AsyncTask
import com.apptomatico.app_movil_kotlin_v3.backup.Credentials
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class RemoteSendTraserEmail(val msg: String, val observaciones: String, val nommovil: String):  AsyncTask<Void, Void, String>()  {


    override fun doInBackground(vararg params: Void?): String {

        try{
            val credentials = Credentials()
            val props = System.getProperties()
            props.put("mail.smtp.host", "mail.dsiin.com")
            props.put("mail.smtp.auth", "true")
            props.put("mail.smtp.port", "587")

            val session = Session.getInstance(props,
                object : javax.mail.Authenticator() {
                    //Authenticating the password
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(credentials.EMAIL, credentials.PASSWORD)
                    }
                })



            val mm = MimeMessage(session)
            val emailId = "jescamilla@dsiin.com"
            val emailccpId = "aeslava@dsiin.com"
            mm.setFrom(InternetAddress(credentials.EMAIL))
            mm.addRecipient(
                Message.RecipientType.TO,
                InternetAddress(emailId)
            )




            mm.addRecipient(
                Message.RecipientType.CC,
                InternetAddress(emailccpId)
            )



            mm.subject = "Error detectado en movil - $nommovil"
            mm.setText("Este correo es generado de manera automática por el sistema Control de Licencias, no es necesario que responda a esta dirección de correo  \n " +
                    "\n Version de la Aplicación: ${BuildConfig.VERSION_NAME}" +
                    "\n Observaciones: $observaciones \n $msg")


            Transport.send(mm)
        }catch (ex: Exception){

            print(ex.message)
        }

        return ""
    }


}