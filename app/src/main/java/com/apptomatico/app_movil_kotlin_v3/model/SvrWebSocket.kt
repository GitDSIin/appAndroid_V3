package com.apptomatico.app_movil_kotlin_v3.model

data class DataServidorWebSocket(
    val data:List<RegServidorWebSocket_List>
)

data class RegServidorWebSocket_List(
    val Servidor_web_socket_puerto: String,
    val intentos_fallidos: String,
    val intentos_permitidos: String
)
