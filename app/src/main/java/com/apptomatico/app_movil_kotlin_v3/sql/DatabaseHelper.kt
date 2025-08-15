package com.apptomatico.app_movil_kotlin_v3.sql


//import android.database.sqlite.SQLiteDatabase

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import com.apptomatico.app_movil_kotlin_v3.model.*
import net.sqlcipher.database.SQLiteStatement
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


/**
 * Created by lalit on 9/12/2016.
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {




    // create table sql query
    private val CREATE_USER_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD + " TEXT)")

    private val CREATE_TABLE_SERVER_SOCKET = ("CREATE TABLE IF NOT EXISTS " + TABLE_SERVER_SOCKET + "("
            + COLUMN_SERVER_SOCKET + " TEXT," + COLUMN_SERVER_SOCKET_PUERTO + " TEXT,"
            + COLUMN_SERVER_SOCKET_FEC_ULT_ACT + " TEXT)")


    private val CREATE_TABLE_SERVER_SOCKET_LOG = ("CREATE TABLE IF NOT EXISTS " + TABLE_SERVER_SOCKET_LOG + "("
            + COLUMN_SERVER_SOCKET_FEC_ALTA + " TEXT,"
            + COLUMN_SERVER_SOCKET_DESCRIPCION + " TEXT)")

    private val CREATE_USER_AVATAR_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_USER_AVATAR + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_AVATAR + " BLOB)")

    private val CREATE_LICENCIA_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_LICENCIA + "("
            + COLUMN_LICENCIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_HARDWARE_KEY + " TEXT,"
            + COLUMN_LICENCIA_ACEPTACION + " TEXT," + COLUMN_ID_MOVIL + " INTEGER,"
            + COLUMN_NOMBRE_MOVIL + " TEXT,"
            + COLUMN_NOMBRE_DOMINIO + " TEXT,"
            + COLUMN_VERSION_SOFTWARE + " TEXT)")


    private val CREATE_CAMBIOS_VERSION_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_CAMBIOS_VERSION + "("
            + COLUMN_VERSION +  " TEXT,"
            + COLUMN_CAMBIOS + " TEXT,"
            + COLUMN_MUESTRA_MENSAJE + " INTEGER)")


    private val CREATE_PARAMETROS_LICENCIA_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_PARAMETROS_LICENCIA + "("
            + COLUMN_VERSION_REQUIERE_UBICACION + " TEXT)")


    private val CREATE_TABLE_CONTROL_ENTORNO = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ENTORNO + "("
            + COLUMN_ENTORNO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ENTORNO + " INTEGER)")

    private val CREATE_VIGENCIA_LICENCIA_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_VIGENCIA_LICENCIA + "("
            + COLUMN_FECHA_DESDE + " TEXT,"
            + COLUMN_FECHA_HASTA + " TEXT,"
            + COLUMN_DIAS_FIN_VIGENCIA + " INTEGER,"
            + COLUMN_INTERVALO_VIGENCIA + " TEXT)")





    private val CREATE_SEGURIDAD_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_SEGURIDAD + "("
            + COLUMN_SEGURIDAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NIP + " TEXT)")

    private val CREATE_SEGURIDAD_NIP_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_SEGURIDAD_NIP + "("
            + COLUMN_NIP_LONGITUD_MIN + " INTEGER,"
            + COLUMN_NIP_LONGITUD_MAX + " INTEGER)")

    private val CREATE_NEGOCIOS_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_NEGOCIOS + "("
            + COLUMN_NEGOCIOS_ID + " TEXT,"
            + COLUMN_NEGOCIOS_NOMBRE + " TEXT)")

    private val CREATE_SEGURIDAD_BIOMETRICA_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_SEGURIDAD_BIOMETRIA + "("
            + COLUMN_BIOMETRIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_BIOMETRIA_TIPO_ACCESO + " TEXT,"
            + COLUMN_BIOMETRIA_FECHA_ACCESO + " TEXT,"
            + COLUMN_BIOMETRIA_ESTATUS_ACCESO + " TEXT)")


    private val CREATE_INFO_ACCION_JSON_TABLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_INFO_ACCION_JSON + "("
            + COLUMN_INFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_RESULT + " TEXT)")

    private val CREATE_INFO_NEGOCIO = ("CREATE TABLE IF NOT EXISTS " + TABLE_INFO_NEGOCIOS + "("
            + COLUMN_INFO_NEGOCIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ID_NEGOCIO + " INTEGER,"
            + COLUMN_ACT_STATUS + " INTEGER," + COLUMN_ACT_FECHA + " TEXT)")


    private val CREATE_CONTROL_GRAFICOS_MONITOR = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_GRAFICOS_MONITOR + "("
            + COLUMN_CONTROL_MN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_CONTROL_MN_ACCION + " INTEGER,"
            + COLUMN_CONTROL_MN_HARDWARE + " TEXT," + COLUMN_CONTROL_MN_GRAFICO + " TEXT)")


    private val CREATE_CONFIGURACION_MOVIL = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_MOVIL + "("
            + COLUMN_CONFIG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_INTERVALO_TIEMPO_MONITOREABLES + " INTEGER,"
            + COLUMN_INTERVALO_TIEMPO_ALERTAS + " INTEGER,"
            + COLUMN_INTERVALO_TIEMPO_EQUIPO + " INTEGER)")


    private val CREATE_CONFIGURACION_PAUSA_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_PAUSA_ALERTAS + "("
            + COLUMN_CONFIG_PA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CONFIG_PA_TIEMPO + " INTEGER,"
            + COLUMN_CONFIG_PA_FECHA_HORA_INICIO + " TEXT,"
            + COLUMN_CONFIG_PA_FECHA_HORA_FIN + " TEXT,"
            + COLUMN_CONFIG_ESTATUS + " INTEGER)")

    private val CREATE_DETALLE_FILTRO_ALERTA = ("CREATE TABLE IF NOT EXISTS " + TABLE_DETALLE_FILTRO_ALERTA + "("
            + COLUMN_DET_FILTRO_ALERTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DET_FILTRO_ALERTA_TIPO + " INTEGER,"
            + COLUMN_DET_FILTRO_ALERTA_PALABA_CLAVE + " TEXT)")



    private val CREATE_CONFIGURACION_MOVIL_MAS_OPCIONES = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES + "("
            + COLUMN_MS_INTERVALO_ACTUALIZACION_EQUIPOS + " INTEGER)")

    private val CREATE_CONFIGURACION_MOVIL_DEFAULT = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_MOVIL_DEFAULT + "("
            + COLUMN_CONF_DEF_MOVIL_INFO_ALERTAS + " INTEGER,"
            + COLUMN_CONF_DEF_MOVIL_ALERTAS_RECIENTES + " INTEGER,"
            + COLUMN_CONF_DEF_MOVIL_INFO_MONITORES + " INTEGER,"
            + COLUMN_CONF_DEF_MOVIL_ESTATUS_EQUIPOS + " INTEGER,"
            + COLUMN_CONF_DEF_MOVIL_INFO_EQUIPOS + " INTEGER)")



    private val CREATE_CONFIGURACION_MOVIL_TOP_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS + "("
            + COLUMN_CONFIG_TOP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TOP_ALERTAS + " INTEGER)")


    private val CREATE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS + "("
            + COLUMN_CONFIG_HISTORICO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_LIMITE_HISTORICO + " INTEGER)")

    private val CREATE_CONTROL_ACCIONES_FAVORITAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ACCIONES_FAVORITAS + "("
            + COLUMN_ACC_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_ACC_FAV_ID_ACCION + " INTEGER,"
            + COLUMN_ACC_FAV_TITULO + " TEXT,"
            + COLUMN_ACC_COMANDO + " TEXT,"
            + COLUMN_ACC_ID_EQUIPO + " TEXT,"
            + COLUMN_ACC_NOM_NEGOCIO + " TEXT,"
            + COLUMN_ACC_FAV_TIPO_ID + " INTEGER,"
            + COLUMN_ACC_FAV_PATH_ICONO + " TEXT,"
            + COLUMN_ACC_FAV_ICONO + " BLOB,"
            + COLUMN_ACC_FAV_ESTATUS + " INTEGER)")


    private val CREATE_CONTROL_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ALERTAS + "("
            + COLUMN_CONTROL_ALERTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ALERTA_ID + " INTEGER,"
            + COLUMN_ALERTA_TITULO + " TEXT,"
            + COLUMN_ALERTA_DESCRIPCION + " TEXT,"
            + COLUMN_ALERTA_NOM_NEGOCIO + " TEXT,"
            + COLUMN_ALERTA_TIPO + " INTEGER,"
            + COLUMN_ALERTA_FECHA + " TEXT,"
            + COLUMN_ALERTA_URL_SCREENSHOT + " TEXT,"
            + COLUMN_ALERTA_EVT_ID + " INT NULL DEFAULT 0)")

    private val CREATE_CONTROL_ALERTAS_ESTATUS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ALERTAS_ESTATUS + "("
            + COLUMN_ALERTA_ESTATUS_ID + " INTEGER,"
            + COLUMN_ALERTA_ESTATUS + " TEXT)")

    private val CREATE_CONTROL_ALERTAS_MARCADAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ALERTAS_MARCADAS + "("
            + COLUMN_ALERTA_ESTATUS_ID + " INTEGER,"
            + COLUMN_ALERTA_ESTATUS + " TEXT)")

    private val CREATE_CONTROL_ALERTAS_ELIMINADAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ALERTAS_ELIMINADAS + "("
            + COLUMN_ALERTA_ELIMINADA_ID + " INTEGER,"
            + COLUMN_ALERTA_ELIMINADA_TITULO + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_DESCRIPCION + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_TIPO + " INTEGER,"
            + COLUMN_ALERTA_ELIMINADA_FECHA + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_URL_SCREENSHOT + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_FECHA_ELIMINACION + " TEXT,"
            + COLUMN_ALERTA_ELIMINADA_EVT_ID + " INT NULL DEFAULT 0)")



    private val CREATE_CONTROL_HISTORICO_SCREENSHOT = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_HISTORICO_SCREENSHOT + "("
            + COLUMN_HSC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_HSC_ID_EQUIPO + " INTEGER,"
            + COLUMN_HSC_NOMBRE_IMAGEN + " TEXT,"
            + COLUMN_HSC_FECHA_CREACION + " TEXT,"
            + COLUMN_HSC_URL_SCREENSHOT + " TEXT,"
            + COLUMN_HSC_IMAGE_ESTATUS + " INTEGER,"
            + COLUMN_HSC_IMAGE_DATA + " BLOB)")


    private val CREATE_TEMP_PASO_IMAGENES_EXPLORADOR = ("CREATE TABLE IF NOT EXISTS " + TEMP_PASO_IMAGENES_EXPLORADOR + "("
            + COLUMN_TMP_IMAGE_DATA + " BLOB)")

    private val CREATE_AUX_TEMP_SCREENSHOT  = ("CREATE TABLE IF NOT EXISTS " + TABLE_AUX_TEMP_SCREENSHOT + "("
            + COLUMN_AUX_ID_EQUIPO + " INTEGER,"
            + COLUMN_AUX_IMAGE_DATA + " BLOB)")




    private val CREATE_CONTROL_ALERTAS_FAVORITAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ALERTAS_FAVORITAS + "("
            + COLUMN_CONTROL_ALERTA_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ALERTA_FAV_TIPO + " INTEGER,"
            + COLUMN_ALERTA_FAV_ULTIMAS_ENTRADAS + " INTEGER)")

    private val CREATE_CONFIGURACION_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURACION_ALERTAS + "("
            + COLUMN_CONFIGURACION_ALERTAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ALERTA_TOP + " INTEGER)")



    private val CREATE_CONTROL_USUARIOS_ALERTAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_USUARIOS_ALERTAS + "("
            + COLUMN_USUALT_ID + " INTEGER,"
            + COLUMN_USUALT_USUARIO + " TEXT,"
            + COLUMN_USUALT_EQUIPO + " INTEGER,"
            + COLUMN_USUALT_NOMBRE + " TEXT,"
            + COLUMN_USUALT_ESTATUS_ACT + " TEXT,"
            + COLUMN_USUALT_ALERTA + " INTEGER,"
            + COLUMN_USUALT_ALERTA_MOVIL + " INTEGER)")




    private val CREATE_CONTROL_CONEXION_RED = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_CONEXION_RED + "("
            + COLUMN_CONTROL_CONEXION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_IP_PUBLICA + " TEXT)")


    private val CREATE_CONTROL_EQUIPOS_API_VM = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_EQUIPOS_API_VM + "("
            + COLUMN_EQUIPO_VM_ID_EQUIPO + " INTEGER,"
            + COLUMN_EQUIPO_VM_URL_AUTH + " TEXT,"
            + COLUMN_EQUIPO_VM_NOM_PROYECTO + " TEXT,"
            + COLUMN_EQUIPO_VM_ZONA + " TEXT,"
            + COLUMN_EQUIPO_VM_NOM_INSTANCIA + " TEXT,"
            + COLUMN_EQUIPO_VM_SCREENSHOT + " INTEGER)")



    private val CREATE_CONTROL_EQUIPOS_X_MOVIL = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_EQUIPOS_X_MOVIL + "("
            + COLUMN_EQUIPOS_X_MOVIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_EQUIPO_ID + " INTEGER,"
            + COLUMN_EQUIPO_IP_PUBLICA + " TEXT,"
            + COLUMN_EQUIPO_IP_LOCAL + " TEXT,"
            + COLUMN_EQUIPO_PUERTO + " INTEGER,"
            + COLUMN_EQUIPO_ID_HARDWARE + " TEXT,"
            + COLUMN_EQUIPO_ICONO_ON + " TEXT,"
            + COLUMN_EQUIPO_ICONO_OFF + " TEXT,"
            + COLUMN_EQUIPO_NOMBRE + " TEXT,"
            + COLUMN_NEGOCIO_ID + " INTEGER,"
            + COLUMN_ACTIVIDAD_MONITOREABLE_ID + " TEXT)")


    private val CREATE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT + "("
            + COLUMN_EQUIPO_ULT_ACT_ID + " INTEGER,"
            + COLUMN_EQUIPO_ULT_ACT_ID_HARDWARE + " TEXT,"
            + COLUMN_FECHA_ULT_ACT + " TEXT)")

    private val CREATE_CONTROL_ESTADO_EQUIPO = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ESTADO_EQUIPOS + "("
            + COLUMN_ESTADO_EQUIPO_ID + " INTEGER,"
            + COLUMN_ESTADO_EQUIPO_ESTATUS + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_NOMBRE + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_HARDWARE + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_IP_PUBLICA + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_IP_LOCAL + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_PUERTO + " TEXT,"
            + COLUMN_ESTADO_EQUIPO_CONTROL + " INT NULL DEFAULT 0)")


    private val CREATE_CONTROL_PARAMS_ACC_DB = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_PARAMS_ACC_DB + "("
            + COLUMN_ID_ACCION + " INTEGER,"
            + COLUMN_CAMPO + " TEXT,"
            + COLUMN_VALOR + " TEXT,"
            + COLUMN_TIPO + " TEXT,"
            + COLUMN_CONSULTA_DB + " TEXT,"
            + COLUMN_CONSULTA_API + " TEXT)")



    private val CREATE_CONTROL_MONITORES = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_MONITORES + "("
            + COLUMN_MONITORES_ID_MOVIL + " INTEGER,"
            + COLUMN_MONITORES_ID_ACCION + " INTEGER,"
            + COLUMN_MONITORES_ORIGEN + " INTEGER,"
            + COLUMN_MONITORES_COMANDO + " TEXT,"
            + COLUMN_MONITORES_ACCION + " TEXT,"
            + COLUMN_MONITORES_PORCENAJE + " TEXT,"
            + COLUMN_MONITORES_TOTAL_USADO + " TEXT,"
            + COLUMN_MONITORES_ESACIO_LIBRE + " TEXT,"
            + COLUMN_MONITORES_PAQUETES_ENVIADOS + " TEXT,"
            + COLUMN__MONITORES_PAQUETES_RECIBIDOS + " TEXT,"
            + COLUMN_MONITORES_NOMBRE_SERVIDOR + " TEXT,"
            + COLUMN_MONITORES_RED_NEGOCIO + " TEXT,"
            + COLUMN_MONITORES_ES_FAVORITO + " TEXT,"
            + COLUMN_MONITORES_FEC_ULT_ACT + " TEXT,"
            + COLUMN_MONITORES_ID_EQUIPO + " INTEGER)")



    private val CREATE_CONTROL_MONITORES_DETTALLE = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_MONITORES_DETALLE + "("
            + COLUMN_MONITORES_ID_EQUIPO + " INTEGER,"
            + COLUMN_MONITORES_ACCION + " TEXT,"
            + COLUMN_MONITORES_PORCENAJE + " TEXT,"
            + COLUMN_MONITORES_TOTAL_USADO + " TEXT,"
            + COLUMN_MONITORES_ESACIO_LIBRE + " TEXT,"
            + COLUMN_MONITORES_PAQUETES_ENVIADOS + " TEXT,"
            + COLUMN__MONITORES_PAQUETES_RECIBIDOS + " TEXT,"
            + COLUMN_MONITORES_FEC_ULT_ACT + " TEXT)")





    private val CREATE_CONTROL_MONITOREABLES_FAVORITOS = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_MONITOREABLES_FAVORITOS + "("
            + COLUMN_ACCION_MONITOREABLE_ID + " INTEGER,"
            + COLUMN_ACCION_MONITOREABLE_HARDWARE + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_TIPO + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_PORCENTAJE + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_NOM_SVR + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_ESPACIO_LIBRE + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_ESPACIO_TOTAL + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_TOT_ENVIADOS + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_TOT_RECIBIDOS + " TEXT,"
            + COLUMN_ACCION_MONITOREABLE_ID_EQUIPO + " INT)")


    private val CREATE_TAG_GRAFICOS_MONITOREABLES = ("CREATE TABLE IF NOT EXISTS " + TABLE_TAG_GRAFICOS_MONITOREABLES + "("
            + COLUMN_ACCION_MONITOR_ID + " INTEGER,"
            + COLUMN_ACCION_MONITOR_TAG + " INTEGER,"
            + COLUMN_ACCION_MONITOR_HARDWARE + " TEXT,"
            + COLUMN_ACCION_MONITOR_GRAFICO + " TEXT)")


    private val CREATE_TAG_VISTAS_GRAFICOS_MONITOREABLES = ("CREATE TABLE IF NOT EXISTS " + TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES + "("
            + COLUMN_ACCION_VISTA_MONITOR_ID + " INTEGER,"
            + COLUMN_ACCION_VISTA_MONITOR_TAG + " INTEGER,"
            + COLUMN_ACCION_VISTA_MONITOR_HARDWARE + " TEXT)")


    private val CREATE_CONTROL_BACKUP_DATABASE_LOCAL = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_BACKUP_DATABASE_LOCAL + "("
            + COLUMN_BACKUP_INTERVALO + " INTEGER,"
            + COLUMN_BACKUP_UNIDAD + " TEXT,"
            + COLUMN_BACKUP_FTP_SERVER + " TEXT,"
            + COLUMN_BACKUP_FTP_USER + " TEXT,"
            + COLUMN_BACKUP_FTP_PWD + " TEXT,"
            + COLUMN_BACKUP_FTP_PUERTO + " TEXT,"
            + COLUMN_BACKUP_FECHA_ULT_ENVIO + " TEXT)")


    private val CREATE_CONTROL_BACKUP_DATABASE_BITACORA = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_BACKUP_DATABASE_BITACORA + "("
            + COLUMN_BACKUP_BITACORA_INTERVALO + " INTEGER,"
            + COLUMN_BACKUP_BITACORA_UNIDAD + " TEXT,"
            + COLUMN_BACKUP_FTP_BITACORA_SERVER + " TEXT,"
            + COLUMN_BACKUP_BITACORA_FECHA_ENVIO + " TEXT)")


    private val CREATE_TABLE_EQUIPOS = ("CREATE TABLE IF NOT EXISTS " + TABLE_EQUIPOS + "("
            + COLUMN_ID_MOVIL_ID + " INTEGER,"
            + COLUMN_EQUIPO_ID_ + " INTEGER,"
            + COLUMN_NOMBRE + " TEXT,"
            + COLUMN_IP_PUBLICA_ + " TEXT,"
            + COLUMN_IP_LOCAL + " TEXT,"
            + COLUMN_PUERTO_LOCAL + " INTEGER,"
            + COLUMN_PUERTO + " INTEGER,"
            + COLUMN_BOVEDA_SOFTWARE + " TEXT,"
            + COLUMN_RUTA_ARCHIVOS_LOCALES + " TEXT,"
            + COLUMN_HARDWARE_KEY_ + " TEXT,"
            + COLUMN_ESTADO_SERVIDOR + " INTEGER,"
            + COLUMN_ESTADO_ULT_ACT + " TEXT,"
            + COLUMN_ICONO_NEGOCIO_ON + " TEXT,"
            + COLUMN_ICONO_NEGOCIO_OFF + " TEXT,"
            + COLUMN_FEC_ACT_ESTATUS + " INTEGER,"
            + COLUMN_CATNOMBRENEG + " TEXT,"
            + COLUMN_ID_CATNEGOCIO + " INTEGER,"
            + COLUMN_TIPO_EQ_ICONO_ON + " TEXT,"
            + COLUMN_TIPO_EQ_ICONO_OFF + " TEXT,"
            + COLUMN_TIPO_EQ_CLAVE + " TEXT,"
            + COLUMN_ID_ACCION_MONITOREABLE + " INTEGER,"
            + COLUMN_MODELO_COMUNICACION + " INTEGER,"
            + COLUMN_WAKE_ON + " INTEGER,"
            + COLUMN_ESTADO_EQUIPO + " TEXT,"
            + COLUMN_ORDENAMIENTO + " INTEGER DEFAULT 0,"
            + COLUMN_EQUIPO_VIRTUAL + " INTEGER DEFAULT 0,"
            + COLUMN_EQUIPO_MNT_COLOR + " INTEGER DEFAULT 0)")


    private val CREATE_TABLE_EQUIPOS_TIPO_LIC = ("CREATE TABLE IF NOT EXISTS " + TABLE_EQUIPOS_TIPO_LIC  + "("
            + COLUMN_EQUIPO_TL_ID + " INTEGER,"
            + COLUMN_EQUIPO_TL_UNIMOVIL + " INTEGER)"
            )







    private val CREATE_TABLE_ACCIONES_DINAMIAS = ("CREATE TABLE IF NOT EXISTS " + TABLE_ACCIONES_DINAMICAS + "("
            + COLUMN_ACCIONES_DINAMICAS_ID + " INTEGER,"
            + COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID + " INTEGER,"
            + COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ICONO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_TIPO_ID + " INTEGER,"
            + COLUMN_ACCIONES_DINAMICAS_GRUPO + " INTEGER,"
            + COLUMN_ACCIONES_DINAMICAS_COMANDO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ORIGEN + " INTEGER,"
            + COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_DESCRIPCION + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO + " TEXT,"
            + COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD + " TEXT)"
            )


    private val CREATE_CONTROL_NIP = ("CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_NIP + "("
            + COLUMN_NUMERO_INTENTOS + " INTEGER,"
            + COLUMN_NUMERO_INTENTOS_FALLIDOS + " INTEGER)")


    private val CREATE_TABLE_EXPLORADOR_ARCHIVOS_X_EQUIPO = ("CREATE TABLE IF NOT EXISTS " + TABLE_EXPLORADOR_ARCHIVOS_X_EQUIPO + "("
            + COLUMN_EQUIPO_ID_DIR + " INTEGER,"
            + COLUMN_ID_DIR + " INTEGER,"
            + COLUMN_NOMBRE_DIR + " TEXT,"
            + COLUMN_PATH_DIR + " TEXT,"
            + COLUMN_PARENT_DIR + " INTEGER,"
            + COLUMN_TIPO_DIR + " INTEGER,"
            + COLUMN_PESO_DIR + " TEXT,"
            + COLUMN_TIPO_ARCHIVO_DIR + " TEXT,"
            + COLUMN_ULT_MOD + " TEXT)")


    private val CREATE_TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO = ( "CREATE TABLE IF NOT EXISTS " + TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO + "("
            + COLUMN_EQUIPO_ID_CS + " INTEGER,"
            + COLUMN_NUMERO_ACCION + " INTEGER,"
            + COLUMN_DESC_ACCION + " TEXT)"
            )


    private val CREATE_TABLE_PERFIL_APP_FILTROS_ENCABEZADO = ("CREATE TABLE IF NOT EXISTS " + TABLE_PERFIL_APP_FILTROS_ENCABEZADO + "("
            + COLUMN_HEADER_ID_NEGOCIO + " TEXT,"
            + COLUMN_HEADER_ID_EQUIPO + " TEXT,"
            + COLUMN_FILTRO_ALERTAS_ESTATUS + " INTEGER,"
            + COLUMN_FILTRO_ALERTAS_EQUIPO + " INTEGER,"
            + COLUMN_FILTRO_ALERTAS_FECHA + " INTEGER,"
            + COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA + " TEXT,"
            + COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS + " INTEGER,"
            + COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA + " INTEGER,"
            + COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA  + " TEXT)")


    // drop table sql query
    /*
    private val DROP_CONTROL_HISTORICO_SCREENSHOT = "DROP TABLE IF EXISTS $TABLE_CONTROL_HISTORICO_SCREENSHOT"
    private val DROP_CREATE_CONTROL_EQUIPOS_API_VM = "DROP TABLE IF EXISTS $TABLE_CONTROL_EQUIPOS_API_VM"
    private val DROP_CONTROL_NIP = "DROP TABLE IF EXISTS $TABLE_CONTROL_NIP"
    private val DROP_PARAMETROS_LICENCIA = "DROP TABLE IF EXISTS $TABLE_PARAMETROS_LICENCIA"
    private val DROP_USER_TABLE = "DROP TABLE IF EXISTS $TABLE_USER"
    private val DROP_USER_AVATAR_TABLE = "DROP TABLE IF EXISTS $TABLE_USER_AVATAR"
    private val DROP_LICENCIA_TABLE = "DROP TABLE IF EXISTS $TABLE_LICENCIA"
    private val DROP_SEGURIDAD_TABLE = "DROP TABLE IF EXISTS $TABLE_SEGURIDAD"
    private val DROP_INFO_ACCION_JSON = "DROP TABLE IF EXISTS $TABLE_INFO_ACCION_JSON"
    private val DROP_INFO_NEGOCIO = "DROP TABLE IF EXISTS $TABLE_INFO_NEGOCIOS"
    private val DROP_CONTROL_GRAFICOS_MONITOR = "DROP TABLE IF EXISTS $TABLE_CONTROL_GRAFICOS_MONITOR"
    private val DROP_CONFIGURACION_MOVIL = "DROP TABLE IF EXISTS $TABLE_CONFIGURACION_MOVIL"
    private val DROP_CONTROL_ACCIONES_FAV = "DROP TABLE IF EXISTS $TABLE_CONTROL_ACCIONES_FAVORITAS"
    private val DROP_CONTROL_ALERTAS = "DROP TABLE IF EXISTS $TABLE_CONTROL_ALERTAS"
    private val DROP_CONTROL_ALERTAS_FAV = "DROP TABLE IF EXISTS $TABLE_CONTROL_ALERTAS_FAVORITAS"
    private val DROP_CONFIGURACION_ALERTAS = "DROP TABLE IF EXISTS $TABLE_CONFIGURACION_ALERTAS"
    private val DROP_CONTROL_CONEXION_RED = "DROP TABLE IF EXISTS $TABLE_CONTROL_CONEXION_RED"
    private val DROP_CONTROL_EQUIPOS_X_MOVIL = "DROP TABLE IF EXISTS $TABLE_CONTROL_EQUIPOS_X_MOVIL"
    private val DROP_CONTROL_ESTADO_EQUIPOS = "DROP TABLE IF EXISTS $TABLE_CONTROL_ESTADO_EQUIPOS"
    private val DROP_CONTROL_MONITOREABLES_FAVORITOS = "DROP TABLE IF EXISTS $TABLE_CONTROL_MONITOREABLES_FAVORITOS"
    private val DROP_TAG_GRAFICOS_MONITOREABLES = "DROP TABLE IF EXISTS $TABLE_TAG_GRAFICOS_MONITOREABLES"
    private val DROP_SEGURIDAD_BIOMETRICA = "DROP TABLE IF EXISTS $TABLE_SEGURIDAD_BIOMETRIA"
    private val DROP_TAG_VISTAS_GRAFICOS_MONITOREABLES = "DROP TABLE IF EXISTS $TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES"
    private val DROP_CONTROL_BACKUP_DATABASE_LOCAL = "DROP TABLE IF EXISTS $TABLE_CONTROL_BACKUP_DATABASE_LOCAL"
    private val DROP_CONTROL_BACKUP_DATABASE_BITACORA = "DROP TABLE IF EXISTS $TABLE_CONTROL_BACKUP_DATABASE_BITACORA"

     */
    //override fun onCreate(db: SQLiteDatabase) {
    //    db.execSQL(CREATE_USER_TABLE)
    //    db.execSQL(CREATE_LICENCIA_TABLE)
    //}

    //override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    //Drop User Table if exist
    // db.execSQL(DROP_USER_TABLE)
    // db.execSQL(DROP_LICENCIA_TABLE)


    // Create tables again
    // onCreate(db)
    // }




    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USER_TABLE)
        db?.execSQL(CREATE_CONTROL_HISTORICO_SCREENSHOT)
        db?.execSQL(CREATE_CONTROL_NIP)
        db?.execSQL(CREATE_TABLE_SERVER_SOCKET)
        db?.execSQL(CREATE_USER_AVATAR_TABLE)
        db?.execSQL(CREATE_LICENCIA_TABLE)
        db?.execSQL(CREATE_VIGENCIA_LICENCIA_TABLE)
        db?.execSQL(CREATE_SEGURIDAD_TABLE)
        db?.execSQL(CREATE_SEGURIDAD_NIP_TABLE)
        db?.execSQL(CREATE_INFO_ACCION_JSON_TABLE)
        db?.execSQL(CREATE_INFO_NEGOCIO)
        db?.execSQL(CREATE_CONTROL_MONITORES)
        db?.execSQL(CREATE_CONTROL_MONITORES_DETTALLE)
        db?.execSQL(CREATE_CONTROL_GRAFICOS_MONITOR)
        db?.execSQL(CREATE_CONFIGURACION_MOVIL)
        db?.execSQL(CREATE_CONFIGURACION_MOVIL_MAS_OPCIONES)
        db?.execSQL(CREATE_CONFIGURACION_MOVIL_TOP_ALERTAS)
        db?.execSQL(CREATE_CONFIGURACION_MOVIL_DEFAULT)
        db?.execSQL(CREATE_CONTROL_ACCIONES_FAVORITAS)
        db?.execSQL(CREATE_CONTROL_ALERTAS)
        db?.execSQL(CREATE_CONTROL_ALERTAS_ESTATUS)
        db?.execSQL(CREATE_CONTROL_ALERTAS_FAVORITAS)
        db?.execSQL(CREATE_CONFIGURACION_ALERTAS)
        db?.execSQL(CREATE_CONTROL_CONEXION_RED)
        db?.execSQL(CREATE_CONTROL_EQUIPOS_X_MOVIL)
        db?.execSQL(CREATE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT)
        db?.execSQL(CREATE_CONTROL_ESTADO_EQUIPO)
        db?.execSQL(CREATE_CONTROL_MONITOREABLES_FAVORITOS)
        db?.execSQL(CREATE_TAG_GRAFICOS_MONITOREABLES)
        db?.execSQL(CREATE_TAG_VISTAS_GRAFICOS_MONITOREABLES)
        db?.execSQL(CREATE_SEGURIDAD_BIOMETRICA_TABLE)
        db?.execSQL(CREATE_CONTROL_BACKUP_DATABASE_LOCAL)
        db?.execSQL(CREATE_CONTROL_BACKUP_DATABASE_BITACORA)
        db?.execSQL(CREATE_NEGOCIOS_TABLE)
        db?.execSQL(CREATE_TABLE_EQUIPOS)
        db?.execSQL(CREATE_TABLE_EQUIPOS_TIPO_LIC)
        db?.execSQL(CREATE_TABLE_ACCIONES_DINAMIAS)
        db?.execSQL(CREATE_PARAMETROS_LICENCIA_TABLE)
        db?.execSQL(CREATE_CONTROL_ALERTAS_ELIMINADAS)
        db?.execSQL(CREATE_TABLE_EXPLORADOR_ARCHIVOS_X_EQUIPO)
        db?.execSQL(CREATE_TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO)
        db?.execSQL(CREATE_CONTROL_EQUIPOS_API_VM)
        db?.execSQL(CREATE_TABLE_PERFIL_APP_FILTROS_ENCABEZADO)
        db?.execSQL(CREATE_TABLE_CONTROL_ENTORNO)
        db?.execSQL(CREATE_DETALLE_FILTRO_ALERTA)
        db?.execSQL(CREATE_CONFIGURACION_PAUSA_ALERTAS)
        db?.execSQL(CREATE_AUX_TEMP_SCREENSHOT)
        db?.execSQL(CREATE_TABLE_SERVER_SOCKET_LOG)
        db?.execSQL(CREATE_TEMP_PASO_IMAGENES_EXPLORADOR)
        db?.execSQL(CREATE_CAMBIOS_VERSION_TABLE)
        db?.execSQL(CREATE_CONTROL_ALERTAS_MARCADAS)
        db?.execSQL(CREATE_CONTROL_PARAMS_ACC_DB)
        db?.execSQL(CREATE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS)
        db?.execSQL(CREATE_CONTROL_USUARIOS_ALERTAS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        //if (newVersion > oldVersion) {
        //      db?.execSQL("ALTER TABLE $TABLE_EQUIPOS ADD COLUMN $COLUMN_EQUIPO_VIRTUAL INTEGER DEFAULT 0")
        // }

        /*
        val sql = "ALTER TABLE " + TABLE_CONTROL_ALERTAS + " ADD COLUMN " +
                "$COLUMN_ALERTA_EVT_ID" + " INT  NULL DEFAULT 0 "
        db?.execSQL(sql)

        val sql_eliminadas = "ALTER TABLE " + TABLE_CONTROL_ALERTAS_ELIMINADAS + " ADD COLUMN " +
                "$COLUMN_ALERTA_ELIMINADA_EVT_ID" + " INT  NULL DEFAULT 0 "
        db?.execSQL(sql_eliminadas)

         */

        /*
        val sql = "ALTER TABLE " +  TABLE_CONTROL_ESTADO_EQUIPOS + " ADD COLUMN " +
                "$COLUMN_ESTADO_EQUIPO_CONTROL" + " INT  NULL DEFAULT 0 "
        db?.execSQL(sql)

         */

        /*
        val sqlusu = "ALTER TABLE " +  TABLE_ACCIONES_DINAMICAS + " ADD COLUMN " +
                "$COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO" + " TEXT "
        db?.execSQL(sqlusu)

        val sqlpwd = "ALTER TABLE " +  TABLE_ACCIONES_DINAMICAS + " ADD COLUMN " +
                "$COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD" + " TEXT "
        db?.execSQL(sqlpwd)

         */



         /*

                val sql = "ALTER TABLE " +  TABLE_CONTROL_MONITORES_DETALLE + " ADD COLUMN " +
                        "$COLUMN_MONITORES_ACCION TEXT"
                db?.execSQL(sql)

          */








        /*
        val sql = "ALTER TABLE " +  TABLE_EQUIPOS + " ADD COLUMN " +
                "$COLUMN_EQUIPO_MNT_COLOR INTEGER DEFAULT 0"
        db?.execSQL(sql)

         */








        //db?.execSQL(TABLE_EQUIPOS)
        //Drop User Table if exist
        //db?.execSQL(DROP_CONTROL_HISTORICO_SCREENSHOT)
        /*
        db?.execSQL(DROP_CONTROL_NIP)
         db?.execSQL(DROP_PARAMETROS_LICENCIA)
        db?.execSQL(DROP_USER_TABLE)
        db?.execSQL(DROP_USER_AVATAR_TABLE)
        db?.execSQL(DROP_LICENCIA_TABLE)
        db?.execSQL(DROP_SEGURIDAD_TABLE)
        db?.execSQL(DROP_INFO_ACCION_JSON)
        db?.execSQL(DROP_INFO_NEGOCIO)
        db?.execSQL(DROP_CONTROL_GRAFICOS_MONITOR)
        db?.execSQL(DROP_CONFIGURACION_MOVIL)
        db?.execSQL(DROP_CONTROL_ACCIONES_FAV)
        db?.execSQL(DROP_CONTROL_ALERTAS)
        db?.execSQL(DROP_CONTROL_ALERTAS_FAV)
        db?.execSQL(DROP_CONFIGURACION_ALERTAS)
        db?.execSQL(DROP_CONTROL_CONEXION_RED)
        db?.execSQL(DROP_CONTROL_EQUIPOS_X_MOVIL)
        db?.execSQL(DROP_CONTROL_ESTADO_EQUIPOS)
        db?.execSQL(DROP_CONTROL_MONITOREABLES_FAVORITOS)
        db?.execSQL(DROP_TAG_GRAFICOS_MONITOREABLES )
        db?.execSQL(DROP_TAG_VISTAS_GRAFICOS_MONITOREABLES)
        db?.execSQL(DROP_SEGURIDAD_BIOMETRICA)
        db?.execSQL(DROP_CONTROL_BACKUP_DATABASE_LOCAL)
        db?.execSQL(DROP_CONTROL_BACKUP_DATABASE_BITACORA)
         db?.execSQL(DROP_CREATE_CONTROL_EQUIPOS_API_VM)

*/



        // Create tables again
        onCreate(db)
    }


    /**
     * This method is to fetch all user and return the list of user records
     *
     * @return list
     */
    fun getAllUser(): List<User> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID, COLUMN_USER_EMAIL, COLUMN_USER_NAME, COLUMN_USER_PASSWORD)

        // sorting orders
        val sortOrder = "$COLUMN_USER_NAME ASC"
        val userList = ArrayList<User>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // query the user table
        val cursor = db.query(TABLE_USER, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val user = User(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_ID)).toInt(),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_NAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_EMAIL)),
                    password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USER_PASSWORD))
                )

                userList.add(user)
            } while (cursor.moveToNext())
        }
        cursor.close()
        // //db.close()
        return userList
    }


    fun getAllLicencia(): List<Licencia> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_LICENCIA_ID, COLUMN_HARDWARE_KEY, COLUMN_LICENCIA_ACEPTACION, COLUMN_ID_MOVIL, COLUMN_NOMBRE_MOVIL, COLUMN_NOMBRE_DOMINIO, COLUMN_VERSION_SOFTWARE)

        // sorting orders
        val sortOrder = "$COLUMN_HARDWARE_KEY ASC"
        val licenciaList = ArrayList<Licencia>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_LICENCIA, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val licencia = Licencia(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LICENCIA_ID)).toInt(),
                    hardware_key = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARDWARE_KEY)),
                    licencia_aceptacion = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_LICENCIA_ACEPTACION
                        )
                    ),
                    id_movil = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_MOVIL)).toInt(),
                    nommbre_movil = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_MOVIL)),
                    nombre_dominio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE_DOMINIO)),
                    version_software = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_VERSION_SOFTWARE
                        )
                    )
                )

                licenciaList.add(licencia)
            } while (cursor.moveToNext())
        }
        cursor.close()
        // //db.close()
        return licenciaList
    }



    fun checkLicencia(id_hardware: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_LICENCIA_ID)

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        val selection = "$COLUMN_HARDWARE_KEY = ?"

        // selection arguments
        val selectionArgs = arrayOf(id_hardware)


        val cursor = db.query(TABLE_LICENCIA, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        //  //db.close()

        if (cursorCount > 0)
            return true

        return false

    }


    fun updDominioLicencia(dominio_porta: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("update $TABLE_LICENCIA set $COLUMN_NOMBRE_DOMINIO = '$dominio_porta'")
        ////db.close()
    }

    fun updVersionLicencia(version: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("update $TABLE_LICENCIA set $COLUMN_VERSION_SOFTWARE = '$version'")
        ////db.close()
    }

    fun addLicencia(licencia: Licencia) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_HARDWARE_KEY, licencia.hardware_key)
        values.put(COLUMN_LICENCIA_ACEPTACION, licencia.licencia_aceptacion)
        values.put(COLUMN_ID_MOVIL, licencia.id_movil)
        values.put(COLUMN_NOMBRE_MOVIL, licencia.nommbre_movil)
        values.put(COLUMN_NOMBRE_DOMINIO, licencia.nombre_dominio)
        values.put(COLUMN_VERSION_SOFTWARE, licencia.version_software)


        // Inserting Row
        db.insert(TABLE_LICENCIA, null, values)
        // //db.close()
    }


    fun addParametrosLicencia(requiere_ubicacion: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_VERSION_REQUIERE_UBICACION, requiere_ubicacion)


        // Inserting Row
        db.insert(TABLE_PARAMETROS_LICENCIA, null, values)
        // //db.close()
    }

    fun deleteParametrosLicencia() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("delete from $TABLE_PARAMETROS_LICENCIA");
        // //db.close()
    }


    fun updParametrosLicencia(requiere_ubicacion: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_VERSION_REQUIERE_UBICACION, requiere_ubicacion)

        // updating row
        db.update(TABLE_PARAMETROS_LICENCIA, values, null, null)
        // //db.close()
    }


    fun delLongitudNip() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("delete from $TABLE_SEGURIDAD_NIP");
        // //db.close()
    }

    fun addLongitudNip(lgMin: Int, lgMax: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_NIP_LONGITUD_MIN, lgMin)
        values.put(COLUMN_NIP_LONGITUD_MAX, lgMax)
        db.insert(TABLE_SEGURIDAD_NIP, null, values)
        // //db.close()
    }

    fun getLongitudMinNIP(): Int {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT IFNULL($COLUMN_NIP_LONGITUD_MIN,4) as longitud_min_nip FROM $TABLE_SEGURIDAD_NIP LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var versionActSft = 4
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                versionActSft = cursor.getInt(cursor.getColumnIndexOrThrow("longitud_min_nip"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        // //db.close()
        return versionActSft
    }



    fun getLongitudMaxNIP(): Int {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT IFNULL($COLUMN_NIP_LONGITUD_MAX,6) as longitud_max_nip FROM $TABLE_SEGURIDAD_NIP LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var versionActSft = 6
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                versionActSft = cursor.getInt(cursor.getColumnIndexOrThrow("longitud_max_nip"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        // //db.close()
        return versionActSft
    }


    fun updLongitudNip(lgMin: Int, lgMax: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("update $TABLE_SEGURIDAD_NIP set $COLUMN_NIP_LONGITUD_MIN = $lgMin, $COLUMN_NIP_LONGITUD_MAX = $lgMax")
        //  //db.close()
    }


    fun CheckRequiereUbicacion(): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_VERSION_REQUIERE_UBICACION FROM $TABLE_PARAMETROS_LICENCIA"
        val cursor = db.rawQuery(Query, null)
        var versionActSft = false

        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                versionActSft = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_VERSION_REQUIERE_UBICACION")) == "1"


            } while (cursor.moveToNext())
            cursor.close()
        }
        // //db.close()
        return versionActSft
    }


    fun getVersionActSoftware(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_VERSION_SOFTWARE FROM $TABLE_LICENCIA LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var versionActSft = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                versionActSft = cursor.getString(cursor.getColumnIndexOrThrow("version_software"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //  //db.close()
        return versionActSft
    }

    /**
     * Seguridad
     *
     */

    fun checkSeguridad(nip: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_SEGURIDAD_ID)

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // selection criteria
        val selection = "$COLUMN_NIP = ?"

        // selection arguments
        val selectionArgs = arrayOf(nip)


        val cursor = db.query(TABLE_SEGURIDAD, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        //  //db.close()

        if (cursorCount > 0)
            return true

        return false

    }

    fun addSeguridad(seguridad: Seguridad) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_NIP, seguridad.nip)

        // Inserting Row
        db.insert(TABLE_SEGURIDAD, null, values)
        // //db.close()
    }


    fun updNIP(newNIP: String, nipAnterior: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_NIP, newNIP)

        // updating row
        db.update(TABLE_SEGURIDAD, values, "$COLUMN_NIP= ?",
            arrayOf(nipAnterior))
        // //db.close()
    }




    /**
     *CONTROL NIP
     */

    fun deleteNumeroIntentosNIP() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("delete from $TABLE_CONTROL_NIP");
        // //db.close()
    }


    fun addNumeroIntentosNIP(intentos: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_NUMERO_INTENTOS, intentos)
        values.put(COLUMN_NUMERO_INTENTOS_FALLIDOS, 0)

        // Inserting Row
        db.insert(TABLE_CONTROL_NIP, null, values)
        // //db.close()
    }








    fun CheckIntentosNIP(): strValidacionAcesoNip? {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val Query = "SELECT $COLUMN_NUMERO_INTENTOS, $COLUMN_NUMERO_INTENTOS_FALLIDOS FROM $TABLE_CONTROL_NIP"
        val cursor = db.rawQuery(Query, null)

        var intentosPermitidos = 0
        var intentosFallidos = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                intentosPermitidos = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_NUMERO_INTENTOS"))
                intentosFallidos = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_NUMERO_INTENTOS_FALLIDOS"))
            } while (cursor.moveToNext())
            cursor.close()

            intentosFallidos += 1
            val values = ContentValues()
            values.put(COLUMN_NUMERO_INTENTOS_FALLIDOS, intentosFallidos)
            db.update(TABLE_CONTROL_NIP, values, null,null)

            var resultado: strValidacionAcesoNip? =
                strValidacionAcesoNip(
                    intentosPermitidos - intentosFallidos,
                    intentosFallidos <= intentosPermitidos
                )


            return  resultado


        }else{
            return null
        }
        // //db.close()

    }


    /**
     * INFO ACCION
     */



    fun getAllInfoAccion(): List<InfoAccion> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INFO_ID, COLUMN_RESULT)

        // sorting orders
        val sortOrder = "$COLUMN_INFO_ID ASC"
        val infoList = ArrayList<InfoAccion>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_INFO_ACCION_JSON, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = InfoAccion(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INFO_ID)).toInt(),
                    result = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESULT))
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        // //db.close()
        return infoList
    }


    fun addInfoAccion(infoaccion: InfoAccion) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_RESULT, infoaccion.result)

        // Inserting Row
        db.insert(TABLE_INFO_ACCION_JSON, null, values)
        // //db.close()
    }


    fun deleteinfoAccion() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("delete from $TABLE_INFO_ACCION_JSON");
        // //db.close()
    }


    /**
     * Funciones CRUD Tabla Info Negocios
     * */

    fun addInfoNegocio(act_status: Int, fec_ult_act: String, negocio_id: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_INFO_NEGOCIOS ($COLUMN_INFO_NEGOCIO_ID,$COLUMN_ACT_STATUS,$COLUMN_ACT_FECHA) SELECT $negocio_id, $act_status, '$fec_ult_act' WHERE NOT EXISTS(SELECT 1 FROM $TABLE_INFO_NEGOCIOS WHERE $COLUMN_INFO_NEGOCIO_ID = $negocio_id)"
        db.execSQL(sql)
    }

    fun updateInfoNegocio(act_status: Int, fec_ult_act: String, negocio_id: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_ACT_STATUS, act_status)
        values.put(COLUMN_ACT_FECHA, fec_ult_act)

        // updating row
        db.update(TABLE_INFO_NEGOCIOS, values, "$COLUMN_INFO_NEGOCIO_ID = ?",
            arrayOf(negocio_id.toString()))
        // //db.close()
    }

    fun getInfoNegocio(id_negocio: String): String {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACT_FECHA)

        // sorting orders
        val sortOrder = "$COLUMN_INFO_NEGOCIO_ID ASC"

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_INFO_NEGOCIO_ID = ?"

        // selection argument
        val selectionArgs = arrayOf(id_negocio)


        // query the user table
        val cursor = db.query(TABLE_INFO_NEGOCIOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACT_FECHA))


            } while (cursor.moveToNext())
        }
        cursor.close()
        // //db.close()
        return ""
    }


    /**
     * CONTROL MONITOREABLES
     */

    fun addGraficoMonitoreable(id_accion: Int, hardware: String, grafico: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_CONTROL_MN_ACCION, id_accion)
        values.put(COLUMN_CONTROL_MN_HARDWARE, hardware)
        values.put(COLUMN_CONTROL_MN_GRAFICO, grafico)

        // Inserting Row
        db.insert(TABLE_CONTROL_GRAFICOS_MONITOR, null, values)
        // //db.close()
    }

    fun updateGraficoMonitoreable(id_accion: Int, hardware: String, grafico: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_CONTROL_MN_GRAFICO, grafico)

        // updating row
        db.update(TABLE_CONTROL_GRAFICOS_MONITOR, values, "$COLUMN_CONTROL_MN_ACCION= ? AND $COLUMN_CONTROL_MN_HARDWARE=?",
            arrayOf(id_accion.toString(), hardware))
        ////db.close()
    }

    fun updateTableMonitoresFavTipo(idaccion: Int, accion: String, idEquipo: Int, tipo: String) {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val sql = "UPDATE $TABLE_CONTROL_MONITOREABLES_FAVORITOS " +
                "SET $COLUMN_ACCION_MONITOREABLE_TIPO = '$tipo' " +
                "WHERE $COLUMN_ACCION_MONITOREABLE_ID = $idaccion " +
                "AND $COLUMN_ACCION_MONITOREABLE_HARDWARE = '$accion' " +
                "AND $COLUMN_ACCION_MONITOREABLE_ID_EQUIPO = $idEquipo"

        db.execSQL(sql)
        //db.close()

    }


    fun deleteGraficoMonitoreable() {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.delete(TABLE_CONTROL_GRAFICOS_MONITOR, null, null)
        //db.close()


    }

    fun getGraficoMonitoreable(id_accion: Int, hardware: String): String {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_CONTROL_MN_GRAFICO)

        // sorting orders
        val sortOrder = "$COLUMN_CONTROL_MN_ID ASC"

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_CONTROL_MN_ACCION = ? AND $COLUMN_CONTROL_MN_HARDWARE = ?"

        // selection argument
        val selectionArgs = arrayOf(id_accion.toString(), hardware)


        // query the user table
        val cursor = db.query(TABLE_CONTROL_GRAFICOS_MONITOR, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTROL_MN_GRAFICO))


            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return ""
    }


    /**
     * CONFIGURACION MOVIl
     */



    fun addConfiguracionMovil(intervalo_tiempo_monitoreables: Int, intervalo_tiempo_alertas: Int, intervalo_tiempo_equipos: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_INTERVALO_TIEMPO_ALERTAS, intervalo_tiempo_alertas)
        values.put(COLUMN_INTERVALO_TIEMPO_MONITOREABLES, intervalo_tiempo_monitoreables)
        values.put(COLUMN_INTERVALO_TIEMPO_EQUIPO, intervalo_tiempo_equipos)

        // Inserting Row
        db.insert(TABLE_CONFIGURACION_MOVIL, null, values)
        //db.close()
    }


    fun addControlEquipos() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_MOVIL ($COLUMN_INTERVALO_TIEMPO_EQUIPO,$COLUMN_INTERVALO_TIEMPO_MONITOREABLES,$COLUMN_INTERVALO_TIEMPO_ALERTAS) SELECT 5, 60, 60 WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_MOVIL)"
        db.execSQL(sql)
        //db.close()

    }


    //PAUSA DE ALERTAS
    fun addControlPausaAlertas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_PAUSA_ALERTAS ($COLUMN_CONFIG_PA_TIEMPO,$COLUMN_CONFIG_PA_FECHA_HORA_INICIO,$COLUMN_CONFIG_PA_FECHA_HORA_FIN, $COLUMN_CONFIG_ESTATUS) SELECT 3600, '', '', 1 WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_PAUSA_ALERTAS)"
        db.execSQL(sql)
        //db.close()
    }


    fun updControlPausaAlertas(intervalo_pausa_alertas: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_PAUSA_ALERTAS SET $COLUMN_CONFIG_PA_TIEMPO = $intervalo_pausa_alertas")
        //db.close()

    }


    fun updControlPausaAlertas(estatus: Int, fec_inicio:String, fec_fin: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_PAUSA_ALERTAS SET $COLUMN_CONFIG_ESTATUS = $estatus, $COLUMN_CONFIG_PA_FECHA_HORA_INICIO = '$fec_inicio', $COLUMN_CONFIG_PA_FECHA_HORA_FIN = '$fec_fin'")
        //db.close()

    }

    fun updControlPausaEstatus(estatus: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_PAUSA_ALERTAS SET $COLUMN_CONFIG_ESTATUS = $estatus")
        //db.close()

    }

    fun getControlPausaAlertas_IntervaloTmAlt(): List<clsPausaAlertas> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_CONFIG_PA_ID, COLUMN_CONFIG_PA_TIEMPO, COLUMN_CONFIG_PA_FECHA_HORA_INICIO, COLUMN_CONFIG_PA_FECHA_HORA_FIN, COLUMN_CONFIG_ESTATUS)

        // sorting orders

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        // selection argument

        val infoList = ArrayList<clsPausaAlertas>()
        // query the user table
        val cursor = db.query(TABLE_CONFIGURACION_PAUSA_ALERTAS, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val pausaalt = clsPausaAlertas(
                    pa_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_PA_ID)).toInt(),
                    pa_tiempo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_PA_TIEMPO)).toInt(),
                    pa_fecha_inicio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_PA_FECHA_HORA_INICIO)),
                    pa_fecha_fin = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_PA_FECHA_HORA_FIN)),
                    pa_estatus = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONFIG_ESTATUS)).toInt()
                )

                infoList.add(pausaalt)



            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }






    fun CheckIfControlAccSocketXEquipoExist(idEquipo: Int , accionDesc: String): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO WHERE $COLUMN_EQUIPO_ID_CS = $idEquipo AND $COLUMN_DESC_ACCION = '$accionDesc'"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {

            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }

    fun addControlAccSocketXEquipo(idEquipo: Int , numAccion: Int, accionDesc: String){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO  ($COLUMN_EQUIPO_ID_CS, $COLUMN_NUMERO_ACCION, $COLUMN_DESC_ACCION) SELECT $idEquipo, $numAccion, '$accionDesc'  WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO  WHERE $COLUMN_EQUIPO_ID_CS = $idEquipo AND $COLUMN_DESC_ACCION = '$accionDesc')"
        db.execSQL(sql)


    }

    fun updControlAccSocketXEquipo(idEquipo: Int , numAccion: Int, accionDesc: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO SET $COLUMN_NUMERO_ACCION = $numAccion WHERE $COLUMN_EQUIPO_ID_CS = $idEquipo  AND $COLUMN_DESC_ACCION = '$accionDesc'")
        //db.close()

    }

    fun delBDControlAccSocketXEquipoAll() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO");
        //db.close()
    }


    fun delBDControlAccSocketXEquipo(idEquipo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO  WHERE $COLUMN_EQUIPO_ID_CS = $idEquipo");
        //db.close()
    }

    fun getControlAccSocketXEquipo(idEquipo: Int, accionDesc: String): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_NUMERO_ACCION FROM $TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO WHERE  $COLUMN_EQUIPO_ID_CS = $idEquipo AND $COLUMN_DESC_ACCION = '$accionDesc'"
        val cursor = db.rawQuery(Query, null)
        var numAccion = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                numAccion = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_NUMERO_ACCION"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  numAccion
    }






    fun updConfiguracionMovilEquipos(intervalo_tiempo_equipos: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL SET $COLUMN_INTERVALO_TIEMPO_EQUIPO = $intervalo_tiempo_equipos")
        //db.close()

    }



    fun addConfiguracionMovilEquiposMasOpciones(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES  ($COLUMN_MS_INTERVALO_ACTUALIZACION_EQUIPOS) SELECT 60 WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES)"
        db.execSQL(sql)
        //db.close()

    }


    fun updConfiguracionMovilEquiposMasOpciones(intervalo_actualizacion_equipos: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES  SET $COLUMN_MS_INTERVALO_ACTUALIZACION_EQUIPOS = $intervalo_actualizacion_equipos")
        //db.close()

    }



    fun getIntervaloEstatusEquipos(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_INTERVALO_TIEMPO_EQUIPO FROM $TABLE_CONFIGURACION_MOVIL"
        val cursor = db.rawQuery(Query, null)
        var topAlerta = 5
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                topAlerta = cursor.getInt(cursor.getColumnIndexOrThrow("intervalo_tiempo_equipos"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  topAlerta
    }


    fun getIntervaloActualizacionEquipos(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_MS_INTERVALO_ACTUALIZACION_EQUIPOS  FROM $TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES"
        val cursor = db.rawQuery(Query, null)
        var topAlerta = 60
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                topAlerta = cursor.getInt(cursor.getColumnIndexOrThrow("intervalo_actualizacion_moviles"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  topAlerta
    }



    fun updConfiguracionMovilMonitoreables(intervalo_tiempo_monitoreables: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL SET $COLUMN_INTERVALO_TIEMPO_MONITOREABLES = $intervalo_tiempo_monitoreables")
        //db.close()

    }

    fun updConfiguracionMovilAlertas(intervalo_tiempo_alertas: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL SET $COLUMN_INTERVALO_TIEMPO_ALERTAS = $intervalo_tiempo_alertas")
        //db.close()

    }

    fun deleteConfiguracionMovil() {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.delete(TABLE_CONFIGURACION_MOVIL, null, null)
        //db.close()

    }

//TOP ALERTAS

    fun addControlTopAlertas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS ($COLUMN_TOP_ALERTAS) SELECT 50 WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS)"
        db.execSQL(sql)
        //db.close()

    }



    fun updConfiguracionTopAlertas(top_alertas_inicio: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS SET $COLUMN_TOP_ALERTAS = $top_alertas_inicio")
        //db.close()

    }

    fun getTopAlertas(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_TOP_ALERTAS FROM  $TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS"
        var cursor: Cursor? = null
        var topAlerta = 50
        try {
            cursor = db.rawQuery(Query, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    topAlerta = cursor.getInt(cursor.getColumnIndexOrThrow("top_alertas"))

                } while (cursor.moveToNext())
                cursor.close()
            }
            //db.close()
        } finally {
            if ( cursor != null)  cursor.close();
        }


        return  topAlerta
    }


    //****HISTORICO ALERTAS*****

    fun addControlHistoricoAlertas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS ($COLUMN_LIMITE_HISTORICO) SELECT 15 WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS)"
        db.execSQL(sql)
        //db.close()

    }



    fun updConfiguracionHistoricoALertas(top_alertas_inicio: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS SET $COLUMN_LIMITE_HISTORICO = $top_alertas_inicio")
        //db.close()

    }

    fun getHistoricoAlertas(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_LIMITE_HISTORICO FROM  $TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS"
        var cursor: Cursor? = null
        var topAlerta = 5
        try {
            cursor = db.rawQuery(Query, null)
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    topAlerta = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LIMITE_HISTORICO))

                } while (cursor.moveToNext())
                cursor.close()
            }
            //db.close()
        } finally {
            if ( cursor != null)  cursor.close();
        }

        return  topAlerta
    }






    //**************************











    fun getConfiguracionMovil_IntervaloTmMnt(): String {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INTERVALO_TIEMPO_MONITOREABLES)

        // sorting orders

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        // selection argument


        // query the user table
        val cursor = db.query(TABLE_CONFIGURACION_MOVIL, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INTERVALO_TIEMPO_MONITOREABLES))


            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return "60"
    }















    fun getConfiguracionMovil_IntervaloTmAlt(): String {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INTERVALO_TIEMPO_ALERTAS)

        // sorting orders

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        // selection argument


        // query the user table
        val cursor = db.query(TABLE_CONFIGURACION_MOVIL, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INTERVALO_TIEMPO_ALERTAS))


            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return "60"
    }



    fun getConfiguracionMovil_IntervaloTmEquipo(): String {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_INTERVALO_TIEMPO_EQUIPO)

        // sorting orders

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        // selection argument


        // query the user table
        val cursor = db.query(TABLE_CONFIGURACION_MOVIL, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                return cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INTERVALO_TIEMPO_EQUIPO))


            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return "5"
    }

    /**
     * CONTROL MONITOREABLES
     */



    fun addAccionesFavoritas(id_accion: Int, titulo: String, comando: String, id_equipo: String, nom_negocio: String, tipo_id: Int, icono: Bitmap, path_imagen: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ACCIONES_FAVORITAS (ctl_acc_fav_id_accion,ctl_acc_fav_titulo, ctl_acc_fav_comando, ctl_acc_fav_id_equipo, ctl_acc_fav_nom_negocio,ctl_acc_fav_tipo_id,ctl_acc_fav_path_icono,ctl_acc_fav_icono,ctl_acc_fav_estatus) VALUES(?,?,?,?,?,?,?,?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val imgByte: ByteArray = getBitmapAsByteArray(icono)
        val bytes = ByteArrayOutputStream()

        var bimg: Bitmap= BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
        bimg.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val byteArray: ByteArray = bytes.toByteArray()




        insertStmt!!.bindLong(1, id_accion.toLong())
        insertStmt!!.bindString(2, titulo)
        insertStmt!!.bindString(3, comando)
        insertStmt!!.bindString(4, id_equipo)
        insertStmt!!.bindString(5, nom_negocio)
        insertStmt!!.bindLong(6, tipo_id.toLong())
        insertStmt!!.bindString(7, path_imagen)
        insertStmt!!.bindBlob(8, byteArray)
        insertStmt!!.bindLong(9, 1)
        insertStmt!!.executeInsert();
        //db.close()
    }


    fun delAccionesFavoritas(id_accion: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_ACCIONES_FAVORITAS WHERE ctl_acc_fav_id_accion = $id_accion");
        //db.close()
    }

    fun activaAccionesFavoritas(id_accion: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONTROL_ACCIONES_FAVORITAS SET $COLUMN_ACC_FAV_ESTATUS = 1 WHERE ctl_acc_fav_id_accion = $id_accion");
        //db.close()
    }
    fun desactivaAccionesFavoritas(id_accion: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONTROL_ACCIONES_FAVORITAS SET $COLUMN_ACC_FAV_ESTATUS = 0 WHERE ctl_acc_fav_id_accion = $id_accion");
        //db.close()
    }

    fun desactivaTodasAccionesFavoritas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONTROL_ACCIONES_FAVORITAS SET $COLUMN_ACC_FAV_ESTATUS = 0")
        //db.close()
    }


    //******************************
    //ACTUALIZACION DEL LISTADO DE EQUIPOS POR EQUIPO ID
    fun desactivaTodasAccionesFavoritasXEquipo(id_equipo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE $TABLE_CONTROL_ACCIONES_FAVORITAS SET $COLUMN_ACC_FAV_ESTATUS = 0 WHERE $COLUMN_ACC_ID_EQUIPO = $id_equipo")
        //db.close()
    }


    //***************************

    @SuppressLint("ResourceAsColor")
    fun getAccionesFavoritasImg(id_accion: Int): Bitmap? {
        val qu = "SELECT * FROM $TABLE_CONTROL_ACCIONES_FAVORITAS WHERE ctl_acc_fav_id_accion = $id_accion"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("ctl_acc_fav_icono")
                    val imgByte: ByteArray = cur.getBlob(index)
                    cur.close()

                    return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
                }
                if (cur != null && !cur.isClosed()) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return null
    }





    @SuppressLint("Range")
    fun getAccionesFavoritasListDataV2(): List<AccionesFavDataCarrousel>{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT a.$COLUMN_ACC_FAV_ID_ACCION, a.$COLUMN_ACC_FAV_TITULO, a.$COLUMN_ACC_COMANDO," +
                " a.$COLUMN_ACC_ID_EQUIPO, a.$COLUMN_ACC_NOM_NEGOCIO, a.$COLUMN_ACC_FAV_TIPO_ID, a.$COLUMN_ACC_FAV_PATH_ICONO," +
                " (SELECT b.titulo_aplicacion FROM acciones_dinamicas b WHERE b.id_negocio_id = a.$COLUMN_ACC_ID_EQUIPO AND b.id = a.$COLUMN_ACC_FAV_ID_ACCION) as titulo_carrousel" +
                " FROM $TABLE_CONTROL_ACCIONES_FAVORITAS a WHERE a.$COLUMN_ACC_FAV_ESTATUS = 1" +
                " AND a.$COLUMN_ACC_ID_EQUIPO NOT IN " +
                "(SELECT id_negocio_id FROM equipos WHERE id_negocio_id = a.$COLUMN_ACC_ID_EQUIPO AND  estado_servidor = 9)"


        var cursor: Cursor? = null
        try{
            cursor = db.rawQuery(Query, null)
            val infoList = ArrayList<AccionesFavDataCarrousel>()
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        val info =
                            AccionesFavDataCarrousel(
                                idAccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_ID_ACCION)).toInt(),
                                titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_TITULO)).toString(),
                                comando = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_COMANDO)),
                                id_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_ID_EQUIPO)),
                                nom_negocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_NOM_NEGOCIO)),
                                tipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_TIPO_ID)).toInt(),
                                path_icono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_PATH_ICONO)),
                                titulo_carrousel = cursor.getString(cursor.getColumnIndexOrThrow("titulo_carrousel"))
                            )

                        infoList.add(info)
                    }while (cursor.moveToNext())
                }
                cursor.close()
            }

            return  infoList

        }
        finally
        {
            cursor?.close()
        }



    }

























    fun getAccionesFavoritasListData(): List<AccionesFavData> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACC_FAV_ID_ACCION, COLUMN_ACC_FAV_TITULO, COLUMN_ACC_COMANDO, COLUMN_ACC_ID_EQUIPO, COLUMN_ACC_NOM_NEGOCIO, COLUMN_ACC_FAV_TIPO_ID, COLUMN_ACC_FAV_PATH_ICONO)

        // sorting orders
        val sortOrder = "$COLUMN_ACC_FAV_ID ASC"
        val selection = "$COLUMN_ACC_FAV_ESTATUS = ?"
        val selectionArgs = arrayOf("1")

        val infoList = ArrayList<AccionesFavData>()



        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // query the user table
        val cursor = db.query(TABLE_CONTROL_ACCIONES_FAVORITAS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = AccionesFavData(
                    idAccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_ID_ACCION))
                        .toInt(),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_TITULO))
                        .toString(),
                    comando = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_COMANDO)),
                    id_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_ID_EQUIPO)),
                    nom_negocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_NOM_NEGOCIO)),
                    tipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_TIPO_ID))
                        .toInt(),
                    path_icono = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACC_FAV_PATH_ICONO))
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }




    fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, outputStream)
        return outputStream.toByteArray()
    }

    ////Control Monitoreables

    fun addControlSendBackupDB(ftpserver: String, ftpuser: String, ftppwd: String, ftppuerto: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val values = ContentValues()
        values.put(COLUMN_BACKUP_INTERVALO, 12)
        values.put(COLUMN_BACKUP_UNIDAD, "Horas")
        values.put(COLUMN_BACKUP_FTP_SERVER, ftpserver)
        values.put(COLUMN_BACKUP_FTP_USER, ftpuser)
        values.put(COLUMN_BACKUP_FTP_PWD, ftppwd)
        values.put(COLUMN_BACKUP_FTP_PUERTO, ftppuerto)
        values.put(COLUMN_BACKUP_FECHA_ULT_ENVIO, "")




        // Inserting Row
        db.insert(TABLE_CONTROL_BACKUP_DATABASE_LOCAL, null, values)
        //db.close()
    }

    fun updControlSendBackupDB(Unidad: String, Intervalo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("UPDATE $TABLE_CONTROL_BACKUP_DATABASE_LOCAL SET $COLUMN_BACKUP_INTERVALO = $Intervalo,$COLUMN_BACKUP_UNIDAD = '$Unidad'")
        //db.close()

    }


    fun getAccionesFavCarrouselFavInicio(id_accion: Int): List<Favoritos_List>{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT a.id, a.icono, a.alias_aplicacion, a.id_negocio_id, \n" +
                "a.aplicacion_ruta_aplicacion, a.aplicacion_nombre_aplicacion, a.aplicacion_parametros_aplicacion, a.exploracion_ruta, a.eliminacion_tipo,eliminacion_nombre_archivo, \n" +
                "a.eliminacion_ruta_archivo, a.tipo_id, a.comando, a.fecha_alta,0 as origen, a.titulo_aplicacion, a.estado_programa_nombre, b.ip_publica, b.puerto, b.hardware_key,\n" +
                "b.nombre, b.boveda_software, b.ip_local, b.puerto_local, a.aplicacion_usuario, a.aplicacion_pwd \n" +
                "FROM acciones_dinamicas a, equipos b WHERE a.id_negocio_id = b.id_negocio_id AND a.id IN ($id_accion)"
        val cursor = db.rawQuery(Query, null)
        val infoList = ArrayList<Favoritos_List>()
        if (cursor.count > 0) {
            if (cursor.moveToFirst()) {
                do {
                    val info = Favoritos_List(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        icono = cursor.getString(cursor.getColumnIndexOrThrow("icono")),
                        alias_aplicacion = cursor.getString(cursor.getColumnIndexOrThrow("alias_aplicacion")),
                        id_negocio_id = cursor.getInt(cursor.getColumnIndexOrThrow("id_negocio_id")),
                        aplicacion_ruta_aplicacion = cursor.getString(cursor.getColumnIndexOrThrow("aplicacion_ruta_aplicacion")),
                        aplicacion_nombre_aplicacion = cursor.getString(cursor.getColumnIndexOrThrow("aplicacion_nombre_aplicacion")),
                        aplicacion_parametros_aplicacion = cursor.getString(cursor.getColumnIndexOrThrow("aplicacion_parametros_aplicacion")),
                        exploracion_ruta = cursor.getString(cursor.getColumnIndexOrThrow("exploracion_ruta")),
                        eliminacion_tipo = cursor.getString(cursor.getColumnIndexOrThrow("eliminacion_tipo")),
                        eliminacion_nombre_archivo = cursor.getString(cursor.getColumnIndexOrThrow("eliminacion_nombre_archivo")),
                        eliminacion_ruta_archivo = cursor.getString(cursor.getColumnIndexOrThrow("eliminacion_ruta_archivo")),
                        tipo_id = cursor.getInt(cursor.getColumnIndexOrThrow("tipo_id")),
                        comando = cursor.getString(cursor.getColumnIndexOrThrow("comando")),
                        fecha_alta = cursor.getString(cursor.getColumnIndexOrThrow("fecha_alta")),
                        origen = cursor.getInt(cursor.getColumnIndexOrThrow("origen")),
                        titulo_aplicacion = cursor.getString(cursor.getColumnIndexOrThrow("titulo_aplicacion")),
                        estado_programa_nombre = cursor.getString(cursor.getColumnIndexOrThrow("estado_programa_nombre")),
                        ip_publica = cursor.getString(cursor.getColumnIndexOrThrow("ip_publica")),
                        puerto = cursor.getInt(cursor.getColumnIndexOrThrow("puerto")),
                        hardware_key = cursor.getString(cursor.getColumnIndexOrThrow("hardware_key")),
                        nombre_negocio = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        boveda_software = cursor.getString(cursor.getColumnIndexOrThrow("boveda_software")),
                        ip_local = cursor.getString(cursor.getColumnIndexOrThrow("ip_local")),
                        puerto_local = cursor.getInt(cursor.getColumnIndexOrThrow("puerto_local")),
                        aplicacion_usuario =  cursor.getString(cursor.getColumnIndexOrThrow("aplicacion_usuario")),
                        aplicacion_pwd = cursor.getString(cursor.getColumnIndexOrThrow("aplicacion_pwd"))
                    )

                    infoList.add(info)
                }while (cursor.moveToNext())
            }
            cursor.close()
        }


        return  infoList

    }

    fun getCOntrolBackupDB(): String{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_BACKUP_INTERVALO, $COLUMN_BACKUP_UNIDAD, $COLUMN_BACKUP_FTP_SERVER, $COLUMN_BACKUP_FTP_USER, $COLUMN_BACKUP_FTP_PWD, $COLUMN_BACKUP_FTP_PUERTO    FROM $TABLE_CONTROL_BACKUP_DATABASE_LOCAL"
        val cursor = db.rawQuery(Query, null)
        var dataBackupDb = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                dataBackupDb = cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_intervalo")) + "|"
                dataBackupDb += cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_unidad")) + "|"
                dataBackupDb += cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_ftp_server")) + "|"
                dataBackupDb += cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_ftp_user")) + "|"
                dataBackupDb += cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_ftp_pwd")) + "|"
                dataBackupDb += cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_ftp_puerto"))

                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  dataBackupDb
    }





    fun getIntervaloBackupDataBase(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_BACKUP_INTERVALO FROM $TABLE_CONTROL_BACKUP_DATABASE_LOCAL"
        val cursor = db.rawQuery(Query, null)
        var intervalobk = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                intervalobk  = cursor.getInt(cursor.getColumnIndexOrThrow("control_send_backup_intervalo"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  intervalobk
    }


    fun getUnidadBackupDataBase(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_BACKUP_UNIDAD FROM $TABLE_CONTROL_BACKUP_DATABASE_LOCAL"
        val cursor = db.rawQuery(Query, null)
        var intervalobk = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                var unidad  = cursor.getString(cursor.getColumnIndexOrThrow("control_send_backup_unidad"))
                if (unidad == "Minutos"){
                    intervalobk = 0
                }else if(unidad == "Horas"){
                    intervalobk = 1
                }

                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  intervalobk
    }




    //Bitacora Backup

    fun addControlBitacoraSendBackupDB(ftpserver: String, intervalo: Int, Unidad: String, FechaEnvio: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_BACKUP_BITACORA_INTERVALO, intervalo)
        values.put(COLUMN_BACKUP_BITACORA_UNIDAD, Unidad)
        values.put(COLUMN_BACKUP_FTP_BITACORA_SERVER, ftpserver)
        values.put(COLUMN_BACKUP_BITACORA_FECHA_ENVIO, FechaEnvio)




        // Inserting Row
        db.insert(TABLE_CONTROL_BACKUP_DATABASE_BITACORA, null, values)
        //db.close()
    }



    //////////////control de alertas marcadas

    fun addAlertaMarcadaEstatus(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS_MARCADAS  ($COLUMN_ALERTA_ESTATUS_ID, $COLUMN_ALERTA_ESTATUS) SELECT $id_alerta, 'marcada' WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_MARCADAS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta)"
        db.execSQL(sql)
        //db.close()

    }

    fun addAlertaMarcadaEstatusAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sqli = "INSERT INTO $TABLE_CONTROL_ALERTAS_MARCADAS  ($COLUMN_ALERTA_ESTATUS_ID, $COLUMN_ALERTA_ESTATUS) SELECT  a.alerta_id, 'marcada' FROM  control_alertas_marcadas a WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_MARCADAS b WHERE b.alerta_id = a.alerta_id) "
        db.execSQL(sqli)
        //db.close()

    }

    fun delAlertaMarcadaEstatus(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_MARCADAS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta"
        db.execSQL(sql)
        //db.close()
    }

    fun delAlertaMarcadaEstatusAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_MARCADAS"
        db.execSQL(sql)
        //db.close()
    }


    @SuppressLint("Range")
    fun getAlertaMarcadaEstatus(id_alerta: Int): String{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT alerta_estatus FROM $TABLE_CONTROL_ALERTAS_MARCADAS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta"
        val cursor = db.rawQuery(Query, null)
        var estatusalerta = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                estatusalerta = cursor.getString(cursor.getColumnIndexOrThrow("alerta_estatus"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  estatusalerta
    }







    //////////////control de alertas


    fun addAlertaEstatus(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS_ESTATUS  ($COLUMN_ALERTA_ESTATUS_ID, $COLUMN_ALERTA_ESTATUS) SELECT $id_alerta, 'vista' WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ESTATUS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta)"
        db.execSQL(sql)
        //db.close()

    }


    fun addAlertaEstatusAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sqli = "INSERT INTO $TABLE_CONTROL_ALERTAS_ESTATUS  ($COLUMN_ALERTA_ESTATUS_ID, $COLUMN_ALERTA_ESTATUS) SELECT  a.alerta_id, 'vista' FROM  control_alertas a WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ESTATUS b WHERE b.alerta_id = a.alerta_id) "
        db.execSQL(sqli)
        //db.close()

    }

    fun delAlertaEliminadasEstatusAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS"
        db.execSQL(sql)
        //db.close()
    }

    fun delAlertaEliminadaEstatus(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS WHERE $COLUMN_ALERTA_ELIMINADA_ID = $id_alerta"
        db.execSQL(sql)
    }


    fun delAlertaEstatus(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_ESTATUS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta"
        db.execSQL(sql)
        //db.close()
    }




    fun addAllAlertasEliminadas(fechaEliminacion: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS_ELIMINADAS ($COLUMN_ALERTA_ELIMINADA_ID, " +
                "$COLUMN_ALERTA_ELIMINADA_TITULO, " +
                "$COLUMN_ALERTA_ELIMINADA_DESCRIPCION, " +
                "$COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO, " +
                "$COLUMN_ALERTA_ELIMINADA_TIPO, " +
                "$COLUMN_ALERTA_ELIMINADA_FECHA, " +
                "$COLUMN_ALERTA_ELIMINADA_URL_SCREENSHOT, " +
                "$COLUMN_ALERTA_ELIMINADA_FECHA_ELIMINACION) " +
                "SELECT b.$COLUMN_ALERTA_ID, " +
                "b.$COLUMN_ALERTA_TITULO, " +
                "b.$COLUMN_ALERTA_DESCRIPCION, " +
                "b.$COLUMN_ALERTA_NOM_NEGOCIO, " +
                "b.$COLUMN_ALERTA_TIPO, " +
                "b.$COLUMN_ALERTA_FECHA, " +
                "b.$COLUMN_ALERTA_URL_SCREENSHOT, " +
                "'$fechaEliminacion' " +
                "FROM $TABLE_CONTROL_ALERTAS b WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS " +
                "WHERE alerta_id = b.$COLUMN_ALERTA_ID)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)



        insertStmt!!.executeInsert()
        //db.close()
    }


    fun RecuperaAllAlertasEliminadas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS ($COLUMN_ALERTA_ID, " +
                "$COLUMN_ALERTA_TITULO, " +
                "$COLUMN_ALERTA_DESCRIPCION, " +
                "$COLUMN_ALERTA_NOM_NEGOCIO, " +
                "$COLUMN_ALERTA_TIPO, " +
                "$COLUMN_ALERTA_FECHA, " +
                "$COLUMN_ALERTA_URL_SCREENSHOT) " +
                "SELECT b.$COLUMN_ALERTA_ELIMINADA_ID, " +
                "b.$COLUMN_ALERTA_ELIMINADA_TITULO, " +
                "b.$COLUMN_ALERTA_ELIMINADA_DESCRIPCION, " +
                "b.$COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO, " +
                "b.$COLUMN_ALERTA_ELIMINADA_TIPO, " +
                "b.$COLUMN_ALERTA_ELIMINADA_FECHA, " +
                "b.$COLUMN_ALERTA_ELIMINADA_URL_SCREENSHOT " +
                "FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS b WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS " +
                "WHERE alerta_id = b.$COLUMN_ALERTA_ELIMINADA_ID)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)



        insertStmt!!.executeInsert()
        //db.close()
    }



    fun delAlertaEstatusAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS_ESTATUS"
        db.execSQL(sql)
        //db.close()
    }



    fun addAlertasEliminadas(idAlerta: Int, fechaEliminacion: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS_ELIMINADAS ($COLUMN_ALERTA_ELIMINADA_ID, " +
                "$COLUMN_ALERTA_ELIMINADA_TITULO, " +
                "$COLUMN_ALERTA_ELIMINADA_DESCRIPCION, " +
                "$COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO, " +
                "$COLUMN_ALERTA_ELIMINADA_TIPO, " +
                "$COLUMN_ALERTA_ELIMINADA_FECHA, " +
                "$COLUMN_ALERTA_ELIMINADA_URL_SCREENSHOT, " +
                "$COLUMN_ALERTA_ELIMINADA_FECHA_ELIMINACION) " +
                "SELECT b.$COLUMN_ALERTA_ID, " +
                "b.$COLUMN_ALERTA_TITULO, " +
                "b.$COLUMN_ALERTA_DESCRIPCION, " +
                "b.$COLUMN_ALERTA_NOM_NEGOCIO, " +
                "b.$COLUMN_ALERTA_TIPO, " +
                "b.$COLUMN_ALERTA_FECHA, " +
                "b.$COLUMN_ALERTA_URL_SCREENSHOT, " +
                "'$fechaEliminacion' " +
                "FROM $TABLE_CONTROL_ALERTAS b WHERE  b.$COLUMN_ALERTA_ID = $idAlerta AND  " +
                "NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS " +
                "WHERE alerta_id = $idAlerta)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)



        insertStmt!!.executeInsert()
        //db.close()
    }



    @SuppressLint("Range")
    fun getMaxIdAlerta(): Int {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_ALERTA_ID FROM $TABLE_CONTROL_ALERTAS ORDER BY $COLUMN_ALERTA_ID DESC LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var alertamaxima = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                alertamaxima = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_ALERTA_ID"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  alertamaxima
    }


    fun delAlerta(id_alerta: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS WHERE $COLUMN_ALERTA_ID = $id_alerta"
        db.execSQL(sql)
        //db.close()
    }


    fun delAlertaAll(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "DELETE FROM $TABLE_CONTROL_ALERTAS"
        db.execSQL(sql)
        //db.close()
    }


    @SuppressLint("Range")
    fun getIdCtlAlerta(id_alerta: Int): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_CONTROL_ALERTA_ID FROM $TABLE_CONTROL_ALERTAS WHERE $COLUMN_ALERTA_ID = $id_alerta"
        val cursor = db.rawQuery(Query, null)
        var idctlalerta = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                idctlalerta = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_CONTROL_ALERTA_ID"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  idctlalerta
    }










    fun getAlertaEstatus(id_alerta: Int): String{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT alerta_estatus FROM $TABLE_CONTROL_ALERTAS_ESTATUS WHERE $COLUMN_ALERTA_ESTATUS_ID = $id_alerta"
        val cursor = db.rawQuery(Query, null)
        var estatusalerta = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                estatusalerta = cursor.getString(cursor.getColumnIndexOrThrow("alerta_estatus"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  estatusalerta
    }







    fun getTotalAlertasSinLeer(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT IFNULL(COUNT(*),0)  as TotAlertasSnLeer FROM control_alertas a WHERE a.alerta_id NOT IN (SELECT b.alerta_id FROM control_alertas_estatus b)"
        var cursor: Cursor? = null
        var estatusalerta = 0
        try {
            cursor = db.rawQuery(Query, null)
            var i = 0
            if (cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    estatusalerta = cursor.getInt(cursor.getColumnIndexOrThrow("TotAlertasSnLeer"))
                    i++
                } while (cursor.moveToNext())
                cursor.close()
            }

        }finally {
            if (cursor != null) cursor.close();
        }
        //db.close()
        return  estatusalerta
    }



    fun igetTotalAlertas(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT IFNULL(COUNT(*),0)  as TotAlertas FROM control_alertas a "
        val cursor = db.rawQuery(Query, null)
        var estatusalerta = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                estatusalerta = cursor.getInt(cursor.getColumnIndexOrThrow("TotAlertas"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  estatusalerta
    }


    fun igetTotalAlertasEliminadas(): Int{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT IFNULL(COUNT(*),0)  as TotAlertas FROM alertas_eliminadas a "
        val cursor = db.rawQuery(Query, null)
        var estatusalerta = 0
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                estatusalerta = cursor.getInt(cursor.getColumnIndexOrThrow("TotAlertas"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  estatusalerta
    }


    //SE UTILIZA PARA NUEVAS ALERTAS
    fun addNewAlertaDB(id_alerta: Int, titulo: String, descripcion: String, nom_negocio: String, tipo_id: Int, urlimagen: String, utm6: Boolean) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var sql: String
        if(utm6){
            sql = "INSERT INTO $TABLE_CONTROL_ALERTAS " +
                    "(alerta_id, " +
                    "alerta_titulo," +
                    "alerta_descripcion, " +
                    "alerta_nom_negocio, " +
                    "alerta_tipo, " +
                    "alerta_fecha, " +
                    "alerta_url_screenshot) " +
                    "SELECT $id_alerta," +
                    "'$titulo'," +
                    "'$descripcion'," +
                    "'$nom_negocio'," +
                    "$tipo_id," +
                    "strftime('%Y-%m-%d %H:%M:%S', datetime('now', '-6 hours'))," +
                    "'$urlimagen' " +
                    "WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS b WHERE b.$COLUMN_ALERTA_ID = $id_alerta) " +
                    "AND NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS c WHERE c.$COLUMN_ALERTA_ELIMINADA_ID = $id_alerta)"
        }else{
            sql = "INSERT INTO $TABLE_CONTROL_ALERTAS " +
                    "(alerta_id, " +
                    "alerta_titulo," +
                    "alerta_descripcion, " +
                    "alerta_nom_negocio, " +
                    "alerta_tipo, " +
                    "alerta_fecha, " +
                    "alerta_url_screenshot) " +
                    "SELECT $id_alerta," +
                    "'$titulo'," +
                    "'$descripcion'," +
                    "'$nom_negocio'," +
                    "$tipo_id," +
                    "strftime('%Y-%m-%d %H:%M:%S', datetime('now'))," +
                    "'$urlimagen' " +
                    "WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS b WHERE b.$COLUMN_ALERTA_ID = $id_alerta) " +
                    "AND NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS c WHERE c.$COLUMN_ALERTA_ELIMINADA_ID = $id_alerta)"
        }





        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.executeInsert()
        //db.close()
    }

    //SE UTILZA PARA ALERTAS RECUPERADAS
    @SuppressLint("SuspiciousIndentation")
    fun addAlertaDB(id_alerta: Int, titulo: String, descripcion: String, nom_negocio: String, tipo_id: Int, fecha: String, urlimagen: String, id_evt: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS " +
                "(alerta_id, " +
                "alerta_titulo," +
                "alerta_descripcion, " +
                "alerta_nom_negocio, " +
                "alerta_tipo, " +
                "alerta_fecha, " +
                "alerta_url_screenshot, " +
                "alerta_evt_id)" +
                "SELECT $id_alerta," +
                "'$titulo'," +
                "'$descripcion'," +
                "'$nom_negocio'," +
                "$tipo_id," +
                "'$fecha'," +
                "'$urlimagen', " +
                "$id_evt"
        "WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS b WHERE b.$COLUMN_ALERTA_ID = $id_alerta) " +
                "AND NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS c WHERE c.$COLUMN_ALERTA_ELIMINADA_ID = $id_alerta)"


        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.executeInsert()
        //db.close()
    }



    fun updControlAlertaDB(id_alerta: Int, id_evt: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("UPDATE $TABLE_CONTROL_ALERTAS SET alerta_evt_id = $id_evt  WHERE $COLUMN_ALERTA_ID = $id_alerta")
        //db.close()

    }


    fun updControlAlertaEliminadaDB(id_alerta: Int, id_evt: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("UPDATE $TABLE_CONTROL_ALERTAS_ELIMINADAS SET alerta_evt_id = $id_evt  WHERE $COLUMN_ALERTA_ELIMINADA_ID = $id_alerta")
        //db.close()

    }


    fun getDetAlertaEspecifica(id_alerta: Int): String{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT alerta_url_screenshot FROM $TABLE_CONTROL_ALERTAS WHERE alerta_id = $id_alerta"
        val cursor = db.rawQuery(Query, null)
        var urlimagenalerta = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                urlimagenalerta = cursor.getString(cursor.getColumnIndexOrThrow("alerta_url_screenshot"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  urlimagenalerta
    }


    fun CheckIfAlertaExist(id_alerta: Int): Boolean {


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_CONTROL_ALERTAS WHERE alerta_id = $id_alerta"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {

            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true




    }



    fun getAlertasAllListData(idnegocio:Int, idequipo: Int , filtroAlertas: Int, filtrofecha: Int, fechasFiltro: String, ultalerta: Int): Int {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ALERTA_ID, COLUMN_ALERTA_TITULO, COLUMN_ALERTA_DESCRIPCION, COLUMN_ALERTA_NOM_NEGOCIO, COLUMN_ALERTA_TIPO, COLUMN_ALERTA_FECHA)

        // sorting orders
        val sortOrder = "$COLUMN_ALERTA_ID DESC"
        var selection = ""
        var cursor: Cursor? = null
        //var selectionArgs = arrayOf(id_accion.toString())

        val infoList = ArrayList<ControlAlertasData>()

        if(idnegocio > 0){
            selection += "TRIM($COLUMN_ALERTA_TITULO) IN (select TRIM(nombre) from equipos where id_catnegocio = $idnegocio)"
        }

        if(idequipo > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "TRIM($COLUMN_ALERTA_TITULO)=(select TRIM(nombre) from equipos where id_negocio_id = $idequipo)"
        }

        if(ultalerta > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID < $ultalerta"
        }

        if(filtroAlertas == 1){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID NOT IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }

        if(filtroAlertas == 2){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }

        //filtro Fecha

        if(filtrofecha == 1){  //Hoy
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) = date('now')"

        }

        if(filtrofecha == 2){  //ayer
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-1 days') AND date('now', '-1 days')"
        }

        if(filtrofecha == 3){  //Ultimos 7
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-7 days') AND date('now', 'localtime')"
        }

        if(filtrofecha == 4){  //Ultimos 30
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-30 days') AND date('now', 'localtime')"
        }

        if(filtrofecha == 5){  //Ultimos 90
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-90 days') AND date('now', 'localtime')"
        }


        if(filtrofecha == 6){  //Desde - Hasta
            if(selection != ""){
                selection += " AND "
            }
            val fec = fechasFiltro.split("|")
            val fecDesde = fec[0]
            val fecHasta = fec[1]
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('$fecDesde') AND date('$fecHasta')"
        }


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        if (selection != ""){
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS WHERE $selection  ORDER BY $sortOrder ", null)

        }else{
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS   ORDER BY $sortOrder ", null)

        }



        if (cursor.moveToFirst()) {
            do {
                val info = ControlAlertasData(
                    idAlerta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_ID)).toInt(),
                    titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_TITULO))
                        .toString(),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_DESCRIPCION)),
                    nom_negocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_NOM_NEGOCIO)),
                    tipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_TIPO)).toInt(),
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_FECHA))
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList.size
    }


    fun getAlertasListData(idnegocio:Int, idequipo: Int , filtroAlertas: Int, filtrofecha: Int, fechasFiltro: String, ultalerta: Int, topAlertas: Int,   filtrotipo: Int, txtBusqueda: String, filttomarcadas: Int): List<ControlAlertasData> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ALERTA_ID, COLUMN_ALERTA_TITULO, COLUMN_ALERTA_DESCRIPCION, COLUMN_ALERTA_NOM_NEGOCIO, COLUMN_ALERTA_TIPO, COLUMN_ALERTA_FECHA)

        // sorting orders
        //val sortOrder = "datetime($COLUMN_ALERTA_FECHA) DESC"
        val sortOrder = "$COLUMN_ALERTA_ID DESC"


        var selection = ""
        var cursor: Cursor? = null
        //var selectionArgs = arrayOf(id_accion.toString())

        val infoList = ArrayList<ControlAlertasData>()



        if(idnegocio > 0){
            selection += "TRIM($COLUMN_ALERTA_TITULO) IN (select TRIM(nombre) from equipos where id_catnegocio = $idnegocio)"
        }

        if(idequipo > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "TRIM($COLUMN_ALERTA_TITULO)=(select TRIM(nombre) from equipos where id_negocio_id = $idequipo)"
        }


        if(ultalerta > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID < $ultalerta"
        }




        if(filtrotipo >0){
            if(selection != ""){
                selection += " AND "
            }
            //selection += "$COLUMN_ALERTA_TIPO IN ($filtrotipo)"
            when (filtrotipo) {
                42 ->  selection += "$COLUMN_ALERTA_DESCRIPCION like '%Se suspendio%'"
                6006 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%Se apago%'"
                6005 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%Se encendio%'"
                9004 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%Red-Envio:%' AND $COLUMN_ALERTA_DESCRIPCION like '%Recepcion:%'"
                9003 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%El disco%' AND $COLUMN_ALERTA_DESCRIPCION like '%se encuentra a%'"
                9002 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%La Memoria se encuentra a%'"
                9001 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%El CPU se encuentra a%'"
                4779 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%EvtID:4779%'"
                4647 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%EvtID:4647%'"
                4634 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%EvtID:4634%'"
                4625 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%EvtID:4625%'"
                4624 -> selection += "$COLUMN_ALERTA_DESCRIPCION like '%EvtID:4624%'"

            }
        }



        if(filtroAlertas == 1){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID NOT IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }


        if(filtroAlertas == 2){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ID IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }

        //filtro Fecha

        if(filtrofecha == 1){  //Hoy
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) = date('now')"

        }

        if(filtrofecha == 2){  //ayer
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-1 days') AND date('now', '-1 days')"
        }

        if(filtrofecha == 3){  //Ultimos 7
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-7 days') AND date('now', 'localtime')"
        }

        if(filtrofecha == 4){  //Ultimos 30
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-30 days') AND date('now', 'localtime')"
        }

        if(filtrofecha == 5){  //Ultimos 90
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('now', '-90 days') AND date('now', 'localtime')"
        }


        if(filtrofecha == 6){  //Desde - Hasta
            if(selection != ""){
                selection += " AND "
            }
            val fec = fechasFiltro.split("|")
            val fecDesde = fec[0]
            val fecHasta = fec[1]
            selection += "date($COLUMN_ALERTA_FECHA) BETWEEN date('$fecDesde') AND date('$fecHasta')"
        }


        if(txtBusqueda != ""){
            if(selection != ""){
                selection += " AND "
            }

            selection += " $COLUMN_ALERTA_DESCRIPCION like '%${txtBusqueda.trim()}%'"
        }



        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        if(filttomarcadas == 1){
            if(selection != ""){
                selection += " AND "
            }
            selection += "alerta_id IN (SELECT k.alerta_id FROM $TABLE_CONTROL_ALERTAS_MARCADAS k WHERE k.alerta_estatus = 'marcada')"
        }

        // query the user table
        if (selection != ""){
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS WHERE $selection  ORDER BY $sortOrder  LIMIT $topAlertas", null)

        }else{
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS   ORDER BY $sortOrder  LIMIT $topAlertas", null)

        }



        try {
            if (cursor.moveToFirst()) {
                do {
                    val info = ControlAlertasData(
                        idAlerta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_ID)).toInt(),
                        titulo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_TITULO))
                            .toString(),
                        descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_DESCRIPCION)),
                        nom_negocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_NOM_NEGOCIO)),
                        tipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_TIPO)).toInt(),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_FECHA))
                    )

                    infoList.add(info)
                } while (cursor.moveToNext())
            }
            cursor.close()

        } finally {
            if (cursor != null) cursor.close()
        }


        //db.close()
        return infoList
    }





    @SuppressLint("Range")
    fun getAlertasEliminadasListData(idnegocio:Int, filtroAlertas: Int, filtrofecha: Int, fechasFiltro: String, ultalerta: Int, topAlertas: Int): List<ControlAlertasEliminadasData> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ALERTA_ELIMINADA_ID, COLUMN_ALERTA_ELIMINADA_TITULO, COLUMN_ALERTA_ELIMINADA_DESCRIPCION, COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO, COLUMN_ALERTA_ELIMINADA_TIPO, COLUMN_ALERTA_ELIMINADA_FECHA, COLUMN_ALERTA_ELIMINADA_EVT_ID)

        // sorting orders
        val sortOrder = "$COLUMN_ALERTA_ELIMINADA_ID DESC"
        var selection = ""
        var cursor: Cursor? = null
        //var selectionArgs = arrayOf(id_accion.toString())

        val infoList = ArrayList<ControlAlertasEliminadasData>()

        if(idnegocio > 0){
            selection += "TRIM($COLUMN_ALERTA_ELIMINADA_TITULO)=(select TRIM(nombre) from equipos where id_catnegocio = $idnegocio)"
        }

        if(ultalerta > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ELIMINADA_ID < $ultalerta"
        }

        if(filtroAlertas == 1){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ELIMINADA_ID NOT IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }

        if(filtroAlertas == 2){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_ALERTA_ELIMINADA_ID IN (SELECT alerta_id FROM $TABLE_CONTROL_ALERTAS_ESTATUS)"
        }

        //filtro Fecha

        if(filtrofecha == 1){  //Hoy
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) = date('now')"

        }

        if(filtrofecha == 2){  //ayer
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) BETWEEN datetime('now', '-1 days') AND datetime('now', '-1 days')"
        }

        if(filtrofecha == 3){  //Ultimos 7
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) BETWEEN datetime('now', '-7 days') AND datetime('now', 'localtime')"
        }

        if(filtrofecha == 4){  //Ultimos 30
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) BETWEEN datetime('now', '-30 days') AND datetime('now', 'localtime')"
        }

        if(filtrofecha == 5){  //Ultimos 90
            if(selection != ""){
                selection += " AND "
            }
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) BETWEEN datetime('now', '-90 days') AND datetime('now', 'localtime')"
        }


        if(filtrofecha == 6){  //Desde - Hasta
            if(selection != ""){
                selection += " AND "
            }
            val fec = fechasFiltro.split("|")
            val fecDesde = fec[0]
            val fecHasta = fec[1]
            selection += "date($COLUMN_ALERTA_ELIMINADA_FECHA) BETWEEN datetime('$fecDesde') AND datetime('$fecHasta')"
        }


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        if (selection != ""){
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS WHERE $selection  ORDER BY $sortOrder  LIMIT $topAlertas", null)

        }else{
            cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_ALERTAS_ELIMINADAS   ORDER BY $sortOrder  LIMIT $topAlertas", null)

        }



        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlAlertasEliminadasData(
                        idAlerta = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_ELIMINADA_ID))
                            .toInt(),
                        titulo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_TITULO
                            )
                        ).toString(),
                        descripcion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_DESCRIPCION
                            )
                        ),
                        nom_negocio = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO
                            )
                        ),
                        tipo_id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_TIPO
                            )
                        ).toInt(),
                        fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_ELIMINADA_FECHA)),
                        fecha_eliminacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_FECHA_ELIMINACION
                            )
                        ),
                        id_evt = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_ELIMINADA_EVT_ID
                            )
                        ).toInt()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }















    fun getUltimasAlertas(lstDataFav: ArrayList<String>, topAlertas: Int): List<ControlAlertasData> {

        // array of columns to fetch
        val joined: String = TextUtils.join(", ", lstDataFav)
        val infoList = ArrayList<ControlAlertasData>()



        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        // val cursor_det = db.rawQuery("SELECT $COLUMN_ALERTA_ID, $COLUMN_ALERTA_TITULO, " +
        //       "$COLUMN_ALERTA_DESCRIPCION, $COLUMN_ALERTA_NOM_NEGOCIO, $COLUMN_ALERTA_TIPO, " +
        //      "$COLUMN_ALERTA_FECHA FROM $TABLE_CONTROL_ALERTAS WHERE $COLUMN_ALERTA_TIPO IN ($joined) " +
        //     " ORDER BY $COLUMN_ALERTA_ID DESC LIMIT $topAlertas;", null)


        val cursor_det = db.rawQuery("SELECT * FROM (SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 1 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 1),0)\n" +
                ")\n" +
                "UNION\n" +
                "SELECT * FROM (\n" +
                "SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 2 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 2),0)\n" +
                ")\n" +
                "UNION\n" +
                "SELECT * FROM (\n" +
                "SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 3 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 3),0)\n" +
                ")\n" +
                "UNION\n" +
                "SELECT * FROM (\n" +
                "SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 4 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 4),0)\n" +
                ")\n" +
                "UNION\n" +
                "SELECT * FROM (\n" +
                "SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 5 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 5),0)\n" +
                ")\n" +
                "UNION\n" +
                "SELECT * FROM (\n" +
                "SELECT alerta_id, alerta_titulo, alerta_descripcion, alerta_nom_negocio, alerta_tipo, alerta_fecha \n" +
                "FROM control_alertas  WHERE alerta_tipo = 6 ORDER BY datetime(alerta_fecha) DESC \n" +
                "LIMIT IFNULL((SELECT alerta_fav_ultimas_entradas FROM control_alertas_favoritas WHERE alerta_fav_tipo = 6),0)\n" +
                ") ORDER BY alerta_fecha DESC \n", null)


        //The sort order
        if (cursor_det.moveToFirst()) {
            do {
                val info = ControlAlertasData(
                    idAlerta = cursor_det.getString(cursor_det.getColumnIndexOrThrow(COLUMN_ALERTA_ID))
                        .toInt(),
                    titulo = cursor_det.getString(cursor_det.getColumnIndexOrThrow(COLUMN_ALERTA_TITULO))
                        .toString(),
                    descripcion = cursor_det.getString(
                        cursor_det.getColumnIndexOrThrow(
                            COLUMN_ALERTA_DESCRIPCION
                        )
                    ),
                    nom_negocio = cursor_det.getString(
                        cursor_det.getColumnIndexOrThrow(
                            COLUMN_ALERTA_NOM_NEGOCIO
                        )
                    ),
                    tipo_id = cursor_det.getString(cursor_det.getColumnIndexOrThrow(COLUMN_ALERTA_TIPO))
                        .toInt(),
                    fecha = cursor_det.getString(cursor_det.getColumnIndexOrThrow(COLUMN_ALERTA_FECHA))
                )

                infoList.add(info)
            } while (cursor_det.moveToNext())
        }
        cursor_det.close()
        //db.close()
        return infoList
    }










    ///Delete Configuracion Alertas
    fun delCOnfiguracionAlertas() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONFIGURACION_ALERTAS")
        //db.close()
    }


    fun addConfiguracionAlertas(top_alertas: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_ALERTAS (alerta_fav_top) VALUES(?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, top_alertas.toLong())
        insertStmt!!.executeInsert();
        //db.close()
    }



    fun getConfiguracionAlertas(): List<ConfiguracionAlertasData> {

        // array of columns to fetch

        val infoList = ArrayList<ConfiguracionAlertasData>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val cursor_det = db.rawQuery("SELECT $COLUMN_ALERTA_TOP " +
                "FROM $TABLE_CONFIGURACION_ALERTAS", null)


        //The sort order
        if (cursor_det.moveToFirst()) {
            do {
                val info =
                    ConfiguracionAlertasData(
                        top_alertas = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ALERTA_TOP
                            )
                        ).toInt()
                    )

                infoList.add(info)
            } while (cursor_det.moveToNext())
        }
        cursor_det.close()
        //db.close()
        return infoList
    }




    ///


    ///Delete Configuracion Alertas
    fun delControlMonitoreableFav(id_accion: Int, tipo_hardware: String, tipo_grafico: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        //db.execSQL("DELETE FROM $TABLE_CONTROL_MONITOREABLES_FAVORITOS WHERE control_accion_monitoreable_id = $id_accion AND control_accion_monitoreable_hardware = '$tipo_hardware'  AND control_accion_monitoreable_tipo = '$tipo_grafico'")
        db.execSQL("DELETE FROM $TABLE_CONTROL_MONITOREABLES_FAVORITOS WHERE control_accion_monitoreable_id = $id_accion AND control_accion_monitoreable_hardware = '$tipo_hardware'")
        //db.close()
    }


    fun addControlMonitoreableFav(accion_monitoreable_id: Int, accion_hardware: String, tipo_grafico: String, porcentaje: String, nom_svr: String, espacio_libre: String, espacio_total: String, tot_enviados: String, tot_recibidos: String, id_equipo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_MONITOREABLES_FAVORITOS (control_accion_monitoreable_id, control_accion_monitoreable_hardware, control_accion_monitoreable_tipo, control_accion_monitoreable_porcentaje, control_accion_monitoreable_nom_svr, control_accion_monitoreable_espacio_libre, control_accion_monitoreable_espacio_total, control_accion_monitoreable_tot_enviados, control_accion_monitoreable_tot_recibidos, control_accion_monitoreable_id_equipo) VALUES(?,?,?,?,?,?,?,?,?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, accion_monitoreable_id.toLong())
        insertStmt!!.bindString(2, accion_hardware)
        insertStmt!!.bindString(3, tipo_grafico)
        insertStmt!!.bindString(4, porcentaje)
        insertStmt!!.bindString(5, nom_svr)
        insertStmt!!.bindString(6, espacio_libre)
        insertStmt!!.bindString(7, espacio_total)
        insertStmt!!.bindString(8, tot_enviados)
        insertStmt!!.bindString(9, tot_recibidos)
        insertStmt!!.bindLong(10, id_equipo.toLong())
        insertStmt!!.executeInsert()
        //db.close()

    }



    @SuppressLint("Range")
    fun getControlMonitoreableFav(): List<DataControlMonitoreablesFav> {

        // array of columns to fetch

        val infoList = ArrayList<DataControlMonitoreablesFav>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        val cursor_det = db.rawQuery("SELECT $COLUMN_ACCION_MONITOREABLE_ID, $COLUMN_ACCION_MONITOREABLE_HARDWARE, $COLUMN_ACCION_MONITOREABLE_TIPO" +
                ", $COLUMN_ACCION_MONITOREABLE_PORCENTAJE, $COLUMN_ACCION_MONITOREABLE_NOM_SVR, $COLUMN_ACCION_MONITOREABLE_ESPACIO_LIBRE " +
                ", $COLUMN_ACCION_MONITOREABLE_ESPACIO_TOTAL, $COLUMN_ACCION_MONITOREABLE_TOT_ENVIADOS, $COLUMN_ACCION_MONITOREABLE_TOT_RECIBIDOS " +
                ", $COLUMN_ACCION_MONITOREABLE_ID_EQUIPO " +
                " FROM $TABLE_CONTROL_MONITOREABLES_FAVORITOS a, $TABLE_EQUIPOS b " +
                " WHERE a.$COLUMN_ACCION_MONITOREABLE_ID_EQUIPO = b.$COLUMN_EQUIPO_ID_ " +
                " AND b.$COLUMN_ESTADO_SERVIDOR < 9 AND $COLUMN_ACCION_MONITOREABLE_ID  " +
                " IN (SELECT f.id FROM acciones_dinamicas f WHERE f.id = $COLUMN_ACCION_MONITOREABLE_ID AND f.origen < 9)", null)


        //The sort order
        if (cursor_det.moveToFirst()) {
            do {
                val info =
                    DataControlMonitoreablesFav(
                        id_accion = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_ID
                            )
                        ).toInt(),
                        tipo_hardware = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_HARDWARE
                            )
                        ).toString(),
                        tipo_grafico = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_TIPO
                            )
                        ).toString(),
                        porcentaje = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_PORCENTAJE
                            )
                        ).toString(),
                        nom_svr = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_NOM_SVR
                            )
                        ).toString(),
                        espacio_libre = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_ESPACIO_LIBRE
                            )
                        ).toString(),
                        espacio_total = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_ESPACIO_TOTAL
                            )
                        ).toString(),
                        tot_enviados = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_TOT_ENVIADOS
                            )
                        ).toString(),
                        tot_recibidos = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_TOT_RECIBIDOS
                            )
                        ).toString(),
                        id_equipo = cursor_det.getString(
                            cursor_det.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOREABLE_ID_EQUIPO
                            )
                        ).toInt()
                    )

                infoList.add(info)
            } while (cursor_det.moveToNext())
        }
        cursor_det.close()
        //db.close()
        return infoList
    }







