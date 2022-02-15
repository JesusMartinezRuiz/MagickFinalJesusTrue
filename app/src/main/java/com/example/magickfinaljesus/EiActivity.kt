package com.example.magickfinaljesus

import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    val listaCartaAdmin by lazy{
        listaAdmin
    }

    val contextoEiActivity by lazy{
        this
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        listaAdmin=ArrayList<Cartas>()



        binding = ActivityEiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarEi.toolbar)

        binding.appBarEi.fab.setOnClickListener { view ->
            val actividad = Intent(applicationContext, CrearCarta::class.java)
            startActivity (actividad)
        }
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
}