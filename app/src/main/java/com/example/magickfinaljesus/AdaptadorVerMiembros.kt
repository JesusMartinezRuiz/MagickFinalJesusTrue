package com.example.magickfinaljesus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.magickfinaljesus.databinding.RowVerMiembrosBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

private lateinit var db_ref: DatabaseReference

class AdaptadorVerMiembros(val elementos: List<ReservaEventos>, val con: EiActivity) :
    RecyclerView.Adapter<AdaptadorVerMiembros.ViewHolder>() {


    class ViewHolder(val bind: RowVerMiembrosBinding)
        : RecyclerView.ViewHolder(bind.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        db_ref= FirebaseDatabase.getInstance().getReference()

        val v =
            RowVerMiembrosBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var elem = elementos[position]


        with(holder.bind){
           rowNombreVerMiembros.text=elem.nombreUser

        }

    }

    override fun getItemCount(): Int {
        return elementos.size
    }
}