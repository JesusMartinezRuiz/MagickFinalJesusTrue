package com.example.magikfinaljesus.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.*
import com.example.magickfinaljesus.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    lateinit var menu: Menu

    val ma by lazy{
        activity as EiActivity
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    val adaptadorCartaAdmin by lazy{
        AdaptadorCartaAdmin(ma.listaCartaAdmin, ma.contextoEiActivity)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()



        homeViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()

        ma.FAB_manager(1)

        with(binding){
            rvCartasAdmin.adapter= adaptadorCartaAdmin
            rvCartasAdmin.layoutManager= LinearLayoutManager(ma)

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null



    }




}