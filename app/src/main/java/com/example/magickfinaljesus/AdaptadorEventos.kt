package com.example.magickfinaljesus

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.magickfinaljesus.databinding.RowEventosBinding
import com.google.firebase.database.*
import java.util.concurrent.CountDownLatch

private lateinit var db_ref: DatabaseReference

class AdaptadorEventos(val elementos: List<Eventos>, val con: UserMain, val idUsuario:String,val nombreDeUsuario:String) :
    RecyclerView.Adapter<AdaptadorEventos.ViewHolder>() {


    class ViewHolder(val bind: RowEventosBinding)
        : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        db_ref= FirebaseDatabase.getInstance().getReference()
        val v =
            RowEventosBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elem = elementos[position]

        with(holder.bind){
            rowNombreEvento.text=elem.nombre
            rowPrecioEvento.text=elem.precio.toString()
            rowAforoMaxEvento.text= elem.aforo_max.toString()
            rowAforoOcupadoEvento.text= elem.aforo_ocupado.toString()
            rowFechaEvento.text= elem.fecha
            Glide.with(con).load(elem.img).into(rowIvEvento)

            rowApuntarseEvento.setOnClickListener {
                val id_reservaEvento=db_ref.child("tienda").child("reservas_eventos").push().key!!
                val nueva_reserva=ReservaEventos(id_reservaEvento,idUsuario,elem.id,nombreDeUsuario)
                db_ref.child("tienda").child("reservas_eventos").child(id_reservaEvento).setValue(nueva_reserva)
                db_ref.child("tienda").child("eventos").child(elem.id.toString()).child("aforo_ocupado").setValue(elem.aforo_ocupado!!.toInt()+1)
                rowAforoOcupadoEvento.text= (elem.aforo_ocupado.toInt()+1).toString()
                rowApuntarseEvento.visibility=View.INVISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return elementos.size
    }
}