///Alertas Favoritas


    fun delAlertasFavoritas(tipo_alerta: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_ALERTAS_FAVORITAS WHERE alerta_fav_tipo = $tipo_alerta");
        //db.close()
    }


    fun addAlertaFavoritasDB(tipo_alerta: Int, ultimas_entradas: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ALERTAS_FAVORITAS (alerta_fav_tipo, alerta_fav_ultimas_entradas) VALUES(?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, tipo_alerta.toLong())
        insertStmt!!.bindLong(2, ultimas_entradas.toLong())
        insertStmt!!.executeInsert()
        //db.close()

    }

    fun getAlertasFavLisData(): List<ControlAlertasFavData> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ALERTA_FAV_TIPO, COLUMN_ALERTA_FAV_ULTIMAS_ENTRADAS)

        // sorting orders
        val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"

        val infoList = ArrayList<ControlAlertasFavData>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_CONTROL_ALERTAS_FAVORITAS, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlAlertasFavData(
                        alerta_tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALERTA_FAV_TIPO))
                            .toInt(),
                        ultimas_entradas = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ALERTA_FAV_ULTIMAS_ENTRADAS
                            )
                        ).toInt()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }






    /////////////




    fun delAControlRed() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_CONEXION_RED")
        //db.close()
    }


    fun addCOntrolDB(ip_publica: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_CONEXION_RED (control_ip_publica) VALUES(?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindString(1, ip_publica)
        insertStmt!!.executeInsert()
        //db.close()

    }

    fun getConexionRed(): List<ControlRedMovil> {

        // array of columns to fetch

        val columns = arrayOf(COLUMN_IP_PUBLICA)

        // sorting orders


        val infoList = ArrayList<ControlRedMovil>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_CONTROL_CONEXION_RED, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = ControlRedMovil(
                    ip_publica = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_PUBLICA))
                        .toString()
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        // //db.close()
        return infoList
    }




    ///TABLAS API VM


    fun addControlEquiposAPIVM(id_equipo: Int, url: String, nom_proyecto: String, zona: String, nom_instancia: String, screenshot_vm: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_EQUIPOS_API_VM ($COLUMN_EQUIPO_VM_ID_EQUIPO, $COLUMN_EQUIPO_VM_URL_AUTH" +
                ", $COLUMN_EQUIPO_VM_NOM_PROYECTO, $COLUMN_EQUIPO_VM_ZONA, $COLUMN_EQUIPO_VM_NOM_INSTANCIA, $COLUMN_EQUIPO_VM_SCREENSHOT) " +
                " SELECT ?,?,?,?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_EQUIPOS_API_VM WHERE $COLUMN_EQUIPO_VM_ID_EQUIPO = $id_equipo)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, id_equipo.toLong())
        insertStmt!!.bindString(2, url)
        insertStmt!!.bindString(3, nom_proyecto)
        insertStmt!!.bindString(4, zona)
        insertStmt!!.bindString(5, nom_instancia)
        insertStmt!!.bindLong(6, screenshot_vm.toLong())
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun updControlEquiposAPIVM(id_equipo: Int, url: String, nom_proyecto: String, zona: String, nom_instancia: String, screenshot_vm: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_EQUIPOS_API_VM SET  $COLUMN_EQUIPO_VM_URL_AUTH = '$url', " +
                " $COLUMN_EQUIPO_VM_NOM_PROYECTO = '$nom_proyecto', $COLUMN_EQUIPO_VM_ZONA = '$zona', " +
                " $COLUMN_EQUIPO_VM_NOM_INSTANCIA = '$nom_instancia', $COLUMN_EQUIPO_VM_SCREENSHOT = $screenshot_vm " +
                " WHERE $COLUMN_EQUIPO_VM_ID_EQUIPO = $id_equipo"


        db.execSQL(sql)

    }




    fun getControlEquiposAPIVM(id_equipo: Int): List<ControlEquiposApiMv> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_EQUIPO_VM_ID_EQUIPO, COLUMN_EQUIPO_VM_URL_AUTH, COLUMN_EQUIPO_VM_NOM_PROYECTO, COLUMN_EQUIPO_VM_ZONA, COLUMN_EQUIPO_VM_NOM_INSTANCIA, COLUMN_EQUIPO_VM_ID_EQUIPO, COLUMN_EQUIPO_VM_SCREENSHOT)



        val infoList = ArrayList<ControlEquiposApiMv>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_EQUIPO_VM_ID_EQUIPO = ?"

        // selection arguments
        val selectionArgs = arrayOf(id_equipo.toString())

        // query the user table
        val cursor = db.query(TABLE_CONTROL_EQUIPOS_API_VM, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = ControlEquiposApiMv(
                    equipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_VM_ID_EQUIPO))
                        .toInt(),
                    url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_VM_URL_AUTH))
                        .toString(),
                    nom_proyecto = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_EQUIPO_VM_NOM_PROYECTO
                        )
                    ).toString(),
                    zona = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_VM_ZONA))
                        .toString(),
                    nom_instancia = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_EQUIPO_VM_NOM_INSTANCIA
                        )
                    ).toString(),
                    screenshot_vm = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_EQUIPO_VM_SCREENSHOT
                        )
                    ).toInt()
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }






















    ///////

    fun delControlEquipos() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_EQUIPOS_X_MOVIL")
        //db.close()
    }


    fun addControlEquipos(id_equipo: Int, ip_publica: String, ip_local: String, puerto: Int, IdHardware: String, icono_on: String, icono_off: String, nombre_equipo: String, id_negocio: Int, id_accion_monitoreable: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_EQUIPOS_X_MOVIL (control_equipo_id, control_equipo_ip_publica, control_equipo_ip_local, control_equipo_puerto, control_equipo_id_hardware, control_equipo_icono_on, control_equipo_icono_off,control_equipo_nombre, control_negocio_id, id_accion_monitoreable) SELECT ?,?,?,?,?,?,?,?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_EQUIPOS_X_MOVIL WHERE control_equipo_id = $id_equipo AND control_equipo_ip_publica = '$ip_publica' AND  control_equipo_id_hardware = '$IdHardware' AND control_negocio_id = '$id_negocio')"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, id_equipo.toLong())
        insertStmt!!.bindString(2, ip_publica)
        insertStmt!!.bindString(3, ip_local)
        insertStmt!!.bindLong(4, puerto.toLong())
        insertStmt!!.bindString(5, IdHardware.toString())
        insertStmt!!.bindString(6, icono_on)
        insertStmt!!.bindString(7, icono_off)
        insertStmt!!.bindString(8, nombre_equipo)
        insertStmt!!.bindLong(9, id_negocio.toLong())
        insertStmt!!.bindString(10, id_accion_monitoreable)
        insertStmt!!.executeInsert()
        //db.close()
    }



    fun getControlEquipos(): List<ControlEquiposXMovil> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_EQUIPO_ID, COLUMN_EQUIPO_IP_PUBLICA, COLUMN_EQUIPO_IP_LOCAL, COLUMN_EQUIPO_PUERTO, COLUMN_EQUIPO_ID_HARDWARE, COLUMN_EQUIPO_ICONO_ON, COLUMN_EQUIPO_ICONO_OFF, COLUMN_EQUIPO_NOMBRE, COLUMN_ACTIVIDAD_MONITOREABLE_ID)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"

        val infoList = ArrayList<ControlEquiposXMovil>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // query the user table
        val cursor = db.query(TABLE_CONTROL_EQUIPOS_X_MOVIL, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlEquiposXMovil(
                        equipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID))
                            .toInt(),
                        ip_publica = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_IP_PUBLICA))
                            .toString(),
                        ip_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_IP_LOCAL))
                            .toString(),
                        puerto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_PUERTO))
                            .toInt(),
                        id_hardware = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_EQUIPO_ID_HARDWARE
                            )
                        ).toString(),
                        icono_on = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ICONO_ON))
                            .toString(),
                        icono_off = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ICONO_OFF))
                            .toString(),
                        nombre_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_NOMBRE))
                            .toString(),
                        id_accion_monitoreable = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACTIVIDAD_MONITOREABLE_ID
                            )
                        ).toString()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }



    fun getControlEquiposXHardware(HardwareKeyEquipo: String): List<ControlEquiposXMovil> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_EQUIPO_ID, COLUMN_EQUIPO_IP_PUBLICA, COLUMN_EQUIPO_IP_LOCAL, COLUMN_EQUIPO_PUERTO, COLUMN_EQUIPO_ID_HARDWARE, COLUMN_EQUIPO_ICONO_ON, COLUMN_EQUIPO_ICONO_OFF, COLUMN_EQUIPO_NOMBRE, COLUMN_ACTIVIDAD_MONITOREABLE_ID)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"

        val infoList = ArrayList<ControlEquiposXMovil>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_EQUIPO_ID_HARDWARE = ?"

        // selection arguments
        val selectionArgs = arrayOf(HardwareKeyEquipo)

        // query the user table
        val cursor = db.query(TABLE_CONTROL_EQUIPOS_X_MOVIL, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlEquiposXMovil(
                        equipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID))
                            .toInt(),
                        ip_publica = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_IP_PUBLICA))
                            .toString(),
                        ip_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_IP_LOCAL))
                            .toString(),
                        puerto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_PUERTO))
                            .toInt(),
                        id_hardware = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_EQUIPO_ID_HARDWARE
                            )
                        ).toString(),
                        icono_on = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ICONO_ON))
                            .toString(),
                        icono_off = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ICONO_OFF))
                            .toString(),
                        nombre_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_NOMBRE))
                            .toString(),
                        id_accion_monitoreable = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACTIVIDAD_MONITOREABLE_ID
                            )
                        ).toString()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }



    fun GetListaEquiposXNegocio(id_negocio: Int):  List<ControlEquiposXNegocio> {
        // array of columns to fetch
        val columns = arrayOf(COLUMN_EQUIPO_ID, COLUMN_EQUIPO_NOMBRE)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"

        val infoList = ArrayList<ControlEquiposXNegocio>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_NEGOCIO_ID = ?"

        // selection arguments
        val selectionArgs = arrayOf(id_negocio.toString())

        // query the user table
        val cursor = db.query(TABLE_CONTROL_EQUIPOS_X_MOVIL, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlEquiposXNegocio(
                        equipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID))
                            .toInt(),
                        nombre_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_NOMBRE))
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }







    /////


    fun delControlEstadoEquipos() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        // db.execSQL("DELETE FROM $TABLE_CONTROL_ESTADO_EQUIPOS")
        db.execSQL("UPDATE $TABLE_CONTROL_ESTADO_EQUIPOS SET $COLUMN_ESTADO_EQUIPO_CONTROL = 0")

        //db.close()
    }

    fun delControlEstadoEquiposXID(id_equipo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        //db.execSQL("DELETE FROM $TABLE_CONTROL_ESTADO_EQUIPOS WHERE control_estado_equipo_id = $id_equipo")
        db.execSQL("UPDATE $TABLE_CONTROL_ESTADO_EQUIPOS SET $COLUMN_ESTADO_EQUIPO_CONTROL = 0 WHERE control_estado_equipo_id = $id_equipo")

        //db.close()
    }


    fun CheckExisteControlEquipo(id_equipo: Int): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_CONTROL_ESTADO_EQUIPOS WHERE control_estado_equipo_id = $id_equipo"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }

    fun updControlEstadoEquipos(id_equipo: Int, estado_equipo: String, estado_nombre_equipo: String, estado_equipo_hardware: String, estado_equipo_ippublica: String, estado_equipo_iplocal: String, estado_equipo_puerto: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("UPDATE $TABLE_CONTROL_ESTADO_EQUIPOS SET control_estado_equipo_estatus = '$estado_equipo',control_estado_equipo_nombre = '$estado_nombre_equipo', control_estado_equipo_hardware = '$estado_equipo_hardware', control_estado_equipo_ip_publica = '$estado_equipo_ippublica', control_estado_equipo_ip_local = '$estado_equipo_iplocal', control_estado_equipo_puerto ='$estado_equipo_puerto', $COLUMN_ESTADO_EQUIPO_CONTROL = 1 WHERE control_estado_equipo_id = $id_equipo")
        //db.close()

    }


    fun addControlEstadoEquipos(id_equipo: Int, estado_equipo: String, estado_nombre_equipo: String, estado_equipo_hardware: String, estado_equipo_ippublica: String, estado_equipo_iplocal: String, estado_equipo_puerto: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ESTADO_EQUIPOS (control_estado_equipo_id, control_estado_equipo_estatus,control_estado_equipo_nombre, control_estado_equipo_hardware, control_estado_equipo_ip_publica, control_estado_equipo_ip_local, control_estado_equipo_puerto, $COLUMN_ESTADO_EQUIPO_CONTROL) SELECT ?,?,?,?,?,?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ESTADO_EQUIPOS WHERE control_estado_equipo_nombre = '$estado_nombre_equipo' AND  control_estado_equipo_hardware = '$estado_equipo_hardware');"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, id_equipo.toLong())
        insertStmt!!.bindString(2, estado_equipo)
        insertStmt!!.bindString(3, estado_nombre_equipo)
        insertStmt!!.bindString(4, estado_equipo_hardware)
        insertStmt!!.bindString(5, estado_equipo_ippublica)
        insertStmt!!.bindString(6, estado_equipo_iplocal)
        insertStmt!!.bindString(7, estado_equipo_puerto)
        insertStmt!!.bindLong(8, 1)
        insertStmt!!.executeInsert()
        //db.close()
    }



    fun getControlEstadoEquipos(): List<ControlEstadoEquipos> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ESTADO_EQUIPO_ID, COLUMN_ESTADO_EQUIPO_ESTATUS, COLUMN_ESTADO_EQUIPO_NOMBRE, COLUMN_ESTADO_EQUIPO_HARDWARE)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"

        val selection = "$COLUMN_ESTADO_EQUIPO_CONTROL = ?"

        // selection argument
        val selectionArgs = arrayOf("1")

        val infoList = ArrayList<ControlEstadoEquipos>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_CONTROL_ESTADO_EQUIPOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlEstadoEquipos(
                        equipo_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_EQUIPO_ID))
                            .toInt(),
                        estado_equipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ESTADO_EQUIPO_ESTATUS
                            )
                        ).toString(),
                        nombre_equipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ESTADO_EQUIPO_NOMBRE
                            )
                        ).toString(),
                        hardware_key = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ESTADO_EQUIPO_HARDWARE
                            )
                        ).toString()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }





    fun getDetEstadoEquipo(id_hardware: String): List<ControlDeatalleEquipos>{
        val infoList = ArrayList<ControlDeatalleEquipos>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT control_estado_equipo_ip_publica, control_estado_equipo_ip_local, control_estado_equipo_puerto FROM control_estado_equipos  WHERE control_estado_equipo_hardware = '$id_hardware' ORDER BY control_estado_equipo_id DESC LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var urlimagenalerta = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val info =
                    ControlDeatalleEquipos(
                        equipo_ip_publica = cursor.getString(cursor.getColumnIndexOrThrow("control_estado_equipo_ip_publica")),
                        equipo_ip_local = cursor.getString(cursor.getColumnIndexOrThrow("control_estado_equipo_ip_local")),
                        equipo_puerto = cursor.getString(cursor.getColumnIndexOrThrow("control_estado_equipo_puerto"))
                    )
                infoList.add(info)

                i++
            } while (cursor.moveToNext())

            cursor.close()
        }
        //db.close()
        return  infoList
    }



