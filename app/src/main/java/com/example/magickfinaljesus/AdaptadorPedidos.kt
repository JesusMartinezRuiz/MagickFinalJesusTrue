package com.example.magickfinaljesus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowPedidosBinding
import com.google.firebase.database.*

private lateinit var db_ref: DatabaseReference

class AdaptadorPedidos(val elementos: List<ReservaCarta>, val con: EiActivity) :
    RecyclerView.Adapter<AdaptadorPedidos.ViewHolder>() {


    class ViewHolder(val bind: RowPedidosBinding) : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        db_ref = FirebaseDatabase.getInstance().getReference()
        val v =
            RowPedidosBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elem = elementos[position]

        with(holder.bind) {

            db_ref.child("tienda").child("reservas_carta").child(elem.id.toString()).child("aceptado")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var aceptado = snapshot.getValue()
                       if (aceptado==true){
                           aceptarPedido.text = "Aceptado"
                           aceptarPedido.isEnabled = false
                       }
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })

        db_ref.child("tienda").child("cartas").child(elem.id_carta.toString()).child("nombre")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var nombreCarta = snapshot.getValue()
                    nombreUserPedido.text = nombreCarta.toString()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

            db_ref.child("tienda").child("cartas").child(elem.id_carta.toString()).child("precio")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var precioCarta = snapshot.getValue()
                        precioPedido.text = precioCarta.toString()
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })

            db_ref.child("tienda").child("usuarios").child(elem.id_usuario.toString()).child("nombre")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var nombreUsuario= snapshot.getValue()
                        nombreCartaPedido.text= nombreUsuario.toString()
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })


            db_ref.child("tienda").child("cartas").child(elem.id_carta.toString()).child("img")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var imagen= snapshot.getValue()
                        Glide.with(con).load(imagen).into(rowIvCartaPedido)
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })


            aceptarPedido.setOnClickListener {

                db_ref.child("tienda").child("reservas_carta").child(elem.id.toString()).child("aceptado").setValue(true)
                aceptarPedido.text = "Aceptado"
                aceptarPedido.isEnabled = false

            }

        }

    }

    override fun getItemCount(): Int {
        return elementos.size
    }
}