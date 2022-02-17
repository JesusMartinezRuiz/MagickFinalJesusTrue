package com.example.magickfinaljesus

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.magickfinaljesus.databinding.ActivityEiBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EiActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityEiBinding
    lateinit var listaAdmin:ArrayList<Cartas>
    lateinit var listaAdminEventos:ArrayList<Eventos>
    lateinit var listaPedidos:ArrayList<ReservaCarta>
    lateinit var listaVerMiembros:ArrayList<ReservaEventos>
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference



    val listaCartaAdmin by lazy{
        listaAdmin
    }

    val contextoEiActivity by lazy{
        this
    }


    val listaEventoAdmin by lazy{
        listaAdminEventos
    }


    val adaptadorEventoAdmin by lazy{
        AdaptadorEventosAdmin(listaEventoAdmin,this)
    }

    val adaptadorPedidos by lazy{
        AdaptadorPedidos(listaPedidos,this)
    }

    val listaMiembros by lazy{
        listaVerMiembros
    }

    val adaptadorVerMiembros by lazy {
        AdaptadorVerMiembros(listaMiembros,this)
    }

    //el adaptador de pedidos aqui igual que este


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        listaAdmin=ArrayList<Cartas>()
        listaAdminEventos=ArrayList()
        listaPedidos= ArrayList<ReservaCarta>()
        listaVerMiembros=ArrayList()

        binding = ActivityEiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarEi.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_ei)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)



        db_ref.child("tienda")
            .child("reservas_eventos")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaVerMiembros.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_verMiembros=hijo?.getValue(ReservaEventos::class.java)
                        listaVerMiembros.add(pojo_verMiembros!!)

                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

        db_ref.child("tienda")
            .child("cartas")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaAdmin.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_carta=hijo?.getValue(Cartas::class.java)
                            listaAdmin.add(pojo_carta!!)

                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })


        db_ref.child("tienda")
            .child("eventos")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaAdminEventos.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_evento=hijo?.getValue(Eventos::class.java)
                            listaAdminEventos.add(pojo_evento!!)


                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

        db_ref.child("tienda")
            .child("reservas_carta")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaPedidos.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_pedido=hijo?.getValue(ReservaCarta::class.java)
                        listaPedidos.add(pojo_pedido!!)

                    }

                }
                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.ei, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_ei)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val actividad = Intent(applicationContext,MainActivity::class.java)
        startActivity (actividad)

    }

    fun FAB_manager(mode:Int){
        when(mode){
            0 -> {
                binding.appBarEi.fab.visibility = View.INVISIBLE
            }
            1 -> {
                binding.appBarEi.fab.visibility = View.VISIBLE
                binding.appBarEi.fab.setImageResource(R.drawable.ic_baseline_add24)
                binding.appBarEi.fab.setOnClickListener { view ->
                    val actividad = Intent(applicationContext, CrearCarta::class.java)
                    startActivity (actividad)
                }
            }
            2 -> {
                binding.appBarEi.fab.visibility = View.VISIBLE
                binding.appBarEi.fab.setImageResource(R.drawable.ic_baseline_add24)
                binding.appBarEi.fab.setOnClickListener { view ->
                    val actividad = Intent(applicationContext,CrearEvento::class.java)
                    startActivity (actividad)
                }
            }
        }
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