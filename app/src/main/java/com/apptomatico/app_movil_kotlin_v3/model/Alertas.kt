package com.apptomatico.app_movil_kotlin_v3.model


data class ControlAlertasData(val idAlerta: Int, val titulo: String, val descripcion: String, val nom_negocio: String, val tipo_id: Int, val fecha: String){
    private var idAlerta_: Int? = null
    private var idctlAlerta: Int? = null
    private var isSelectedA = false

    fun ControlAlertasData(idAlerta: Int) {
        idAlerta_ = idAlerta
    }

    fun getIdAlerta(): Int? {
        return idAlerta_
    }



    fun setSelectedA(selected: Boolean) {
        isSelectedA = selected
    }


    fun isSelectedA(): Boolean {
        return isSelectedA
    }

    //MARCADAS

    fun ControlAlertasMarcadaData(idAlerta: Int) {
        idAlerta_ = idAlerta
    }

    fun getIdAlertaMarcada(): Int? {
        return idAlerta_
    }

    fun setSelectedAMarcada(selected: Boolean) {
        isSelectedA = selected
    }


    fun isSelectedAMarcada(): Boolean {
        return isSelectedA
    }

}


data class ControlAlertasEliminadasData(val idAlerta: Int, val titulo: String, val descripcion: String, val nom_negocio: String, val tipo_id: Int, val fecha: String, val fecha_eliminacion: String, val id_evt: Int){
    private var idAlerta_: Int? = null
    private var isSelectedA = false

    fun ControlAlertasData(idAlerta: Int) {
        idAlerta_ = idAlerta
    }

    fun getIdAlerta(): Int? {
        return idAlerta_
    }

    fun setSelectedA(selected: Boolean) {
        isSelectedA = selected
    }


    fun isSelectedA(): Boolean {
        return isSelectedA
    }
}

data class ControlAlertasFavData(val alerta_tipo: Int, val ultimas_entradas: Int)

data class ConfiguracionAlertasData(val top_alertas: Int)

data class DataListAlertas(
        val data:List<Alertas_List>
)

data class Alertas_List(
        val id: Int,
        val dispositivo: String,
        val alerta: String,
        val descripcion: String,
        val fecha_alerta: String,
        val favorito: Int,
        val tipo_alerta: Int,
        val nombre_equipo: String,
        val id_accion_negocio: Int,
        val evento_id_id: Int



)



data class clsPausaAlertas(val pa_id: Int, val pa_tiempo: Int, val pa_fecha_inicio: String, val pa_fecha_fin:String, val pa_estatus: Int )
