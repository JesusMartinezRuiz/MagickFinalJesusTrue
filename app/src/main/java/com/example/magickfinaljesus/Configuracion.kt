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

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP"
        var SP = getSharedPreferences(sp_name,0)


        cambiarTema.setOnCheckedChangeListener { buttonView, isChecked ->

        if(isChecked){
            AppCompatDelegate.MODE_NIGHT_YES

            with(SP.edit()) {
                putString(
                    getString(R.string.modo),
                    "night"
                )
            }

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        }else{
            AppCompatDelegate.MODE_NIGHT_NO

            with(SP.edit()) {
                putString(
                    getString(R.string.modo),
                    "light"
                )
            }

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        }

    }

}