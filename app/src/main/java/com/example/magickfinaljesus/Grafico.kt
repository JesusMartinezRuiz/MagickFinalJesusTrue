package com.example.magickfinaljesus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.data.PieData

class Grafico : AppCompatActivity() {

    lateinit var datos : List<String>
    lateinit var pieData: PieData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grafico)

        datos= listOf("blanco","negro","azul","rojo","verde")

    }
}