package com.example.magickfinaljesus

import java.io.Serializable

data class Usuario(var id:String?=null,
                 var nombre: String? = null,
                 var contraseña:String?=null,
                 var tipo:String?=null,
                 var correo:String?=null,
                 var url_usuario:String?=null,
                 var estado_noti:Int?=0,
                 var user_notificador:String?=null,
                 var nombreAnterior:String?=null
                ):Serializable
