package com.example.magikfinaljesus.ui.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.magickfinaljesus.EiActivity
import com.example.magickfinaljesus.R
import com.example.magickfinaljesus.UserMain
import com.example.magickfinaljesus.databinding.FragmentGalleryBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val ma by lazy{
        activity as EiActivity
    }

    private val binding get() = _binding!!

    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root


        galleryViewModel.text.observe(viewLifecycleOwner, Observer {

        })
        return root
    }

    override fun onStart() {
        super.onStart()


        binding.rvEventosAdmin.apply {
            adapter= ma.adaptadorEventoAdmin
            layoutManager= LinearLayoutManager(ma)
            setHasFixedSize(true)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}