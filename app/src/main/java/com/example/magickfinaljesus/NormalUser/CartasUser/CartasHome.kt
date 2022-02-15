package com.example.magickfinaljesus.NormalUser.CartasUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.AdaptadorCartas
import com.example.magickfinaljesus.UserMain
import com.example.magickfinaljesus.databinding.FragmentCartasBinding
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CartasHome : Fragment() {

    private lateinit var cartasHomeView: CartasHomeView
    private var _binding: FragmentCartasBinding? = null
    lateinit var menu: Menu
    var colorChecked=mutableListOf(true,true,true,true,true)

    val ma by lazy{
        activity as UserMain
    }
    private val binding get() = _binding!!

    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    val adaptadorCarta by lazy{
        AdaptadorCartas(ma.listaCarta, ma.contextoUserMain , colorChecked)
    }


    // This property is only valid between onCreateView and
    // onDestroyView.

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        cartasHomeView =
            ViewModelProvider(this).get(CartasHomeView::class.java)

        _binding = FragmentCartasBinding.inflate(inflater, container, false)
        val root: View = binding.root



        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()


       var listaCheck = listOf(
            binding.checkBlancoCartas,binding.checkNegroCartas,binding.checkAzulCartas,binding.checkRojoCartas,binding.checkVerdeCartas
        )

        listaCheck.forEach {
            it.isEnabled = false
            it.isChecked= false
        }

        binding.swiCartas.setOnCheckedChangeListener { compoundButton, b ->
            listaCheck.forEach {
                it.isEnabled = b
                it.isChecked= true
            }
            adaptadorCarta.allSelected = !b
            refreshFilter()
        }

        return root
    }



    override fun onStart() {
        super.onStart()

        var listaCheck = listOf(
            binding.checkBlancoCartas,binding.checkNegroCartas,binding.checkAzulCartas,binding.checkRojoCartas,binding.checkVerdeCartas
        )

        with(binding){


                rvComprarCartas.adapter= adaptadorCarta
                rvComprarCartas.layoutManager=LinearLayoutManager(ma)
                cartasSearcher.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    adaptadorCarta.filter.filter(p0)
                    return false
                }
            })


            listaCheck.forEach {
                it.setOnClickListener {
                    val cb = it as CheckBox
                    val index = listaCheck.indexOf(cb)
                    colorChecked[index] = cb.isChecked
                    adaptadorCarta.colors = colorChecked
                    adaptadorCarta.filter.filter(cartasSearcher.query)
                }
            }

        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun refreshFilter(){
        adaptadorCarta.filter.filter("")
    }




}