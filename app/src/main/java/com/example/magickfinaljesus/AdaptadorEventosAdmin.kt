package com.example.magickfinaljesus

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowEventosAdminBinding

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.Serializable

private lateinit var db_ref: DatabaseReference


class AdaptadorEventosAdmin(val elementos: List<Eventos>, val contexto: EiActivity) :
    RecyclerView.Adapter<AdaptadorEventosAdmin.ViewHolder>(){




    class ViewHolder(val bind:RowEventosAdminBinding)
        : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        db_ref= FirebaseDatabase.getInstance().getReference()
        val v =
            RowEventosAdminBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elem = elementos[position]

        with(holder.bind){
            rowNombreEventoAdmin.text = elem.nombre
            rowPrecioEventoAdmin.text= elem.precio.toString()
            Glide.with(contexto).load(elem.img).into(ivRowAdminEventos)
            rowAforoMaxAdmin.text = elem.aforo_max.toString()
            rowAforoOcAdmin.text= elem.aforo_ocupado.toString()
            rowFechaAdmin.text=elem.fecha

            verMiembros.setOnClickListener {
                val inte = Intent (contexto, VerMiembros::class.java)
                inte.putExtra("Evento",elem)
                contexto.startActivity(inte)
            }


            }

        }


    override fun getItemCount(): Int {
        return elementos.size
    }



}