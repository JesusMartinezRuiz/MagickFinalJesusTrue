package com.example.magickfinaljesus

import java.io.Serializable

data class ReservaCarta(var id:String?=null,
                          var id_usuario:String? = null,
                          var id_carta:String?=null,
                          var fecha:String?=null,
                          var aceptado:Boolean?=null):Serializable