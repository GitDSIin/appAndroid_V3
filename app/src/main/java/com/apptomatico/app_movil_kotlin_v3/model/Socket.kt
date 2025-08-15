package com.apptomatico.app_movil_kotlin_v3.model

data class RespuestaWebSocketDel(
        var message : SKMessageDel? = SKMessageDel()



)

data class SKMessageDel (
        var id: String? = null,
        var idequipo: String? = null,
        var movil : String? = null,
        var funcion   : String? = null,
        var respuesta : String? = null

)


data class RespuestaWebSocket(
        var message : SKMessage? = SKMessage()



)

data class SKMessage (
        var id: String? = null,
        var idequipo: String? = null,
        var movil : String? = null,
        var funcion   : String? = null,
        var respuesta : String? = null,
        var info: String? = null

)


data class RespuestaEncodeImageWebSocket(
    var message : EncodeImgMessage? = EncodeImgMessage()



)

data class EncodeImgMessage (
    var id: String? = null,
    var idequipo: String? = null,
    var movil : String? = null,
    var nomimagen : String? = null,
    var funcion   : String? = null,
    var respuesta : String? = null,
    var info: String? = null

)



data class WSRESMONITORACCION(

        var message : SKMonitorAccMessage? = SKMonitorAccMessage()

)



data class SKMonitorAccMessage(
        var id : String? = null,
        var idequipo: String? = null,
        var movil     : String?              = null,
        var funcion   : String?              = null,
        var respuesta : ArrayList<Monitor_List> = arrayListOf()

)


data class WSRESMONITORACCIONGR(

        var message : SKMonitorAccMessageGR? = SKMonitorAccMessageGR()

)



data class SKMonitorAccMessageGR(
        var id : String? = null,
        var idequipo: String? = null,
        var movil     : String?              = null,
        var funcion   : String?              = null,
        var dataequipo  : String? = null,
        var respuesta : ArrayList<Monitor_Deatlle_Grefico_List> = arrayListOf()

)


data class WSEXPLORACIONEQUIPO (

        var message : SkExploracionArchivoMessage? = SkExploracionArchivoMessage()

)

data class SkExploracionArchivoMessage (

        var id : String? = null,
        var idequipo: String? = null,
        var movil     : String?  = null,
        var funcion   : String?  = null,
        var errores   : String?  = null,
        var respuesta : ArrayList<Negocio_Directorio> = arrayListOf()

)



data class WSEXPLORACIONEQUIPOERROR (

        var message : SkExploracionArchivoMessageERROR? = SkExploracionArchivoMessageERROR()

)

data class SkExploracionArchivoMessageERROR (

        var id : String? = null,
        var idequipo: String? = null,
        var movil     : String?  = null,
        var funcion   : String?  = null,
        var errores   : String?  = null,
        var respuesta : String? = null

)



data class RespuestaExploracionEquipo (

        var idMenu      : Int?    = null,
        var nombre      : String? = null,
        var Path        : String? = null,
        var parent      : Int?    = null,
        var tipo        : Int?    = null,
        var peso        : String? = null,
        var peso_real   :  Long?    = null,
        var tipoArchivo : String? = null,
        var ultMod      : String? = null,
        var nivel       : Int?    = null

)

//VALIDACION EQUIPO ACTIVO

data class NewWebSocketEstatus(
        var data: ArrayList<DataEstadoEquipoSocket> = arrayListOf()
)

data class DataEstadoEquipoSocket(
        var estatus_chat: String? = null,
        var fecha_actualizacion: String? = null
)


//VALIDACION EQUIPO ACTIVO ALL

data class NewWebSocketEstatusAll(
        var data: ArrayList<DataEstadoEquipoSocketAll> = arrayListOf()
)

data class DataEstadoEquipoSocketAll(
        var negocio_id: Int? = null,
        var estatus_chat: String? = null,
        var fecha_actualizacion: String? = null
)







data class RespuestaDBWebSocket(
        var message : SKDBMessage? = SKDBMessage()



)

data class SKDBMessage (

        var id: String? = null,
        var idequipo: String? = null,
        var movil : String? = null,
        var funcion   : String? = null,
        var infoconexion   : String? = null,
        var respuesta : String? = null

)

//******************************************************

data class RespuestaApiExtSocket(
    var message : SKApiExtMessage? = SKApiExtMessage()



)

data class SKApiExtMessage (

    var id: String? = null,
    var idequipo: String? = null,
    var movil : String? = null,
    var funcion   : String? = null,
    var idaccion : String? = null,
    var inforespuesta   : String? = null,
    var respuesta : String? = null

)


//*******************************************************

data class RespuestaWebSocketBitacoraTODO(
    var message : List<SKMessageBitacora>
)



//*******************************************************



data class RespuestaWebSocketBitacora(
        var message : SKMessageBitacora? = SKMessageBitacora()
)




data class SKMessageBitacora (

       var id           : String? = null,
       var fecregistro : String? = null,
       var idequipo     : String? = null,
       var movil        : String? = null,
       var idmovil      : String? = null,
       var funcion      : String? = null,
       var respuesta    : String? = null,
       var accion       : String? = null,
       var nomequipo    : String? = null,
       var porcentaje   : String? = null,
       var uso_total     : String? = null,
       var uso_libre     : String? = null,
       var paq_enviados  : String? = null,
       var paq_recibidos : String? = null,
       var uso_disco     : String? = null

)





data class RespuestaWebSocketHistoricoSC(
        var message : SKMessageHistoricoSC? = SKMessageHistoricoSC()



)




data class SKMessageHistoricoSC (

        var id           : String? = null,
        var idequipo     : String? = null,
        var idmovil      : String? = null,
        var funcion      : String? = null,
        var respuesta    :  ArrayList<Respuesta> = arrayListOf()

)

data class Respuesta (

       var idMenu      : Int?    = null,
       var nombre      : String? = null,
       var Path        : String? = null,
       var parent      : Int?    = null,
       var tipo        : Int?    = null,
       var peso        : String? = null,
       var pesoReal    : Int?    = null,
       var mascara     : String? = null,
       var tipoArchivo : String? = null,
       var ultMod      : String? = null,
       var nivel       : Int?    = null

)



data class RespuestaWebSocketHistoricoSCImg(
    var message : SKMessageHistoricoSCImg? = SKMessageHistoricoSCImg()



)

data class SKMessageHistoricoSCImg (

   var id        : String? = null,
   var idequipo  : String? = null,
   var idmovil   : String? = null,
   var nomimagen : String? = null,
   var funcion   : String? = null,
   var respuesta : String? = null

)
