package com.example.magickfinaljesus

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.magickfinaljesus.databinding.ActivityUserMainBinding
import com.example.magikfinaljesus.ui.home.HomeFragment
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Serializable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger


class UserMain : AppCompatActivity() {


    private lateinit var binding: ActivityUserMainBinding
    lateinit var lista:ArrayList<Cartas>
    lateinit var listaMisCartas:ArrayList<ReservaCarta>
    lateinit var listaEventos:ArrayList<Eventos>
    private lateinit var androidId:String
    private lateinit var generador: AtomicInteger


    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    val adaptadorMisCartas by lazy{
        AdaptadorMisCartas(listaMisCartas,this)
    }

    val adaptadorEvento by lazy{
        AdaptadorEventos(listaEventos,this, idDeUsuario,nombreDeUsuario)
    }


    val listaCarta by lazy{
       lista
    }

    val contextoUserMain by lazy{
        this
    }

    val idDeUsuario by lazy{
        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)

        SP.getString(
            getString(R.string.id),
            "falloShared"
        ).toString()
    }

    val nombreDeUsuario by lazy{
        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)

        SP.getString(
            getString(R.string.username),
            "falloShared"
        ).toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        generador=AtomicInteger(0)

        androidId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        lista=ArrayList<Cartas>()
        listaEventos=ArrayList<Eventos>()
        listaMisCartas=ArrayList<ReservaCarta>()



        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_user_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_cartas, R.id.navigation_eventos, R.id.navigation_cesta
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        db_ref.child("tienda")
            .child("cartas")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_carta=hijo?.getValue(Cartas::class.java)
                        if(pojo_carta!!.disponible==true){
                            lista.add(pojo_carta!!)
                        }

                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })


        db_ref.child("tienda")
            .child("eventos")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaEventos.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_evento=hijo?.getValue(Eventos::class.java)
                        if(pojo_evento!!.aforo_ocupado!! < pojo_evento.aforo_max!!){
                            listaEventos.add(pojo_evento!!)
                        }

                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

        db_ref.child("tienda")
            .child("reservas_carta")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMisCartas.clear()
                    GlobalScope.launch(Dispatchers.IO) {
                        snapshot.children.forEach { hijo: DataSnapshot? ->
                            val pojo_pedido = hijo?.getValue(ReservaCarta::class.java)

                            //USAMOS EL SEMAFORO PARA SINCRONIZAR: LINEALIZAMOS EL CODIGO

                            var semaforo = CountDownLatch(2)

                            db_ref.child("tienda")
                                .child("cartas")
                                .child(pojo_pedido!!.id_carta!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pojo_carta = snapshot?.getValue(Cartas::class.java)
                                        //RELLENAR LOS DATOS PARA EL ADAPTADOR
                                        pojo_pedido.nombre_carta = pojo_carta!!.nombre
                                        pojo_pedido.img_carta = pojo_carta!!.img
                                        pojo_pedido.precio=pojo_carta!!.precio
                                        semaforo.countDown()

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        println(error.message)
                                    }
                                })

                            db_ref.child("tienda")
                                .child("usuarios")
                                .child(pojo_pedido!!.id_usuario!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val pojo_user = snapshot?.getValue(Usuario::class.java)
                                        //RELLENAR LOS DATOS PARA EL ADAPTADOR
                                        pojo_pedido.nombre_user = pojo_user!!.nombre
                                        semaforo.countDown()

                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        println(error.message)
                                    }
                                })

                            semaforo.await();
                            if (pojo_pedido.id_usuario==idDeUsuario){
                                listaMisCartas.add(pojo_pedido!!)
                            }

                        }

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        db_ref.child("tienda").child("reservas_cartas").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val pojo_usuario2=snapshot.getValue(Usuario::class.java)
                if(!pojo_usuario2!!.user_notificador.equals(androidId) && pojo_usuario2.estado_noti==Estado.CREADO){
                    generarNotificacion(generador.incrementAndGet(),pojo_usuario2,"El Usuario "+pojo_usuario2.nombre+" ha hecho un pedido","Pedido",HomeFragment::class.java)
                    db_ref.child("tienda").child("usuarios").child(pojo_usuario2.id!!).child("estado_noti").setValue(Estado.NOTIFICADO)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
}

    override fun onBackPressed() {
        super.onBackPressed()
        val actividad = Intent(applicationContext,MainActivity::class.java)
        startActivity (actividad)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.user_main_kebab, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP"
        var SP = getSharedPreferences(sp_name,0)

        return when (item.itemId) {
            R.id.action_settings -> {
                val ir_creditos = Intent(applicationContext, Configuracion::class.java)
                startActivity(ir_creditos)
                true
            }
            R.id.action_author -> {
                val intent = Intent(applicationContext, Autor::class.java)
                startActivity(intent)
                true
            }
            R.id.action_grafico ->{
                val intent = Intent(applicationContext, Grafico::class.java)
                startActivity(intent)
                true
            }
            R.id.action_logOut ->{
                with(SP.edit()){
                    putString("ID", "")
                    commit()
                }
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)

                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun generarNotificacion(id_noti:Int, pojo: Serializable, contenido:String, titulo:String, destino:Class<*>) {
        val idcanal = getString(R.string.id_canal)
        val iconolargo = BitmapFactory.decodeResource(
            resources,
            R.drawable.logonotif
        )
        val actividad = Intent(applicationContext,destino)
        actividad.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
        actividad.putExtra("club", pojo)
        val pendingIntent= PendingIntent.getActivity(this,0,actividad, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, idcanal)
            .setLargeIcon(iconolargo)
            .setSmallIcon(R.drawable.logonotif)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de información")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)){
            notify(id_noti,notification)
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = getString(R.string.nombre_canal)
            val idcanal = getString(R.string.id_canal)
            val descripcion = getString(R.string.description_canal)
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(idcanal, nombre, importancia).apply {
                description = descripcion
            }

            val nm: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }


}