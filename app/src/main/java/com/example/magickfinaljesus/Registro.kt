package com.example.magickfinaljesus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Button
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

class Registro : AppCompatActivity() {

    lateinit var img: ImageView
    lateinit var nombre: TextInputEditText
    lateinit var pass: TextInputEditText
    lateinit var pass2: TextInputEditText
    lateinit var correo: TextInputEditText
    lateinit var registrarse: Button
    var url_usuario: Uri?=null

    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        img=findViewById(R.id.registro_iv_foto)
        nombre=findViewById(R.id.registro_et_nombre)
        pass=findViewById(R.id.registro_et_pass)
        pass2=findViewById(R.id.registro_et_pass2)
        correo=findViewById(R.id.registro_et_correo)
        registrarse=findViewById(R.id.registro_btn)


        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference


        registrarse.setOnClickListener {
            if(pass.text.toString()==""||pass2.text.toString()==""||nombre.text.toString()==""||correo.text.toString()==""){
                Toast.makeText(applicationContext,
                    "Porfavor, rellene todos los campos",
                    Toast.LENGTH_SHORT).show()
            }else if(pass.text.toString()!=pass2.text.toString()){
                Toast.makeText(applicationContext,
                    "Las contraseñas deben coincidir",
                    Toast.LENGTH_SHORT).show()

            } else{

                val identificador=db_ref.child("tienda")
                    .child("usuarios").push().key


                val fecha= Calendar.getInstance()
                val today=("${fecha.get(Calendar.YEAR)}-${fecha.get(Calendar.MONTH)+1}-${fecha.get(
                    Calendar.DAY_OF_MONTH)}").toString()



                GlobalScope.launch(Dispatchers.IO) {


                    if (url_usuario != null) {



                            val androidId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                            val url_firebase = sto_ref.child("tienda")
                                .child("usuarios")
                                .child(identificador!!)
                                .putFile(url_usuario!!)
                                .await().storage.downloadUrl.await()


                            val nuevo_usuario = Usuario(
                                identificador,
                                nombre.text.toString().trim(),
                                pass.text.toString().trim(),
                                "1",
                                today.toString(),
                                false,
                                correo.text.toString().trim(),
                                url_firebase.toString(),
                                Estado.CREADO,
                                androidId

                            )

                            if (existe_usuario(nombre.text.toString().trim())) {
                                tostadaCorrutina("El usuario introducido no está disponible")

                            } else {
                                db_ref.child("tienda")
                                    .child("usuarios")
                                    .child(identificador!!)
                                    .setValue(nuevo_usuario)

                                tostadaCorrutina("Usuario Registrado")


                                val actividad = Intent(applicationContext, MainActivity::class.java)
                                startActivity(actividad)
                            }



                    } else {
                        val androidId =
                            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                        val nuevo_usuario = Usuario(
                            identificador,
                            nombre.text.toString().trim(),
                            pass.text.toString().trim(),
                            "1",
                            today.toString(),
                            false,
                            correo.text.toString().trim(),
                            "",
                            Estado.CREADO,
                            androidId

                        )

                        db_ref.child("tienda")
                            .child("usuarios")
                            .child(identificador!!)
                            .setValue(nuevo_usuario)

                        tostadaCorrutina("Usuario Registrado")


                        val actividad = Intent(applicationContext, MainActivity::class.java)
                        startActivity(actividad)

                    }

                }

            }
        }


        val getCamera=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                img.setImageURI(url_usuario)

            }else{
                Toast.makeText(applicationContext, "No se ha hechado una foto", Toast.LENGTH_SHORT).show()
            }
        }




        img.setOnClickListener {
            obtener_url.launch("image/*")

        }



        img.setOnLongClickListener{
            val ficheroFoto=crearFicheroImagen()
            url_usuario= FileProvider.getUriForFile(applicationContext,"com.example.magickfinaljesus.fileprovider",ficheroFoto)
            getCamera.launch(url_usuario)
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

            url_usuario=uri

            img.setImageURI(uri)

            Toast.makeText(applicationContext,"Imagen seleccionada",
                Toast.LENGTH_SHORT).show()
        }

    }

    private suspend fun existe_usuario(nombre:String):Boolean{
        var resultado:Boolean?=false

        val semaforo= CountDownLatch(1)
        db_ref.child("tienda")
            .child("usuarios")
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



    suspend fun insertarimg(id:String,imagen:Uri):String{
        return ""
    }


    private suspend fun insertarClub(id:String,nombre:String,ciudad:String,fundacion:Int,url_firebase:String){

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