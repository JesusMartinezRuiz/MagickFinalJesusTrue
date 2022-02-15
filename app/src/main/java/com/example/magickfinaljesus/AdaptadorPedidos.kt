package com.example.magickfinaljesus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowPedidosBinding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var db_ref: DatabaseReference

class AdaptadorPedidos(val elementos: List<Eventos>, val con: UserMain, val idUsuario:String) :
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
            nombrePedido.text = elem.nombre
            precioPedido.text = elem.precio.toString()
            Glide.with(con).load(elem.img).into(rowIvCartaPedido)


            aceptarPedido.setOnClickListener {
//                val id_reservaEvento=db_ref.child("tienda").child("reservas_eventos").push().key!!
//                val nueva_reserva= ReservaEventos(id_reservaEvento,idUsuario,elem.id)
//                db_ref.child("tienda").child("reservas_eventos").child(id_reservaEvento).setValue(nueva_reserva)
//                db_ref.child("tienda").child("eventos").child(elem.id.toString()).child("aforo_ocupado").setValue(elem.aforo_ocupado!!.toInt()+1)
//
                //Aqui cambiamos con el db y los child el estado de la reserva_carta correspondiente ^^^^^^

                aceptarPedido.text = "Acpetado"
                aceptarPedido.isEnabled = false

            }

        }

    }

    override fun getItemCount(): Int {
        return elementos.size
    }
}