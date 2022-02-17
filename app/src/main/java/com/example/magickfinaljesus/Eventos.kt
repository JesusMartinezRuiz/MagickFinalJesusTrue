package com.example.magickfinaljesus

import java.io.Serializable

data class Eventos(
    val id:String?=null,
    val nombre:String?=null,
    val precio:Float?=null,
    val aforo_max:Int?=null,
    val aforo_ocupado:Int?=null,
    val fecha:String?=null,
    val img:String?=null):Serializable
