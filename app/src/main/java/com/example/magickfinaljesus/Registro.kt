package com.example.magickfinaljesus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
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
            if(isValid()){
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

            }else{

            }
        }

        val getCamera=registerForActivityResult(ActivityResultContracts.TakePicture()){
            if(it){
                img.setImageURI(url_usuario)

            }else{

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

    fun validUser(e: EditText):Boolean{
        var isValid = true
        if(e.text.length<5){
            e.error = "Debe ser mayor a 5 caracteres"
            isValid = false
        }
        return isValid
    }

    fun validPass(e: EditText):Boolean{
        var isValid = true
        if(e.text.length<5){
            e.error = "Debe ser mayor a 5 caracteres"
            isValid = false
        }else if(!e.text.contains("[0-9]".toRegex())){
            e.error = "Debe contener numeros"
            isValid = false
        }else if(e.text.length==0){
            e.error = "Rellena la contraseña"
            isValid = false
        }
        return isValid
    }

    fun validRepass(e: EditText):Boolean{
        var isValid = true
        if(e.text.toString() != pass.text.toString()){
            e.error = "Las contraseñas deben coincidir"
            isValid = false
        }
        return isValid
    }

    fun validEmail(e:EditText):Boolean{
        var isValid = false
        val text = e.text
        if(
            text.isNotEmpty()
            && android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()
        ){
            isValid = true
        }
        else{
            e.error = "El correo no es valido"
        }
        return isValid
    }

    fun isValid():Boolean{
        var validated = true
        val checkers = listOf(
            Pair(nombre, this::validUser),
            Pair(pass, this::validPass),
            Pair(pass2, this::validRepass),
            Pair(correo, this::validEmail)
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