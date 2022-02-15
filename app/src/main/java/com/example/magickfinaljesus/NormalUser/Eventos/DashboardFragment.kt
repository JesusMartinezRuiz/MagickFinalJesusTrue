package com.example.magickfinaljesus.NormalUser.Eventos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.UserMain
import com.example.magickfinaljesus.databinding.FragmentEventosBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DashboardFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentEventosBinding? = null
    lateinit var menu: Menu
    // This property is only valid between onCreateView and
    // onDestroyView.

    val ma by lazy{
        activity as UserMain
    }
    private val binding get() = _binding!!

    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()

        _binding = FragmentEventosBinding.inflate(inflater, container, false)
        val root: View = binding.root


        dashboardViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()


        binding.rvEventos.apply {
            adapter= ma.adaptadorEvento
            layoutManager= LinearLayoutManager(ma)
            setHasFixedSize(true)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}