package com.example.magickfinaljesus

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch


lateinit var carta:Cartas
private lateinit var db_ref: DatabaseReference
private lateinit var sto_ref: StorageReference
lateinit var foto:ImageView
lateinit var nombre:TextInputEditText
lateinit var precio:TextInputEditText
lateinit var disponible:Switch
lateinit var blanco:RadioButton
lateinit var negro:RadioButton
lateinit var azul:RadioButton
lateinit var rojo:RadioButton
lateinit var verde:RadioButton
lateinit var editar:Button
private var url_carta: Uri?=null

class EditarCarta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_carta)

        carta= intent.getSerializableExtra("carta") as Cartas
        foto=findViewById(R.id.iv_editarCarta)
        nombre=findViewById(R.id.nombre_editarCarta)
        precio=findViewById(R.id.precio_editarCarta)
        disponible=findViewById(R.id.disponible_editar)
        blanco=findViewById(R.id.blancoEditar)
        negro=findViewById(R.id.negroEditar)
        azul=findViewById(R.id.azulEditar)
        rojo=findViewById(R.id.rojoEditar)
        verde=findViewById(R.id.verdeEditar)
        editar=findViewById(R.id.editarBoton)
        var cambiarColor=""

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()


        db_ref.child("tienda")
            .child("cartas").child(carta.id!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    var pojo_carta= snapshot.getValue(Cartas::class.java)


                    nombre.setText(pojo_carta?.nombre)
                    precio.setText(pojo_carta?.precio.toString())
                    disponible.isChecked= pojo_carta?.disponible!!
                    if (pojo_carta?.color=="blanco"){
                        blanco.isChecked=true
                    }else if(pojo_carta?.color=="negro"){
                        negro.isChecked=true
                    }else if(pojo_carta?.color=="azul"){
                        azul.isChecked=true
                    }else if(pojo_carta?.color=="rojo"){
                        rojo.isChecked=true
                    }else if(pojo_carta?.color=="verde"){
                        verde.isChecked=true
                    }



                    Glide.with(applicationContext).load(pojo_carta?.img).into(foto)


                    foto.setOnClickListener {
                        obtener_url.launch("image/*")
                    }


                    editar.setOnClickListener{

                        if (blanco.isChecked){
                            cambiarColor="blanco"
                        }else if(negro.isChecked){
                            cambiarColor="negro"
                        }else if(azul.isChecked){
                            cambiarColor="azul"
                        }else if(rojo.isChecked){
                            cambiarColor="rojo"
                        }else if(verde.isChecked){
                            cambiarColor="verde"
                        }

                        if(nombre.text.toString().trim().equals("") ||
                            precio.text.toString().trim().equals("")) {

                            Toast.makeText(applicationContext, "Faltan datos", Toast.LENGTH_SHORT).show()

                        }else{


                            var url_carta_firebase:String?=pojo_carta?.img


                            GlobalScope.launch(Dispatchers.IO) {
                                if(!nombre.text.toString().trim().equals(pojo_carta?.nombre) && existe_carta(nombre.text.toString().trim())){
                                    tostadaCorrutina("Este nombre ya lo tiene otra carta")

                                }else{
                                    if(url_carta!=null){
                                        url_carta_firebase=editarImagen(pojo_carta?.id!!,url_carta!!)
                                    }

                                    editarCarta(pojo_carta?.id!!,
                                        nombre.text.toString().trim(),
                                        precio.text.toString().toInt(),
                                        disponible.isChecked!!,
                                        url_carta_firebase!!,
                                        cambiarColor
                                    )
                                    tostadaCorrutina("Datos de la carta modificados")

                                }
                            }

                        }

                    }



                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })
    }

    private fun existe_carta(nombre:String):Boolean{
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

    private fun editarCarta( id:String, nombre:String, precio:Int, disponible:Boolean, img:String, color:String){
        val nueva_carta= Cartas(
            id,
            nombre,
            precio,
            disponible,
            img,
            color
        )
        db_ref.child("tienda").child("cartas").child(id).setValue(nueva_carta)

    }


    private suspend fun editarImagen(id:String,imagen: Uri):String{
        var url_carta_firebase: Uri?=null

        url_carta_firebase=sto_ref.child("tienda").child("cartas").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return url_carta_firebase.toString()
    }

    val obtener_url= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri: Uri?->
        when (uri){

            else->{
                url_carta=uri
                foto.setImageURI(url_carta)

            }
        }
    }

    private suspend fun tostadaCorrutina(texto:String){
        runOnUiThread{
            Toast.makeText(
                applicationContext,
                texto,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}