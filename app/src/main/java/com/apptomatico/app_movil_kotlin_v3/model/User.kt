package com.apptomatico.app_movil_kotlin_v3.model

/**
 * Created by Lalit Vasan on 9/12/2016.
 */

// model class
data class User(val id: Int = -1, val name: String, val email: String, val password: String)


data class ValNip(
        val data:List<GetUserNip>
)
data class GetUserNip (
        val id: Int,
        val movil_longitud_min_nip: Int,
        val movil_longitud_max_nip: Int

)