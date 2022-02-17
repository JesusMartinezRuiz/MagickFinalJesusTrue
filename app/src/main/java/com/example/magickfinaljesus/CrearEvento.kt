package com.example.magickfinaljesus

import android.content.Intent
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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


class CrearEvento : AppCompatActivity() {

    lateinit var img:ImageView
    lateinit var nombre:TextInputEditText
    lateinit var precio:TextInputEditText
    lateinit var aforoMax:TextInputEditText
    lateinit var crear:Button
    var url_evento: Uri?=null
    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_evento)

        img=findViewById(R.id.iv_crearEvento)
        nombre=findViewById(R.id.nombre_crearEvento)
        precio=findViewById(R.id.precio_crearEvento)
        aforoMax=findViewById(R.id.aforoMax_crearEvento)
        crear=findViewById(R.id.boton_crearEvento)

        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference


        crear.setOnClickListener {

            if(isValid()){
                val identificador=db_ref.child("tienda")
                    .child("eventos").push().key


                val fecha= Calendar.getInstance()
                val today=("${fecha.get(Calendar.YEAR)}-${fecha.get(Calendar.MONTH)+1}-${fecha.get(
                    Calendar.DAY_OF_MONTH)}").toString()



                GlobalScope.launch(Dispatchers.IO) {


                    if (url_evento != null) {

                        val url_firebase = sto_ref.child("tienda")
                            .child("eventos")
                            .child(identificador!!)
                            .putFile(url_evento!!)
                            .await().storage.downloadUrl.await()


                        val nuevo_evento = Eventos(
                            identificador,
                            nombre.text.toString().trim(),
                            precio.text.toString().toFloat(),
                            aforoMax.text.toString().toInt(),
                            0,
                            today,
                            url_firebase.toString()

                        )

                        if (existe_carta(nombre.text.toString().trim())) {
                            tostadaCorrutina("Ya hay una carta con este nombre")

                        } else {
                            db_ref.child("tienda")
                                .child("eventos")
                                .child(identificador!!)
                                .setValue(nuevo_evento)

                            tostadaCorrutina("Evento Creado")


                        }



                    } else {

                        val nuevo_evento = Eventos(
                            identificador,
                            nombre.text.toString().trim(),
                            precio.text.toString().toFloat(),
                            aforoMax.text.toString().toInt(),
                            0,
                            today,
                            ""

                        )

                        db_ref.child("tienda")
                            .child("eventos")
                            .child(identificador!!)
                            .setValue(nuevo_evento)

                        tostadaCorrutina("Evento Creado")


                    }

                }

                val actividad = Intent(applicationContext,EiActivity::class.java)
                startActivity (actividad)


            }else{

            }

        }


        val getCamera=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                img.setImageURI(url_evento)

            }else{
                Toast.makeText(applicationContext, "No se ha hechado una foto", Toast.LENGTH_SHORT).show()
            }
        }




        img.setOnClickListener {
            obtener_url.launch("image/*")

        }



        img.setOnLongClickListener{
            val ficheroFoto=crearFicheroImagen()
            url_evento= FileProvider.getUriForFile(applicationContext,"com.example.magickfinaljesus.fileprovider",ficheroFoto)
            getCamera.launch(url_evento)
            return@setOnLongClickListener true
        }



    }

    val obtener_url= registerForActivityResult(ActivityResultContracts.GetContent())
    {
            uri: Uri?->

        if (uri==null){

        }else{

            url_evento=uri

            img.setImageURI(uri)



        }

    }

    private suspend fun existe_carta(nombre:String):Boolean{
        var resultado:Boolean?=false

        val semaforo= CountDownLatch(1)
        db_ref.child("tienda")
            .child("eventos")
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

    fun validNombre(e: EditText):Boolean{
        var isValid = true
        if(e.text.length<5){
            e.error = "Debe ser mayor a 5 caracteres"
            isValid = false
        }
        return isValid
    }
    fun validPrecio(e: EditText):Boolean{
        var isValid = true
        if(e.text.length==0){
            e.error = "Inserta un precio"
            isValid = false
        }
        return isValid
    }

    fun validAforoMax(e: EditText):Boolean{
        var isValid = true
        if(e.text.length==0){
            e.error = "Inserta un Aforo"
            isValid = false
        }else if(e.text.length<1){
            e.error = "Debe ser mayor a 1 caracter"
            isValid = false
        }
        return isValid
    }


    fun isValid():Boolean{
        var validated = true
        val checkers = listOf(
            Pair(nombre, this::validNombre),
            Pair(precio, this::validPrecio),
            Pair(aforoMax, this::validAforoMax)

            )
        for(c in checkers){
            val x = c.first
            val f = c.second
            val y = f(x)
            validated = y
            if(!validated) break
        }
        return validated
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