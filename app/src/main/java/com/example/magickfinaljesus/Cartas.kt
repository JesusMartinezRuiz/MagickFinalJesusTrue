package com.example.magickfinaljesus

import java.io.Serializable

data class Cartas(
    val id:String?=null,
    val nombre:String?=null,
    val precio:Int?=null,
    val disponible:Boolean?=null,
    val img:String?=null,
    val color:String?=null):Serializable