///////////////////////////////////


    fun addNegocio(negocio: Cat_Negocios_list) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_NEGOCIOS_ID, negocio.id)
        values.put(COLUMN_NEGOCIOS_NOMBRE, negocio.nombre)

        db.insert(TABLE_NEGOCIOS, null, values)
        //db.close()
    }

    fun CheckExistNegocio(negocio: Cat_Negocios_list): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_NEGOCIOS WHERE negocios_id = '${negocio.id}' AND negocios_nombre = '${negocio.nombre}' "
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }

    fun delNegocio() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_NEGOCIOS")
        //db.close()
    }

    fun GetListaNegocios():  List<Cat_Negocios_list> {
        val columns = arrayOf(COLUMN_NEGOCIOS_ID, COLUMN_NEGOCIOS_NOMBRE)


        val infoList = ArrayList<Cat_Negocios_list>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        var selection = "negocios_id = 'Todos' OR negocios_id IN (SELECT id_catnegocio FROM equipos WHERE estado_servidor < ?)"
        val selectionArgs = arrayOf("9")
        //infoList.add(Cat_Negocios_list("Todos","Todos"))




        // query the user table
        val cursor = db.query(TABLE_NEGOCIOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            "$COLUMN_NEGOCIOS_ID DESC")         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = Cat_Negocios_list(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEGOCIOS_ID)).toString(),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NEGOCIOS_NOMBRE))
                        .toString()
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }















    fun checkifexistvalueTodos(context: Context){
        SQLiteDatabase.loadLibs(context)
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_NEGOCIOS ($COLUMN_NEGOCIOS_ID,$COLUMN_NEGOCIOS_NOMBRE) SELECT 'Todos', 'Todos' WHERE NOT EXISTS(SELECT 1 FROM $TABLE_NEGOCIOS WHERE $COLUMN_NEGOCIOS_ID = 'Todos')"
        db.execSQL(sql)
        //db.close()

    }
    ////////////////////





    fun delControlTagGraficoMonitoreable() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_TAG_GRAFICOS_MONITOREABLES")
        //db.close()
    }


    fun addControlTagGraficoMonitoreable(id_accion: Int, tag_grafico: Int, hardware: String, grafico: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_TAG_GRAFICOS_MONITOREABLES (control_accion_monitor_id, control_accion_monitor_tag, control_accion_monitor_hardware, control_accion_monitor_grafico) VALUES(?,?,?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, id_accion.toLong())
        insertStmt!!.bindLong(2, tag_grafico.toLong())
        insertStmt!!.bindString(3, hardware)
        insertStmt!!.bindString(4, grafico)
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun CheckTagGraficoMonitoreable(id_accion: Int, tag_grafico: Int, hardware: String, grafico: String): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_TAG_GRAFICOS_MONITOREABLES WHERE control_accion_monitor_id = $id_accion AND control_accion_monitor_tag = $tag_grafico AND control_accion_monitor_hardware = '$hardware' AND control_accion_monitor_grafico = '$grafico'"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }


    fun GetTagGraficoMonitoreable(id_accion: Int, hardware: String, grafico: String): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT control_accion_monitor_tag FROM $TABLE_TAG_GRAFICOS_MONITOREABLES WHERE control_accion_monitor_id = $id_accion  AND control_accion_monitor_hardware = '$hardware' AND control_accion_monitor_grafico = '$grafico'"
        val cursor = db.rawQuery(Query, null)
        var tagdelgrafico = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                tagdelgrafico = cursor.getString(cursor.getColumnIndexOrThrow("control_accion_monitor_tag"))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  tagdelgrafico
    }



    fun getControlTagGraficoMonitoreables(): List<DataControlMonitoreablesTag> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACCION_MONITOR_ID, COLUMN_ACCION_MONITOR_TAG, COLUMN_ACCION_MONITOR_HARDWARE, COLUMN_ACCION_MONITOR_GRAFICO)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"



        val infoList = ArrayList<DataControlMonitoreablesTag>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_TAG_GRAFICOS_MONITOREABLES, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    DataControlMonitoreablesTag(
                        id_accion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCION_MONITOR_ID))
                            .toInt(),
                        tag_grafico = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOR_TAG
                            )
                        ).toInt(),
                        hardware = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOR_HARDWARE
                            )
                        ).toString(),
                        grafico = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_MONITOR_GRAFICO
                            )
                        ).toString()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }


    /***
     * EQUIPOS
     */



    fun addEquipos(equipos: Negocios_List) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()


        val sql = "INSERT INTO $TABLE_EQUIPOS ($COLUMN_ID_MOVIL_ID," +
                "$COLUMN_EQUIPO_ID_, $COLUMN_NOMBRE," +
                "$COLUMN_IP_PUBLICA_, $COLUMN_IP_LOCAL," +
                "$COLUMN_PUERTO_LOCAL, $COLUMN_PUERTO," +
                "$COLUMN_BOVEDA_SOFTWARE, $COLUMN_RUTA_ARCHIVOS_LOCALES," +
                "$COLUMN_HARDWARE_KEY_,$COLUMN_ESTADO_SERVIDOR," +
                "$COLUMN_ESTADO_ULT_ACT,$COLUMN_ICONO_NEGOCIO_ON," +
                "$COLUMN_ICONO_NEGOCIO_OFF,$COLUMN_FEC_ACT_ESTATUS," +
                "$COLUMN_CATNOMBRENEG,$COLUMN_ID_CATNEGOCIO," +
                "$COLUMN_TIPO_EQ_ICONO_ON,$COLUMN_TIPO_EQ_ICONO_OFF," +
                "$COLUMN_TIPO_EQ_CLAVE,$COLUMN_ID_ACCION_MONITOREABLE," +
                "$COLUMN_MODELO_COMUNICACION, $COLUMN_WAKE_ON, $COLUMN_ESTADO_EQUIPO, " +
                "$COLUMN_ORDENAMIENTO, " +
                "$COLUMN_EQUIPO_VIRTUAL )" +
                " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                " WHERE NOT EXISTS(SELECT 1 FROM $TABLE_EQUIPOS " +
                " WHERE $COLUMN_ID_MOVIL_ID = ${equipos.id_movil_id} " +
                " AND $COLUMN_EQUIPO_ID_ = ${equipos.id_negocio_id})"

        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        var idAccionMonitoreable:Long = 0
        var modeloComunicacion: Long = 0
        var wakeOn: Long = 0
        if(equipos.id_accion_monitoreable != null){
            idAccionMonitoreable = equipos.id_accion_monitoreable.toLong()
        }

        if(equipos.modelo_comunicacion != null){
            modeloComunicacion = equipos.modelo_comunicacion.toLong()
        }

        if(equipos.wake_on != null){
            wakeOn = equipos.wake_on.toLong()
        }
        var iorden: Int = 0
        if(equipos.ordenamiento_equipos != null &&  equipos.ordenamiento_equipos != ""){
            iorden = equipos.ordenamiento_equipos.toInt()
        }

        insertStmt!!.bindLong(1, equipos.id_movil_id.toLong())
        insertStmt!!.bindLong(2, equipos.id_negocio_id.toLong())
        insertStmt!!.bindString(3, equipos.nombre)
        insertStmt!!.bindString(4, equipos.ip_publica)
        insertStmt!!.bindString(5, equipos.ip_local)
        insertStmt!!.bindLong(6, equipos.puerto_local.toLong())
        insertStmt!!.bindLong(7, equipos.puerto.toLong())
        insertStmt!!.bindString(8, equipos.boveda_software)
        insertStmt!!.bindString(9, equipos.ruta_archivos_locales)
        insertStmt!!.bindString(10, equipos.hardware_key)
        insertStmt!!.bindLong(11, equipos.estado_servidor.toLong())
        insertStmt!!.bindString(12, equipos.estado_ult_act)
        insertStmt!!.bindString(13, equipos.icono_negocio_on)
        insertStmt!!.bindString(14, equipos.icono_negocio_off)
        insertStmt!!.bindLong(15, equipos.fec_act_status.toLong())
        insertStmt!!.bindString(16, equipos.catnombreneg)
        insertStmt!!.bindLong(17, equipos.id_catnegocio.toLong())
        insertStmt!!.bindString(18, equipos.tipo_eq_icono_on)
        insertStmt!!.bindString(19, equipos.tipo_eq_icono_off)
        insertStmt!!.bindString(20, equipos.tipo_eq_clave)
        insertStmt!!.bindLong(21, idAccionMonitoreable)
        insertStmt!!.bindLong(22, modeloComunicacion)
        insertStmt!!.bindLong(23, wakeOn)
        insertStmt!!.bindString(24, equipos.estatus_equipo!!)
        insertStmt!!.bindLong(25, iorden.toLong())
        insertStmt!!.bindLong(26, equipos.equipo_virtual!!.toLong())

        insertStmt!!.executeInsert()
        //db.close()
    }


    fun addEquiposTipoLIc(id_equipo: Int, tipo_lic: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_EQUIPOS_TIPO_LIC ($COLUMN_EQUIPO_TL_ID, $COLUMN_EQUIPO_TL_UNIMOVIL) " +
                " SELECT ?,?" +
                " WHERE NOT EXISTS(SELECT 1 FROM $TABLE_EQUIPOS_TIPO_LIC " +
                " WHERE $COLUMN_EQUIPO_TL_ID = $id_equipo )"

        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindLong(1, id_equipo.toLong())
        insertStmt!!.bindLong(2, tipo_lic.toLong())
        insertStmt!!.executeInsert()

    }


    fun updEquiposTipoLIc(id_equipo: Int, tipo_lic: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_EQUIPOS_TIPO_LIC SET  $COLUMN_EQUIPO_TL_UNIMOVIL = $tipo_lic " +
                " WHERE $COLUMN_EQUIPO_TL_ID = $id_equipo"


        db.execSQL(sql)

    }

    fun updateEquipos(equipos: Negocios_List) {

        var idAccionMonitoreable:Long = 0
        var modeloComunicacion: Long = 0
        var wakeOn: Long = 0
        if(equipos.id_accion_monitoreable != null){
            idAccionMonitoreable = equipos.id_accion_monitoreable.toLong()
        }

        if(equipos.modelo_comunicacion != null){
            modeloComunicacion = equipos.modelo_comunicacion.toLong()
        }

        if(equipos.wake_on != null){
            wakeOn = equipos.wake_on.toLong()
        }

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_EQUIPOS " +
                "SET " +
                "$COLUMN_NOMBRE = '${equipos.nombre}'," +
                "$COLUMN_IP_PUBLICA_ = '${equipos.ip_publica}', $COLUMN_IP_LOCAL = '${equipos.ip_local}'," +
                "$COLUMN_PUERTO_LOCAL = ${equipos.puerto_local.toLong()}, $COLUMN_PUERTO = ${equipos.puerto.toLong()}," +
                "$COLUMN_BOVEDA_SOFTWARE = '${equipos.boveda_software}', $COLUMN_RUTA_ARCHIVOS_LOCALES = '${equipos.ruta_archivos_locales}'," +
                "$COLUMN_HARDWARE_KEY_ = '${equipos.hardware_key}',$COLUMN_ESTADO_SERVIDOR = ${equipos.estado_servidor.toLong()}," +
                "$COLUMN_ESTADO_ULT_ACT = '${equipos.estado_ult_act}',$COLUMN_ICONO_NEGOCIO_ON = '${equipos.icono_negocio_on}'," +
                "$COLUMN_ICONO_NEGOCIO_OFF = '${equipos.icono_negocio_off}',$COLUMN_FEC_ACT_ESTATUS = ${equipos.fec_act_status.toLong()}," +
                "$COLUMN_CATNOMBRENEG = '${equipos.catnombreneg}',$COLUMN_ID_CATNEGOCIO = ${equipos.id_catnegocio.toLong()}," +
                "$COLUMN_TIPO_EQ_ICONO_ON = '${equipos.tipo_eq_icono_on}',$COLUMN_TIPO_EQ_ICONO_OFF = '${equipos.tipo_eq_icono_off}'," +
                "$COLUMN_TIPO_EQ_CLAVE = '${equipos.tipo_eq_clave}',$COLUMN_ID_ACCION_MONITOREABLE = ${idAccionMonitoreable}," +
                "$COLUMN_MODELO_COMUNICACION = ${modeloComunicacion}, $COLUMN_WAKE_ON = $wakeOn," +
                "$COLUMN_ESTADO_EQUIPO = '${equipos.estatus_equipo}', " +
                "$COLUMN_EQUIPO_VIRTUAL = ${equipos.equipo_virtual} " +
                " WHERE $COLUMN_ID_MOVIL_ID = ${equipos.id_movil_id.toLong()}" +
                " AND $COLUMN_EQUIPO_ID_ = ${equipos.id_negocio_id.toLong()}"




        db.execSQL(sql)
        //db.close()

    }

    fun updateEstadoInactivoEquipos() {


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_EQUIPOS " +
                "SET " +
                "$COLUMN_ESTADO_SERVIDOR = 9"

        db.execSQL(sql)
        //db.close()

    }


    fun updateOrdenamientoEquipos(id_equipo: Int, posicion: Int) {


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_EQUIPOS " +
                "SET " +
                "$COLUMN_ORDENAMIENTO = $posicion" +
                " WHERE $COLUMN_EQUIPO_ID_ = $id_equipo"

        db.execSQL(sql)
        //db.close()

    }

    fun getOrdenamientoEquipos(): String {
        val qu = "SELECT $COLUMN_EQUIPO_ID_, $COLUMN_ORDENAMIENTO FROM $TABLE_EQUIPOS"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var lsOrdenamiento = ""
        try {
            var   cur = db.rawQuery(qu, arrayOf())
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val index: Int = cur.getColumnIndexOrThrow("$COLUMN_EQUIPO_ID_")
                        val idEquipo: Int = cur.getInt(index)

                        val index2: Int = cur.getColumnIndexOrThrow("$COLUMN_ORDENAMIENTO")
                        val orden: Int= cur.getInt(index2)

                        lsOrdenamiento += "$idEquipo-$orden|"
                    } while (cur.moveToNext())
                    cur.close()
                    return lsOrdenamiento
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }








    fun getEstadoEquipoID(id_equipo: Int): Int {
        val qu = "SELECT $COLUMN_ESTADO_SERVIDOR FROM $TABLE_EQUIPOS WHERE id_negocio_id  = $id_equipo"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_ESTADO_SERVIDOR")
                    val idEquipo: Int = cur.getInt(index)
                    cur.close()

                    return idEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 9
    }


    fun updateEstadoInactivoAccionesEquipos() {


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_ACCIONES_DINAMICAS " +
                "SET $COLUMN_ACCIONES_DINAMICAS_ORIGEN = 9"

        db.execSQL(sql)
        //db.close()

    }





    fun getEquipoID(nombreEquipo: String): Int {
        val qu = "SELECT id_negocio_id FROM $TABLE_EQUIPOS WHERE nombre = '$nombreEquipo'"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("id_negocio_id")
                    val idEquipo: Int = cur.getInt(index)
                    cur.close()

                    return idEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 0
    }

    fun getEquipoNombre(equipoId: Int): String {
        val qu = "SELECT nombre  FROM $TABLE_EQUIPOS WHERE id_negocio_id = $equipoId"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var  cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("nombre")
                    val nomEquipo: String = cur.getString(index)
                    cur.close()

                    return nomEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }


    //******************************************************************

    fun getEstadoEquipo(equipoId: Int): String {
        val qu = "SELECT estado_equipo  FROM $TABLE_EQUIPOS WHERE id_negocio_id = $equipoId"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var  cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("estado_equipo")
                    val stdEquipo: String = cur.getString(index)
                    cur.close()

                    return stdEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }




    //******************************************************************

    fun getEquipoIDXHardware(hardwareEquipo: String): Int {
        val qu = "SELECT id_negocio_id FROM $TABLE_EQUIPOS WHERE $COLUMN_HARDWARE_KEY_ = '$hardwareEquipo'"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("id_negocio_id")
                    val idEquipo: Int = cur.getInt(index)
                    cur.close()

                    return idEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 0
    }



    fun getEquipoIDHardwareXID(idEquipo: String): String {
        val qu = "SELECT $COLUMN_HARDWARE_KEY_  FROM $TABLE_EQUIPOS WHERE id_negocio_id = $idEquipo"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_HARDWARE_KEY_")
                    val iHardwareEquipo: String = cur.getString(index)
                    cur.close()

                    return iHardwareEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }


    fun getIDHardwareXNombreEquipo(NomEquipo: String): String {
        val qu = "SELECT $COLUMN_HARDWARE_KEY_  FROM $TABLE_EQUIPOS WHERE nombre  = '$NomEquipo'"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_HARDWARE_KEY_")
                    val iHardwareEquipo: String = cur.getString(index)
                    cur.close()

                    return iHardwareEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }










    fun getEquipoTipoComunicacion(id_equipo:  Int): Int {
        val qu = "SELECT $COLUMN_MODELO_COMUNICACION FROM $TABLE_EQUIPOS WHERE $COLUMN_EQUIPO_ID_ = $id_equipo"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("modelo_comunicacion")
                    val idEquipo: Int = cur.getInt(index)
                    cur.close()

                    return idEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 0
    }


    fun getEquipoTipoComunicacionXHardware(hardware_equipo:  String): Int {
        val qu = "SELECT $COLUMN_MODELO_COMUNICACION FROM $TABLE_EQUIPOS WHERE $COLUMN_HARDWARE_KEY_ = '$hardware_equipo'"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("modelo_comunicacion")
                    val idEquipo: Int = cur.getInt(index)
                    cur.close()

                    return idEquipo
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 0
    }


    //********

    fun getEquipoXHardware(hardware_equipo:  String): List<Negocios_parconexion_List>  {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_NOMBRE, COLUMN_IP_PUBLICA_, COLUMN_IP_LOCAL, COLUMN_PUERTO_LOCAL,
            COLUMN_PUERTO)

        // sorting orders
        val sortOrder = "$COLUMN_ORDENAMIENTO ASC"

        val cursor: Cursor
        val infoList = ArrayList<Negocios_parconexion_List>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selection = "$COLUMN_ESTADO_SERVIDOR < ?"
        val selectionArgs = arrayOf("9")
        cursor = db.query(TABLE_EQUIPOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)



        // query the user table
        //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = Negocios_parconexion_List(
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)).toString(),
                    ip_publica = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_PUBLICA_))
                        .toString(),
                    ip_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_LOCAL)).toString(),
                    puerto_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUERTO_LOCAL))
                        .toInt(),
                    puerto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUERTO)).toInt()

                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }






    //********


    fun GetListaEquiposHeardeBar(id_negocio: Int):  List<Cat_Negocios_list> {
        val columns = arrayOf(COLUMN_EQUIPO_ID_, COLUMN_NOMBRE)


        val infoList = ArrayList<Cat_Negocios_list>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val selectionArgs = arrayOf("9")
        var selection = "$COLUMN_ESTADO_SERVIDOR < ?"
        if(id_negocio > 0){
            selection += " AND id_catnegocio = $id_negocio"
        }
        infoList.add(
            Cat_Negocios_list(
                "Todos",
                "Todos"
            )
        )




        // query the user table
        val cursor = db.query(TABLE_EQUIPOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            "$COLUMN_EQUIPO_ID_ DESC")         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = Cat_Negocios_list(
                    id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID_)).toString(),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)).toString()
                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }





    @SuppressLint("Range")
    fun getListEquipos(id_negocio: Int): List<Negocios_List> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ID_MOVIL_ID, COLUMN_EQUIPO_ID_, COLUMN_NOMBRE, COLUMN_IP_PUBLICA_, COLUMN_IP_LOCAL, COLUMN_PUERTO_LOCAL,
            COLUMN_PUERTO, COLUMN_BOVEDA_SOFTWARE, COLUMN_RUTA_ARCHIVOS_LOCALES, COLUMN_HARDWARE_KEY_, COLUMN_ESTADO_SERVIDOR,
            COLUMN_ESTADO_ULT_ACT, COLUMN_ICONO_NEGOCIO_ON, COLUMN_ICONO_NEGOCIO_OFF, COLUMN_FEC_ACT_ESTATUS,
            COLUMN_CATNOMBRENEG, COLUMN_ID_CATNEGOCIO, COLUMN_TIPO_EQ_ICONO_ON, COLUMN_TIPO_EQ_ICONO_OFF, COLUMN_TIPO_EQ_CLAVE,
            COLUMN_ID_ACCION_MONITOREABLE, COLUMN_MODELO_COMUNICACION, COLUMN_WAKE_ON, COLUMN_ESTADO_EQUIPO, COLUMN_ORDENAMIENTO, COLUMN_EQUIPO_VIRTUAL)

        // sorting orders
        val sortOrder = "$COLUMN_ORDENAMIENTO, $COLUMN_ESTADO_EQUIPO DESC"

        val cursor: Cursor
        val infoList = ArrayList<Negocios_List>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        if(id_negocio > 0){
            val selection = "$COLUMN_ID_CATNEGOCIO = ? AND $COLUMN_ESTADO_SERVIDOR < ?"
            val selectionArgs = arrayOf(id_negocio.toString(), "9")
            cursor = db.query(TABLE_EQUIPOS, //Table to query
                columns,            //columns to return
                selection,     //columns for the WHERE clause
                selectionArgs,  //The values for the WHERE clause
                null,      //group the rows
                null,       //filter by row groups
                sortOrder)
        }else{
            val selection = "$COLUMN_ESTADO_SERVIDOR < ?"
            val selectionArgs = arrayOf("9")
            cursor = db.query(TABLE_EQUIPOS, //Table to query
                columns,            //columns to return
                selection,     //columns for the WHERE clause
                selectionArgs,  //The values for the WHERE clause
                null,      //group the rows
                null,       //filter by row groups
                sortOrder)
        }








        // query the user table
        //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = Negocios_List(
                    id_movil_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_MOVIL_ID))
                        .toInt(),
                    id_negocio_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID_))
                        .toInt(),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)).toString(),
                    ip_publica = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_PUBLICA_))
                        .toString(),
                    ip_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IP_LOCAL)).toString(),
                    puerto_local = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUERTO_LOCAL))
                        .toInt(),
                    puerto = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PUERTO)).toInt(),
                    boveda_software = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BOVEDA_SOFTWARE))
                        .toString(),
                    ruta_archivos_locales = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_RUTA_ARCHIVOS_LOCALES
                        )
                    ).toString(),
                    hardware_key = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARDWARE_KEY_))
                        .toString(),
                    estado_servidor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_SERVIDOR))
                        .toInt(),
                    estado_ult_act = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_ULT_ACT))
                        .toString(),
                    icono_negocio_on = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ICONO_NEGOCIO_ON
                        )
                    ).toString(),
                    icono_negocio_off = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ICONO_NEGOCIO_OFF
                        )
                    ).toString(),
                    fec_act_status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FEC_ACT_ESTATUS))
                        .toInt(),
                    catnombreneg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATNOMBRENEG))
                        .toString(),
                    id_catnegocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID_CATNEGOCIO))
                        .toInt(),
                    tipo_eq_icono_on = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_TIPO_EQ_ICONO_ON
                        )
                    ).toString(),
                    tipo_eq_icono_off = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_TIPO_EQ_ICONO_OFF
                        )
                    ).toString(),
                    tipo_eq_clave = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO_EQ_CLAVE))
                        .toString(),
                    id_accion_monitoreable = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ID_ACCION_MONITOREABLE
                        )
                    ).toInt(),
                    modelo_comunicacion = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_MODELO_COMUNICACION
                        )
                    ).toInt(),
                    wake_on = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_WAKE_ON)).toInt(),
                    unimovil = 0,
                    gc_project_vm = "",
                    gc_project_screenshot = 0,
                    estatus_equipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ESTADO_EQUIPO))
                        .toString(),
                    ordenamiento_equipos = cursor.getString(cursor.getColumnIndexOrThrow(
                        COLUMN_ORDENAMIENTO))
                        .toString(),
                    equipo_virtual = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_VIRTUAL)).toInt()


                )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }


    @SuppressLint("Range")
    fun getListEquiposAll(): List<Negocios_List_All> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_EQUIPO_ID_, COLUMN_NOMBRE, COLUMN_ID_ACCION_MONITOREABLE, COLUMN_HARDWARE_KEY_)

        // sorting orders
        val sortOrder = "$COLUMN_EQUIPO_ID_ ASC"

        val cursor: Cursor
        val infoList = ArrayList<Negocios_List_All>()
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val selection = "$COLUMN_ESTADO_SERVIDOR < ?"
        val selectionArgs = arrayOf("9")
        cursor = db.query(TABLE_EQUIPOS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            sortOrder)

        if (cursor.moveToFirst()) {
            do {
                val info = Negocios_List_All(
                    id_negocio_id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EQUIPO_ID_))
                        .toInt(),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)).toString(),
                    id_accion_monitoreable = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                            COLUMN_ID_ACCION_MONITOREABLE
                        )
                    ).toInt(),
                    hardware_key = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HARDWARE_KEY_))
                        .toString()
                )
                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }

















    fun getEquipoTipoLic(id_equipo: Int): Int {
        val qu = "SELECT $COLUMN_EQUIPO_TL_UNIMOVIL FROM $TABLE_EQUIPOS_TIPO_LIC WHERE $COLUMN_EQUIPO_TL_ID = $id_equipo"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("unimovil")
                    val iTipoLic: Int = cur.getInt(index)
                    cur.close()

                    return iTipoLic
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {
            return 0
        }
        //db.close()
        return 0
    }




    /***
     * ACCIONES DINAMICAS
     ***/


    fun addAccionesDinamicas(acciones: AcionesDinamicas_List) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_ACCIONES_DINAMICAS ($COLUMN_ACCIONES_DINAMICAS_ID," +
                "$COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID, $COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION," +
                "$COLUMN_ACCIONES_DINAMICAS_ICONO, $COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION," +
                "$COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION, $COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION," +
                "$COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA, $COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO," +
                "$COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO,$COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO," +
                "$COLUMN_ACCIONES_DINAMICAS_TIPO_ID,$COLUMN_ACCIONES_DINAMICAS_GRUPO," +
                "$COLUMN_ACCIONES_DINAMICAS_COMANDO,$COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA," +
                "$COLUMN_ACCIONES_DINAMICAS_ORIGEN,$COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION," +
                "$COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE,$COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO," +
                "$COLUMN_ACCIONES_DINAMICAS_DESCRIPCION,$COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO," +
                "$COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO,$COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD)" +
                " SELECT ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?" +
                " WHERE NOT EXISTS(SELECT 1 FROM $TABLE_ACCIONES_DINAMICAS " +
                " WHERE $COLUMN_ACCIONES_DINAMICAS_ID = ${acciones.id} " +
                " AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = ${acciones.id_negocio_id})"

        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt?.bindLong(1, acciones.id.toLong())
        insertStmt?.bindLong(2, acciones.id_negocio_id.toLong())
        insertStmt?.bindString(3, acciones.alias_aplicacion)
        insertStmt?.bindString(4, acciones.icono)
        insertStmt?.bindString(5, acciones.aplicacion_ruta_aplicacion)
        insertStmt?.bindString(6, acciones.aplicacion_nombre_aplicacion)
        insertStmt?.bindString(7, acciones.aplicacion_parametros_aplicacion)
        insertStmt?.bindString(8, acciones.exploracion_ruta)
        insertStmt?.bindString(9, acciones.eliminacion_tipo)
        insertStmt?.bindString(10, acciones.eliminacion_nombre_archivo)
        insertStmt?.bindString(11, acciones.eliminacion_ruta_archivo)
        insertStmt?.bindLong(12, acciones.tipo_id.toLong())
        insertStmt?.bindLong(13, acciones.grupo.toLong())
        insertStmt?.bindString(14, acciones.comando)
        insertStmt?.bindString(15, acciones.fecha_alta)
        insertStmt?.bindLong(16, acciones.origen.toLong())
        insertStmt?.bindString(17, acciones.titulo_aplicacion)
        insertStmt?.bindString(18, acciones.estado_programa_nombre)
        insertStmt?.bindString(19, acciones.screenshot_tipo)
        insertStmt?.bindString(20, acciones.descripcion)
        insertStmt?.bindString(21, acciones.nombre_equipo)
        insertStmt?.bindString(22, acciones.aplicacion_usuario)
        insertStmt?.bindString(23, acciones.aplicacion_pwd)

        insertStmt!!.executeInsert()
        //db.close()
    }


    fun CheckExisteAccionesDinamicas(id_accion: Int, id_equipo: Int): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_ACCIONES_DINAMICAS WHERE $COLUMN_ACCIONES_DINAMICAS_ID = $id_accion" +
                " AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = $id_equipo AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID NOT IN " +
                "(SELECT id_negocio_id FROM equipos WHERE id_negocio_id = $id_equipo AND  estado_servidor = 9)"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }

    fun updAccionesDinamicas(acciones: AcionesDinamicas_List) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_ACCIONES_DINAMICAS " +
                "SET $COLUMN_ACCIONES_DINAMICAS_ID = ${acciones.id}, " +
                "$COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = ${acciones.id_negocio_id}, " +
                "$COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION = '${acciones.alias_aplicacion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_ICONO = '${acciones.icono}', " +
                "$COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION = '${acciones.aplicacion_ruta_aplicacion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION = '${acciones.aplicacion_nombre_aplicacion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION = '${acciones.aplicacion_parametros_aplicacion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA = '${acciones.exploracion_ruta}'," +
                "$COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO = '${acciones.eliminacion_tipo}', " +
                "$COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO = '${acciones.eliminacion_nombre_archivo}', " +
                "$COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO = '${acciones.eliminacion_ruta_archivo}', " +
                "$COLUMN_ACCIONES_DINAMICAS_TIPO_ID = ${acciones.tipo_id}, " +
                "$COLUMN_ACCIONES_DINAMICAS_GRUPO = ${acciones.grupo}, " +
                "$COLUMN_ACCIONES_DINAMICAS_COMANDO = '${acciones.comando}', " +
                "$COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA = '${acciones.fecha_alta}', " +
                "$COLUMN_ACCIONES_DINAMICAS_ORIGEN = ${acciones.origen}, " +
                "$COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION = '${acciones.titulo_aplicacion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE = '${acciones.estado_programa_nombre}', " +
                "$COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO = '${acciones.screenshot_tipo}', " +
                "$COLUMN_ACCIONES_DINAMICAS_DESCRIPCION = '${acciones.descripcion}', " +
                "$COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO = '${acciones.nombre_equipo}', " +
                "$COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO = '${acciones.aplicacion_usuario}'," +
                "$COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD = '${acciones.aplicacion_pwd}' " +
                " WHERE $COLUMN_ACCIONES_DINAMICAS_ID = ${acciones.id} " +
                " AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = ${acciones.id_negocio_id}"




        db.execSQL(sql)
        //db.close()

    }


    @SuppressLint("Range")
    fun getListAccionesDinamicas(): List<AcionesDinamicas_List> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACCIONES_DINAMICAS_ID, COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID, COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_ICONO, COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION, COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA, COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO,
            COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO, COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO,
            COLUMN_ACCIONES_DINAMICAS_TIPO_ID, COLUMN_ACCIONES_DINAMICAS_GRUPO,
            COLUMN_ACCIONES_DINAMICAS_COMANDO, COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA,
            COLUMN_ACCIONES_DINAMICAS_ORIGEN, COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE, COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO,
            COLUMN_ACCIONES_DINAMICAS_DESCRIPCION, COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO,
            COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO, COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD)




        val infoList = ArrayList<AcionesDinamicas_List>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val selection = "$COLUMN_ACCIONES_DINAMICAS_ORIGEN NOT IN (?) AND $COLUMN_ACCIONES_DINAMICAS_GRUPO NOT IN (2)"

        // selection arguments
        val selectionArgs = arrayOf("9")


        val cursor = db.query(TABLE_ACCIONES_DINAMICAS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    AcionesDinamicas_List(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCIONES_DINAMICAS_ID))
                            .toInt(),
                        id_negocio_id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID
                            )
                        ).toInt(),
                        alias_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION
                            )
                        ),
                        icono = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ICONO
                            )
                        ),
                        aplicacion_ruta_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION
                            )
                        ),
                        aplicacion_nombre_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION
                            )
                        ),
                        aplicacion_parametros_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION
                            )
                        ),
                        exploracion_ruta = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA
                            )
                        ),
                        eliminacion_tipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO
                            )
                        ),
                        eliminacion_nombre_archivo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO
                            )
                        ),
                        eliminacion_ruta_archivo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO
                            )
                        ),
                        tipo_id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_TIPO_ID
                            )
                        ).toInt(),
                        grupo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_GRUPO
                            )
                        ).toInt(),
                        comando = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_COMANDO
                            )
                        ),
                        fecha_alta = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA
                            )
                        ),
                        origen = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ORIGEN
                            )
                        ).toInt(),
                        titulo_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION
                            )
                        ),
                        estado_programa_nombre = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE
                            )
                        ),
                        screenshot_tipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO
                            )
                        ),
                        descripcion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_DESCRIPCION
                            )
                        ),
                        nombre_equipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO
                            )
                        ),
                        aplicacion_usuario = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO
                            )
                        ),
                        aplicacion_pwd = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD
                            )
                        )
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }


    fun getListAccionesDinamicas_Panel(id_negocio: Int, id_equipo: Int): List<AcionesDinamicas_List> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACCIONES_DINAMICAS_ID, COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID, COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_ICONO, COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION, COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA, COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO,
            COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO, COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO,
            COLUMN_ACCIONES_DINAMICAS_TIPO_ID, COLUMN_ACCIONES_DINAMICAS_GRUPO,
            COLUMN_ACCIONES_DINAMICAS_COMANDO, COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA,
            COLUMN_ACCIONES_DINAMICAS_ORIGEN, COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION,
            COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE, COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO,
            COLUMN_ACCIONES_DINAMICAS_DESCRIPCION, COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO,
            COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO, COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD)




        val infoList = ArrayList<AcionesDinamicas_List>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        var selection = "$COLUMN_ACCIONES_DINAMICAS_GRUPO = ? AND $COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO NOT IN (?,?,?) AND $COLUMN_ACCIONES_DINAMICAS_ORIGEN NOT IN (?)   "

        if(id_negocio > 0){
            selection += " AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID IN (SELECT id_negocio_id FROM equipos WHERE id_catnegocio = $id_negocio)"
        }
        if(id_equipo > 0){
            selection += " AND $COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = $id_equipo"
        }
        // selection arguments
        val selectionArgs = arrayOf("1", "sc_inicio_sesion", "sc_intervalo", "sc_ejecutar_programa", "9")


        // query the user table
        val cursor = db.query(TABLE_ACCIONES_DINAMICAS, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    AcionesDinamicas_List(
                        id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ACCIONES_DINAMICAS_ID))
                            .toInt(),
                        id_negocio_id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID
                            )
                        ).toInt(),
                        alias_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION
                            )
                        ),
                        icono = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ICONO
                            )
                        ),
                        aplicacion_ruta_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION
                            )
                        ),
                        aplicacion_nombre_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION
                            )
                        ),
                        aplicacion_parametros_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION
                            )
                        ),
                        exploracion_ruta = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA
                            )
                        ),
                        eliminacion_tipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO
                            )
                        ),
                        eliminacion_nombre_archivo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO
                            )
                        ),
                        eliminacion_ruta_archivo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO
                            )
                        ),
                        tipo_id = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_TIPO_ID
                            )
                        ).toInt(),
                        grupo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_GRUPO
                            )
                        ).toInt(),
                        comando = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_COMANDO
                            )
                        ),
                        fecha_alta = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA
                            )
                        ),
                        origen = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ORIGEN
                            )
                        ).toInt(),
                        titulo_aplicacion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION
                            )
                        ),
                        estado_programa_nombre = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE
                            )
                        ),
                        screenshot_tipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO
                            )
                        ),
                        descripcion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_DESCRIPCION
                            )
                        ),
                        nombre_equipo = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO
                            )
                        ),
                        aplicacion_usuario = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO
                            )
                        ),
                        aplicacion_pwd = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD
                            )
                        )
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }



    //*******





    fun delControlVistasTagGraficoMonitoreable() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES")
        //db.close()
    }


    fun delEquiposNoAsignadosAMovil(idMovil: Int){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("DELETE FROM $TABLE_EQUIPOS WHERE id_movil_id NOT IN ($idMovil)")
    }

    fun addControlVistasTagGraficoMonitoreable(id_accion: Int, tag_vista_grafico: Int, hardware: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES (control_accion_vista_monitor_id,  control_accion_vista_monitor_tag, control_accion_vista_monitor_hardware) VALUES(?,?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindLong(1, id_accion.toLong())
        insertStmt!!.bindLong(2, tag_vista_grafico.toLong())
        insertStmt!!.bindString(3, hardware)
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun CheckVistasTagGraficoMonitoreable(id_accion: Int, tag_vista_grafico: Int, hardware: String): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES WHERE control_accion_vista_monitor_id = $id_accion AND control_accion_vista_monitor_tag = $tag_vista_grafico AND control_accion_vista_monitor_hardware = '$hardware'"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }




    fun getControlVistasTagGraficoMonitoreables(): List<DataControlVistasMonitoreablesTag> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_ACCION_VISTA_MONITOR_ID, COLUMN_ACCION_VISTA_MONITOR_TAG, COLUMN_ACCION_VISTA_MONITOR_HARDWARE)

        // sorting orders
        //val sortOrder = "$COLUMN_CONTROL_ALERTA_FAV_ID ASC"



        val infoList = ArrayList<DataControlVistasMonitoreablesTag>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table
        val cursor = db.query(TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info =
                    DataControlVistasMonitoreablesTag(
                        id_accion = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_VISTA_MONITOR_ID
                            )
                        ).toInt(),
                        tag_vista_grafico = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_VISTA_MONITOR_TAG
                            )
                        ).toInt(),
                        hardware = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_ACCION_VISTA_MONITOR_HARDWARE
                            )
                        ).toString()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }


    /***
     *
     *
     */

    //Seguridad Biometrica///



    fun addSeguridadBiometrica(tipo_acceso: String, fecha_acceso: String, estatus_acceso: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_SEGURIDAD_BIOMETRIA ($COLUMN_BIOMETRIA_TIPO_ACCESO, $COLUMN_BIOMETRIA_FECHA_ACCESO, $COLUMN_BIOMETRIA_ESTATUS_ACCESO) VALUES(?,?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindString(1, tipo_acceso)
        insertStmt!!.bindString(2, fecha_acceso)
        insertStmt!!.bindString(3, estatus_acceso)
        insertStmt!!.executeInsert()
        //db.close()
    }



    fun CheckAccesosFallidosBiometria(): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_SEGURIDAD_BIOMETRIA WHERE $COLUMN_BIOMETRIA_ESTATUS_ACCESO = 'authentication_error'"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 3) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }


    fun DeleteAccesosFallidosBiometria(){
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_SEGURIDAD_BIOMETRIA WHERE $COLUMN_BIOMETRIA_ESTATUS_ACCESO = 'authentication_error'")
        //db.close()
    }



    /**
     * This method is to create user record
     *
     * @param user
     */






    fun addUserAvatar(avatar_img: Bitmap) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_USER_AVATAR ($COLUMN_USER_AVATAR) VALUES(?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val imgByte: ByteArray = getBitmapAsByteArray(avatar_img)
        val bytes = ByteArrayOutputStream()




        //var bimgi: Bitmap=BitmapFactory.decodeFile(selectedImageUri.path)
        //bimgi.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        //val byteArrayi: ByteArray = bytes.toByteArray()

        // var bimg: Bitmap= BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size)
        //bimg.setHasAlpha(true)
        //val redColorValue = Color.RED
        //bimg = replaceColor(bimg, redColorValue)!!
        avatar_img.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val byteArray: ByteArray = bytes.toByteArray()





        insertStmt!!.bindBlob(1, byteArray)
        insertStmt!!.executeInsert();
        //db.close()
    }



    private fun replaceColor(src: Bitmap?, targetColor: Int): Bitmap? {
        if (src == null) {
            return null
        }
        // Source image size
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        for (x in pixels.indices) {
            if(pixels[x] == -16711423) {
                pixels[x] = 0
            }
        }
        // create result bitmap output
        val result = Bitmap.createBitmap(width, height, src.config)
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    fun deleteAvatarUser() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_USER_AVATAR")
        //db.close()


    }


    fun getAvatarUser(): Bitmap? {
        val qu = "SELECT * FROM $TABLE_USER_AVATAR"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("user_avatar")
                    val imgByte: ByteArray = cur.getBlob(index)
                    cur.close()

                    val options = BitmapFactory.Options()
                    options.inScreenDensity
                    return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size, options)


                }
                if (cur != null && !cur.isClosed()) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return null
    }




    fun addUser(user: User) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)


        // Inserting Row
        db.insert(TABLE_USER, null, values)
        //db.close()
    }


    fun getUserName(): String? {
        val qu = "SELECT * FROM $TABLE_USER"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("user_name")
                    val username: String= cur.getString(index)
                    cur.close()

                    return username
                }
                if (cur != null && !cur.isClosed()) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return ""
    }


    /**Monitores*/

    fun addTableMonitores(id_movil: Int, idaccion: Int, origen: Int ,comando: String, accion: String, porcentaje: String, total_usado: String, espacio_libre: String,
                          paquetes_enviados: String, paquetes_recibidos: String,nomServidor: String, redNegocio:String, esFavorito: Boolean, fecUltAct: String, idEquipo: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_MONITORES_ID_MOVIL, id_movil)
        values.put(COLUMN_MONITORES_ID_ACCION, idaccion)
        values.put(COLUMN_MONITORES_ORIGEN, origen)
        values.put(COLUMN_MONITORES_COMANDO, comando)
        values.put(COLUMN_MONITORES_ACCION, accion)
        values.put(COLUMN_MONITORES_PORCENAJE, porcentaje)
        values.put(COLUMN_MONITORES_TOTAL_USADO, total_usado)
        values.put(COLUMN_MONITORES_ESACIO_LIBRE, espacio_libre)
        values.put(COLUMN_MONITORES_PAQUETES_ENVIADOS, paquetes_enviados)
        values.put(COLUMN__MONITORES_PAQUETES_RECIBIDOS, paquetes_recibidos)
        values.put(COLUMN_MONITORES_RED_NEGOCIO, redNegocio)
        values.put(COLUMN_MONITORES_NOMBRE_SERVIDOR, nomServidor)
        values.put(COLUMN_MONITORES_ES_FAVORITO,  esFavorito)
        values.put(COLUMN_MONITORES_FEC_ULT_ACT, fecUltAct)
        values.put(COLUMN_MONITORES_ID_EQUIPO, idEquipo)


        // Inserting Row
        db.insert(TABLE_CONTROL_MONITORES, null, values)
        //db.close()
    }


    @SuppressLint("SuspiciousIndentation")
    fun updateTableMonitores(id_movil: Int, idaccion: Int, origen: Int, comando: String, accion: String, porcentaje: String, total_usado: String, espacio_libre: String,
                             paquetes_enviados: String, paquetes_recibidos: String, nomServidor: String, redNegocio:String, esFavorito: Boolean, fecUltAct: String, idEquipo: Int) {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_MONITORES " +
                "SET " +
                "$COLUMN_MONITORES_ORIGEN = $origen," +
                "$COLUMN_MONITORES_COMANDO = '$comando'," +
                "$COLUMN_MONITORES_PORCENAJE = '$porcentaje'," +
                "$COLUMN_MONITORES_TOTAL_USADO = '$total_usado'," +
                "$COLUMN_MONITORES_ESACIO_LIBRE = '$espacio_libre'," +
                "$COLUMN_MONITORES_PAQUETES_ENVIADOS = '$paquetes_enviados'," +
                "$COLUMN__MONITORES_PAQUETES_RECIBIDOS = '$paquetes_recibidos'," +
                "$COLUMN_MONITORES_RED_NEGOCIO = '$redNegocio'," +
                "$COLUMN_MONITORES_NOMBRE_SERVIDOR = '$nomServidor'," +
                "$COLUMN_MONITORES_ES_FAVORITO = '$esFavorito'," +
                "$COLUMN_MONITORES_FEC_ULT_ACT = '$fecUltAct' " +
                "WHERE $COLUMN_MONITORES_ID_MOVIL = $id_movil " +
                "AND $COLUMN_MONITORES_ID_ACCION = $idaccion " +
                "AND $COLUMN_MONITORES_ACCION = '$accion' " +
                "AND $COLUMN_MONITORES_ID_EQUIPO = $idEquipo"


        db.execSQL(sql)
        //db.close()

    }


    fun updateTableMonitoresFav(id_movil: Int, idaccion: Int, origen: Int ,comando: String, accion: String, porcentaje: String, total_usado: String, espacio_libre: String,
                                paquetes_enviados: String, paquetes_recibidos: String,nomServidor: String, redNegocio:String, esFavorito: Boolean, fecUltAct: String, idEquipo: Int) {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        val sql = "UPDATE $TABLE_CONTROL_MONITOREABLES_FAVORITOS " +
                "SET " +
                "$COLUMN_ACCION_MONITOREABLE_PORCENTAJE = '$porcentaje'," +
                "$COLUMN_ACCION_MONITOREABLE_ESPACIO_TOTAL = '$total_usado'," +
                "$COLUMN_ACCION_MONITOREABLE_ESPACIO_LIBRE = '$espacio_libre'," +
                "$COLUMN_ACCION_MONITOREABLE_TOT_ENVIADOS = '$paquetes_enviados'," +
                "$COLUMN_ACCION_MONITOREABLE_TOT_RECIBIDOS = '$paquetes_recibidos' " +
                "WHERE $COLUMN_ACCION_MONITOREABLE_ID = $idaccion " +
                "AND $COLUMN_ACCION_MONITOREABLE_HARDWARE = '$accion' " +
                "AND $COLUMN_ACCION_MONITOREABLE_ID_EQUIPO = $idEquipo"

        db.execSQL(sql)
        //db.close()

    }



    fun depuraBaseLocalMonitoresNoValidos() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_MONITORES  WHERE accion NOT  IN ('RED', 'MEMORIA', 'DISCO', 'CPU', 'C:', 'E:', 'F:', 'G:', 'H:')")
        //db.close()
    }


    fun depuraBaseLocalMonitoresNoValidos_V2() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_MONITORES   WHERE cast(porcentaje as decimal) <= 0 AND cast(total_usado as decimal) <=0 AND cast(espacio_libre as decimal) <=0 AND paquetes_enviados = '' AND paquetes_recibidos = ''")
        //db.close()
    }




    fun CheckExisteTableMonitores(id_movil: Int, idaccion: Int, accion: String,  idEquipo: Int): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_CONTROL_MONITORES WHERE $COLUMN_MONITORES_ID_MOVIL = $id_movil " +
                "AND $COLUMN_MONITORES_ID_ACCION = $idaccion " +  "AND $COLUMN_MONITORES_ACCION = '$accion' " + "AND $COLUMN_MONITORES_ID_EQUIPO = $idEquipo"

        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        //db.close()
        return true
    }



    fun CheckVaciaTableMonitores(): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT * FROM $TABLE_CONTROL_MONITORES"

        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return true
        }
        cursor.close()
        //db.close()
        return false
    }









    /***
    DETALLE MONITORES
     */

    fun addTableDetalleMonitores(idEquipo: Int, accion: String, porcentaje: String, total_usado: String, espacio_libre: String,
                                 paquetes_enviados: String, paquetes_recibidos: String, fecUltAct: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_MONITORES_ID_EQUIPO, idEquipo)
        values.put(COLUMN_MONITORES_ACCION, accion)
        values.put(COLUMN_MONITORES_PORCENAJE, porcentaje)
        values.put(COLUMN_MONITORES_TOTAL_USADO, total_usado)
        values.put(COLUMN_MONITORES_ESACIO_LIBRE, espacio_libre)
        values.put(COLUMN_MONITORES_PAQUETES_ENVIADOS, paquetes_enviados)
        values.put(COLUMN__MONITORES_PAQUETES_RECIBIDOS, paquetes_recibidos)
        values.put(COLUMN_MONITORES_FEC_ULT_ACT, fecUltAct)

        // Inserting Row
        db.insert(TABLE_CONTROL_MONITORES_DETALLE, null, values)
        //db.close()
    }


    fun deleteMonitoresDetalleXTiempo(idEquipo: Int, accion: String, intervalo: String, numregistros: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_MONITORES_DETALLE WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo" +
                " AND $COLUMN_MONITORES_ACCION  = '$accion' AND $COLUMN_MONITORES_FEC_ULT_ACT <= datetime('now', '$intervalo', 'localtime')" +
                " AND (SELECT COUNT(*) FROM $TABLE_CONTROL_MONITORES_DETALLE WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo" +
                " AND $COLUMN_MONITORES_ACCION  = '$accion') >= $numregistros")
        //db.close()

    }



    @SuppressLint("Range")
    fun getListTableDetalleMonitoreables(idEquipos: Int, accion: String): ArrayList<ItemAccionesDetalleMonitoreables> {

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_MONITORES_ID_EQUIPO,
            COLUMN_MONITORES_ACCION,
            COLUMN_MONITORES_PORCENAJE,
            COLUMN_MONITORES_TOTAL_USADO,
            COLUMN_MONITORES_ESACIO_LIBRE,
            COLUMN_MONITORES_PAQUETES_ENVIADOS,
            COLUMN__MONITORES_PAQUETES_RECIBIDOS,
            COLUMN_MONITORES_FEC_ULT_ACT
        )



        val infoList = ArrayList<ItemAccionesDetalleMonitoreables>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        var selection = "$COLUMN_MONITORES_ID_EQUIPO = ? AND $COLUMN_MONITORES_ACCION = ?"
        val selectionArgs = arrayOf("$idEquipos", accion)


        // query the user table
        val cursor = db.query(
            TABLE_CONTROL_MONITORES_DETALLE, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null
        )         //The sort order
        if (cursor.moveToFirst()) {
            do {


                val info = ItemAccionesDetalleMonitoreables( idEquipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ID_EQUIPO)).toInt(),
                    accion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ACCION)).toString(),
                    porcentaje = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_PORCENAJE)).toString(),
                    total_usado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_TOTAL_USADO)).toString(),
                    espacio_libre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ESACIO_LIBRE)).toString(),
                    paquetes_enviados = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_PAQUETES_ENVIADOS)).toString(),
                    paquetes_recibidos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN__MONITORES_PAQUETES_RECIBIDOS)).toString(),
                    fecUltAct =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_FEC_ULT_ACT)).toString())

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }










    /**
     * FUNCIONES PARA SOCKET BITACORA DE SOFTWARE
     */


    fun updateTableMonitoresXBitacora( accion: String, porcentaje: String, total_usado: String, espacio_libre: String,
                                       paquetes_enviados: String, paquetes_recibidos: String, fecUltAct: String, idEquipo: Int) {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_MONITORES " +
                "SET " +
                "$COLUMN_MONITORES_PORCENAJE = '$porcentaje'," +
                "$COLUMN_MONITORES_TOTAL_USADO = '$total_usado'," +
                "$COLUMN_MONITORES_ESACIO_LIBRE = '$espacio_libre'," +
                "$COLUMN_MONITORES_PAQUETES_ENVIADOS = '$paquetes_enviados'," +
                "$COLUMN__MONITORES_PAQUETES_RECIBIDOS = '$paquetes_recibidos'," +
                "$COLUMN_MONITORES_FEC_ULT_ACT = '$fecUltAct' " +
                "WHERE $COLUMN_MONITORES_ACCION = '$accion' " +
                "AND $COLUMN_MONITORES_ID_EQUIPO = $idEquipo"

        db.execSQL(sql)
        //db.close()

    }

    /**
     *
     */




    fun getFecUltActTableMonitores( idaccion: Int, accion: String,  idEquipo: Int): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var Query = ""



        Query =  "SELECT $COLUMN_MONITORES_FEC_ULT_ACT FROM $TABLE_CONTROL_MONITORES WHERE " +
                " $COLUMN_MONITORES_ID_ACCION = $idaccion AND $COLUMN_MONITORES_ACCION = '$accion' AND $COLUMN_MONITORES_ID_EQUIPO = $idEquipo"

        val cursor = db.rawQuery(Query, null)
        var versionActSft = ""
        var i = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                versionActSft = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_FEC_ULT_ACT"))
                i++
            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return versionActSft
    }





    fun getListTableMonitoreables(idNegocios: Int, idEquipos: Int): ArrayList<com.apptomatico.app_movil_kotlin_v3.monitoreables.ui.model.ItemAccionesMonitoreables> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_MONITORES_ID_MOVIL,
            COLUMN_MONITORES_ID_ACCION,
            COLUMN_MONITORES_ORIGEN,
            COLUMN_MONITORES_COMANDO,
            COLUMN_MONITORES_ACCION,
            COLUMN_MONITORES_PORCENAJE,
            COLUMN_MONITORES_TOTAL_USADO,
            COLUMN_MONITORES_ESACIO_LIBRE,
            COLUMN_MONITORES_PAQUETES_ENVIADOS,
            COLUMN__MONITORES_PAQUETES_RECIBIDOS,
            COLUMN_MONITORES_RED_NEGOCIO,
            COLUMN_MONITORES_NOMBRE_SERVIDOR,
            COLUMN_MONITORES_ES_FAVORITO,
            COLUMN_MONITORES_FEC_ULT_ACT,
            COLUMN_MONITORES_ID_EQUIPO)



        val infoList = ArrayList<com.apptomatico.app_movil_kotlin_v3.monitoreables.ui.model.ItemAccionesMonitoreables>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        var selection = "$COLUMN_MONITORES_ID_EQUIPO NOT IN ( SELECT $COLUMN_MONITORES_ID_EQUIPO  FROM  $TABLE_CONTROL_MONITORES WHERE  cast(porcentaje as decimal) <= 0 AND cast(total_usado as decimal) <=0 AND cast(espacio_libre as decimal) <=0 AND paquetes_enviados = '' AND paquetes_recibidos = '') "

        if (idNegocios != 0){
            if(idEquipos != 0){
                selection += " AND $COLUMN_MONITORES_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where $COLUMN_EQUIPO_ID_ = $idEquipos AND  id_catnegocio = $idNegocios and estado_servidor <> 9) AND $COLUMN_MONITORES_ID_ACCION IN (select c.id from acciones_dinamicas c where  c.id = $COLUMN_MONITORES_ID_ACCION and c.origen not in (9))"

            }else{
                selection += " AND $COLUMN_MONITORES_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where id_catnegocio = $idNegocios and estado_servidor <> 9) AND $COLUMN_MONITORES_ID_ACCION IN (select c.id from acciones_dinamicas c where  c.id = $COLUMN_MONITORES_ID_ACCION and c.origen not in (9))"
            }

        }else{
            if(idEquipos != 0){
                selection += "AND $COLUMN_MONITORES_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where  $COLUMN_EQUIPO_ID_ = $idEquipos AND estado_servidor <> 9) AND $COLUMN_MONITORES_ID_ACCION IN (select c.id from acciones_dinamicas c where c.id = $COLUMN_MONITORES_ID_ACCION and c.origen not in (9))"

            }else{
                selection += " AND $COLUMN_MONITORES_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where estado_servidor <> 9) AND $COLUMN_MONITORES_ID_ACCION IN (select c.id from acciones_dinamicas c where c.id = $COLUMN_MONITORES_ID_ACCION and c.origen not in (9))"

            }

        }




        // selection arguments



        // query the user table
        val cursor = db.query(TABLE_CONTROL_MONITORES, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            COLUMN_MONITORES_ID_EQUIPO + " ASC")         //The sort order
        if (cursor.moveToFirst()) {
            do {
                var iRed:  MutableList<RedEstatusServer>? =  ArrayList()
                var iFav = false

                var iaccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ACCION)).toString()
                var valuesRed = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_RED_NEGOCIO)).toString()
                if (iaccion == "RED" && valuesRed != ""){

                    var iresult = valuesRed.split("|")
                    iRed?.add(
                        RedEstatusServer(
                            nombre = "${iresult[0]}",
                            enviados = "${iresult[1]}",
                            recibidos = "${iresult[2]}"
                        )
                    )
                }
                var valueFav = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ES_FAVORITO)).toString()
                if(valueFav == "1" || valueFav == "true"){
                    iFav = true
                }




                val info = com.apptomatico.app_movil_kotlin_v3.monitoreables.ui.model.ItemAccionesMonitoreables(id_movil = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ID_MOVIL)).toInt(),
                    idaccion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ID_ACCION)).toInt(),
                    origen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ORIGEN)).toInt(),
                    comando = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_COMANDO)).toString(),
                    accion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ACCION)).toString(),
                    porcentaje = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_PORCENAJE)).toString(),
                    total_usado = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_TOTAL_USADO)).toString(),
                    espacio_libre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ESACIO_LIBRE)).toString(),
                    paquetes_enviados = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_PAQUETES_ENVIADOS)).toString(),
                    paquetes_recibidos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN__MONITORES_PAQUETES_RECIBIDOS)).toString(),
                    nomServidor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_NOMBRE_SERVIDOR)).toString(),
                    redNegocio = iRed,
                    esFavorito = iFav,
                    fecUltAct =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_FEC_ULT_ACT)).toString(),
                    idEquipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONITORES_ID_EQUIPO)).toInt())

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }




    @SuppressLint("Range")
    fun getMetricaGraficoCPU(idEquipo: Int, idAccion: Int): FecMetricas? {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_MONITORES_PORCENAJE, $COLUMN_MONITORES_FEC_ULT_ACT FROM $TABLE_CONTROL_MONITORES WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo AND" +
                " $COLUMN_MONITORES_ID_ACCION = $idAccion AND $COLUMN_MONITORES_ACCION = 'CPU'"
        val cursor = db.rawQuery(Query, null)
        var fechaConsulta = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        fechaConsulta  = ""
        var porcentajeuso = ""
        var info: FecMetricas? = null
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                porcentajeuso  = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_PORCENAJE")).toString()
                fechaConsulta = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_FEC_ULT_ACT"))


            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        var year_Hasta = 0
        var month_Hasta = ""
        var day_Hasta = ""
        var Horas_Hasta = ""
        var Minutos_Hasta = ""
        var Segundos_Hasta = ""
        if(fechaConsulta != ""){


            val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fechaConsulta)
            val calender2 = Calendar.getInstance()
            calender2.time = convertedTodayDateHasta
            year_Hasta = calender2.get(Calendar.YEAR)
            when (calender2.get(Calendar.MONTH)) {
                0 -> month_Hasta = "01"
                1 -> month_Hasta = "02"
                2 -> month_Hasta = "03"
                3 -> month_Hasta = "04"
                4 -> month_Hasta = "05"
                5 -> month_Hasta = "06"
                6 -> month_Hasta = "07"
                7 -> month_Hasta = "08"
                8 -> month_Hasta = "09"
                9 -> month_Hasta = "10"
                10 -> month_Hasta = "11"
                else -> { // Note the block
                    month_Hasta = "12"
                }
            }


            day_Hasta = when (calender2.get(Calendar.DAY_OF_MONTH)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.DAY_OF_MONTH).toString()
                }
            }


            Horas_Hasta = when (calender2.get(Calendar.HOUR_OF_DAY)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.HOUR_OF_DAY).toString()
                }
            }


            Minutos_Hasta = when (calender2.get(Calendar.MINUTE)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.MINUTE).toString()
                }
            }


            Segundos_Hasta = when (calender2.get(Calendar.SECOND)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.SECOND).toString()
                }
            }

            info = FecMetricas(
                fecha = "$day_Hasta-$month_Hasta-$year_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta",
                metrica = "$porcentajeuso"
            )


        }






        return  info


    }

    @SuppressLint("Range")
    fun getMetricaGraficoMEMORIA(idEquipo: Int, idAccion: Int): FecMetricas? {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_MONITORES_PORCENAJE, $COLUMN_MONITORES_TOTAL_USADO, $COLUMN_MONITORES_ESACIO_LIBRE, $COLUMN_MONITORES_FEC_ULT_ACT FROM $TABLE_CONTROL_MONITORES WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo AND" +
                " $COLUMN_MONITORES_ID_ACCION = $idAccion AND $COLUMN_MONITORES_ACCION = 'MEMORIA'"
        val cursor = db.rawQuery(Query, null)
        var fechaConsulta = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var info: FecMetricas? = null
        fechaConsulta  = ""
        var porcentajeuso = 0
        var metricaLibre = 0
        var metricaTotal = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                porcentajeuso  = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_PORCENAJE"))
                metricaLibre  = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_ESACIO_LIBRE"))
                metricaTotal = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_TOTAL_USADO"))
                fechaConsulta = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_FEC_ULT_ACT"))


            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        val imetricaLibre = ((metricaLibre.toDouble() * 100).roundToInt() / 100.0).roundToInt()
        val imetricaTotal = ((metricaTotal.toDouble() * 100).roundToInt() / 100.0).roundToInt()

        var year_Hasta = 0
        var month_Hasta = ""
        var day_Hasta = ""
        var Horas_Hasta = ""
        var Minutos_Hasta = ""
        var Segundos_Hasta = ""
        if(fechaConsulta != ""){

            val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fechaConsulta)
            val calender2 = Calendar.getInstance()
            calender2.time = convertedTodayDateHasta
            year_Hasta = calender2.get(Calendar.YEAR)
            when (calender2.get(Calendar.MONTH)) {
                0 -> month_Hasta = "01"
                1 -> month_Hasta = "02"
                2 -> month_Hasta = "03"
                3 -> month_Hasta = "04"
                4 -> month_Hasta = "05"
                5 -> month_Hasta = "06"
                6 -> month_Hasta = "07"
                7 -> month_Hasta = "08"
                8 -> month_Hasta = "09"
                9 -> month_Hasta = "10"
                10 -> month_Hasta = "11"
                else -> { // Note the block
                    month_Hasta = "12"
                }
            }


            day_Hasta = when (calender2.get(Calendar.DAY_OF_MONTH)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.DAY_OF_MONTH).toString()
                }
            }


            Horas_Hasta = when (calender2.get(Calendar.HOUR_OF_DAY)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.HOUR_OF_DAY).toString()
                }
            }


            Minutos_Hasta = when (calender2.get(Calendar.MINUTE)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.MINUTE).toString()
                }
            }


            Segundos_Hasta = when (calender2.get(Calendar.SECOND)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.SECOND).toString()
                }
            }


            info = FecMetricas(
                fecha = "$day_Hasta-$month_Hasta-$year_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta",
                metrica = "RAM " + porcentajeuso + "% (" + "${imetricaLibre}/${imetricaTotal} GB)"
            )
        }



        return  info

    }


    @SuppressLint("Range")
    fun getMetricaGraficoDISCO(idEquipo: Int, idAccion: Int, Unidad: String): FecMetricas? {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_MONITORES_PORCENAJE, $COLUMN_MONITORES_TOTAL_USADO, $COLUMN_MONITORES_ESACIO_LIBRE, $COLUMN_MONITORES_FEC_ULT_ACT FROM $TABLE_CONTROL_MONITORES WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo AND" +
                " $COLUMN_MONITORES_ID_ACCION = $idAccion AND $COLUMN_MONITORES_ACCION = '$Unidad'"
        val cursor = db.rawQuery(Query, null)

        var fechaConsulta = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var info: FecMetricas? = null
        fechaConsulta  = ""
        var porcentajeuso = 0.0
        var metricaLibre = 0.0
        var metricaTotal = 0.0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {


                porcentajeuso  = cursor.getDouble(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_PORCENAJE"))
                metricaLibre  = cursor.getDouble(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_ESACIO_LIBRE"))
                metricaTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_TOTAL_USADO"))
                fechaConsulta = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_FEC_ULT_ACT"))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()


        var iporcentajeHard = (porcentajeuso)!!.toDouble().roundToInt()
        var imetricaLibre = metricaLibre.toDouble() / 1024 / 1024 / 1024 // Bites 0.000000000125
        var imetricaTotal = metricaTotal.toDouble() / 1024 / 1024 / 1024 // Bites 0.000000000125
        var iimetricaLibre = ((imetricaLibre * 100).roundToInt() / 100.0).roundToInt()
        var iimetricaTotal = ((imetricaTotal * 100).roundToInt() / 100.0).roundToInt()

        var year_Hasta = 0
        var month_Hasta = ""
        var day_Hasta = ""
        var Horas_Hasta = ""
        var Minutos_Hasta = ""
        var Segundos_Hasta = ""
        if(fechaConsulta != ""){

            val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fechaConsulta)
            val calender2 = Calendar.getInstance()
            calender2.time = convertedTodayDateHasta
            year_Hasta = calender2.get(Calendar.YEAR)
            when (calender2.get(Calendar.MONTH)) {
                0 -> month_Hasta = "01"
                1 -> month_Hasta = "02"
                2 -> month_Hasta = "03"
                3 -> month_Hasta = "04"
                4 -> month_Hasta = "05"
                5 -> month_Hasta = "06"
                6 -> month_Hasta = "07"
                7 -> month_Hasta = "08"
                8 -> month_Hasta = "09"
                9 -> month_Hasta = "10"
                10 -> month_Hasta = "11"
                else -> { // Note the block
                    month_Hasta = "12"
                }
            }


            day_Hasta = when (calender2.get(Calendar.DAY_OF_MONTH)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.DAY_OF_MONTH).toString()
                }
            }


            Horas_Hasta = when (calender2.get(Calendar.HOUR_OF_DAY)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.HOUR_OF_DAY).toString()
                }
            }


            Minutos_Hasta = when (calender2.get(Calendar.MINUTE)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.MINUTE).toString()
                }
            }


            Segundos_Hasta = when (calender2.get(Calendar.SECOND)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.SECOND).toString()
                }
            }

            info = FecMetricas(
                fecha = "$day_Hasta-$month_Hasta-$year_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta",
                metrica = "$Unidad " + iporcentajeHard + "% (" + "${iimetricaLibre}/${iimetricaTotal} GB)"
            )

        }


        return info




    }



    @SuppressLint("Range")
    fun getMetricaGraficoRED(idEquipo: Int, idAccion: Int): FecMetricas? {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_MONITORES_PAQUETES_ENVIADOS, $COLUMN__MONITORES_PAQUETES_RECIBIDOS, $COLUMN_MONITORES_FEC_ULT_ACT FROM $TABLE_CONTROL_MONITORES WHERE $COLUMN_MONITORES_ID_EQUIPO = $idEquipo AND" +
                " $COLUMN_MONITORES_ID_ACCION = $idAccion AND $COLUMN_MONITORES_ACCION = 'RED'"
        val cursor = db.rawQuery(Query, null)
        var fechaConsulta = ""
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var info: FecMetricas? = null
        fechaConsulta  = ""

        var paquetesenviados = 0.0
        var paquetesrecibidos = 0.0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {

                paquetesenviados  = cursor.getDouble(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_PAQUETES_ENVIADOS"))
                paquetesrecibidos = cursor.getDouble(cursor.getColumnIndexOrThrow("$COLUMN__MONITORES_PAQUETES_RECIBIDOS"))
                fechaConsulta = cursor.getString(cursor.getColumnIndexOrThrow("$COLUMN_MONITORES_FEC_ULT_ACT"))


            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()

        var enviados = (paquetesenviados.toDouble() * 0.000125).roundToInt()


        var year_Hasta = 0
        var month_Hasta = ""
        var day_Hasta = ""
        var Horas_Hasta = ""
        var Minutos_Hasta = ""
        var Segundos_Hasta = ""
        if(fechaConsulta != ""){

            val simpleDateFormatHasta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val convertedTodayDateHasta: Date  =  simpleDateFormatHasta.parse(fechaConsulta)
            val calender2 = Calendar.getInstance()
            calender2.time = convertedTodayDateHasta
            year_Hasta = calender2.get(Calendar.YEAR)
            when (calender2.get(Calendar.MONTH)) {
                0 -> month_Hasta = "01"
                1 -> month_Hasta = "02"
                2 -> month_Hasta = "03"
                3 -> month_Hasta = "04"
                4 -> month_Hasta = "05"
                5 -> month_Hasta = "06"
                6 -> month_Hasta = "07"
                7 -> month_Hasta = "08"
                8 -> month_Hasta = "09"
                9 -> month_Hasta = "10"
                10 -> month_Hasta = "11"
                else -> { // Note the block
                    month_Hasta = "12"
                }
            }


            day_Hasta = when (calender2.get(Calendar.DAY_OF_MONTH)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.DAY_OF_MONTH).toString()
                }
            }


            Horas_Hasta = when (calender2.get(Calendar.HOUR_OF_DAY)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.HOUR_OF_DAY).toString()
                }
            }


            Minutos_Hasta = when (calender2.get(Calendar.MINUTE)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.MINUTE).toString()
                }
            }


            Segundos_Hasta = when (calender2.get(Calendar.SECOND)){
                0 -> "00"
                1 -> "01"
                2 -> "02"
                3 -> "03"
                4 -> "04"
                5 -> "05"
                6 -> "06"
                7 -> "07"
                8 -> "08"
                9 -> "09"
                else -> { // Note the block
                    calender2.get(Calendar.SECOND).toString()
                }
            }

            info = FecMetricas(
                fecha = "$day_Hasta-$month_Hasta-$year_Hasta $Horas_Hasta:$Minutos_Hasta:$Segundos_Hasta",
                metrica = "RED: $enviados Kbps"
            )
        }




        return  info


    }





    fun getNumeroGraficos(): Int {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT COUNT(*) as registros FROM $TABLE_CONTROL_MONITORES"
        val cursor = db.rawQuery(Query, null)
        var numRegistros = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                numRegistros  = cursor.getInt(cursor.getColumnIndexOrThrow("registros"))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  numRegistros
    }










    /**
     * Vigencia Licencia
     */


    fun addVigenciaLicencia(fec_desde: String, fec_hasta: String, fin_vigencia: Int, intervalo: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_VIGENCIA_LICENCIA " +
                "($COLUMN_FECHA_DESDE, $COLUMN_FECHA_HASTA," +
                "$COLUMN_DIAS_FIN_VIGENCIA, $COLUMN_INTERVALO_VIGENCIA)" +
                " SELECT ?,?,?,? " +
                " WHERE NOT EXISTS(SELECT 1 FROM $TABLE_VIGENCIA_LICENCIA)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindString(1, fec_desde)
        insertStmt!!.bindString(2, fec_hasta)
        insertStmt!!.bindLong(3, fin_vigencia.toLong())
        insertStmt!!.bindString(4, intervalo)
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun updVigenciaLicencia(fec_desde: String, fec_hasta: String, fin_vigencia: Int, intervalo: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_VIGENCIA_LICENCIA " +
                "SET $COLUMN_FECHA_DESDE = ?, $COLUMN_FECHA_HASTA = ?," +
                "$COLUMN_DIAS_FIN_VIGENCIA= ?, $COLUMN_INTERVALO_VIGENCIA = ?"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindString(1, fec_desde)
        insertStmt!!.bindString(2, fec_hasta)
        insertStmt!!.bindLong(3, fin_vigencia.toLong())
        insertStmt!!.bindString(4, intervalo)
        insertStmt!!.executeInsert()
        //db.close()
    }

    fun getFecHasta(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_FECHA_HASTA FROM $TABLE_VIGENCIA_LICENCIA"
        val cursor = db.rawQuery(Query, null)
        var tagfechasta = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                tagfechasta = cursor.getString(cursor.getColumnIndexOrThrow("vigencia_hasta"))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  tagfechasta
    }


    fun getDiasFinVigencia(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_DIAS_FIN_VIGENCIA FROM $TABLE_VIGENCIA_LICENCIA"
        val cursor = db.rawQuery(Query, null)
        var tagfinvigencia = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                tagfinvigencia  = cursor.getString(cursor.getColumnIndexOrThrow("vigencia_dias_fin_vigencia"))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  tagfinvigencia
    }


    /**
     *Control Usuario Fecha Ult Act
     */

    fun addControlEquiposFecUltAct(id_equipo: Int,  IdHardware: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT ($COLUMN_EQUIPO_ULT_ACT_ID,  $COLUMN_EQUIPO_ULT_ACT_ID_HARDWARE, $COLUMN_FECHA_ULT_ACT) SELECT ?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT WHERE $COLUMN_EQUIPO_ULT_ACT_ID = $id_equipo  AND  $COLUMN_EQUIPO_ULT_ACT_ID_HARDWARE = '$IdHardware')"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDateAndTime: String  =  simpleDateFormat.format(Date())

        insertStmt!!.bindLong(1, id_equipo.toLong())
        insertStmt!!.bindString(2, IdHardware)
        insertStmt!!.bindString(3, currentDateAndTime)
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun updControlEquiposFecUltAct(id_equipo: Int,  IdHardware: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT " +
                "SET $COLUMN_FECHA_ULT_ACT = ? " +
                "WHERE $COLUMN_EQUIPO_ULT_ACT_ID = $id_equipo AND " +
                "$COLUMN_EQUIPO_ULT_ACT_ID_HARDWARE = '$IdHardware'"

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDateAndTime: String  =  simpleDateFormat.format(Date())

        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindString(1, currentDateAndTime)
        insertStmt!!.executeInsert()
        //db.close()
    }


    /**
     *Control Entorno Desarrollo
     */

    fun addControlEntornoConexion(entorno: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_ENTORNO  ($COLUMN_ENTORNO) SELECT ? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_ENTORNO WHERE $COLUMN_ENTORNO_ID = 1 )"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDateAndTime: String  =  simpleDateFormat.format(Date())
        insertStmt!!.bindLong(1, entorno.toLong())
        insertStmt!!.executeInsert()
        //db.close()
    }

    fun updControlEntornoConexion(entorno: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_ENTORNO " +
                "SET $COLUMN_ENTORNO = ? " +
                " WHERE $COLUMN_ENTORNO_ID = 1"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindLong(1, entorno.toLong())

        insertStmt!!.executeInsert()
        //db.close()
    }


    fun getControlEntornoConexion(context: Context): Int {
        //#CORECCION DE ERROR DATABASE
        SQLiteDatabase.loadLibs(context)
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_ENTORNO FROM $TABLE_CONTROL_ENTORNO WHERE $COLUMN_ENTORNO_ID = 1"
        val cursor = db.rawQuery(Query, null)
        var svrEntorno = 0
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                svrEntorno  = cursor.getInt(cursor.getColumnIndexOrThrow("$COLUMN_ENTORNO"))


            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return   svrEntorno
    }




    /**
     *Control Web Server Socket
     */


    fun deleteServerSocket() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_SERVER_SOCKET")
        //db.close()


    }

    fun addControlServerSocket(server_socket: String,  puerto_server_socket: String?) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var puerto = ""
        if(puerto_server_socket == null || puerto_server_socket == ""){
            puerto = "0"
        }else{
            puerto = puerto_server_socket
        }
        val sql = "INSERT INTO $TABLE_SERVER_SOCKET ($COLUMN_SERVER_SOCKET,  $COLUMN_SERVER_SOCKET_PUERTO, $COLUMN_SERVER_SOCKET_FEC_ULT_ACT) SELECT ?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_SERVER_SOCKET WHERE $COLUMN_SERVER_SOCKET = '$server_socket')"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDateAndTime: String  =  simpleDateFormat.format(Date())
        insertStmt!!.bindString(1, server_socket)
        insertStmt!!.bindString(2, puerto)
        insertStmt!!.bindString(3, currentDateAndTime)
        insertStmt!!.executeInsert()
        //db.close()
    }






    fun getControlServerSocket(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_SERVER_SOCKET, $COLUMN_SERVER_SOCKET_PUERTO  FROM $TABLE_SERVER_SOCKET"
        val cursor = db.rawQuery(Query, null)
        var svrSocket = ""
        var svrPuerto = ""
        var svrDataSocket = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                svrSocket  = cursor.getString(cursor.getColumnIndexOrThrow("server_socket"))
                svrPuerto  = cursor.getString(cursor.getColumnIndexOrThrow("server_socket_puerto"))

                if(svrPuerto == null || svrPuerto == "0"){
                    svrDataSocket = "$svrSocket"
                }else{
                    svrDataSocket = "$svrSocket:$svrPuerto"
                }



            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  svrDataSocket
    }





    /**
     *Control Web Server Socket Log
     */




    fun addControlServerSocketLog(server_socket_log: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_SERVER_SOCKET_LOG ($COLUMN_SERVER_SOCKET_FEC_ALTA,  $COLUMN_SERVER_SOCKET_DESCRIPCION) SELECT ?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_SERVER_SOCKET_LOG)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val currentDateAndTime: String  =  simpleDateFormat.format(Date())
        insertStmt!!.bindString(1, currentDateAndTime)
        insertStmt!!.bindString(2, server_socket_log)
        insertStmt!!.executeInsert()
        //db.close()
    }


    /**
     * CONFIGURACION DEFAULT
     */

    fun deleteConfDefault() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONFIGURACION_MOVIL_DEFAULT")
        //db.close()
    }

    fun addConfiguracionDefault(infoAlert: Int, numAlert: Int, infoMonitores: Int, estEquipos: Int, infoEquipos: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONFIGURACION_MOVIL_DEFAULT ($COLUMN_CONF_DEF_MOVIL_INFO_ALERTAS, " +
                "$COLUMN_CONF_DEF_MOVIL_ALERTAS_RECIENTES,$COLUMN_CONF_DEF_MOVIL_INFO_MONITORES, $COLUMN_CONF_DEF_MOVIL_ESTATUS_EQUIPOS," +
                "$COLUMN_CONF_DEF_MOVIL_INFO_EQUIPOS) SELECT $infoAlert, $numAlert, $infoMonitores, $estEquipos, $infoEquipos WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONFIGURACION_MOVIL_DEFAULT)"
        db.execSQL(sql)
        //db.close()

    }



    fun getConfiguracionDefaultMovil(): List<config_default_movil> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_CONF_DEF_MOVIL_INFO_ALERTAS,COLUMN_CONF_DEF_MOVIL_ALERTAS_RECIENTES,COLUMN_CONF_DEF_MOVIL_INFO_MONITORES, COLUMN_CONF_DEF_MOVIL_ESTATUS_EQUIPOS,COLUMN_CONF_DEF_MOVIL_INFO_EQUIPOS)




        val infoList = ArrayList<config_default_movil>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria




        // query the user table
        val cursor = db.query(TABLE_CONFIGURACION_MOVIL_DEFAULT, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {

                val info =
                    config_default_movil(
                        get_conf_movil_alertas = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_CONF_DEF_MOVIL_INFO_ALERTAS
                            )
                        ).toInt(),
                        get_conf_movil_alertas_recientes = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_CONF_DEF_MOVIL_ALERTAS_RECIENTES
                            )
                        ).toInt(),
                        get_conf_movil_monitores = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_CONF_DEF_MOVIL_INFO_MONITORES
                            )
                        ).toInt(),
                        get_conf_movil_estatus_equipos = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_CONF_DEF_MOVIL_ESTATUS_EQUIPOS
                            )
                        ).toInt(),
                        get_conf_movil_info_equipos = cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                COLUMN_CONF_DEF_MOVIL_INFO_EQUIPOS
                            )
                        ).toInt()
                    )



                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }







    //CRUD PERFIL APP FILTROS Y ENCABEZADO
    fun addPerfilFiltroEncabezado(id_negocio: String, id_equipo: String, alerta_estatus: Int, alerta_equipo: Int, alerta_fecha: Int,
                                  alerta_fecha_desde_hasta: String, alerta_eliminada_estatus: Int, alerta_eliminada_fecha: Int, alerta_eliminada_desde_hasta: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_PERFIL_APP_FILTROS_ENCABEZADO ($COLUMN_HEADER_ID_NEGOCIO," +
                "$COLUMN_HEADER_ID_EQUIPO, $COLUMN_FILTRO_ALERTAS_ESTATUS," +
                "$COLUMN_FILTRO_ALERTAS_EQUIPO, $COLUMN_FILTRO_ALERTAS_FECHA," +
                "$COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA, $COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS," +
                "$COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA, $COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA)" +
                " VALUES (?,?,?,?,?,?,?,?,?)"

        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        insertStmt!!.bindString(1, id_negocio)
        insertStmt!!.bindString(2, id_equipo)
        insertStmt!!.bindLong(3, alerta_estatus.toLong())
        insertStmt!!.bindLong(4, alerta_equipo.toLong())
        insertStmt!!.bindLong(5, alerta_fecha.toLong())
        insertStmt!!.bindString(6, alerta_fecha_desde_hasta)
        insertStmt!!.bindLong(7, alerta_eliminada_estatus.toLong())
        insertStmt!!.bindLong(8, alerta_eliminada_fecha.toLong())
        insertStmt!!.bindString(9, alerta_eliminada_desde_hasta)


        insertStmt!!.executeInsert()
        //db.close()
    }


    fun deletePerfilFiltroEncabezado() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        db.execSQL("DELETE FROM $TABLE_PERFIL_APP_FILTROS_ENCABEZADO")
        //db.close()
    }


    fun updPerfilFiltroEncabezado(id_negocio: String, id_equipo: String, alerta_estatus: Int, alerta_equipo: Int, alerta_fecha: Int,
                                  alerta_fecha_desde_hasta: String, alerta_eliminada_estatus: Int, alerta_eliminada_fecha: Int, alerta_eliminada_desde_hasta: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_PERFIL_APP_FILTROS_ENCABEZADO " +
                "SET $COLUMN_HEADER_ID_NEGOCIO = ?," +
                "$COLUMN_HEADER_ID_EQUIPO = ?, $COLUMN_FILTRO_ALERTAS_ESTATUS = ?," +
                "$COLUMN_FILTRO_ALERTAS_EQUIPO = ?, $COLUMN_FILTRO_ALERTAS_FECHA = ?," +
                "$COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA = ?, $COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS = ?," +
                "$COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA = ?, $COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA = ?"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindString(1, id_negocio)
        insertStmt!!.bindString(2, id_equipo)
        insertStmt!!.bindLong(3, alerta_estatus.toLong())
        insertStmt!!.bindLong(4, alerta_equipo.toLong())
        insertStmt!!.bindLong(5, alerta_fecha.toLong())
        insertStmt!!.bindString(6, alerta_fecha_desde_hasta)
        insertStmt!!.bindLong(7, alerta_eliminada_estatus.toLong())
        insertStmt!!.bindLong(8, alerta_eliminada_fecha.toLong())
        insertStmt!!.bindString(9, alerta_eliminada_desde_hasta)
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun chkPerfilFiltroEncabezado(): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_HEADER_ID_NEGOCIO  FROM $TABLE_PERFIL_APP_FILTROS_ENCABEZADO LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var svrDataSocket = false
        if (cursor.count > 0) {
            svrDataSocket = true
            cursor.close()
        }
        //db.close()
        return  svrDataSocket
    }





    fun getPerfilFiltroEncabezado(): ArrayList<perfil_app_header_filters> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_HEADER_ID_NEGOCIO,
            COLUMN_HEADER_ID_EQUIPO,
            COLUMN_FILTRO_ALERTAS_ESTATUS,
            COLUMN_FILTRO_ALERTAS_EQUIPO,
            COLUMN_FILTRO_ALERTAS_FECHA,
            COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA,
            COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS,
            COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA,
            COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA)



        val infoList = ArrayList<perfil_app_header_filters>()

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        var selection = " LIMIT 1 "



        // selection arguments



        // query the user table
        val cursor = db.query(TABLE_PERFIL_APP_FILTROS_ENCABEZADO, //Table to query
            columns,            //columns to return
            null,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order
        if (cursor.moveToFirst()) {
            do {

                val info = perfil_app_header_filters(header_id_negocio = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_ID_NEGOCIO)).toString(),
                    header_id_equipos = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HEADER_ID_EQUIPO)).toString(),
                    filtro_alertas_estatus=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_ESTATUS)).toInt(),
                    filtro_alertas_equipo=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_EQUIPO)).toInt(),
                    filtro_alertas_fecha=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_FECHA)).toInt(),
                    filtro_alertas_fecha_desde_hasta=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA)).toString(),
                    filtro_alertas_eliminadas_estatus=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS)).toInt(),
                    filtro_alertas_eliminadas_fecha=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA)).toInt(),
                    filtro_alertas_eliminadas_fecha_desde_hasta=cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA)).toString())

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }












    /**
     * This method to update user record
     *
     * @param user
     */
    fun updateUser(user: User) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, user.name)
        values.put(COLUMN_USER_EMAIL, user.email)
        values.put(COLUMN_USER_PASSWORD, user.password)

        // updating row
        db.update(TABLE_USER, values, "$COLUMN_USER_ID = ?",
            arrayOf(user.id.toString()))
        //db.close()
    }

    /**
     * This method is to delete user record
     *
     * @param user
     */
    fun deleteUser() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_USER")
        //db.close()


    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @return true/false
     */
    fun checkUser(email: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID)
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        val selection = "$COLUMN_USER_EMAIL = ?"

        // selection argument
        val selectionArgs = arrayOf(email)

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        val cursor = db.query(TABLE_USER, //Table to query
            columns,        //columns to return
            selection,      //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,  //group the rows
            null,   //filter by row groups
            null)  //The sort order


        val cursorCount = cursor.count
        cursor.close()
        //db.close()

        if (cursorCount > 0) {
            return true
        }

        return false
    }

    /**
     * This method to check user exist or not
     *
     * @param email
     * @param password
     * @return true/false
     */
    fun checkUser(email: String, password: String): Boolean {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_USER_ID)

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // selection criteria
        val selection = "$COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"

        // selection arguments
        val selectionArgs = arrayOf(email, password)

        // query user table with conditions
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com' AND user_password = 'qwerty';
         */
        val cursor = db.query(TABLE_USER, //Table to query
            columns, //columns to return
            selection, //columns for the WHERE clause
            selectionArgs, //The values for the WHERE clause
            null,  //group the rows
            null, //filter by row groups
            null) //The sort order

        val cursorCount = cursor.count
        cursor.close()
        //db.close()

        if (cursorCount > 0)
            return true

        return false

    }



    fun updDesactivaTodoHistoricoScreenshot(idEquipo: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE  $TABLE_CONTROL_HISTORICO_SCREENSHOT SET $COLUMN_HSC_IMAGE_ESTATUS = 9  WHERE $COLUMN_HSC_ID_EQUIPO = $idEquipo")
        //db.close()


    }

    fun updActivaHistoricoScreenshot(idEquipo: String, nomimagen: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE  $TABLE_CONTROL_HISTORICO_SCREENSHOT SET $COLUMN_HSC_IMAGE_ESTATUS = 1  WHERE $COLUMN_HSC_ID_EQUIPO = $idEquipo AND $COLUMN_HSC_NOMBRE_IMAGEN = '$nomimagen'")
        //db.close()


    }

    fun updImageDataHistoricoScreenshot(idEquipo: String, nomimagen: String, imagedata: Bitmap) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("UPDATE  $TABLE_CONTROL_HISTORICO_SCREENSHOT SET $COLUMN_HSC_IMAGE_DATA= $imagedata  WHERE $COLUMN_HSC_ID_EQUIPO = $idEquipo AND $COLUMN_HSC_NOMBRE_IMAGEN = '$nomimagen'")
        //db.close()


    }


    fun updImageDataHistoricoScreenshott(idEquipo: String, nomimagen: String, imagedata: Bitmap) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CONTROL_HISTORICO_SCREENSHOT SET $COLUMN_HSC_IMAGE_DATA = ? WHERE $COLUMN_HSC_ID_EQUIPO = ? AND $COLUMN_HSC_NOMBRE_IMAGEN = ?"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val bytes = ByteArrayOutputStream()

        imagedata.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val byteArray: ByteArray = bytes.toByteArray()


        insertStmt!!.bindBlob(1, byteArray)
        insertStmt!!.bindLong(2, idEquipo.toLong())
        insertStmt!!.bindString(3, nomimagen)
        insertStmt!!.execute()
        //db.close()
    }


    //HISTORICO SCREENSHOT
    fun addAllHistoricoScreenshot(idEquipo: String, nomimagen: String, fechacreacion: String, urlimagen: String) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_HISTORICO_SCREENSHOT(" +
                "$COLUMN_HSC_ID_EQUIPO, " +
                "$COLUMN_HSC_NOMBRE_IMAGEN, " +
                "$COLUMN_HSC_FECHA_CREACION, " +
                "$COLUMN_HSC_URL_SCREENSHOT, " +
                "$COLUMN_HSC_IMAGE_ESTATUS)" +
                "SELECT " + idEquipo + ",'" + nomimagen +"','" +
                fechacreacion + "','" +
                urlimagen + "', 1" +
                " WHERE " +
                "NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_HISTORICO_SCREENSHOT " +
                "WHERE $COLUMN_HSC_NOMBRE_IMAGEN = '$nomimagen' AND $COLUMN_HSC_ID_EQUIPO = $idEquipo)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.executeInsert()
        //db.close()
    }







    fun getAllHistoricoScreenshotListData(idNegocios: Int, idEquipos: Int,  nomimagen: String, fechacreacion: String, urlimagen: String, ultimaimagen: Int): ArrayList<ControlHistoricoScreenshotData> {

        // array of columns to fetch
        val columns = arrayOf(COLUMN_HSC_ID, COLUMN_HSC_ID_EQUIPO,  COLUMN_HSC_NOMBRE_IMAGEN, COLUMN_HSC_FECHA_CREACION, COLUMN_HSC_URL_SCREENSHOT, COLUMN_HSC_IMAGE_ESTATUS)

        // sorting orders

        //var selectionArgs = arrayOf(id_accion.toString())

        val infoList = ArrayList<ControlHistoricoScreenshotData>()


        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // query the user table

        // selection arguments
        var selection = ""

        //val selectionArgs = arrayOf("9")

        if (idNegocios != 0){
            if(idEquipos != 0){
                selection += "AND $COLUMN_HSC_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where $COLUMN_EQUIPO_ID_ = $idEquipos AND  id_catnegocio = $idNegocios and estado_servidor <> 9) AND $COLUMN_HSC_IMAGE_ESTATUS NOT IN (9) "

            }else{
                selection += "$COLUMN_HSC_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where id_catnegocio = $idNegocios and estado_servidor <> 9) AND $COLUMN_HSC_IMAGE_ESTATUS NOT IN (9)"
            }

        }else{
            if(idEquipos != 0){
                selection += "$COLUMN_HSC_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where  $COLUMN_EQUIPO_ID_ = $idEquipos AND estado_servidor <> 9) AND $COLUMN_HSC_IMAGE_ESTATUS NOT IN (9)"

            }else{
                selection += "$COLUMN_HSC_ID_EQUIPO IN (select id_negocio_id from $TABLE_EQUIPOS where estado_servidor <> 9) AND $COLUMN_HSC_IMAGE_ESTATUS NOT IN (9)"

            }

        }

        if(ultimaimagen > 0){
            if(selection != ""){
                selection += " AND "
            }
            selection += "$COLUMN_HSC_ID > $ultimaimagen"
        }

        val cursor = db.query(TABLE_CONTROL_HISTORICO_SCREENSHOT, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            null,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null)         //The sort order

        // cursor = db.rawQuery("SELECT * FROM $TABLE_CONTROL_HISTORICO_SCREENSHOT WHERE $COLUMN_HSC_IMAGE_ESTATUS NOT IN (?)", selectionArgs)
        if (cursor.moveToFirst()) {
            do {
                val info =
                    ControlHistoricoScreenshotData(
                        idImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_ID))
                            .toInt(),
                        idEquipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_ID_EQUIPO)).toInt(),
                        nombreImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_NOMBRE_IMAGEN)).toString(),
                        fechaCreacion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_FECHA_CREACION)),
                        urlImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_URL_SCREENSHOT)),
                        estatusImagen = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_HSC_IMAGE_ESTATUS)).toInt()
                    )

                infoList.add(info)
            } while (cursor.moveToNext())
        }
        cursor.close()
        //db.close()
        return infoList
    }


    fun chkImgHistoricoaExist(idEquipo: String, nomimagen: String): Boolean{
        val qu = "SELECT $COLUMN_HSC_IMAGE_DATA FROM $TABLE_CONTROL_HISTORICO_SCREENSHOT  WHERE $COLUMN_HSC_ID_EQUIPO = $idEquipo AND $COLUMN_HSC_NOMBRE_IMAGEN = '$nomimagen' AND $COLUMN_HSC_IMAGE_DATA IS NOT NULL"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        try {

            var cur = db.rawQuery(qu, arrayOf())

            return cur.count > 0
            if (cur != null && !cur.isClosed()) {

                cur.close()
            }

        }catch (e: Exception) {

        }
        return false
    }

    fun getImageHistricoSC(idEquipo: String, nomimagen: String): Bitmap? {
        val qu = "SELECT $COLUMN_HSC_IMAGE_DATA FROM $TABLE_CONTROL_HISTORICO_SCREENSHOT WHERE $COLUMN_HSC_ID_EQUIPO = $idEquipo AND $COLUMN_HSC_NOMBRE_IMAGEN = '$nomimagen'"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_HSC_IMAGE_DATA")
                    val imgByte: ByteArray = cur.getBlob(index)
                    cur.close()

                    val options = BitmapFactory.Options()
                    options.inScreenDensity
                    return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size, options)


                }
                if (cur != null && !cur.isClosed()) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return null
    }




    //AUX TEMP IMAGEN EXPLORADOR

    fun deleteTmpImgExplorador() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TEMP_PASO_IMAGENES_EXPLORADOR")
        //db.close()


    }


    fun addTmpImgExplorador(imagedata: Bitmap) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TEMP_PASO_IMAGENES_EXPLORADOR ($COLUMN_TMP_IMAGE_DATA) VALUES(?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val bytes = ByteArrayOutputStream()

        imagedata.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val byteArray: ByteArray = bytes.toByteArray()

        insertStmt!!.bindBlob(1, byteArray)
        insertStmt!!.executeInsert();
        //db.close()

    }




    fun getTmpImgExplorador(): Bitmap? {
        val qu = "SELECT $COLUMN_TMP_IMAGE_DATA FROM $TEMP_PASO_IMAGENES_EXPLORADOR"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_TMP_IMAGE_DATA")
                    val imgByte: ByteArray = cur.getBlob(index)
                    cur.close()

                    val options = BitmapFactory.Options()
                    options.inScreenDensity
                    return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size, options)


                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return null
    }




    //AUX SCREENSHOT

    fun deleteAuxScreenshot() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_AUX_TEMP_SCREENSHOT")
        //db.close()


    }


    fun addAuxScreenshot(idEquipo: String, imagedata: Bitmap) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_AUX_TEMP_SCREENSHOT ($COLUMN_AUX_ID_EQUIPO, $COLUMN_AUX_IMAGE_DATA) VALUES(?,?)"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        val bytes = ByteArrayOutputStream()

        imagedata.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val byteArray: ByteArray = bytes.toByteArray()

        insertStmt!!.bindLong(1, idEquipo.toLong())
        insertStmt!!.bindBlob(2, byteArray)
        insertStmt!!.executeInsert();
        //db.close()

    }

    fun getAuxScreenshot(idEquipo: String): Bitmap? {
        val qu = "SELECT $COLUMN_AUX_IMAGE_DATA FROM $TABLE_AUX_TEMP_SCREENSHOT WHERE $COLUMN_AUX_ID_EQUIPO = $idEquipo"
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        try {
            var   cur = db.rawQuery(qu, arrayOf())

            if (cur != null) {
                if (cur.moveToFirst()) {
                    val index: Int = cur.getColumnIndexOrThrow("$COLUMN_AUX_IMAGE_DATA")
                    val imgByte: ByteArray = cur.getBlob(index)
                    cur.close()

                    val options = BitmapFactory.Options()
                    options.inScreenDensity
                    return BitmapFactory.decodeByteArray(imgByte, 0, imgByte.size, options)


                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return null
    }


    /***
     * CRUD PARAMETROS DE ACCIONES
     */


    fun addControlParamsAcc(id_accion: Int, campo: String, valor: String, tipo: String, qry: String?, qryapi: String?) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CONTROL_PARAMS_ACC_DB($COLUMN_ID_ACCION,$COLUMN_CAMPO" +
                ", $COLUMN_VALOR, $COLUMN_TIPO, $COLUMN_CONSULTA_DB, $COLUMN_CONSULTA_API) " +
                " SELECT ?,?,?,?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CONTROL_PARAMS_ACC_DB WHERE $COLUMN_ID_ACCION = $id_accion AND $COLUMN_CAMPO = '$campo')"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)

        var iqry = ""
        var iqryapi = ""
        if(qry == null || qry == "null"){
            iqry = "SN"
        }else{
            iqry = qry!!
        }
        if(qryapi == null || qryapi == "null"){
            iqryapi = "SN"
        }else{
            iqryapi = qryapi!!
            iqryapi = iqryapi.replace("'", "''")
            iqryapi = iqryapi.replace("\"", "\"\"")
        }
        insertStmt!!.bindLong(1, id_accion.toLong())
        insertStmt!!.bindString(2, campo)
        insertStmt!!.bindString(3, valor)
        insertStmt!!.bindString(4, tipo)
        insertStmt!!.bindString(5, iqry)
        insertStmt!!.bindString(6, iqryapi)
        insertStmt!!.executeInsert()
        //db.close()
    }



    fun updControlParamsAcc(id_accion: Int, campo: String, valor: String, tipo: String, qry: String?, qryapi: String?) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var iqry = ""
        var iqryapi = ""
        if(qry == null || qry == "null"){
            iqry = "SN"
        }else{
            iqry = qry!!
        }
        if(qryapi == null || qryapi == "null"){
            iqryapi = "SN"
        }else{

            iqryapi = qryapi!!
            iqryapi = iqryapi.replace("'", "''")
            iqryapi = iqryapi.replace("\"", "\"\"")
        }


        // delete user record by id
        db.execSQL("UPDATE  $TABLE_CONTROL_PARAMS_ACC_DB SET $COLUMN_CAMPO = '$campo', $COLUMN_VALOR = '$valor', $COLUMN_TIPO = '$tipo', $COLUMN_CONSULTA_DB = '$iqry', $COLUMN_CONSULTA_API = '$iqryapi'   WHERE $COLUMN_ID_ACCION = $id_accion AND $COLUMN_CAMPO = '$campo'")
        //db.close()


    }


    @SuppressLint("Range")
    fun getConsultaControlParamsAccDB(id_accion: Int): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_CONSULTA_DB FROM $TABLE_CONTROL_PARAMS_ACC_DB WHERE $COLUMN_ID_ACCION = $id_accion LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var iconsulta = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                iconsulta  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSULTA_DB))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  iconsulta
    }




    @SuppressLint("Range")
    fun getConsultaControlParamsAccAPI(id_accion: Int): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_CONSULTA_API FROM $TABLE_CONTROL_PARAMS_ACC_DB WHERE $COLUMN_ID_ACCION = $id_accion LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var iconsulta = ""
        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                iconsulta  = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSULTA_API))

            } while (cursor.moveToNext())
            cursor.close()
        }
        //db.close()
        return  iconsulta
    }




    fun chkExistControlParamsAcc(id_accion: Int, campo: String): Boolean{
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val Query = "SELECT $COLUMN_CAMPO FROM $TABLE_CONTROL_PARAMS_ACC_DB  WHERE $COLUMN_ID_ACCION = $id_accion AND $COLUMN_CAMPO = '$campo'   LIMIT 1"
        val cursor = db.rawQuery(Query, null)
        var svrDataSocket = false
        if (cursor.count > 0) {
            svrDataSocket = true
            cursor.close()
        }
        //db.close()
        return  svrDataSocket
    }

    @SuppressLint("Range")
    fun chkExistControlParamsAcc(id_accion: Int): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // Seleccionamos el campo consulta_db_act, ya que es el que quieres evaluar con regex
        val query = """
        SELECT $COLUMN_CONSULTA_DB 
        FROM $TABLE_CONTROL_PARAMS_ACC_DB 
        WHERE $COLUMN_ID_ACCION = $id_accion
        LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        // Regex que busca: opcional comilla + uno o más dígitos + signo de interrogación + opcional comilla
        val pattern = Regex("""'?[0-9]+\?'?""")

        var found = false
        var value = ""
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSULTA_DB))
                if (pattern.containsMatchIn(value)) {
                    found = true
                    break
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return found
    }


    @SuppressLint("Range")
    fun chkExistControlParamsAccAPI(id_accion: Int): Boolean {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()

        // Seleccionamos el campo consulta_db_act, ya que es el que quieres evaluar con regex
        val query = """
        SELECT $COLUMN_CONSULTA_API 
        FROM $TABLE_CONTROL_PARAMS_ACC_DB 
        WHERE $COLUMN_ID_ACCION = $id_accion
        LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        // Regex que busca: opcional comilla + uno o más dígitos + signo de interrogación + opcional comilla
        val pattern = Regex("""'?[0-9]+\?'?""")

        var found = false
        var value = ""
        if (cursor.moveToFirst()) {
            do {
                value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONSULTA_API))
                if (pattern.containsMatchIn(value)) {
                    found = true
                    break
                }
            } while (cursor.moveToNext())
        }

        cursor.close()
        return found
    }




    fun getAllParamsAccDB(id_accion: Int): ArrayList<ItemParametrosAccDB> {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val infoList = ArrayList<ItemParametrosAccDB>()

        // array of columns to fetch
        val columns = arrayOf(
            COLUMN_CAMPO,
            COLUMN_VALOR,
            COLUMN_TIPO
        )

        var selection = "$COLUMN_ID_ACCION = ?"
        val selectionArgs = arrayOf("$id_accion")
        val cursor = db.query(
            TABLE_CONTROL_PARAMS_ACC_DB, //Table to query
            columns,            //columns to return
            selection,     //columns for the WHERE clause
            selectionArgs,  //The values for the WHERE clause
            null,      //group the rows
            null,       //filter by row groups
            null
        )         //The sort order
        if (cursor.moveToFirst()) {
            do {
                val info = ItemParametrosAccDB(campo =  cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAMPO)).toString(),
                    valor = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALOR)).toString(),
                    tipo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPO)).toString())

                infoList.add(info)
            } while (cursor.moveToNext())
        }

        cursor.close()
        //db.close()
        return infoList

    }



    /***
     * Recupera Bandera para cambiar color de los metricas durante la actualizacion de los monitores
     */

    fun getMntColor(id_equipo: Int): Int {
        val qu = "SELECT $COLUMN_EQUIPO_MNT_COLOR FROM $TABLE_EQUIPOS WHERE $COLUMN_EQUIPO_ID_ = $id_equipo "
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var valcolor = 0
        try {
            var   cur = db.rawQuery(qu, arrayOf())
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val index: Int = cur.getColumnIndexOrThrow("$COLUMN_EQUIPO_MNT_COLOR")
                        val valor: Int = cur.getInt(index)
                        valcolor = valor
                    } while (cur.moveToNext())
                    cur.close()
                    return valcolor
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return 0
    }



    fun updMntColor(id_equipo: Int, valor: Int) {

        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_EQUIPOS " +
                "SET $COLUMN_EQUIPO_MNT_COLOR = $valor" +
                " WHERE  $COLUMN_EQUIPO_ID_ = $id_equipo"

        db.execSQL(sql)
        //db.close()

    }


    fun delCambiosVersion() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CAMBIOS_VERSION")
        //db.close()

    }

    fun addCambiosVersion(version: String, mesnaje: String, vmsj: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "INSERT INTO $TABLE_CAMBIOS_VERSION ($COLUMN_VERSION, $COLUMN_CAMBIOS, $COLUMN_MUESTRA_MENSAJE) " +
                " SELECT ?,?,? WHERE NOT EXISTS(SELECT 1 FROM $TABLE_CAMBIOS_VERSION WHERE $COLUMN_VERSION = '$version')"
        val insertStmt: SQLiteStatement? = db.compileStatement(sql)
        insertStmt!!.bindString(1, version)
        insertStmt!!.bindString(2, mesnaje)
        insertStmt!!.bindLong(3, vmsj.toLong())
        insertStmt!!.executeInsert()
        //db.close()
    }


    fun updCambiosVersion(vmsj: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val sql = "UPDATE $TABLE_CAMBIOS_VERSION " +
                "SET $COLUMN_MUESTRA_MENSAJE = $vmsj"
        db.execSQL(sql)
    }


    fun getMsgCambiosVersion(): String {
        val qu = "SELECT $COLUMN_CAMBIOS FROM $TABLE_CAMBIOS_VERSION WHERE $COLUMN_MUESTRA_MENSAJE = 1 "
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        var icambios = ""
        try {
            var   cur = db.rawQuery(qu, arrayOf())
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        val index: Int = cur.getColumnIndexOrThrow("$COLUMN_CAMBIOS")
                        val valor: String = cur.getString(index)

                        icambios = valor
                    } while (cur.moveToNext())
                    cur.close()
                    return icambios
                }
                if (cur != null && !cur.isClosed) {
                    cur.close()
                }
            }

        } catch (e: Exception) {

        }
        //db.close()
        return "NT"
    }



    fun eliminarAlertasAntiguas(dias: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Calcular la fecha límite
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -dias)
        }
        val fechaLimite = dateFormat.format(calendar.time)

        // Eliminar registros cuya fecha sea menor a la fecha límite
        val whereClause = "alerta_fecha < ?"
        val whereArgs = arrayOf(fechaLimite)

        db.delete(TABLE_CONTROL_ALERTAS, whereClause, whereArgs)

    }


    /*****
     *
     * PERMISOS ALERTA USUARIOS
     */



    fun upsertUsuario(usuario: uariosPermitidosRecAlertasMovil_list) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val cursor = db.rawQuery(
            "SELECT 1 FROM $TABLE_CONTROL_USUARIOS_ALERTAS WHERE $COLUMN_USUALT_ID = ?",
            arrayOf(usuario.id.toString())
        )
        val exists = cursor.moveToFirst()
        cursor.close()



        if (exists) {
            val values = ContentValues().apply {
                put(COLUMN_USUALT_ID, usuario.id)
                put(COLUMN_USUALT_USUARIO, usuario.usuario)
                put(COLUMN_USUALT_EQUIPO, usuario.equipo)
                put(COLUMN_USUALT_NOMBRE, usuario.nombre)
                put(COLUMN_USUALT_ESTATUS_ACT, usuario.estatus_act)
                put(COLUMN_USUALT_ALERTA, usuario.alerta)
            }

            db.update(
                TABLE_CONTROL_USUARIOS_ALERTAS,
                values,
                "$COLUMN_USUALT_ID = ?",
                arrayOf(usuario.id.toString())
            )
        } else {
            val values = ContentValues().apply {
                put(COLUMN_USUALT_ID, usuario.id)
                put(COLUMN_USUALT_USUARIO, usuario.usuario)
                put(COLUMN_USUALT_EQUIPO, usuario.equipo)
                put(COLUMN_USUALT_NOMBRE, usuario.nombre)
                put(COLUMN_USUALT_ESTATUS_ACT, usuario.estatus_act)
                put(COLUMN_USUALT_ALERTA, usuario.alerta)
                put(COLUMN_USUALT_ALERTA_MOVIL, usuario.alerta) // Inicializa igual, si aplica
            }
            db.insert(TABLE_CONTROL_USUARIOS_ALERTAS, null, values)
        }
    }



    fun listarUsuarios(id_equipo: Int): List<MovilUsuPermitidosRecAlertasMovil_list> {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val lista = mutableListOf<MovilUsuPermitidosRecAlertasMovil_list>()
        var qryUsuarios = ""

        if(id_equipo == 0){
            qryUsuarios = "SELECT id, REPLACE(usuario, '_', ' ') AS usuario, equipo, nombre, estatus_act, alerta, alerta_movil " +
                    " FROM $TABLE_CONTROL_USUARIOS_ALERTAS " +
                    " ORDER BY $COLUMN_USUALT_EQUIPO, $COLUMN_USUALT_NOMBRE DESC"
        }else{
            qryUsuarios = "SELECT id, REPLACE(usuario, '_', ' ') AS usuario, equipo, nombre, estatus_act, alerta, alerta_movil  " +
                    " FROM $TABLE_CONTROL_USUARIOS_ALERTAS WHERE $COLUMN_USUALT_EQUIPO = $id_equipo  " +
                    " ORDER BY $COLUMN_USUALT_EQUIPO, $COLUMN_USUALT_NOMBRE DESC"
        }

        val cursor = db.rawQuery(qryUsuarios, null)
        while (cursor.moveToNext()) {
            val usuario = MovilUsuPermitidosRecAlertasMovil_list(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUALT_ID)),
                usuario = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUALT_USUARIO)),
                equipo = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUALT_EQUIPO)),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUALT_NOMBRE)),
                estatus_act = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USUALT_ESTATUS_ACT)),
                alerta = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUALT_ALERTA)),
                alerta_movil = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USUALT_ALERTA_MOVIL))
            )
            lista.add(usuario)
        }
        cursor.close()
        return lista
    }


    fun obtenerIdsComoCadena(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val resultado = StringBuilder()
        var cursor: Cursor? = null

        return try {
            val query = "SELECT id_negocio_id FROM equipos"
            cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                resultado.append(id).append(",")
            }

            if (resultado.isNotEmpty()) {
                resultado.setLength(resultado.length - 1) // Quita la última coma
            }

            resultado.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            cursor?.close()
        }
    }





    fun actualizarAlertaMovil(id: Int, nuevoValor: Int) {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val values = ContentValues().apply {
            put(COLUMN_USUALT_ALERTA_MOVIL, nuevoValor)
        }
        db.update(
            TABLE_CONTROL_USUARIOS_ALERTAS,
            values,
            "$COLUMN_USUALT_ID = ?",
            arrayOf(id.toString())
        )
    }



    fun delHistoricoScrfeenShot() {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        // delete user record by id
        db.execSQL("DELETE FROM $TABLE_CONTROL_HISTORICO_SCREENSHOT")
        //db.close()
    }



    fun obtCadenaIdsUsuAlertasBloqeuados(): String {
        val db = this.getWritableDatabase(PASS_PHRASE)
        db.enableWriteAheadLogging()
        val resultado = StringBuilder()
        var cursor: Cursor? = null

        return try {
            val query = "SELECT id FROM control_usuarios_alertas WHERE  alerta_movil = 0 "
            cursor = db.rawQuery(query, null)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(0)
                resultado.append(id).append(",")
            }

            if (resultado.isNotEmpty()) {
                resultado.setLength(resultado.length - 1) // Quita la última coma
            }

            resultado.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            cursor?.close()
        }
    }

   


    companion object {

        @Volatile
        private var sInstance: DatabaseHelper? = null
        public fun getInstance(context: Context): DatabaseHelper {
            return sInstance ?: synchronized(this) {
                sInstance ?: DatabaseHelper(context).also { sInstance = it }
            }
        }

        // Database Version
        private val DATABASE_VERSION = 67

        // Database Name
        private val DATABASE_NAME = "UserManager.db"

        //const val PASS_PHRASE = "@eyJ0eXAiOiJKV1QiLCJhb#GciOiJIUzI1NiJ9"
        const val PASS_PHRASE = ""

        // User table name
        private val TABLE_USER = "user"

        private val TABLE_USER_AVATAR = "user_avatar"

        private val TABLE_LICENCIA = "licencia"

        private val TABLE_PARAMETROS_LICENCIA = "parametros_licencia"

        private val TABLE_SEGURIDAD = "seguridad"

        private val TABLE_INFO_ACCION_JSON = "info_accion_json"

        private val TABLE_INFO_NEGOCIOS = "info_accion_negocios"

        private val TABLE_CONTROL_GRAFICOS_MONITOR = "control_graficos_monitoreables"

        private val TABLE_CONFIGURACION_MOVIL = "configuracion_movil"

        private val TABLE_CONFIGURACION_MOVIL_TOP_ALERTAS = "configuracion_movil_top_alertas"

        private val TABLE_CONFIGURACION_MOVIL_HISTORICO_ALERTAS = "configuracion_movil_historico_alertas"

        private val TABLE_CONTROL_ACCIONES_FAVORITAS = "control_acciones_favoritas"


        private val TABLE_CONTROL_ALERTAS = "control_alertas"

        private val TABLE_CONTROL_ALERTAS_ESTATUS = "control_alertas_estatus"

        private val TABLE_CONTROL_ALERTAS_MARCADAS = "control_alertas_marcadas"

        private val TABLE_CONTROL_ALERTAS_FAVORITAS = "control_alertas_favoritas"


        private val TABLE_CONFIGURACION_ALERTAS = "configuracion_alertas"

        private val TABLE_CONTROL_CONEXION_RED = "control_conexion_red"

        private val TABLE_CONTROL_EQUIPOS_X_MOVIL = "control_equipos_x_movil"

        private val TABLE_CONTROL_ESTADO_EQUIPOS = "control_estado_equipos"

        private val TABLE_CONTROL_MONITOREABLES_FAVORITOS = "control_monitoreables_favoritos"

        private val TABLE_TAG_GRAFICOS_MONITOREABLES = "control_tag_graficos_monitoreables"

        private val TABLE_TAG_VISTAS_GRAFICOS_MONITOREABLES = "control_tag_vistas_graficos_monitoreables"

        private val TABLE_SEGURIDAD_BIOMETRIA = "seguridad_biometrica"

        private val TABLE_SEGURIDAD_NIP = "seguridad_nip"

        private val COLUMN_NIP_LONGITUD_MIN = "longitud_min_nip"
        private val COLUMN_NIP_LONGITUD_MAX = "longitud_max_nip"


        private val COLUMN_LICENCIA_ID = "licencia_id"
        private val COLUMN_HARDWARE_KEY = "hardware_name"
        private val COLUMN_LICENCIA_ACEPTACION = "licencia_aceptacion"
        private val COLUMN_ID_MOVIL = "id_movil"
        private val COLUMN_NOMBRE_MOVIL = "nom_movil"
        private val COLUMN_NOMBRE_DOMINIO = "nombre_dominio"
        private val COLUMN_VERSION_SOFTWARE = "version_software"
        private val COLUMN_VERSION_REQUIERE_UBICACION = "requiere_ubicacion"
        private val COLUMN_PREFERENCES_AUTO_START = "preferencias_auto_start"


        // User Table Columns names
        private val COLUMN_USER_ID = "user_id"
        private val COLUMN_USER_NAME = "user_name"
        private val COLUMN_USER_EMAIL = "user_email"
        private val COLUMN_USER_PASSWORD = "user_password"


        private val COLUMN_USER_AVATAR_ID = "user_avatar_id"
        private val COLUMN_USER_AVATAR = "user_avatar"



        //Seguridad Table Columns names
        private val COLUMN_SEGURIDAD_ID = "seguridad_id"
        private val COLUMN_NIP = "nip"

        private val COLUMN_INFO_ID = "info_id"
        private val COLUMN_RESULT = "result"

        //Info Seguridad
        private val COLUMN_INFO_NEGOCIO_ID = "info_negocio_id"
        private val COLUMN_ID_NEGOCIO = "id_negocio"
        private val COLUMN_ACT_STATUS = "act_status"
        private val COLUMN_ACT_FECHA  = "act_fecha"


        //Control Monitoreables
        private val COLUMN_CONTROL_MN_ID = "id_control"
        private val COLUMN_CONTROL_MN_ACCION = "id_accion_mn"
        private val COLUMN_CONTROL_MN_HARDWARE = "hardware_mn"
        private val COLUMN_CONTROL_MN_GRAFICO = "grafico_mn"

        //Configuracion Movil
        private val COLUMN_CONFIG_ID = "id_configuracion"
        private val COLUMN_INTERVALO_TIEMPO_MONITOREABLES = "intervalo_tiempo_monitoreables"
        private val COLUMN_INTERVALO_TIEMPO_ALERTAS = "intervalo_tiempo_alertas"
        private val COLUMN_INTERVALO_TIEMPO_EQUIPO = "intervalo_tiempo_equipos"

        private val COLUMN_CONFIG_TOP_ID = "id_configuracion_top"
        private val COLUMN_TOP_ALERTAS = "top_alertas"


        private val COLUMN_CONFIG_HISTORICO_ID = "id_configuracion_historico"
        private val COLUMN_LIMITE_HISTORICO = "limite_historico"

        //Control acciones favoritas
        private val COLUMN_ACC_FAV_ID = "id_ctl_acc_fav"
        private val COLUMN_ACC_FAV_ID_ACCION = "ctl_acc_fav_id_accion"
        private val COLUMN_ACC_FAV_TITULO = "ctl_acc_fav_titulo"
        private val COLUMN_ACC_COMANDO  = "ctl_acc_fav_comando"
        private val COLUMN_ACC_ID_EQUIPO = "ctl_acc_fav_id_equipo"
        private val COLUMN_ACC_NOM_NEGOCIO = "ctl_acc_fav_nom_negocio"
        private val COLUMN_ACC_FAV_TIPO_ID = "ctl_acc_fav_tipo_id"
        private val COLUMN_ACC_FAV_PATH_ICONO = "ctl_acc_fav_path_icono"
        private val COLUMN_ACC_FAV_ICONO = "ctl_acc_fav_icono"
        private val COLUMN_ACC_FAV_ESTATUS = "ctl_acc_fav_estatus"


        //CONTROL ALERTAS
        private val COLUMN_CONTROL_ALERTA_ID = "id_ctl_alerta"
        private val COLUMN_ALERTA_ID = "alerta_id"
        private val COLUMN_ALERTA_TITULO = "alerta_titulo"
        private val COLUMN_ALERTA_DESCRIPCION = "alerta_descripcion"
        private val COLUMN_ALERTA_NOM_NEGOCIO = "alerta_nom_negocio"
        private val COLUMN_ALERTA_TIPO = "alerta_tipo"
        private val COLUMN_ALERTA_FECHA = "alerta_fecha"
        private val COLUMN_ALERTA_URL_SCREENSHOT = "alerta_url_screenshot"
        private val COLUMN_ALERTA_EVT_ID = "alerta_evt_id"


        //
        private val COLUMN_CONTROL_ALERTA_FAV_ID = "alerta_fav_id"
        private val COLUMN_ALERTA_FAV_TIPO = "alerta_fav_tipo"
        private val COLUMN_ALERTA_FAV_ULTIMAS_ENTRADAS = "alerta_fav_ultimas_entradas"


        //
        private val COLUMN_ALERTA_ESTATUS_ID = "alerta_id"
        private val COLUMN_ALERTA_ESTATUS = "alerta_estatus"



        //
        private val COLUMN_CONFIGURACION_ALERTAS_ID = "control_config_alerta_id"
        private val COLUMN_ALERTA_TOP = "alerta_fav_top"


        //
        private val COLUMN_CONTROL_CONEXION_ID = "control_conexion_id"
        private val COLUMN_IP_PUBLICA = "control_ip_publica"

        //
        private val COLUMN_EQUIPOS_X_MOVIL_ID = "control_equipos_x_movil"
        private val COLUMN_EQUIPO_ID  = "control_equipo_id"
        private val COLUMN_EQUIPO_IP_PUBLICA  = "control_equipo_ip_publica"
        private val COLUMN_EQUIPO_IP_LOCAL   = "control_equipo_ip_local"
        private val COLUMN_EQUIPO_PUERTO   = "control_equipo_puerto"
        private val COLUMN_EQUIPO_ID_HARDWARE = "control_equipo_id_hardware"
        private val COLUMN_EQUIPO_ICONO_ON = "control_equipo_icono_on"
        private val COLUMN_EQUIPO_ICONO_OFF = "control_equipo_icono_off"
        private val COLUMN_EQUIPO_NOMBRE = "control_equipo_nombre"
        private val COLUMN_NEGOCIO_ID = "control_negocio_id"
        private val COLUMN_ACTIVIDAD_MONITOREABLE_ID = "id_accion_monitoreable"

        //
        private val COLUMN_ESTADO_EQUIPO_ID = "control_estado_equipo_id"
        private val COLUMN_ESTADO_EQUIPO_ESTATUS = "control_estado_equipo_estatus"
        private val COLUMN_ESTADO_EQUIPO_NOMBRE  = "control_estado_equipo_nombre"
        private val COLUMN_ESTADO_EQUIPO_HARDWARE = "control_estado_equipo_hardware"
        private val COLUMN_ESTADO_EQUIPO_IP_PUBLICA = "control_estado_equipo_ip_publica"
        private val COLUMN_ESTADO_EQUIPO_IP_LOCAL = "control_estado_equipo_ip_local"
        private val COLUMN_ESTADO_EQUIPO_PUERTO = "control_estado_equipo_puerto"
        private val COLUMN_ESTADO_EQUIPO_CONTROL = "control_estado_equipo_control"


        private val COLUMN_BIOMETRIA_ID  = "seguridad_biometrica_id"
        private val COLUMN_BIOMETRIA_TIPO_ACCESO = "seguridad_biometrica_tipo_acceso"
        private val COLUMN_BIOMETRIA_FECHA_ACCESO  = "seguridad_biometrica_fecha_acceso"
        private val COLUMN_BIOMETRIA_ESTATUS_ACCESO = "seguridad_biometrica_estatus_acceso"



        //
        private val COLUMN_ACCION_MONITOREABLE_ID = "control_accion_monitoreable_id"
        private val COLUMN_ACCION_MONITOREABLE_HARDWARE = "control_accion_monitoreable_hardware"
        private val COLUMN_ACCION_MONITOREABLE_TIPO = "control_accion_monitoreable_tipo"
        private val COLUMN_ACCION_MONITOREABLE_PORCENTAJE = "control_accion_monitoreable_porcentaje"
        private val COLUMN_ACCION_MONITOREABLE_NOM_SVR = "control_accion_monitoreable_nom_svr"
        private val COLUMN_ACCION_MONITOREABLE_ESPACIO_LIBRE = "control_accion_monitoreable_espacio_libre"
        private val COLUMN_ACCION_MONITOREABLE_ESPACIO_TOTAL = "control_accion_monitoreable_espacio_total"
        private val COLUMN_ACCION_MONITOREABLE_TOT_ENVIADOS = "control_accion_monitoreable_tot_enviados"
        private val COLUMN_ACCION_MONITOREABLE_TOT_RECIBIDOS = "control_accion_monitoreable_tot_recibidos"
        private val COLUMN_ACCION_MONITOREABLE_ID_EQUIPO = "control_accion_monitoreable_id_equipo"

        ///
        private val COLUMN_ACCION_MONITOR_ID = "control_accion_monitor_id"
        private val COLUMN_ACCION_MONITOR_TAG = "control_accion_monitor_tag"
        private val COLUMN_ACCION_MONITOR_HARDWARE = "control_accion_monitor_hardware"
        private val COLUMN_ACCION_MONITOR_GRAFICO = "control_accion_monitor_grafico"


        //
        private val COLUMN_ACCION_VISTA_MONITOR_ID = "control_accion_vista_monitor_id"
        private val COLUMN_ACCION_VISTA_MONITOR_TAG = "control_accion_vista_monitor_tag"
        private val COLUMN_ACCION_VISTA_MONITOR_HARDWARE = "control_accion_vista_monitor_hardware"


        //Backup datatable
        private val TABLE_CONTROL_BACKUP_DATABASE_LOCAL = "control_send_backup_database_local"
        private val COLUMN_BACKUP_INTERVALO = "control_send_backup_intervalo"
        private val COLUMN_BACKUP_UNIDAD = "control_send_backup_unidad"
        private val COLUMN_BACKUP_FTP_SERVER = "control_send_backup_ftp_server"
        private val COLUMN_BACKUP_FTP_USER = "control_send_backup_ftp_user"
        private val COLUMN_BACKUP_FTP_PWD = "control_send_backup_ftp_pwd"
        private val COLUMN_BACKUP_FTP_PUERTO = "control_send_backup_ftp_puerto"
        private val COLUMN_BACKUP_FECHA_ULT_ENVIO = "control_send_backup_fecha_utl_envio"

        //Backup Bitacora
        private val TABLE_CONTROL_BACKUP_DATABASE_BITACORA = "control_send_backup_database_bitacora"
        private val COLUMN_BACKUP_BITACORA_INTERVALO = "control_send_backup_bitacora_intervalo"
        private val COLUMN_BACKUP_BITACORA_UNIDAD = "control_send_bitacora_backup_unidad"
        private val COLUMN_BACKUP_FTP_BITACORA_SERVER = "control_send_backup_bitacora_ftp_server"
        private val COLUMN_BACKUP_BITACORA_FECHA_ENVIO  = "control_send_backup_bitacora_fecha_utl_envio"


        private val TABLE_NEGOCIOS = "negocios"
        private val COLUMN_NEGOCIOS_ID = "negocios_id"
        private val COLUMN_NEGOCIOS_NOMBRE = "negocios_nombre"


        //////////////////


        private val TABLE_EQUIPOS = "equipos"
        private val COLUMN_ID_MOVIL_ID = "id_movil_id"
        private val COLUMN_EQUIPO_ID_ = "id_negocio_id"
        private val COLUMN_NOMBRE = "nombre"
        private val COLUMN_IP_PUBLICA_ = "ip_publica"
        private val COLUMN_IP_LOCAL = "ip_local"
        private val COLUMN_PUERTO_LOCAL = "puerto_local"
        private val COLUMN_PUERTO = "puerto"
        private val COLUMN_BOVEDA_SOFTWARE = "boveda_software"
        private val COLUMN_RUTA_ARCHIVOS_LOCALES = "ruta_archivos_locales"
        private val COLUMN_HARDWARE_KEY_ = "hardware_key"
        private val COLUMN_ESTADO_SERVIDOR = "estado_servidor"
        private val COLUMN_ESTADO_ULT_ACT = "estado_ult_act"
        private val COLUMN_ICONO_NEGOCIO_ON = "icono_negocio_on"
        private val COLUMN_ICONO_NEGOCIO_OFF = "icono_negocio_off"
        private val COLUMN_FEC_ACT_ESTATUS = "fec_act_status"
        private val COLUMN_CATNOMBRENEG = "catnombreneg"
        private val COLUMN_ID_CATNEGOCIO = "id_catnegocio"
        private val COLUMN_TIPO_EQ_ICONO_ON = "tipo_eq_icono_on"
        private val COLUMN_TIPO_EQ_ICONO_OFF = "tipo_eq_icono_off"
        private val COLUMN_TIPO_EQ_CLAVE = "tipo_eq_clave"
        private val COLUMN_ID_ACCION_MONITOREABLE = "id_accion_monitoreable"
        private val COLUMN_MODELO_COMUNICACION = "modelo_comunicacion"
        private val COLUMN_WAKE_ON = "wake_on"
        private val COLUMN_ESTADO_EQUIPO = "estado_equipo"
        private val COLUMN_ORDENAMIENTO = "ordenamiento"
        private val COLUMN_EQUIPO_VIRTUAL = "equipo_virtual"
        private val COLUMN_EQUIPO_MNT_COLOR = "color_carrousel_mnt"


        ////////////////////////


        private val TABLE_EQUIPOS_TIPO_LIC = "equipos_tipo_licencia"
        private val COLUMN_EQUIPO_TL_ID = "id_negocio_id"
        private val COLUMN_EQUIPO_TL_UNIMOVIL = "unimovil"


        //////////////////////////////

        private val TABLE_CONTROL_EQUIPOS_API_VM  = "control_equipos_api_vm"
        private val COLUMN_EQUIPO_VM_ID_EQUIPO = "id_equipo"
        private val COLUMN_EQUIPO_VM_URL_AUTH = "url_autenticacion"
        private val COLUMN_EQUIPO_VM_NOM_PROYECTO = "nombre_proyecto"
        private val COLUMN_EQUIPO_VM_ZONA = "zona_vm"
        private val COLUMN_EQUIPO_VM_NOM_INSTANCIA =  "nombre_instancia"
        private val COLUMN_EQUIPO_VM_SCREENSHOT =  "screenshot_vm"

        ///////////////////////

        private val TABLE_ACCIONES_DINAMICAS = "acciones_dinamicas"
        private val COLUMN_ACCIONES_DINAMICAS_ID = "id"
        private val COLUMN_ACCIONES_DINAMICAS_NEGOCIO_ID = "id_negocio_id"
        private val COLUMN_ACCIONES_DINAMICAS_ALIAS_APLICACION = "alias_aplicacion"
        private val COLUMN_ACCIONES_DINAMICAS_ICONO = "icono"
        private val COLUMN_ACCIONES_DINAMICAS_RUTA_APLICACION = "aplicacion_ruta_aplicacion"
        private val COLUMN_ACCIONES_DINAMICAS_NOMBRE_APLICACION = "aplicacion_nombre_aplicacion"
        private val COLUMN_ACCIONES_DINAMICAS_PARAMETROS_APLICACION = "aplicacion_parametros_aplicacion"
        private val COLUMN_ACCIONES_DINAMICAS_EXPLORACION_RUTA = "exploracion_ruta"
        private val COLUMN_ACCIONES_DINAMICAS_ELIMINACION_TIPO = "eliminacion_tipo"
        private val COLUMN_ACCIONES_DINAMICAS_ELIMINACION_NOMBRE_ARCHIVO = "eliminacion_nombre_archivo"
        private val COLUMN_ACCIONES_DINAMICAS_ELIMINACION_RUTA_ARCHIVO = "eliminacion_ruta_archivo"
        private val COLUMN_ACCIONES_DINAMICAS_TIPO_ID = "tipo_id"
        private val COLUMN_ACCIONES_DINAMICAS_GRUPO = "grupo"
        private val COLUMN_ACCIONES_DINAMICAS_COMANDO = "comando"
        private val  COLUMN_ACCIONES_DINAMICAS_FECHA_ALTA = "fecha_alta"
        private val COLUMN_ACCIONES_DINAMICAS_ORIGEN = "origen"
        private val COLUMN_ACCIONES_DINAMICAS_TITULO_APLICACION = "titulo_aplicacion"
        private val COLUMN_ACCIONES_DINAMICAS_ESTADO_PROGRAMA_NOMBRE = "estado_programa_nombre"
        private val COLUMN_ACCIONES_DINAMICAS_SCREENSHOT_TIPO = "screenshot_tipo"
        private val COLUMN_ACCIONES_DINAMICAS_DESCRIPCION = "descripcion"
        private val COLUMN_ACCIONES_DINAMICAS_NOMBRE_EQUIPO = "nombre_equipo"
        private val COLUMN_ACCIONES_DINAMICAS_APLICACION_USUARIO = "aplicacion_usuario"
        private val COLUMN_ACCIONES_DINAMICAS_APLICACION_PWD = "aplicacion_pwd"



        private val  TABLE_CONFIGURACION_MOVIL_MAS_OPCIONES = "configuracion_movil_mas_opciones"
        private val  COLUMN_MS_INTERVALO_ACTUALIZACION_EQUIPOS = "intervalo_actualizacion_moviles"


        //***Configuracion Default
        private val  TABLE_CONFIGURACION_MOVIL_DEFAULT = "configuracion_movil_default"
        private val COLUMN_CONF_DEF_MOVIL_INFO_ALERTAS = "informacion_alertas"
        private val COLUMN_CONF_DEF_MOVIL_ALERTAS_RECIENTES = "alertas_recientes"
        private val COLUMN_CONF_DEF_MOVIL_INFO_MONITORES = "informacion_monitores"
        private val COLUMN_CONF_DEF_MOVIL_ESTATUS_EQUIPOS = "estatus_equipos"
        private val COLUMN_CONF_DEF_MOVIL_INFO_EQUIPOS = "informacion_equipos"

        //****Tabla moniores


        private val TABLE_CONTROL_MONITORES = "monitores"
        private val COLUMN_MONITORES_ID_MOVIL = "id_movil"
        private val COLUMN_MONITORES_ID_ACCION = "idaccion"
        private val COLUMN_MONITORES_ORIGEN = "origen"
        private val COLUMN_MONITORES_COMANDO = "comando"
        private val COLUMN_MONITORES_ACCION = "accion"
        private val COLUMN_MONITORES_PORCENAJE = "porcentaje"
        private val COLUMN_MONITORES_TOTAL_USADO = "total_usado"
        private val COLUMN_MONITORES_ESACIO_LIBRE = "espacio_libre"
        private val COLUMN_MONITORES_PAQUETES_ENVIADOS = "paquetes_enviados"
        private val COLUMN__MONITORES_PAQUETES_RECIBIDOS = "paquetes_recibidos"
        private val COLUMN_MONITORES_NOMBRE_SERVIDOR = "nomServidor"
        private val COLUMN_MONITORES_RED_NEGOCIO = "redNegocio"
        private val COLUMN_MONITORES_ES_FAVORITO = "esFavorito"
        private val COLUMN_MONITORES_FEC_ULT_ACT = "fecUltAct"
        private val COLUMN_MONITORES_ID_EQUIPO = "idEquipo"


        //Detalle Monitores

        private val TABLE_CONTROL_MONITORES_DETALLE = "monitores_detalle"



        //Alertas Eliminadas

        private val TABLE_CONTROL_ALERTAS_ELIMINADAS = "alertas_eliminadas"
        private val COLUMN_ALERTA_ELIMINADA_ID = "alerta_id"
        private val COLUMN_ALERTA_ELIMINADA_TITULO = "alerta_titulo"
        private val COLUMN_ALERTA_ELIMINADA_DESCRIPCION = "alerta_descripcion"
        private val COLUMN_ALERTA_ELIMINADA_NOM_NEGOCIO = "alerta_nom_negocio"
        private val COLUMN_ALERTA_ELIMINADA_TIPO = "alerta_tipo"
        private val COLUMN_ALERTA_ELIMINADA_FECHA = "alerta_fecha"
        private val COLUMN_ALERTA_ELIMINADA_URL_SCREENSHOT = "alerta_url_screenshot"
        private val COLUMN_ALERTA_ELIMINADA_FECHA_ELIMINACION = "alerta_fec_eliminacion"
        private val COLUMN_ALERTA_ELIMINADA_EVT_ID = "alerta_evt_id"



        //Vigencia Licencias

        private val TABLE_VIGENCIA_LICENCIA = "vigencia_licencia"
        private val COLUMN_FECHA_DESDE = "vigencia_desde"
        private val COLUMN_FECHA_HASTA = "vigencia_hasta"
        private val COLUMN_DIAS_FIN_VIGENCIA = "vigencia_dias_fin_vigencia"
        private val COLUMN_INTERVALO_VIGENCIA = "vigencia_intervalo_vigancia"


        //ULTIMA ACTUALIZAICON DE EQUIPOS

        private val TABLE_CONTROL_EQUIPOS_X_MOVIL_ULT_ACT = "control_equipos_x_movil_ult_act"
        private val COLUMN_EQUIPO_ULT_ACT_ID = "control_equipos_ult_act_id"
        private val COLUMN_EQUIPO_ULT_ACT_ID_HARDWARE = "control_equipos_ult_act_id_hardware"
        private val COLUMN_FECHA_ULT_ACT = "control_equipos_fecha_ult_act"

        //SERVIDOR WEBSOCKET

        private val TABLE_SERVER_SOCKET = "control_server_socket"
        private val COLUMN_SERVER_SOCKET = "server_socket"
        private val COLUMN_SERVER_SOCKET_PUERTO = "server_socket_puerto"
        private val COLUMN_SERVER_SOCKET_FEC_ULT_ACT = "server_socket_fec_ult_act"


        //LOG WEBSOCKET

        private val TABLE_SERVER_SOCKET_LOG = "control_server_socket_log"
        private val COLUMN_SERVER_SOCKET_FEC_ALTA = "server_socket_fec_alta"
        private val COLUMN_SERVER_SOCKET_DESCRIPCION  = "server_socket_descripcion"


        //
        private val TABLE_CONTROL_NIP = "control_nip"
        private val COLUMN_NUMERO_INTENTOS = "control_numero_intentos"
        private val COLUMN_NUMERO_INTENTOS_FALLIDOS = "control_numero_intentos_fallidos"


        //
        private val TABLE_EXPLORADOR_ARCHIVOS_X_EQUIPO = "explorador_archivos_x_equipo"
        private val COLUMN_EQUIPO_ID_DIR = "equipo_id"
        private val COLUMN_ID_DIR = "id_directorio"
        private val COLUMN_NOMBRE_DIR = "nombre_directorio"
        private val COLUMN_PATH_DIR = "path_directorio"
        private val COLUMN_PARENT_DIR = "parent_directorio"
        private val COLUMN_TIPO_DIR = "tipo_directorio"
        private val COLUMN_PESO_DIR = "peso_directorio"
        private val  COLUMN_TIPO_ARCHIVO_DIR = "tipo_archivo_directorio"
        private val COLUMN_ULT_MOD = "ultima_mod"

        //

        private val TABLE_CONTROL_ACCIONES_SOCKET_X_EQUIPO = "control_acciones_x_equipo"
        private val COLUMN_EQUIPO_ID_CS = "equipo_id"
        private val COLUMN_NUMERO_ACCION = "numero_acciones"
        private val COLUMN_DESC_ACCION = "descripcion_accion"

        //

        private val TABLE_PERFIL_APP_FILTROS_ENCABEZADO = "perfil_app_filtros_encabezado"
        private val COLUMN_HEADER_ID_NEGOCIO = "header_id_negocio"
        private val COLUMN_HEADER_ID_EQUIPO = "header_id_equipo"
        private val COLUMN_FILTRO_ALERTAS_ESTATUS = "filtro_alertas_estatus"
        private val COLUMN_FILTRO_ALERTAS_EQUIPO = "filtro_alertas_equipo"
        private val COLUMN_FILTRO_ALERTAS_FECHA = "filtro_alertas_fecha"
        private val COLUMN_FILTRO_ALERTAS_FECHA_DESDE_HASTA = "filtro_alertas_fecha_desde_hasta"
        private val COLUMN_FILTRO_ALERTAS_ELIMINADAS_ESTATUS = "filtro_alertas_eliminadas_estatus"
        private val COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA = "filtro_alertas_eliminadas_fecha"
        private val COLUMN_FILTRO_ALERTAS_ELIMINADAS_FECHA_DESDE_HASTA = "filtro_alertas_eliminadas_fecha_desde_hasta"


        //
        private val TABLE_CONTROL_ENTORNO = "control_entorno_conexion"
        private val COLUMN_ENTORNO_ID = "id_entorno"
        private val COLUMN_ENTORNO = "entorno_activo"



        //Historico Screenshot

        private val TABLE_CONTROL_HISTORICO_SCREENSHOT = "control_historico_screenshot"
        private val COLUMN_HSC_ID = "hsc_id"
        private val COLUMN_HSC_ID_EQUIPO = "id_equipo_id"
        private val COLUMN_HSC_NOMBRE_IMAGEN = "hsc_nombre_imagen"
        private val COLUMN_HSC_FECHA_CREACION = "hsc_fecha_creacion"
        private val COLUMN_HSC_URL_SCREENSHOT = "hsc_url_screenshot"
        private val COLUMN_HSC_IMAGE_ESTATUS = "hsc_imagen_status"
        private val COLUMN_HSC_IMAGE_DATA = "hsc_image_data"


        //FILTRO DETALLE ALERTAS
        private val TABLE_DETALLE_FILTRO_ALERTA = "filtro_alertas_detalle"
        private val COLUMN_DET_FILTRO_ALERTA_ID = "det_filtro_id"
        private val COLUMN_DET_FILTRO_ALERTA_TIPO = "det_filtro_tipo"
        private val COLUMN_DET_FILTRO_ALERTA_PALABA_CLAVE = "det_filtro_clave"


        //CONTROL PAUSA ALERTAS
        private val TABLE_CONFIGURACION_PAUSA_ALERTAS = "control_pausa_alertas"
        private val COLUMN_CONFIG_PA_ID = "pa_id"
        private val COLUMN_CONFIG_PA_TIEMPO = "pa_tiempo"
        private val COLUMN_CONFIG_PA_FECHA_HORA_INICIO = "pa_fecha_inicio"
        private val COLUMN_CONFIG_PA_FECHA_HORA_FIN = "pa_fecha_fin"
        private val COLUMN_CONFIG_ESTATUS = "pa_estatus"


        //AUX SCREENSHOT
        private val TABLE_AUX_TEMP_SCREENSHOT = "control_aux_temp_screenshot"
        private val COLUMN_AUX_ID_EQUIPO = "aux_id_equipo"
        private val COLUMN_AUX_IMAGE_DATA = "aux_image_data"


        //TEMPORAL PASO IMAGE
        private val TEMP_PASO_IMAGENES_EXPLORADOR = "tmp_paso_imagenes_explorador"
        private val COLUMN_TMP_IMAGE_DATA = "image_data"

        //TABLA CAMBIOS VERSION
        private val TABLE_CAMBIOS_VERSION = "cambios_ult_version"
        private val COLUMN_VERSION = "version"
        private val COLUMN_CAMBIOS = "cambios"
        private val COLUMN_MUESTRA_MENSAJE = "muestra_mensaje"



        private val TABLE_CONTROL_PARAMS_ACC_DB = "control_params_acc_db"
        private val COLUMN_ID_ACCION = "id_accion"
        private val COLUMN_CAMPO = "campo"
        private val COLUMN_VALOR = "valor"
        private val COLUMN_TIPO = "tipo"
        private val COLUMN_CONSULTA_DB = "consulta_db_act"
        private val COLUMN_CONSULTA_API = "consulta_api_act"



        private val TABLE_CONTROL_USUARIOS_ALERTAS = "control_usuarios_alertas"
        private val COLUMN_USUALT_ID = "id"
        private val COLUMN_USUALT_USUARIO = "usuario"
        private val COLUMN_USUALT_EQUIPO = "equipo"
        private val COLUMN_USUALT_NOMBRE = "nombre"
        private val COLUMN_USUALT_ESTATUS_ACT = "estatus_act"
        private val COLUMN_USUALT_ALERTA = "alerta"
        private val COLUMN_USUALT_ALERTA_MOVIL = "alerta_movil"



    }



}
