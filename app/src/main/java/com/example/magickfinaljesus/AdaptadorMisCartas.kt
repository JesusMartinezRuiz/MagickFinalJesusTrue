package com.example.magickfinaljesus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowMisCartasBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase



private lateinit var db_ref: DatabaseReference

class AdaptadorMisCartas(val elementos: List<ReservaCarta>, val con: UserMain,) :
    RecyclerView.Adapter<AdaptadorMisCartas.ViewHolder>() {


    class ViewHolder(val bind: RowMisCartasBinding)
        : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        db_ref= FirebaseDatabase.getInstance().getReference()

        val v =
            RowMisCartasBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var elem = elementos[position]

        with(holder.bind){
            nombreMisCartas.text=elem.nombre_carta
            precioMisCartas.text=elem.precio.toString()
            Glide.with(con).load(elem.img_carta).into(ivMisCartas)
        }

    }

    override fun getItemCount(): Int {
        return elementos.size
    }
}