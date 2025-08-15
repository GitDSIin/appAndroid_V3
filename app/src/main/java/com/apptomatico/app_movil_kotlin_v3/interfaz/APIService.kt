package com.apptomatico.app_movil_kotlin_v3.interfaz



import com.apptomatico.app_movil_kotlin_v3.model.*
import io.reactivex.Completable
import com.apptomatico.app_movil_kotlin_v3.BuildConfig
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface APIService {


    ///CORUTINES

    @Headers("Content-Type: application/json")
    @POST("token-auth/")
    suspend fun getTokenAdministracion(@Body body: com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest): Response<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>


    //GET ACCIONES MONITOREABLES
    @GET("/api/get_acciones_tipo_monitoreables_list")
    suspend fun getCorrutineAccionesMonitoreablesServidor(@Query("id_hardware") id_hardware:String,
                                                          @Query("id_hardware_movil") id_hardware_movil:String,
                                                          @Query("id_cat_negocio") id_cat_negocio:String,
                                                          @Header("Authorization") Token:String,
                                                          @Header("Content-Type") content:String): Response<com.apptomatico.app_movil_kotlin_v3.model.DataListMonitoreables>




    //NEGOCIOMOVIL
    @GET("api/token-auth")
    suspend fun getTCorrutineokenNegocio(@Header("IdHardware") IdHardware:String, @Header("Content-Type") content:String): Response<String>



    @GET("api/monitoreo_servidor_grafico")
    suspend fun getCorrutineMonitorServidorGrafico(@Header("IdAccion") IdAccion:String,
                                                   @Header("Origen") Origen:String,
                                                   @Header("Tipo") Tipo:String,
                                                   @Header("Proceso") Proceso:String,
                                                   @Header("Token") Token:String,
                                                   @Header("Content-Type") content:String): Response<List<com.apptomatico.app_movil_kotlin_v3.model.Monitor_Deatlle_Grefico_List>>




    //Recupera Monitoreables de favoritos
    @GET("api/get_favoritos_monitoreables_movil/")
    suspend fun getCorrutinesFavMonitoreablesXMovil(@Query("id_accion") id_accion:String,
                                                    @Query("id_movil") id_movil:Int,
                                                    @Query("comando") comando:String,
                                                    @Query("accion") accion:String,
                                                    @Query("origen") origen:Int,
                                                    @Header("Authorization") token:String,
                                                    @Header("Content-Type") content:String): Response<com.apptomatico.app_movil_kotlin_v3.model.DataListFavMonitoreables>


    ////////END CORUTINES

    //TOKEN

    @Headers("Content-Type: application/json")
    @POST("token-auth/")
    fun submit(@Body body: com.apptomatico.app_movil_kotlin_v3.model.UserDataRequest): Call<com.apptomatico.app_movil_kotlin_v3.model.UserDataCollectionItem>


    //Configuracin Maestra
    @GET("api/conf_maestra_intentos")
    fun getNoIntLicencia(@Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataConfMaestra>




    //Registra NIP
    @PUT("api/valida_nip/")
    fun newNip(@Query("nip") nip: String, @Query("id_hardware") id_hardware: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.ValNip>


    //Valida NIP
    @GET("api/valida_nip/")
    fun valNip(@Query("nip") nip: String,@Query("id_hardware") id_hardware: String, @Query("ult_latitud") ult_latitud: String, @Query("ult_longitud") ult_longitud: String, @Query("ult_conexion") ult_conexion:String,@Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.ValNip>


    //DISPOSITIVO
    @GET
    fun listUsers(@Path("Usuario") usuariol: String): Call<List<com.apptomatico.app_movil_kotlin_v3.model.UsuariosDataCollectionItem>>

    //Licencia

    @GET("api/licencia/")
    fun getLicencias( @Query("hardware_key") hardware_key: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataValLicencia>


    @GET("api/aceptacion_movil_licencia/")
    fun getValLicencias(@Query("licencia_key") licencia_key: String, @Query("hardware_key") hardware_key: String, @Query("mac_movil") mac_movil: String, @Query("numero_movil_local") numero_movil_local: String, @Query("id_suscriptor") id_suscriptor: String, @Query("token_notificacion") token_notificacion:String, @Query("version_real_so_movil") version_real_so_movil:String, @Header("Authorization") token:String, @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataValLicenciaMovil>


    //Licencia Vencida
    @GET("api/i_licencia_movil_vencida/")
    fun getLicenciaVencida( @Query("hardware_key") hardware_key: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataResVigenciaMovil>



    //RECUPERA USUARIOS PERMITIDOS PARA EJECUCION DEL EQUIPO
    @GET("api/get_all_usuario_autorizados_ejecucion_x_equipo/")
    fun getUsuarioPermitidosEjecEquipo( @Query("id_equipo") id_equipo: Int, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosEjecucion>


    //RECUPERA USUARIOS PERMITIDOS PARA ENVIO DE ALERTA X MOVIL
    @GET("api/get_usuarios_envio_alertas_moviles_negocio/")
    fun getUsuarioPermitidosRecAlertaMovil( @Query("negocio_listado") negocio_listado: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataUsuariosPermitidosRecAlertasMovil>





    //RECUPERA ACCION API EXT
    @GET("api/get_acc_api_ext_x_id/")
    fun getAccApiExtXId( @Query("id_accion") id_equipo: Int, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataApiExtResult>


    //REGISTRO LICENCIA

    @GET("api/registro_licencia/")
    fun getRegLicencias( @Query("hardware_key") hardware_key: String, @Query("intento") intento: Int,  @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>

    @POST("api/registro_licencia/")
    fun postRegLicencias( @Query("hardware_key") hardware_key: String, @Query("intento") intento: Int,  @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataRegLicecnia>


    @PUT("api/registro_licencia/{hardware_key}")
    fun putRegLicencias(@Path("hardware_key") hardware_key: Int, @Body body: com.apptomatico.app_movil_kotlin_v3.model.updRegLicencia, @Header("Authorization") token:String, @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.RegLicencia>



    //List Negocios
    @GET("api/list_moviles_negocios/")
    fun getListNegocios( @Query("id_movil") id_movil: String,
                         @Query("id_negocio") id_negocio:String,
                         @Query("ls_ordenamiento") ls_ordenamiento:String,
                         @Header("Authorization") token:String,
                         @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListNegocios>


    //LISTA ACCIONES PERMITIDAS DEL MOVIL
    @GET("api/list_moviles_acciones/")
    fun getListAccionesMovil(@Query("id_movil") id_movil: Int, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAccionesMovil>



    //Alertas
    // @Headers("Content-Type: application/json")
    @POST("api/alertas/")
    fun newAlerta(@Query("ip_publica") ip_publica: String, @Query("id_hardware") id_hardware: String,@Query("telefono") telefono: String, @Query("mac") mac: String,  @Query("imei") imei: String,  @Query("alerta") alerta: String, @Query("descripcion") descripcion: String, @Query("nombre_dispositivo") nombre_dispositivo: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Completable


    //NEGOCIOMOVIL
    @GET("api/token-auth")
    fun getTokenNegocio(@Header("IdHardware") IdHardware:String, @Header("Content-Type") content:String): Call<String>

    //NEGOCIODIR
    @GET("api/token-auth")
    fun getTokenNegocioDir(@Header("IdHardware") IdHardware:String, @Header("Content-Type") content:String): Call<String>


    //BOVEDA_SOFTWARE_NEGOCIO
    @GET("api/servidor_boveda_software")
    fun getBovedaNegocio(@Query("id_negocio") id_negocio:String,@Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataBovedaNegocio>


    //ACTUALIZA BANDERA ENCENDIDO APAGADO NEGOCIO
    @GET("api/negocio_shutdown")
    fun getEstatusNegocioSvr(@Query("negocio_id") id_negocio:String,@Header("Authorization") token:String,@Header("Content-Type") content:String): Completable



    //ListaFavoritos
    @GET("api/get_favoritos_movil/")
    fun getFavoritosMovil(@Query("idHardwareMovil") idHardwareMovil:String,
                          @Header("Authorization") token:String,
                          @Header("Content-Type") content:String): Call<String>

    //AddFavoritos
    @GET("api/add_favoritos_movil/")
    fun addFavoritosMovil(@Query("idHardwareMovil") idHardwareMovil:String,
                          @Query("id_accion") id_accion:String,
                          @Header("Authorization") token:String,
                          @Header("Content-Type") content:String): Call<String>
    //ListaFavoritos
    @GET("api/lis_detalle_favoritos_movil/")
    fun listDetFavoritosMovil(@Query("idHardwareMovil") idHardwareMovil:String,
                              @Query("id_cat_negocio") id_cat_negocio:String,
                              @Header("Authorization") token:String,
                              @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListFavoritos>


    //DelFavoritos
    @GET("api/del_favoritos_movil/")
    fun delFavoritosMovil(@Query("idHardwareMovil") idHardwareMovil:String,
                          @Query("id_accion") id_accion:String,
                          @Header("Authorization") token:String,
                          @Header("Content-Type") content:String): Call<String>

    @GET("api/confirma_upd_dominio_db_local_movil/")
    fun confirmaUpdDominio(@Query("id_movil") id_movil:String,
                           @Header("Authorization") token:String,
                           @Header("Content-Type") content:String): Call<String>

    //AccionesNegocio
    @GET("api/negocio_acciones")
    fun getListAccionesNegocios(@Query("negocio_id") negocio_id: String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAccionesNegocios>


    //AccionesDinamicas 2021
    @GET("api/moviles_get_acciones_validas_x_negocio/")
    fun getMovilAccionesValidasXNegocio(@Query("id_movil") id_movil:String, @Query("id_negocio") id_negocio:Int, @Header("Authorization") token:String, @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAcionesDinamicas>


    //Consulta Extensiones por Negocio
    @GET("api/get_extensiones_ejecutables_x_negocio")
    fun getListExtensionesNegocios(@Query("id_hardware") id_hardware: String,
                                   @Header("Authorization") token:String,
                                   @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.data_extensiones_x_negocio>



    //Obtiene Alertas del sistema por Movil
    @GET("api/get_alertas_x_movil/")
    fun getAlertasXMovil(@Query("id_movil") id_movil:String, @Query("hardware_movil") hardware_movil:String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>

    //Obtiene Alertas del negocio  por Movil
    @GET("api/get_alertas_x_negocio_x_movil_top/")
    fun getAlertasXNegocioXMovil(@Query("id_movil") id_movil:String, @Query("hardware_movil") hardware_movil:String,@Query("top_alerta") top_alerta:String, @Query("ls_id_usuarios") ls_id_usuarios:String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>


    //Agrega Alertas del negocio  por Movil
    @GET("api/add_alertas_favoritas_x_movil/")
    fun addAlertasXMovil(@Query("id_movil") id_movil:String,
                         @Query("id_alerta") id_alerta:String,
                         @Header("Authorization") token:String,
                         @Header("Content-Type") content:String): Call<String>

    //Elimina Alertas del negocio  por Movil
    @GET("api/del_alertas_favoritas_x_movil/")
    fun delAlertasXMovil(@Query("id_movil") id_movil:String,
                         @Query("id_alerta") id_alerta:String,
                         @Header("Authorization") token:String,
                         @Header("Content-Type") content:String): Call<String>


    //Obtiene las alertas favoritas por moviles
    @GET("api/get_alertas_favoritas_x_movil/")
    fun getAlertasFavoritasXMovil(@Query("id_movil") id_movil:String,
                                  @Query("id_cat_negocio") id_cat_negocio:String,
                                  @Header("Authorization") token:String,
                                  @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListAlertas>


    ///////////////////////////////////////////////


    //NEGOCIOMOVIL


    //Agrega Monitoreables a favoritos
    @GET("api/add_favoritos_monitoreables_movil/")
    fun addFavMonitoreablesXMovil(@Query("id_accion") id_accion:String,
                                  @Query("id_movil") id_movil:Int,
                                  @Query("comando") comando:String,
                                  @Query("accion") accion:String,
                                  @Query("origen") origen:Int,
                                  @Query("tipo_grafico") tipo_grafico:String,
                                  @Header("Authorization") token:String,
                                  @Header("Content-Type") content:String): Call<String>

    //Elimina Monitoreables de favoritos
    @GET("api/del_favoritos_monitoreables_movil/")
    fun delFavMonitoreablesXMovil(@Query("id_accion") id_accion:String,
                                  @Query("id_movil") id_movil:Int,
                                  @Query("comando") comando:String,
                                  @Query("accion") accion:String,
                                  @Query("origen") origen:Int,
                                  @Header("Authorization") token:String,
                                  @Header("Content-Type") content:String): Call<String>



    //Recupera Monitoreables de favoritos
    @GET("api/get_favoritos_monitoreables_movil/")
    fun getFavMonitoreablesXMovil(@Query("id_accion") id_accion:String,
                                  @Query("id_movil") id_movil:Int,
                                  @Query("comando") comando:String,
                                  @Query("accion") accion:String,
                                  @Query("origen") origen:Int,
                                  @Header("Authorization") token:String,
                                  @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListFavMonitoreables>


    //CALCULADORA
    @GET("app/calculadora")
    fun startAPPCalculadora(@Header("IdMovil") IdMovil:String, @Header("NomMovil") NomMovil:String, @Header("bovedaSoftware") bovedaSoftware:String, @Header("token") token:String,@Header("Content-Type") content:String): Call<String>


    //EJECUTA CUALQUIER EXE DEL SERVIDOR
    @GET("app/aplicacion")
    fun startAPPServidor(@Header("IdMovil") IdMovil:String, @Header("NomMovil") NomMovil:String, @Header("ruta") ruta:String, @Header("nombre") nombre:String, @Header("parametros") parametros:String, @Header("token") token:String,@Header("Content-Type") content:String): Call<String>

    //TERMINA CUALQUIER PROCESO O APLICACION DEL SERVIDOR
    @GET("app/terminaproceso")
    fun endAPPServidor(@Header("IdMovil") IdMovil:String, @Header("NomMovil") NomMovil:String, @Header("ruta") ruta:String, @Header("nombre") nombre:String, @Header("parametros") parametros:String, @Header("token") token:String,@Header("Content-Type") content:String): Call<String>



    //ACCIONES SOBRE SERVIDOR
    @GET("api/control_servidor")
    fun setStateServer(@Header("Accion") Accion:String, @Header("Codigo") Codigo:String,@Header("IdMovil") IdMovil:String,@Header("NomMovil") NomMovil:String, @Header("NomNegocio") NomNegocio:String, @Header("Token") token:String,@Header("Content-Type") content:String): Call<String>


    //GET TODAS LAS  ACCIONES MONITOREABLES

    //GET ACCIONES MONITOREABLES
    @GET("/api/get_acciones_tipo_monitoreables_list")
    fun getAccionesMonitoreablesServidor(@Query("id_hardware") id_hardware:String,
                                         @Query("id_hardware_movil") id_hardware_movil:String,
                                         @Query("id_cat_negocio") id_cat_negocio:String,
                                         @Header("Authorization") Token:String,
                                         @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataListMonitoreables>

    //GET ACTUALIZA NIP
    @GET("api/actualiza_nip")
    fun getActualizaNIP(@Query("nip") nip:String,
                        @Query("id_hardware") id_hardware:String,
                        @Header("Authorization") Token:String,
                        @Header("Content-Type") content:String): Call<ResponseBody>

    @GET("api/solicitud_recupera_nip_movil")
    fun recuperaNipMovil(@Query("hardware_key") hardware_key:String,
                         @Header("Authorization") token:String,
                         @Header("Content-Type") content:String): Call<ResponseBody>



    //MONITOREO SOBRE SERVIDOR
    @GET("api/monitoreo_servidor_grafico")
    fun getMonitorServidorGrafico(@Header("IdAccion") IdAccion:String,
                                  @Header("Origen") Origen:String,
                                  @Header("Tipo") Tipo:String,
                                  @Header("Proceso") Proceso:String,
                                  @Header("Token") Token:String,
                                  @Header("Content-Type") content:String): Call<List<com.apptomatico.app_movil_kotlin_v3.model.Monitor_Deatlle_Grefico_List>>


    //MONITOREO SOBRE SERVIDOR
    @GET("api/monitoreo")
    fun getMonitorServidor(@Header("IdAccion") IdAccion:String,
                           @Header("Origen") Origen:String,
                           @Header("Tipo") Tipo:String,
                           @Header("Proceso") Proceso:String,
                           @Header("Token") Token:String,
                           @Header("Content-Type") content:String): Call<List<com.apptomatico.app_movil_kotlin_v3.model.Monitor_List>>



    //CONSULTA DINAMICA A BASE DE DATOS LOCAL DEL SERVIDOR
    @GET("/api/consulta_programa_en_ejecucion")
    fun getInfoProgramSvr(@Header("Proceso") Proceso:String,
                          @Header("Token") Token:String,
                          @Header("Content-Type") content:String): Call<ResponseBody>


    //CONSULTA DINAMICA A BASE DE DATOS LOCAL DEL SERVIDOR
    @GET("/api/consulta/base_srv_local")
    fun getConsultaDinamicaBdSvr(@Header("origen") origen:String,
                                 @Header("id_accion") id_accion:String,
                                 @Header("id_negocio") id_negocio:String,
                                 @Header("tipo_accion") tipo_accion:String,
                                 @Header("Token") Token:String,
                                 @Header("Content-Type") content:String): Call<ResponseBody>


    //GETARTICULOSSERVIDOR
    @GET("/api/catalogo/articulos")
    fun getArticulosServidor( @Header("id_negocio") id_negocio:String, @Header("Token") Token:String,@Header("Content-Type") content:String): Call<List<com.apptomatico.app_movil_kotlin_v3.model.Articulos_List>>

    //POSTARTICULOSSERVIDOR
    @POST("/api/catalogo/articulos")
    fun postArticulosServidor(@Header("id_negocio") id_negocio:String, @Body body: com.apptomatico.app_movil_kotlin_v3.model.add_Articulos, @Header("Token") Token:String, @Header("Content-Type") content:String): Call<String>


    //POSTARTICULOSSERVIDOR
    @DELETE("/api/catalogo/articulos")
    fun deleteArticulosServidor(@Query("id") id: Int, @Header("id_negocio") id_negocio:String,  @Header("Token") Token:String,@Header("Content-Type") content:String): Call<String>


    //PUTARTICULOSSERVIDOR
    @PUT("/api/catalogo/articulos")
    fun putArticulosServidor(@Body body: com.apptomatico.app_movil_kotlin_v3.model.upd_Articulos, @Header("id_negocio") id_negocio:String, @Header("Token") Token:String, @Header("Content-Type") content:String): Call<String>







    //CONSULTAHORARIOLABORAL
    @GET("/api/horario_laboral_moviles/")
    fun getHorarioMovil(@Query("id_movil") id_movil:Int,@Query("id_negocio") id_negocio:Int, @Query("hardware_key") hardware_key:String, @Header("Authorization") token:String,@Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataHorariosMovil>


    //CONSULTA ACTUALIZACIONES DEL SOFTWARE
    @GET("api/busca_actualizaciones_movil/")
    fun getUltVersionMoviles(@Query("id_hardware") id_hardware: String, @Query("aplicacion") aplicacion: String, @Query("version") version: String, @Header("Authorization") token: String, @Header("Content-Type") content: String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataVersionSoftwareMoviles>


    @GET("api/aux_actualiza_token_fmc_movil/")
    fun setActTokenFMCMovil(@Query("id_hardware") id_hardware: String, @Query("token_notificacion") token_notificacion: String, @Header("Authorization") token: String, @Header("Content-Type") content: String): Call<ResponseBody>


    //CONSULTA MAC DEL EQUIPO
    @GET("api/get_dir_mac_x_equipo/")
    fun getDirMacXEquipo(@Query("id_hardware") id_hardware: String, @Query("adaptador") adaptador: String, @Query("dir_mac") dir_mac: String, @Header("Authorization") token: String, @Header("Content-Type") content: String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataDirMacEquipo>






    //GETDIRECTORIONEGOCIO
    @GET("/document/viewer")
    fun getDirectorioServidor(@Header("Ruta") Ruta:String,  @Header("Token") Token:String,@Header("Content-Type") content:String): Call<List<com.apptomatico.app_movil_kotlin_v3.model.Negocio_Directorio>>


    //OBTIENEARCHIVOSERVIDOR
    @GET("/api/updruta")
    fun getFileServidor(@Header("Ruta") Ruta:String, @Header("Nombre") Nombre:String,  @Header("Token") Token:String,@Header("Content-Type") content:String): Call<String>

    //OBTIENESCREENSHOTNEGOCIO
    @GET("/api/get_screenshot")
    fun getSreenShotNegocio(@Header("Nombre") Nombre: String,
                            @Header("Ruta") Ruta:String,
                            @Header("IdHardware") IdHardware:String,
                            @Header("IdHardawareMovil") IdHardawareMovil:String,
                            @Header("Token") Token:String,
                            @Header("Content-Type") content:String): Call<String>


    //ELIMINA ARCHIVO O DIRECTORIO NEGOCIO
    @GET("/api/del_archivo_directorio")
    fun delArchDirNegocio(@Header("Ruta") Ruta:String,
                          @Header("Nombre") Nombre:String,
                          @Header("Tipo") Tipo:String,
                          @Header("Copia") Copia:Boolean,
                          @Header("Token") Token:String,
                          @Header("Content-Type") content:String): Call<String>


    //CLONA ARCHIVO O DIRECTORIO NEGOCIO
    @GET("/api/clonar_archivo_directorio")
    fun clonarArchDirNegocio(@Header("Ruta") Ruta:String,
                             @Header("Nombre") Nombre:String,
                             @Header("Tipo") Tipo:String,
                             @Header("Token") Token:String,
                             @Header("Content-Type") content:String): Call<String>



    //ELIMINAARCHIVOTEMPORAL
    @GET("/api/delruta")
    fun delFileServidor( @Header("Nombre") Nombre:String,  @Header("Token") Token:String,@Header("Content-Type") content:String): Call<String>















    @Headers("Content-Type: application/json")
    @POST("/{tabla}")
    fun newUser(@Path("tabla") tabla: String, @Body body: com.apptomatico.app_movil_kotlin_v3.model.Usuario_List): Completable

    @Headers("Content-Type: application/json")
    @PUT("/{tabla}")
    fun updUsuarios(@Path("tabla") tabla: String, @Body body: com.apptomatico.app_movil_kotlin_v3.model.Usuario_Upd): Completable

    @DELETE("/{tabla}")
    fun deleteUsuarios(@Path("tabla") tabla: String,@Query("id") id: Int): Completable


    //ENCIENDE EQUIPO GC VM
    @GET("api/inicia_instancia_vm_gc/")
    fun getEnciendeEquipo(@Query("id_hardware") id_hardware:String,
                          @Header("Authorization") Token:String,
                          @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>


    //SCREENSHOT EQUIPO GC VM
    @GET("api/screenshot_instancia_vm_gc/")
    fun getScreenshotEquipoGcVm(@Query("id_hardware") id_hardware:String,
                                @Header("Authorization") Token:String,
                                @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataStatusEncendidoEquipo>



    //GET CATNEGOCIO
    @GET("api/get_catnegocios_list/")
    fun getListCatNegocios(@Query("id_hardware") id_hardware:String,
                           @Header("Authorization") Token:String,
                           @Header("Content-Type") content:String): Call<com.apptomatico.app_movil_kotlin_v3.model.DataCatNegocio_List>


    companion object {

        fun create(): APIService {

            val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://${BuildConfig.DOMINIO_PORTAL}/")
                .build()

            return retrofit.create(APIService::class.java)
        }


    }




}