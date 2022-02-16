package com.example.magickfinaljesus.NormalUser.Seguimiento

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.UserMain
import com.example.magickfinaljesus.databinding.FragmentNotificationsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentNotificationsBinding? = null
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
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root



        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()


        binding.rvMisCartas.apply {
            adapter= ma.adaptadorMisCartas
            layoutManager= LinearLayoutManager(ma)
            setHasFixedSize(true)
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}