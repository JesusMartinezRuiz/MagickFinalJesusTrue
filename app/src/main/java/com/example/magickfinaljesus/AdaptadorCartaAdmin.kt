package com.example.magickfinaljesus

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowCartaAdminBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

private lateinit var db_ref: DatabaseReference

class AdaptadorCartaAdmin(val elementos: List<Cartas>, val contexto: EiActivity) :
    RecyclerView.Adapter<AdaptadorCartaAdmin.ViewHolder>(){





    class ViewHolder(val bind:RowCartaAdminBinding)
        : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        db_ref= FirebaseDatabase.getInstance().getReference()
        val v =
            RowCartaAdminBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elem = elementos[position]

        with(holder.bind){
            tvNombreCartaAdmin.text = elem.nombre
            Glide.with(contexto).load(elem.img).into(ivCartaAdmin)
            tvPrecioAdmin.text = elem.precio.toString()
            swiDisponible.isChecked = elem.disponible!!


            swiDisponible.setOnClickListener {
                if(!swiDisponible.isChecked){
                    db_ref.child("tienda").child("cartas").child(elem.id.toString()).child("disponible").setValue(false)
                }else{
                    db_ref.child("tienda").child("cartas").child(elem.id.toString()).child("disponible").setValue(true)

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return elementos.size
    }



}