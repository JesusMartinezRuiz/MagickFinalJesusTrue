package com.example.magickfinaljesus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.magikfinaljesus.ui.home.HomeFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

class CrearCarta : AppCompatActivity() {

    lateinit var img: ImageView
    lateinit var nombre: TextInputEditText
    lateinit var precio: TextInputEditText

    lateinit var crear: Button
    lateinit var blanco:RadioButton
    lateinit var negro:RadioButton
    lateinit var azul:RadioButton
    lateinit var rojo:RadioButton
    lateinit var verde:RadioButton
    lateinit var disponible:Switch
    var url_carta: Uri?=null

    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_carta)


        img=findViewById(R.id.iv_crearCarta)
        nombre=findViewById(R.id.nombre_crearCarta)
        precio=findViewById(R.id.precio_crearCarta)
        crear=findViewById(R.id.btn_crearCarta)
        blanco=findViewById(R.id.blancoCrear)
        negro=findViewById(R.id.negroCrear)
        azul=findViewById(R.id.azulCrear)
        rojo=findViewById(R.id.rojoCrear)
        verde=findViewById(R.id.verdeCrear)
        disponible=findViewById(R.id.disponible_crear_carta)
        var color=""




        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference

        crear.setOnClickListener {

            if (blanco.isChecked){
                color="blanco"
            }else if (negro.isChecked){
                color="negro"
            }else if (azul.isChecked){
                color="azul"
            }else if (rojo.isChecked){
                color="rojo"
            }else if (verde.isChecked){
                color="verde"
            }


                val identificador=db_ref.child("tienda")
                    .child("cartas").push().key



                GlobalScope.launch(Dispatchers.IO) {


                    if (url_carta != null) {

                        val url_firebase = sto_ref.child("tienda")
                            .child("cartas")
                            .child(identificador!!)
                            .putFile(url_carta!!)
                            .await().storage.downloadUrl.await()


                        val nueva_carta = Cartas(
                            identificador,
                            nombre.text.toString().trim(),
                            precio.text.toString().toInt(),
                            disponible.isChecked,
                            url_firebase.toString(),
                            color

                        )

                        if (existe_carta(nombre.text.toString().trim())) {
                            tostadaCorrutina("Ya hay una carta con este nombre")

                        } else {
                            db_ref.child("tienda")
                                .child("cartas")
                                .child(identificador!!)
                                .setValue(nueva_carta)

                            tostadaCorrutina("Carta creada")


                        }



                    } else {
                        val androidId =
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                        val nueva_carta = Cartas(
                            identificador,
                            nombre.text.toString().trim(),
                            precio.text.toString().toInt(),
                            disponible.isChecked,
                            "",
                            color

                        )

                        db_ref.child("tienda")
                            .child("cartas")
                            .child(identificador!!)
                            .setValue(nueva_carta)

                        tostadaCorrutina("Carta creada")


                    }

                }


                val actividad = Intent(applicationContext,EiActivity::class.java)
                startActivity (actividad)


        }


        val getCamera=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                img.setImageURI(url_carta)

            }else{
                Toast.makeText(applicationContext, "No se ha hechado una foto", Toast.LENGTH_SHORT).show()
            }
        }




        img.setOnClickListener {
            obtener_url.launch("image/*")

        }



        img.setOnLongClickListener{
            val ficheroFoto=crearFicheroImagen()
            url_carta= FileProvider.getUriForFile(applicationContext,"com.example.magickfinaljesus.fileprovider",ficheroFoto)
            getCamera.launch(url_carta)
            return@setOnLongClickListener true
        }



    }

    val obtener_url= registerForActivityResult(ActivityResultContracts.GetContent())
    {
            uri:Uri?->

        if (uri==null){
            Toast.makeText(applicationContext,"No has seleccionado una imagen",
                Toast.LENGTH_SHORT).show()
        }else{

            url_carta=uri

            img.setImageURI(uri)

            Toast.makeText(applicationContext,"Imagen seleccionada",
                Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun existe_carta(nombre:String):Boolean{
        var resultado:Boolean?=false

        val semaforo= CountDownLatch(1)
        db_ref.child("tienda")
            .child("cartas")
            .orderByChild("nombre")
            .equalTo(nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        resultado=true;
                    }
                    semaforo.countDown()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        semaforo.await();

        return resultado!!;
    }


    private fun crearFicheroImagen(): File {
        val cal: Calendar?= Calendar.getInstance()
        val timeStamp:String?= SimpleDateFormat("yyyyMMdd_HHmmss").format(cal!!.time)
        val nombreFichero:String?="JPGE_"+timeStamp+"_"
        val carpetaDir: File?=applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val ficheroImagen: File?= File.createTempFile(nombreFichero!!,".jpg",carpetaDir)

        return ficheroImagen!!
    }


    suspend fun tostadaCorrutina(texto:String){
        runOnUiThread({
            Toast.makeText(
                applicationContext,
                texto,
                Toast.LENGTH_SHORT
            ).show()
        })
    }

}