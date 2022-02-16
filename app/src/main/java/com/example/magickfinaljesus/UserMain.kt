package com.example.magickfinaljesus

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.magickfinaljesus.databinding.ActivityUserMainBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch


class UserMain : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityUserMainBinding
    lateinit var lista:ArrayList<Cartas>
    lateinit var listaMisCartas:ArrayList<Cartas>
    lateinit var listaEventos:ArrayList<Eventos>


    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    val adaptadorMisCartas by lazy{
        AdaptadorMisCartas(listaMisCartas,this)
    }

    val adaptadorEvento by lazy{
        AdaptadorEventos(listaEventos,this, idDeUsuario)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        lista=ArrayList<Cartas>()
        listaEventos=ArrayList<Eventos>()
        listaMisCartas=ArrayList<Cartas>()



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
            .child("reservas_cartas")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaMisCartas.clear()

                    snapshot.children.forEach { hijo ->
                        val pojo_reserva = hijo?.getValue(ReservaCarta::class.java)

                        db_ref.child("tienda")
                            .child("cartas")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    snapshot.children.forEach { hijo ->
                                        val pojo_carta = hijo?.getValue(Cartas::class.java)
                                        if (pojo_reserva!!.id_usuario == idDeUsuario && pojo_reserva.aceptado == true) {
                                            listaMisCartas.add(pojo_carta!!)
                                        }
                                    }

                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                    }


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

}