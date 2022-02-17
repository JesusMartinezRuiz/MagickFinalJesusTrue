package com.example.magickfinaljesus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch

class VerMiembros : AppCompatActivity() {

    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference
    lateinit var evento:Eventos
    lateinit var listaMiembros:ArrayList<ReservaEventos>

    lateinit var recycler:RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_miembros)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        listaMiembros=ArrayList()



        evento=intent.getSerializableExtra("evento")  as Eventos


        db_ref.child("tienda")
            .child("reservas_eventos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMiembros.clear()
                    GlobalScope.launch(Dispatchers.IO) {
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val pojo_pedido = hijo?.getValue(ReservaEventos::class.java)

                            //USAMOS EL SEMAFORO PARA SINCRONIZAR: LINEALIZAMOS EL CODIGO

                            var semaforo = CountDownLatch(1)

                            db_ref.child("tienda")
                                .child("usuarios")
                                .child(pojo_pedido!!.id_usuario!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pojo_user = snapshot?.getValue(Usuario::class.java)
                                        //RELLENAR LOS DATOS PARA EL ADAPTADOR
                                        pojo_pedido.nombreUser= pojo_user!!.nombre

                                        semaforo.countDown()

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        println(error.message)
                                    }
                                })

                            semaforo.await();

                                listaMiembros.add(pojo_pedido!!)


                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        recycler=findViewById(R.id.rvVerMiembros)
        recycler.adapter=AdaptadorVerMiembros(listaMiembros,EiActivity().contextoEiActivity)
        recycler.layoutManager= LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)
    }

}