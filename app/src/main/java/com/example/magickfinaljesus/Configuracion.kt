package com.example.magickfinaljesus

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate

class Configuracion : AppCompatActivity() {

    lateinit var cambiarTema: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        cambiarTema=findViewById(R.id.swiTema)
    }

    override fun onStart() {
        super.onStart()

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP"
        var SP = getSharedPreferences(sp_name,0)



        cambiarTema.setOnCheckedChangeListener { _, b ->

            val tema = if(b){
                AppCompatDelegate.MODE_NIGHT_YES
            }else{
                AppCompatDelegate.MODE_NIGHT_NO
            }

            with(SP.edit()) {
                putBoolean(
                    getString(R.string.modo),
                    b
                )
            }

            cambiarTema.isChecked=b
            AppCompatDelegate.setDefaultNightMode(tema)

        }


    }

}