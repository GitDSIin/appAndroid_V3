package com.apptomatico.app_movil_kotlin_v3.usuario

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.apptomatico.app_movil_kotlin_v3.R
import com.apptomatico.app_movil_kotlin_v3.sql.DatabaseHelper
import com.rommansabbir.animationx.Zoom
import com.rommansabbir.animationx.animationXFade

class avatar_usuario: AppCompatActivity() {


    var databaseHelper_Data: DatabaseHelper = DatabaseHelper.getInstance(this@avatar_usuario)


    // One Button
    var BSelectImage: Button? = null

    var avatar_image_search: ImageView? = null
    var avatar_image_delete: ImageView? = null

    // One Preview Image
    var IVPreviewImageDF: ImageView? = null
    var IVPreviewImage: ImageView? = null

    // constant to compare
    // the activity result code
    var SELECT_PICTURE = 200


    var btnCancelarImageAvatar: Button? = null

    var btnGuardarImageAvatar: Button? = null
    var selectedImageUri: Uri? = null
    var btnEliminarAvatar: TextView? = null
    var btnGuardarAvatar: ImageView? = null
    private var btnModalAvatarReturn: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_avatar_user)
        //val tab_layout = findViewById<TabLayout>(R.id.tab_layout)
        //tab_layout.addTab(tab_layout.newTab().setText("Seleccionar foto"))

        // register the UI widgets with their appropriate IDs
        BSelectImage = findViewById(R.id.BSelectImage)
        btnModalAvatarReturn = findViewById<ImageView>(R.id.btnModalAvatarReturn)
        btnGuardarAvatar = findViewById(R.id.avatar_image_save)
        avatar_image_search = findViewById<ImageView>(R.id.avatar_image_search)
        avatar_image_delete = findViewById(R.id.avatar_image_delete )
        IVPreviewImageDF = findViewById(R.id.IVPreviewImageDf)
        IVPreviewImage = findViewById(R.id.IVPreviewImage)
        val imgUri = Uri.parse("android.resource://$packageName/drawable/usuario512")
        IVPreviewImageDF!!.setImageURI(imgUri)

        var IVPreviewImageAct = findViewById<ImageView>(R.id.IVPreviewImageAct)
        btnEliminarAvatar = findViewById(R.id.btnEliminarAvatar)
        var imgbd : Bitmap? = databaseHelper_Data.getAvatarUser()
        if(imgbd != null) {


            val d: Drawable = BitmapDrawable(resources, Bitmap.createScaledBitmap(imgbd, 100, 100, true))

            IVPreviewImage!!.setImageBitmap(imgbd)
            IVPreviewImage!!.visibility = View.VISIBLE
            IVPreviewImageDF!!.visibility = View.GONE
            avatar_image_search!!.visibility = View.GONE
            avatar_image_delete!!.visibility = View.VISIBLE
        }else{
            //val imgUri = Uri.parse("android.resource://$packageName/drawable/usuario512")
            //IVPreviewImage!!.setImageURI(imgUri)
            IVPreviewImage!!.visibility = View.GONE
            IVPreviewImageDF!!.visibility = View.VISIBLE
            avatar_image_search!!.visibility = View.VISIBLE
            avatar_image_delete!!.visibility = View.GONE
        }

        avatar_image_search!!.setOnClickListener {
            imageChooser()
        }


        avatar_image_delete!!.setOnClickListener {
            avatar_image_delete!!.animationXFade(Zoom.ZOOM_IN)
            var dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("¿Está seguro de que desea eliminar esta imagen?")
                .setCancelable(true)
                .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                    databaseHelper_Data.deleteAvatarUser()
                    IVPreviewImage!!.setImageDrawable(null)

                    IVPreviewImage!!.visibility = View.GONE
                    IVPreviewImageDF!!.visibility = View.VISIBLE
                    //val imgUri = Uri.parse("android.resource://$packageName/drawable/usuario512")
                    //IVPreviewImage!!.setImageURI(imgUri)
                    avatar_image_delete!!.visibility = View.GONE
                    avatar_image_search!!.visibility = View.VISIBLE

                })

                .setNeutralButton("Cancelar", DialogInterface.OnClickListener { view, _ ->
                    view.dismiss()
                })
            var alert = dialogBuilder.create()
            alert.setTitle("¡Aviso!")
            alert.show()

        }



        btnCancelarImageAvatar = findViewById(R.id.btnCancelarImageAvatar)
        btnModalAvatarReturn!!.setOnClickListener {
            btnModalAvatarReturn!!.animationXFade(Zoom.ZOOM_IN)

            val output = Intent()
            output.putExtra("ACTUALIZA-MENU", 1)
            setResult(2,output)
            finish()
            // val accountsIntent = Intent(this@avatar_usuario, NegocioActivity::class.java)
            // startActivity(accountsIntent)

        }

        btnGuardarImageAvatar = findViewById(R.id.btnGuardarImageAvatar)
        btnGuardarAvatar!!.setOnClickListener {
            btnGuardarAvatar!!.animationXFade(Zoom.ZOOM_IN)
            if (IVPreviewImage!!.drawable != null){
                databaseHelper_Data.deleteAvatarUser()
                val bm = (IVPreviewImage!!.drawable as BitmapDrawable).bitmap
                databaseHelper_Data.addUserAvatar(bm)

                //val accountsIntent = Intent(this@avatar_usuario, NegocioActivity::class.java)
                // startActivity(accountsIntent)
                val output = Intent()
                output.putExtra("ACTUALIZA-MENU", 1)
                setResult(2,output)
                finish()
            }else{
                var dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("¡Seleccione una imagen de perfil!")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()

                    })


                var alert = dialogBuilder.create()
                alert.setTitle("¡Aviso!")
                alert.show()
            }


        }


        IVPreviewImage!!.setOnClickListener {
            if(IVPreviewImage!!.drawable != null) {

                //  showBottomSheetDialog()
            }
        }


    }

    // this function is triggered when
    // the Select Image Button is clicked
    fun imageChooser() {

        // create an instance of the
        // intent of the type image
        val i = Intent()
        i.type = "image/*"
        i.action = Intent.ACTION_GET_CONTENT

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Seleccione una imagen"), SELECT_PICTURE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {




                selectedImageUri = data!!.data

                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    IVPreviewImage!!.background = null
                    IVPreviewImage!!.setImageURI(selectedImageUri)
                    IVPreviewImage!!.visibility = View.VISIBLE
                    IVPreviewImageDF!!.visibility = View.GONE
                    avatar_image_delete!!.visibility = View.VISIBLE
                    avatar_image_search!!.visibility = View.GONE
                }
            }
        }
    }






}